package ma.ensa.dop.assujettis;

import com.jayway.jsonpath.JsonPath;
import ma.ensa.dop.declaration.entity.Declaration;
import ma.ensa.dop.declaration.repository.BienImmobilierRepository;
import ma.ensa.dop.declaration.repository.DeclarationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** S15 - Tests d'intégration : biens immobiliers d'un brouillon. */
@SpringBootTest(properties = "dop.jwt.secret=test-only-secret-0123456789-abcdefghij")
@AutoConfigureMockMvc
class BienImmobilierFlowTests {
    private static final MediaType JSON = MediaType.APPLICATION_JSON;

    @Autowired MockMvc mvc;
    @Autowired DeclarationRepository declarations;
    @Autowired BienImmobilierRepository biens;

    @BeforeEach void clean() { declarations.deleteAll(); }      // supprime aussi les biens (ON DELETE CASCADE)

    private static RequestPostProcessor as(String role, String email) {
        return jwt().jwt(j -> j.subject(email)).authorities(new SimpleGrantedAuthority("ROLE_" + role));
    }
    private static RequestPostProcessor sara() { return as("DECLARANT", "sara@test.ma"); }
    private static RequestPostProcessor omar() { return as("DECLARANT", "omar@test.ma"); }

    private static String bien(String adresse, String superficie, String valeur, String mode) {
        return "{\"adresse\":\"%s\",\"superficie\":%s,\"valeur\":%s,\"modeAcquisition\":\"%s\"}"
            .formatted(adresse, superficie, valeur, mode);
    }
    private static String bienValide() { return bien("12 rue des Orangers, Marrakech", "120.5", "1500000", "ACHAT"); }

    private long creerDeclaration(RequestPostProcessor user, String type) throws Exception {
        String json = mvc.perform(post("/declarations").with(user).contentType(JSON).content("{\"type\":\"" + type + "\"}"))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(json, "$.id")).longValue();
    }
    private long ajouterBien(RequestPostProcessor user, long declarationId, String body) throws Exception {
        String json = mvc.perform(post(url(declarationId)).with(user).contentType(JSON).content(body))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(json, "$.id")).longValue();
    }
    private static String url(long declarationId) { return "/declarations/" + declarationId + "/biens-immobiliers"; }

    // ================= Ajout =================

    @Test void adds_a_property_with_the_four_fields() throws Exception {
        long decl = creerDeclaration(sara(), "INITIALE");
        mvc.perform(post(url(decl)).with(sara()).contentType(JSON).content(bienValide()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.adresse").value("12 rue des Orangers, Marrakech"))
            .andExpect(jsonPath("$.superficie").value(120.5))
            .andExpect(jsonPath("$.valeur").value(1500000.0))
            .andExpect(jsonPath("$.modeAcquisition").value("ACHAT"));
    }

    @Test void several_properties_can_be_added_and_are_listed_in_order() throws Exception {
        long decl = creerDeclaration(sara(), "INITIALE");
        ajouterBien(sara(), decl, bien("Adresse 1", "100", "1000000", "ACHAT"));
        ajouterBien(sara(), decl, bien("Adresse 2", "250.75", "2500000.50", "HERITAGE"));
        ajouterBien(sara(), decl, bien("Adresse 3", "80", "600000", "DONATION"));

        mvc.perform(get(url(decl)).with(sara()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[0].adresse").value("Adresse 1"))
            .andExpect(jsonPath("$[1].modeAcquisition").value("HERITAGE"))
            .andExpect(jsonPath("$[2].adresse").value("Adresse 3"));
    }

    @Test void all_acquisition_modes_are_accepted() throws Exception {
        long decl = creerDeclaration(sara(), "INITIALE");
        for (String mode : new String[]{"ACHAT", "HERITAGE", "DONATION", "ECHANGE", "AUTRE"})
            ajouterBien(sara(), decl, bien("Adresse " + mode, "100", "1000000", mode));
        mvc.perform(get(url(decl)).with(sara())).andExpect(jsonPath("$.length()").value(5));
    }

    @Test void properties_of_two_declarations_stay_separate() throws Exception {
        long initiale = creerDeclaration(sara(), "INITIALE");
        long cessation = creerDeclaration(sara(), "CESSATION");
        ajouterBien(sara(), initiale, bienValide());
        mvc.perform(get(url(cessation)).with(sara())).andExpect(jsonPath("$.length()").value(0));
    }

    // ================= Validation =================

    @Test void invalid_properties_are_rejected() throws Exception {
        long decl = creerDeclaration(sara(), "INITIALE");
        String[] invalides = {
            bien("", "100", "1000000", "ACHAT"),                       // adresse vide
            bien("Adresse", "0", "1000000", "ACHAT"),                  // superficie nulle
            bien("Adresse", "-5", "1000000", "ACHAT"),                 // superficie négative
            bien("Adresse", "100", "0", "ACHAT"),                      // valeur nulle
            bien("Adresse", "100", "-1", "ACHAT"),                     // valeur négative
            bien("Adresse", "100", "1000000", "VOL"),                  // mode inconnu
            bien("Adresse", "100.123", "1000000", "ACHAT"),            // trop de décimales
            "{\"adresse\":\"Adresse\",\"superficie\":100}",            // champs manquants
            "{}"
        };
        for (String corps : invalides)
            mvc.perform(post(url(decl)).with(sara()).contentType(JSON).content(corps))
                .andExpect(status().isBadRequest());
        mvc.perform(get(url(decl)).with(sara())).andExpect(jsonPath("$.length()").value(0));   // rien n'a été enregistré
    }

    @Test void a_text_in_a_numeric_field_is_rejected() throws Exception {
        long decl = creerDeclaration(sara(), "INITIALE");
        mvc.perform(post(url(decl)).with(sara()).contentType(JSON)
                .content("{\"adresse\":\"A\",\"superficie\":\"abc\",\"valeur\":1000,\"modeAcquisition\":\"ACHAT\"}"))
            .andExpect(status().isBadRequest());
    }

    // ================= Modification / suppression =================

    @Test void a_property_can_be_updated_then_deleted() throws Exception {
        long decl = creerDeclaration(sara(), "INITIALE");
        long id = ajouterBien(sara(), decl, bienValide());

        mvc.perform(put(url(decl) + "/" + id).with(sara()).contentType(JSON)
                .content(bien("Nouvelle adresse", "200", "3000000", "DONATION")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.adresse").value("Nouvelle adresse"))
            .andExpect(jsonPath("$.modeAcquisition").value("DONATION"));

        mvc.perform(get(url(decl)).with(sara()))
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].adresse").value("Nouvelle adresse"));

        mvc.perform(delete(url(decl) + "/" + id).with(sara())).andExpect(status().isNoContent());
        mvc.perform(get(url(decl)).with(sara())).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(delete(url(decl) + "/" + id).with(sara())).andExpect(status().isNotFound());
    }

    @Test void a_property_cannot_be_reached_through_another_declaration() throws Exception {
        long initiale = creerDeclaration(sara(), "INITIALE");
        long cessation = creerDeclaration(sara(), "CESSATION");
        long id = ajouterBien(sara(), initiale, bienValide());
        mvc.perform(put(url(cessation) + "/" + id).with(sara()).contentType(JSON).content(bienValide()))
            .andExpect(status().isNotFound());
        mvc.perform(delete(url(cessation) + "/" + id).with(sara())).andExpect(status().isNotFound());
    }

    // ================= Isolation entre agents =================

    @Test void another_agent_cannot_see_or_touch_my_properties() throws Exception {
        long decl = creerDeclaration(sara(), "INITIALE");
        long id = ajouterBien(sara(), decl, bienValide());

        mvc.perform(get(url(decl)).with(omar())).andExpect(status().isNotFound());
        mvc.perform(post(url(decl)).with(omar()).contentType(JSON).content(bienValide())).andExpect(status().isNotFound());
        mvc.perform(put(url(decl) + "/" + id).with(omar()).contentType(JSON).content(bienValide()))
            .andExpect(status().isNotFound());
        mvc.perform(delete(url(decl) + "/" + id).with(omar())).andExpect(status().isNotFound());
        mvc.perform(get(url(decl)).with(sara())).andExpect(jsonPath("$.length()").value(1));   // intact
    }

    // ================= Verrouillage après soumission =================

    @Test void a_submitted_declaration_is_read_only() throws Exception {
        long decl = creerDeclaration(sara(), "INITIALE");
        long id = ajouterBien(sara(), decl, bienValide());

        Declaration d = declarations.findById(decl).orElseThrow();
        d.soumettre(Instant.now());                                    // simule S19 (soumission)
        declarations.save(d);

        mvc.perform(post(url(decl)).with(sara()).contentType(JSON).content(bienValide())).andExpect(status().isConflict());
        mvc.perform(put(url(decl) + "/" + id).with(sara()).contentType(JSON).content(bienValide()))
            .andExpect(status().isConflict());
        mvc.perform(delete(url(decl) + "/" + id).with(sara())).andExpect(status().isConflict());
        mvc.perform(get(url(decl)).with(sara())).andExpect(status().isOk())          // la lecture reste permise
            .andExpect(jsonPath("$.length()").value(1));
    }

    // ================= Limite =================

    @Test void the_number_of_properties_per_declaration_is_capped() throws Exception {
        long decl = creerDeclaration(sara(), "INITIALE");
        for (int i = 0; i < 100; i++) ajouterBien(sara(), decl, bien("Adresse " + i, "100", "1000000", "ACHAT"));
        mvc.perform(post(url(decl)).with(sara()).contentType(JSON).content(bienValide()))
            .andExpect(status().isConflict());
    }

    // ================= Suppression de la déclaration =================

    @Test void deleting_a_declaration_removes_its_properties() throws Exception {
        long decl = creerDeclaration(sara(), "INITIALE");
        ajouterBien(sara(), decl, bienValide());
        ajouterBien(sara(), decl, bienValide());
        assertThat(biens.count()).isEqualTo(2);
        declarations.deleteAll();
        assertThat(biens.count()).isZero();
    }

    // ================= Sécurité =================

    @Test void only_declarants_can_use_the_endpoints() throws Exception {
        long decl = creerDeclaration(sara(), "INITIALE");
        mvc.perform(post(url(decl)).with(as("AUTORITE_TUTELLE", "t@test.ma")).contentType(JSON).content(bienValide()))
            .andExpect(status().isForbidden());
        mvc.perform(get(url(decl)).with(as("MAGISTRAT", "m@test.ma"))).andExpect(status().isForbidden());
        mvc.perform(get(url(decl))).andExpect(status().isUnauthorized());
    }
}
