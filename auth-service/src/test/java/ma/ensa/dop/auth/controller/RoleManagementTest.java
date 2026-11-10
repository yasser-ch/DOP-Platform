package ma.ensa.dop.auth.controller;

import ma.ensa.dop.auth.Role;
import ma.ensa.dop.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
    properties = {"dop.admin.email=admin@test.ma", "dop.admin.password=Admin12345"})
@TestPropertySource(
    properties = "dop.jwt.secret=test-only-secret-0123456789-abcdefghij"

)
@AutoConfigureMockMvc
class RoleManagementTest {

    @Autowired MockMvc mvc;
    @Autowired UserRepository users;

    private Long registerUser(String email) throws Exception {
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"Password123\"}"))
            .andExpect(status().isCreated());
        return users.findByEmail(email).orElseThrow().getId();
    }

    private static String roleBody(String role) { return "{\"role\":\"" + role + "\"}"; }

    @Test
    void adminPeutAssignerUnRole() throws Exception {
        Long id = registerUser("juge1@test.ma");
        mvc.perform(put("/auth/users/" + id + "/role")
                .with(httpBasic("admin@test.ma", "Admin12345"))
                .contentType(MediaType.APPLICATION_JSON).content(roleBody("MAGISTRAT")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.role").value("MAGISTRAT"));
        assertEquals(Role.MAGISTRAT, users.findById(id).orElseThrow().getRole());
    }

    @Test
    void nonAdminRecoit403() throws Exception {
        Long id = registerUser("agent1@test.ma");
        mvc.perform(put("/auth/users/" + id + "/role")
                .with(httpBasic("agent1@test.ma", "Password123"))
                .contentType(MediaType.APPLICATION_JSON).content(roleBody("ADMIN")))
            .andExpect(status().isForbidden());
    }

    @Test
    void sansAuthentificationRecoit401() throws Exception {
        mvc.perform(put("/auth/users/1/role")
                .contentType(MediaType.APPLICATION_JSON).content(roleBody("ADMIN")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void roleInconnuRecoit400() throws Exception {
        Long id = registerUser("agent2@test.ma");
        mvc.perform(put("/auth/users/" + id + "/role")
                .with(httpBasic("admin@test.ma", "Admin12345"))
                .contentType(MediaType.APPLICATION_JSON).content(roleBody("ROI")))
            .andExpect(status().isBadRequest());
    }

    @Test
    void utilisateurInexistantRecoit404() throws Exception {
        mvc.perform(put("/auth/users/99999/role")
                .with(httpBasic("admin@test.ma", "Admin12345"))
                .contentType(MediaType.APPLICATION_JSON).content(roleBody("MAGISTRAT")))
            .andExpect(status().isNotFound());
    }

    @Test
    void adminNePeutPasModifierSonPropreRole() throws Exception {
        Long adminId = users.findByEmail("admin@test.ma").orElseThrow().getId();
        mvc.perform(put("/auth/users/" + adminId + "/role")
                .with(httpBasic("admin@test.ma", "Admin12345"))
                .contentType(MediaType.APPLICATION_JSON).content(roleBody("DECLARANT")))
            .andExpect(status().isConflict());
    }
}
