package ma.ensa.dop.audit.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Tests HTTP + sécurité du journal d'audit (S40). */
@SpringBootTest(properties = "dop.jwt.secret=" + AuditControllerTest.SECRET)
@AutoConfigureMockMvc
class AuditControllerTest {
    static final String SECRET = "test-only-secret-0123456789-abcdefghij";
    private static final String CORPS =
            "{\"action\":\"DECLARATION_CONSULTEE\",\"resource\":\"declaration/42\",\"details\":\"lecture\"}";

    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;

    @BeforeEach
    void vider() { jdbc.update("delete from audit_event"); }

    private static RequestPostProcessor as(String role, String email) {
        return jwt().jwt(j -> j.subject(email)).authorities(new SimpleGrantedAuthority("ROLE_" + role));
    }
    private static RequestPostProcessor admin() { return as("ADMIN", "admin@test.ma"); }
    private static RequestPostProcessor agent() { return as("AGENT_DECLARANT", "agent@test.ma"); }

    @Test
    void sans_token_on_ne_peut_ni_ecrire_ni_lire() throws Exception {
        mvc.perform(post("/audit/events").contentType(MediaType.APPLICATION_JSON).content(CORPS))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/audit/events")).andExpect(status().isUnauthorized());
        mvc.perform(get("/audit/events/verify")).andExpect(status().isUnauthorized());
    }

    @Test
    void un_utilisateur_authentifie_journalise_en_son_propre_nom() throws Exception {
        // même si le corps tente d'imposer un autre acteur, l'acteur est celui du JWT
        String corpsMalicieux = "{\"actor\":\"admin@test.ma\",\"action\":\"DECLARATION_CONSULTEE\","
                + "\"resource\":\"declaration/42\"}";
        mvc.perform(post("/audit/events").with(agent()).contentType(MediaType.APPLICATION_JSON).content(corpsMalicieux))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sequence").value(1))
                .andExpect(jsonPath("$.actor").value("agent@test.ma"))
                .andExpect(jsonPath("$.hash").isString());
    }

    @Test
    void un_corps_invalide_est_refuse() throws Exception {
        mvc.perform(post("/audit/events").with(agent()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"action\":\"\",\"resource\":\"x\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void seul_l_administrateur_consulte_et_verifie() throws Exception {
        mvc.perform(post("/audit/events").with(agent()).contentType(MediaType.APPLICATION_JSON).content(CORPS))
                .andExpect(status().isCreated());

        mvc.perform(get("/audit/events").with(agent())).andExpect(status().isForbidden());
        mvc.perform(get("/audit/events/verify").with(agent())).andExpect(status().isForbidden());

        mvc.perform(get("/audit/events").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].actor").value("agent@test.ma"));
        mvc.perform(get("/audit/events/verify").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.checked").value(1));
    }

    @Test
    void la_verification_signale_une_alteration() throws Exception {
        mvc.perform(post("/audit/events").with(agent()).contentType(MediaType.APPLICATION_JSON).content(CORPS))
                .andExpect(status().isCreated());
        jdbc.update("update audit_event set action = ? where seq_no = ?", "AUTRE", 1);

        mvc.perform(get("/audit/events/verify").with(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.firstBrokenSequence").value(1));
    }

    @Test
    void aucune_route_de_modification_ou_de_suppression_n_existe() throws Exception {
        mvc.perform(delete("/audit/events").with(admin())).andExpect(status().isMethodNotAllowed());
        mvc.perform(put("/audit/events").with(admin()).contentType(MediaType.APPLICATION_JSON).content(CORPS))
                .andExpect(status().isMethodNotAllowed());
        mvc.perform(patch("/audit/events").with(admin()).contentType(MediaType.APPLICATION_JSON).content(CORPS))
                .andExpect(status().isMethodNotAllowed());
    }
}
