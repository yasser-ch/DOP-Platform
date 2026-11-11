package ma.ensa.dop.auth;

import ma.ensa.dop.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"dop.admin.email=admin@test.ma", "dop.admin.password=Admin12345"})
@AutoConfigureMockMvc
@TestPropertySource(properties = "dop.jwt.secret=test-only-secret-0123456789-abcdefghij")
class AuthFlowTests {
    @Autowired MockMvc mvc;
    @Autowired
    UserRepository users;

    @Value("${dop.admin.email}")
    String adminEmail;

    @BeforeEach
     void clean() {
        users.findAll().stream()
            .filter(user -> !user.getEmail().equals(adminEmail))
            .forEach(users::delete);
    }
    private ResultActions post(String url, String json) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.post(url)
            .contentType(MediaType.APPLICATION_JSON).content(json));
    }
    private static String body(String email, String pwd) {
        return "{\"email\":\"%s\",\"password\":\"%s\"}".formatted(email, pwd);
    }

    @Test void register_stores_a_bcrypt_hash_not_the_password() throws Exception {
        post("/auth/register", body("a@test.ma", "motdepasse123")).andExpect(status().isCreated());
        String hash = users.findByEmail("a@test.ma").orElseThrow().getPasswordHash();
        assertThat(hash).startsWith("$2").isNotEqualTo("motdepasse123");
    }

    @Test void duplicate_email_is_rejected() throws Exception {
        post("/auth/register", body("a@test.ma", "motdepasse123")).andExpect(status().isCreated());
        post("/auth/register", body("a@test.ma", "motdepasse123")).andExpect(status().isConflict());
    }

    @Test
    void short_password_is_rejected() throws Exception {
        post("/auth/register", body("a@test.ma", "court")).andExpect(status().isBadRequest());
    }

    @Test void login_succeeds_with_good_credentials() throws Exception {
        post("/auth/register", body("a@test.ma", "motdepasse123"));
        post("/auth/login", body("a@test.ma", "motdepasse123")).andExpect(status().isOk());
    }

    @Test void wrong_password_and_unknown_email_look_identical() throws Exception {
        post("/auth/register", body("a@test.ma", "motdepasse123"));
        String r1 = post("/auth/login", body("a@test.ma", "mauvaismdp1"))
            .andExpect(status().isUnauthorized()).andReturn().getResponse().getContentAsString();
        String r2 = post("/auth/login", body("inconnu@test.ma", "motdepasse123"))
            .andExpect(status().isUnauthorized()).andReturn().getResponse().getContentAsString();
        assertThat(r1).isEqualTo(r2);
    }

    @Test void health_is_public_and_the_rest_is_closed() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/actuator/health")).andExpect(status().isOk());
        mvc.perform(MockMvcRequestBuilders.get("/nimporte/quoi")).andExpect(status().isUnauthorized());
    }
}
