package ma.ensa.dop.declaration;

import com.jayway.jsonpath.JsonPath;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import ma.ensa.dop.declaration.entity.Declaration;
import ma.ensa.dop.declaration.repository.DeclarationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** S14 - Tests d'intégration : création, sauvegarde automatique, reprise, sécurité. */
@SpringBootTest(properties = "dop.jwt.secret=" + BrouillonFlowTests.SECRET)
@AutoConfigureMockMvc
class BrouillonFlowTests {
    static final String SECRET = "test-only-secret-0123456789-abcdefghij";

    @Autowired MockMvc mvc;
    @Autowired DeclarationRepository declarations;

    @BeforeEach void clean() { declarations.deleteAll(); }

    /** Simule un utilisateur connecté (JWT déjà validé) avec son rôle et son e-mail. */
    private static RequestPostProcessor as(String role, String email) {
        return jwt().jwt(j -> j.subject(email)).authorities(new SimpleGrantedAuthority("ROLE_" + role));
    }
    private static RequestPostProcessor sara() { return as("DECLARANT", "sara@test.ma"); }
    private static RequestPostProcessor omar() { return as("DECLARANT", "omar@test.ma"); }

    private static final MediaType JSON = MediaType.APPLICATION_JSON;

    /** Crée un brouillon pour l'utilisateur donné et renvoie sa réponse JSON. */
    private String creer(RequestPostProcessor user, String type) throws Exception {
        return mvc.perform(post("/declarations").with(user).contentType(JSON).content("{\"type\":\"" + type + "\"}"))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
    }
    private static int id(String json) { return JsonPath.read(json, "$.id"); }
    private static int version(String json) { return ((Number) JsonPath.read(json, "$.version")).intValue(); }

    // ================= Création =================

    @Test void creates_a_draft_of_the_chosen_type() throws Exception {
        for (String type : new String[]{"INITIALE", "RENOUVELLEMENT", "CESSATION"}) {
            mvc.perform(post("/declarations").with(sara()).contentType(JSON).content("{\"type\":\"" + type + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value(type))
                .andExpect(jsonPath("$.statut").value("BROUILLON"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.derniereSauvegarde").isNotEmpty());
        }
    }

    @Test void missing_or_unknown_type_is_rejected() throws Exception {
        mvc.perform(post("/declarations").with(sara()).contentType(JSON).content("{}"))
            .andExpect(status().isBadRequest());
        mvc.perform(post("/declarations").with(sara()).contentType(JSON).content("{\"type\":\"BIDON\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test void second_draft_of_the_same_type_is_refused_and_points_to_the_existing_one() throws Exception {
        int premier = id(creer(sara(), "INITIALE"));
        mvc.perform(post("/declarations").with(sara()).contentType(JSON).content("{\"type\":\"INITIALE\"}"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.declarationId").value(premier));
        creer(sara(), "CESSATION");                       // un autre type reste possible
        creer(omar(), "INITIALE");                        // un autre agent aussi
    }

    // ================= Sauvegarde automatique + reprise =================

    @Test void autosave_persists_the_draft_and_it_can_be_resumed() throws Exception {
        String cree = creer(sara(), "INITIALE");

        mvc.perform(patch("/declarations/" + id(cree) + "/brouillon").with(sara()).contentType(JSON)
                .content("{\"anneeReference\":2025,\"observations\":\"Premiere saisie\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.anneeReference").value(2025))
            .andExpect(jsonPath("$.statut").value("BROUILLON"));

        // "Reprise à tout moment" : on relit le brouillon plus tard
        mvc.perform(get("/declarations/" + id(cree)).with(sara()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.anneeReference").value(2025))
            .andExpect(jsonPath("$.observations").value("Premiere saisie"));
    }

    @Test void partial_save_keeps_the_fields_that_are_not_sent() throws Exception {
        String cree = creer(sara(), "INITIALE");
        String url = "/declarations/" + id(cree) + "/brouillon";
        mvc.perform(patch(url).with(sara()).contentType(JSON).content("{\"anneeReference\":2025}"))
            .andExpect(status().isOk());
        mvc.perform(patch(url).with(sara()).contentType(JSON).content("{\"observations\":\"Note\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.anneeReference").value(2025))        // toujours là
            .andExpect(jsonPath("$.observations").value("Note"));
    }

    @Test void an_incomplete_draft_is_accepted() throws Exception {
        String cree = creer(sara(), "INITIALE");
        mvc.perform(patch("/declarations/" + id(cree) + "/brouillon").with(sara()).contentType(JSON).content("{}"))
            .andExpect(status().isOk());
    }

    @Test void invalid_values_are_rejected() throws Exception {
        String cree = creer(sara(), "INITIALE");
        mvc.perform(patch("/declarations/" + id(cree) + "/brouillon").with(sara()).contentType(JSON)
                .content("{\"anneeReference\":1800}"))
            .andExpect(status().isBadRequest());
    }

    @Test void my_list_contains_only_my_drafts_most_recent_first() throws Exception {
        String premier = creer(sara(), "INITIALE");
        Thread.sleep(10);
        creer(sara(), "CESSATION");
        creer(omar(), "INITIALE");
        Thread.sleep(10);
        // on sauvegarde le premier brouillon : il devient le plus récent
        mvc.perform(patch("/declarations/" + id(premier) + "/brouillon").with(sara()).contentType(JSON)
            .content("{\"observations\":\"maj\"}")).andExpect(status().isOk());

        mvc.perform(get("/declarations").with(sara()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].type").value("INITIALE"))
            .andExpect(jsonPath("$[1].type").value("CESSATION"));
    }

    // ================= Concurrence (deux onglets) =================

    @Test void saving_with_the_current_version_works_and_increments_it() throws Exception {
        String cree = creer(sara(), "INITIALE");
        String apres = mvc.perform(patch("/declarations/" + id(cree) + "/brouillon").with(sara()).contentType(JSON)
                .content("{\"observations\":\"v1\",\"version\":" + version(cree) + "}"))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        org.assertj.core.api.Assertions.assertThat(version(apres)).isGreaterThan(version(cree));
    }

    @Test void saving_with_a_stale_version_is_refused() throws Exception {
        String cree = creer(sara(), "INITIALE");
        mvc.perform(patch("/declarations/" + id(cree) + "/brouillon").with(sara()).contentType(JSON)
                .content("{\"observations\":\"autre onglet\",\"version\":" + (version(cree) + 5) + "}"))
            .andExpect(status().isConflict());
    }

    // ================= Isolation entre agents =================

    @Test void another_agent_cannot_read_or_modify_my_draft() throws Exception {
        int monId = id(creer(sara(), "INITIALE"));
        mvc.perform(get("/declarations/" + monId).with(omar())).andExpect(status().isNotFound());
        mvc.perform(patch("/declarations/" + monId + "/brouillon").with(omar()).contentType(JSON)
            .content("{\"observations\":\"intrus\"}")).andExpect(status().isNotFound());
    }

    // ================= Verrouillage après soumission =================

    @Test void a_submitted_declaration_cannot_be_modified() throws Exception {
        int monId = id(creer(sara(), "INITIALE"));
        Declaration d = declarations.findById((long) monId).orElseThrow();
        d.soumettre(Instant.now());                         // simule S19 (soumission)
        declarations.save(d);

        mvc.perform(patch("/declarations/" + monId + "/brouillon").with(sara()).contentType(JSON)
                .content("{\"observations\":\"trop tard\"}"))
            .andExpect(status().isConflict());
    }

    // ================= Sécurité =================

    @Test void only_declarants_can_use_the_endpoints() throws Exception {
        mvc.perform(post("/declarations").with(as("AUTORITE_TUTELLE", "t@test.ma")).contentType(JSON)
            .content("{\"type\":\"INITIALE\"}")).andExpect(status().isForbidden());
        mvc.perform(get("/declarations").with(as("MAGISTRAT", "m@test.ma"))).andExpect(status().isForbidden());
        mvc.perform(get("/declarations")).andExpect(status().isUnauthorized());
    }

    private static String token(String secret, String issuer, String role) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder().issuer(issuer).subject("sara@test.ma")
            .issuedAt(now).expiresAt(now.plus(5, ChronoUnit.MINUTES)).claim("role", role).build();
        var encoder = new NimbusJwtEncoder(new ImmutableSecret<>(
            new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256")));
        return encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
    }

    @Test void real_token_from_auth_service_is_accepted() throws Exception {
        mvc.perform(get("/declarations").header("Authorization", "Bearer " + token(SECRET, "dop-auth-service", "DECLARANT")))
            .andExpect(status().isOk());
    }

    @Test void forged_or_foreign_tokens_are_rejected() throws Exception {
        mvc.perform(get("/declarations").header("Authorization",
                "Bearer " + token("un-autre-secret-0123456789-abcdefghijkl", "dop-auth-service", "DECLARANT")))
            .andExpect(status().isUnauthorized());
        mvc.perform(get("/declarations").header("Authorization",
                "Bearer " + token(SECRET, "autre-emetteur", "DECLARANT")))
            .andExpect(status().isUnauthorized());
    }

    @Test void health_is_public() throws Exception {
        mvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }
}
