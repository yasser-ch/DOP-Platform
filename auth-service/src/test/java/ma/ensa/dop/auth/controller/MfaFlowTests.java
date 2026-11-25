package ma.ensa.dop.auth.controller;

import com.jayway.jsonpath.JsonPath;
import ma.ensa.dop.auth.entity.MfaCredential;
import ma.ensa.dop.auth.repository.MfaCredentialRepository;
import ma.ensa.dop.auth.repository.UserRepository;
import ma.ensa.dop.auth.service.TotpService;
import org.apache.commons.codec.binary.Base32;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Parcours MFA complet via l'API HTTP, avec une horloge pilotable pour contrôler les pas TOTP. */
@SpringBootTest(properties = {"dop.admin.email=admin@test.ma", "dop.admin.password=Admin12345"})
@TestPropertySource(properties = "dop.jwt.secret=test-only-secret-0123456789-abcdefghij")
@AutoConfigureMockMvc
class MfaFlowTests {
    private static final String PWD = "Password123";

    /** Horloge modifiable, utilisée par MfaService à la place de l'horloge système. */
    static class MutableClock extends Clock {
        private volatile Instant now;
        MutableClock(Instant start) { this.now = start; }
        void advance(Duration d) { now = now.plus(d); }
        @Override public Instant instant() { return now; }
        @Override public ZoneId getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(ZoneId zone) { return this; }
    }

    @TestConfiguration
    static class TestClockConfig {
        @Bean @Primary
        MutableClock testClock() { return new MutableClock(Instant.parse("2026-01-01T00:00:10Z")); }
    }

    @Autowired MockMvc mvc;
    @Autowired MutableClock clock;
    @Autowired TotpService totp;
    @Autowired UserRepository users;
    @Autowired MfaCredentialRepository creds;

    private static final AtomicInteger SEQ = new AtomicInteger();

    // ---------- helpers ----------

    private String newUser() throws Exception {
        String email = "mfa" + SEQ.incrementAndGet() + "@test.ma";
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"" + email + "\",\"password\":\"" + PWD + "\"}")).andExpect(status().isCreated());
        return email;
    }

    private ResultActions login(String email) throws Exception {
        return mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"" + email + "\",\"password\":\"" + PWD + "\"}"));
    }

    private String accessToken(String email) throws Exception {
        return JsonPath.read(login(email).andExpect(status().isOk()).andReturn().getResponse().getContentAsString(),
            "$.accessToken");
    }

    private String mfaToken(String email) throws Exception {
        return JsonPath.read(login(email).andExpect(status().isOk()).andExpect(jsonPath("$.mfaRequired").value(true))
            .andExpect(jsonPath("$.accessToken").doesNotExist())
            .andReturn().getResponse().getContentAsString(), "$.mfaToken");
    }

    private static String codeJson(String code) { return "{\"code\":\"" + code + "\"}"; }

    private ResultActions authedPost(String url, String bearer, String json) throws Exception {
        var req = post(url).header("Authorization", "Bearer " + bearer);
        return mvc.perform(json == null ? req : req.contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private ResultActions verify(String mfaToken, String code) throws Exception {
        return mvc.perform(post("/auth/mfa/verify").contentType(MediaType.APPLICATION_JSON)
            .content("{\"mfaToken\":\"" + mfaToken + "\",\"code\":\"" + code + "\"}"));
    }

    private String codeNow(byte[] secret) { return totp.generate(secret, clock.instant()); }

    /** Un code à 6 chiffres qui n'est valide dans aucune des 3 fenêtres acceptées. */
    private String wrongCode(byte[] secret) {
        long cur = totp.stepOf(clock.instant());
        for (int i = 0; i < 1_000_000; i++) {
            String c = "%06d".formatted(i);
            if (!c.equals(totp.generateForStep(secret, cur - 1)) && !c.equals(totp.generateForStep(secret, cur))
                && !c.equals(totp.generateForStep(secret, cur + 1))) return c;
        }
        throw new IllegalStateException();
    }

    /** Enrôle l'utilisateur (setup + enable) puis avance d'un pas pour que le prochain code soit « neuf ». */
    private byte[] enroll(String email) throws Exception {
        String token = accessToken(email);
        String setup = authedPost("/auth/mfa/setup", token, null).andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        byte[] secret = new Base32().decode((String) JsonPath.read(setup, "$.secret"));
        authedPost("/auth/mfa/enable", token, codeJson(codeNow(secret))).andExpect(status().isNoContent());
        clock.advance(Duration.ofSeconds(30));
        return secret;
    }

    // ---------- enrôlement ----------

    @Test
    void login_without_mfa_returns_an_access_token() throws Exception {
        String email = newUser();
        login(email).andExpect(status().isOk()).andExpect(jsonPath("$.accessToken").isNotEmpty())
            .andExpect(jsonPath("$.tokenType").value("Bearer")).andExpect(jsonPath("$.mfaRequired").doesNotExist());
    }

    @Test
    void setup_returns_secret_and_qr_uri_and_stores_the_secret_encrypted() throws Exception {
        String email = newUser();
        String res = authedPost("/auth/mfa/setup", accessToken(email), null).andExpect(status().isOk())
            .andExpect(jsonPath("$.secret").isNotEmpty())
            .andExpect(jsonPath("$.otpauthUri").value(org.hamcrest.Matchers.startsWith("otpauth://totp/DOP:")))
            .andReturn().getResponse().getContentAsString();
        String secret = JsonPath.read(res, "$.secret");

        MfaCredential c = creds.findByUserId(users.findByEmail(email).orElseThrow().getId()).orElseThrow();
        assertThat(c.getSecretEnc()).doesNotContain(secret);
        assertThat(c.isEnabled()).isFalse();
    }

    @Test
    void setup_requires_authentication() throws Exception {
        mvc.perform(post("/auth/mfa/setup")).andExpect(status().isUnauthorized());
    }

    @Test
    void enable_with_a_wrong_code_is_refused_and_mfa_stays_off() throws Exception {
        String email = newUser();
        String token = accessToken(email);
        String setup = authedPost("/auth/mfa/setup", token, null).andReturn().getResponse().getContentAsString();
        byte[] secret = new Base32().decode((String) JsonPath.read(setup, "$.secret"));
        authedPost("/auth/mfa/enable", token, codeJson(wrongCode(secret))).andExpect(status().isUnauthorized());
        login(email).andExpect(status().isOk()).andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    void enable_without_setup_is_a_state_error() throws Exception {
        authedPost("/auth/mfa/enable", accessToken(newUser()), codeJson("123456")).andExpect(status().isConflict());
    }

    @Test
    void setup_is_refused_when_mfa_is_already_enabled() throws Exception {
        String email = newUser();
        byte[] secret = enroll(email);
        // le compte est protégé : on passe par le défi MFA pour obtenir un jeton d'accès
        String access = JsonPath.read(verify(mfaToken(email), codeNow(secret)).andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString(), "$.accessToken");
        authedPost("/auth/mfa/setup", access, null).andExpect(status().isConflict());
    }

    @Test
    void malformed_code_is_a_400() throws Exception {
        String token = accessToken(newUser());
        authedPost("/auth/mfa/enable", token, codeJson("12ab")).andExpect(status().isBadRequest());
        authedPost("/auth/mfa/enable", token, codeJson("1234567")).andExpect(status().isBadRequest());
        authedPost("/auth/mfa/enable", token, "{}").andExpect(status().isBadRequest());
    }

    // ---------- connexion avec second facteur ----------

    @Test
    void login_with_mfa_requires_the_second_factor() throws Exception {
        String email = newUser();
        byte[] secret = enroll(email);

        String mfaToken = mfaToken(email);
        String access = JsonPath.read(verify(mfaToken, codeNow(secret)).andExpect(status().isOk())
            .andExpect(jsonPath("$.tokenType").value("Bearer")).andReturn().getResponse().getContentAsString(),
            "$.accessToken");

        // le jeton obtenu est réellement accepté : 403 (authentifié mais pas admin), pas 401
        mvc.perform(get("/auth/users").header("Authorization", "Bearer " + access)).andExpect(status().isForbidden());
    }

    @Test
    void wrong_code_is_refused() throws Exception {
        String email = newUser();
        byte[] secret = enroll(email);
        verify(mfaToken(email), wrongCode(secret)).andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.accessToken").doesNotExist());
    }

    @Test
    void a_code_cannot_be_replayed() throws Exception {
        String email = newUser();
        byte[] secret = enroll(email);
        String code = codeNow(secret);
        verify(mfaToken(email), code).andExpect(status().isOk());
        verify(mfaToken(email), code).andExpect(status().isUnauthorized());      // même code, même pas
        clock.advance(Duration.ofSeconds(30));
        verify(mfaToken(email), codeNow(secret)).andExpect(status().isOk());      // code du pas suivant
    }

    @Test
    void one_step_of_clock_drift_is_tolerated_but_not_two() throws Exception {
        String email = newUser();
        byte[] secret = enroll(email);
        long cur = totp.stepOf(clock.instant());
        verify(mfaToken(email), totp.generateForStep(secret, cur + 2)).andExpect(status().isUnauthorized());
        verify(mfaToken(email), totp.generateForStep(secret, cur + 1)).andExpect(status().isOk());
    }

    @Test
    void account_is_locked_after_five_failures_then_recovers() throws Exception {
        String email = newUser();
        byte[] secret = enroll(email);
        String mfaToken = mfaToken(email);

        for (int i = 0; i < 5; i++)
            verify(mfaToken, wrongCode(secret)).andExpect(status().isUnauthorized());
        // même le bon code est refusé pendant le verrouillage
        verify(mfaToken, codeNow(secret)).andExpect(status().isTooManyRequests())
            .andExpect(header().exists("Retry-After"));

        clock.advance(Duration.ofMinutes(16));
        verify(mfaToken, codeNow(secret)).andExpect(status().isOk());
    }

    // ---------- contournements ----------

    @Test
    void mfa_token_is_not_an_access_token() throws Exception {
        String email = newUser();
        enroll(email);
        String mfaToken = mfaToken(email);
        mvc.perform(get("/auth/users").header("Authorization", "Bearer " + mfaToken)).andExpect(status().isUnauthorized());
        authedPost("/auth/mfa/setup", mfaToken, null).andExpect(status().isUnauthorized());
        authedPost("/auth/mfa/disable", mfaToken, codeJson("123456")).andExpect(status().isUnauthorized());
    }

    @Test
    void access_token_is_not_a_valid_mfa_token() throws Exception {
        String email = newUser();
        String access = accessToken(email);       // jeton d'accès émis avant l'activation du MFA
        byte[] secret = enroll(email);
        verify(access, codeNow(secret)).andExpect(status().isUnauthorized());
    }

    @Test
    void forged_or_garbage_mfa_token_is_refused() throws Exception {
        verify("n.importe.quoi", "123456").andExpect(status().isUnauthorized());
        String email = newUser();
        byte[] secret = enroll(email);
        String t = mfaToken(email);
        verify(t.substring(0, t.length() - 2) + "xx", codeNow(secret)).andExpect(status().isUnauthorized());
    }

    @Test
    void http_basic_with_password_alone_is_refused_once_mfa_is_enabled() throws Exception {
        String email = newUser();
        // avant MFA : Basic fonctionne (authentifié, mais non admin → 403)
        mvc.perform(get("/auth/users").with(httpBasic(email, PWD))).andExpect(status().isForbidden());
        enroll(email);
        // après MFA : le mot de passe seul ne suffit plus
        mvc.perform(get("/auth/users").with(httpBasic(email, PWD))).andExpect(status().isUnauthorized());
    }

    // ---------- désactivation ----------

    @Test
    void disable_needs_a_valid_code_then_login_has_no_challenge() throws Exception {
        String email = newUser();
        byte[] secret = enroll(email);
        String access = JsonPath.read(verify(mfaToken(email), codeNow(secret)).andReturn().getResponse()
            .getContentAsString(), "$.accessToken");

        authedPost("/auth/mfa/disable", access, codeJson(wrongCode(secret))).andExpect(status().isUnauthorized());
        mfaToken(email);   // toujours protégé

        clock.advance(Duration.ofSeconds(30));
        authedPost("/auth/mfa/disable", access, codeJson(codeNow(secret))).andExpect(status().isNoContent());
        login(email).andExpect(status().isOk()).andExpect(jsonPath("$.accessToken").isNotEmpty());
        authedPost("/auth/mfa/disable", access, codeJson("123456")).andExpect(status().isConflict());
    }

}
