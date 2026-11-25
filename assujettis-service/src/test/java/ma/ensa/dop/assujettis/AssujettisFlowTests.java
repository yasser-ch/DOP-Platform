package ma.ensa.dop.assujettis;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import ma.ensa.dop.assujettis.repository.AssujettiRepository;
import ma.ensa.dop.assujettis.repository.ListeImportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Tests d'intégration de S11, S12 et S13 (HTTP + sécurité + base H2). */
@SpringBootTest(properties = "dop.jwt.secret=" + AssujettisFlowTests.SECRET)
@AutoConfigureMockMvc
class AssujettisFlowTests {
    static final String SECRET = "test-only-secret-0123456789-abcdefghij";
    private static final String ENTETE = "cin,nom,prenom,email,fonction,organisme,date_entree_fonction\n";

    @Autowired MockMvc mvc;
    @Autowired AssujettiRepository assujettis;
    @Autowired ListeImportRepository imports;

    @BeforeEach void clean() { assujettis.deleteAll(); imports.deleteAll(); }

    /** Simule un utilisateur connecté (JWT déjà validé) avec son rôle et son e-mail. */
    private static RequestPostProcessor as(String role, String email) {
        return jwt().jwt(j -> j.subject(email)).authorities(new SimpleGrantedAuthority("ROLE_" + role));
    }
    private static RequestPostProcessor tutelle() { return as("AUTORITE_TUTELLE", "tutelle@test.ma"); }

    private static MockMultipartFile csv(String contenu) {
        return new MockMultipartFile("file", "liste.csv", "text/csv", contenu.getBytes(StandardCharsets.UTF_8));
    }
    private static String formulaire(String cin, String email, LocalDate entree) {
        return """
            {"cin":"%s","nom":"Alaoui","prenom":"Sara","email":"%s",
             "fonction":"Chef de service","organisme":"Ministère X","dateEntreeFonction":"%s"}"""
            .formatted(cin, email, entree);
    }

    // ================= S11 =================

    @Test void import_csv_then_list_is_visible_and_timestamped() throws Exception {
        mvc.perform(multipart("/assujettis/import").file(csv(ENTETE
                + "AB123456,Alaoui,Sara,sara@test.ma,Chef,Ministère X,2026-03-15\n"
                + "CD654321,Benani,Omar,omar@test.ma,Directeur,Ministère Y,2025-01-10\n")).with(tutelle()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.crees").value(2))
            .andExpect(jsonPath("$.misAJour").value(0))
            .andExpect(jsonPath("$.importedAt").isNotEmpty());

        mvc.perform(get("/assujettis").with(tutelle()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].importedAt").isNotEmpty())
            .andExpect(jsonPath("$[0].importedBy").value("tutelle@test.ma"));

        mvc.perform(get("/assujettis/imports").with(tutelle()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].source").value("CSV:liste.csv"));
    }

    @Test void reimporting_the_same_cin_updates_instead_of_duplicating() throws Exception {
        String ligne = "AB123456,Alaoui,Sara,sara@test.ma,Chef,Ministère X,2026-03-15\n";
        mvc.perform(multipart("/assujettis/import").file(csv(ENTETE + ligne)).with(tutelle()))
            .andExpect(jsonPath("$.crees").value(1));
        mvc.perform(multipart("/assujettis/import").file(csv(ENTETE + ligne)).with(tutelle()))
            .andExpect(jsonPath("$.crees").value(0)).andExpect(jsonPath("$.misAJour").value(1));
        mvc.perform(get("/assujettis").with(tutelle())).andExpect(jsonPath("$.length()").value(1));
    }

    @Test void invalid_csv_is_refused_with_422_and_nothing_is_saved() throws Exception {
        mvc.perform(multipart("/assujettis/import").file(csv(ENTETE
                + "AB123456,Alaoui,Sara,sara@test.ma,Chef,Ministère X,2026-03-15\n"
                + "XX,Benani,Omar,pas-un-email,Directeur,Ministère Y,2025-01-10\n")).with(tutelle()))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.details[0].ligne").value(2));
        mvc.perform(get("/assujettis").with(tutelle())).andExpect(jsonPath("$.length()").value(0));
    }

    @Test void form_entry_works_and_invalid_form_is_rejected() throws Exception {
        mvc.perform(post("/assujettis").with(tutelle()).contentType(MediaType.APPLICATION_JSON)
                .content(formulaire("AB123456", "sara@test.ma", LocalDate.of(2026, 3, 15))))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.source").value("FORMULAIRE"));
        mvc.perform(post("/assujettis").with(tutelle()).contentType(MediaType.APPLICATION_JSON)
                .content(formulaire("???", "sara@test.ma", LocalDate.of(2026, 3, 15))))
            .andExpect(status().isBadRequest());
    }

    @Test void only_autorite_de_tutelle_can_import_or_list() throws Exception {
        mvc.perform(multipart("/assujettis/import").file(csv(ENTETE
                    + "AB123456,Alaoui,Sara,sara@test.ma,Chef,Ministère X,2026-03-15\n"))
                .with(as("DECLARANT", "agent@test.ma")))
            .andExpect(status().isForbidden());
        mvc.perform(get("/assujettis").with(as("MAGISTRAT", "juge@test.ma"))).andExpect(status().isForbidden());
        mvc.perform(get("/assujettis")).andExpect(status().isUnauthorized());
    }

    // ================= S12 =================

    @Test void agent_on_the_list_gets_access_to_the_form() throws Exception {
        mvc.perform(post("/assujettis").with(tutelle()).contentType(MediaType.APPLICATION_JSON)
            .content(formulaire("AB123456", "agent@test.ma", LocalDate.of(2026, 3, 15)))).andExpect(status().isCreated());

        mvc.perform(get("/assujettis/me/verification").with(as("DECLARANT", "AGENT@test.ma")))   // casse ignorée
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.assujetti").value(true))
            .andExpect(jsonPath("$.accesFormulaire").value(true))
            .andExpect(jsonPath("$.formulaireUrl").isNotEmpty())
            .andExpect(jsonPath("$.echeanceDeclaration").value("2026-06-15"));
    }

    @Test void agent_not_on_the_list_gets_a_clear_message_and_no_form() throws Exception {
        mvc.perform(get("/assujettis/me/verification").with(as("DECLARANT", "inconnu@test.ma")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.assujetti").value(false))
            .andExpect(jsonPath("$.accesFormulaire").value(false))
            .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("ne figurez pas")))
            .andExpect(jsonPath("$.formulaireUrl").doesNotExist());
    }

    @Test void verification_is_reserved_to_declarants() throws Exception {
        mvc.perform(get("/assujettis/me/verification").with(tutelle())).andExpect(status().isForbidden());
        mvc.perform(get("/assujettis/me/verification")).andExpect(status().isUnauthorized());
    }

    // ================= S13 =================

    @Test void deadlines_are_computed_and_overdue_ones_are_flagged() throws Exception {
        LocalDate today = LocalDate.now(ZoneId.of("Africa/Casablanca"));
        // en poste depuis 2 mois -> échéance dans environ 1 mois ; en poste depuis 2020 -> échéance dépassée
        mvc.perform(post("/assujettis").with(tutelle()).contentType(MediaType.APPLICATION_JSON)
            .content(formulaire("AB123456", "recent@test.ma", today.minusMonths(2)))).andExpect(status().isCreated());
        mvc.perform(post("/assujettis").with(tutelle()).contentType(MediaType.APPLICATION_JSON)
            .content(formulaire("CD654321", "ancien@test.ma", LocalDate.of(2020, 1, 10)))).andExpect(status().isCreated());

        mvc.perform(get("/assujettis/echeances?jours=60").with(tutelle()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].echeanceDeclaration").value("2020-04-10"))
            .andExpect(jsonPath("$[0].echeanceRenouvellement").value("2023-02-28"))
            .andExpect(jsonPath("$[0].statutEcheance").value("DEPASSEE"));

        mvc.perform(get("/assujettis/echeances?jours=5").with(tutelle()))
            .andExpect(jsonPath("$.length()").value(1));            // seule l'échéance dépassée
        mvc.perform(get("/assujettis/echeances?jours=9999").with(tutelle()))
            .andExpect(status().isBadRequest());                    // garde-fou @Max(365)
    }

    // ================= Sécurité JWT (vrai token signé comme auth-service) =================

    private static String token(String secret, String issuer, String role) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder().issuer(issuer).subject("tutelle@test.ma")
            .issuedAt(now).expiresAt(now.plus(5, ChronoUnit.MINUTES)).claim("role", role).build();
        var encoder = new NimbusJwtEncoder(new ImmutableSecret<>(
            new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256")));
        return encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
    }

    @Test void real_token_from_auth_service_is_accepted_and_role_is_read_from_the_claim() throws Exception {
        mvc.perform(get("/assujettis").header("Authorization", "Bearer " + token(SECRET, "dop-auth-service", "AUTORITE_TUTELLE")))
            .andExpect(status().isOk());
        mvc.perform(get("/assujettis").header("Authorization", "Bearer " + token(SECRET, "dop-auth-service", "DECLARANT")))
            .andExpect(status().isForbidden());
    }

    @Test void forged_or_foreign_tokens_are_rejected() throws Exception {
        String autreSecret = "un-autre-secret-0123456789-abcdefghijkl";
        mvc.perform(get("/assujettis").header("Authorization", "Bearer " + token(autreSecret, "dop-auth-service", "AUTORITE_TUTELLE")))
            .andExpect(status().isUnauthorized());
        mvc.perform(get("/assujettis").header("Authorization", "Bearer " + token(SECRET, "autre-emetteur", "AUTORITE_TUTELLE")))
            .andExpect(status().isUnauthorized());
    }

    @Test void health_is_public() throws Exception {
        mvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }
}
