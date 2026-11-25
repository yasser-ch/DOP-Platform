package ma.ensa.dop.auth.service;

import org.apache.commons.codec.binary.Base32;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class TotpServiceTest {
    private final TotpService totp = new TotpService();
    /** Secret ASCII "12345678901234567890" des vecteurs de test de la RFC 6238 (annexe B, SHA-1). */
    private static final byte[] RFC_SECRET = "12345678901234567890".getBytes(StandardCharsets.US_ASCII);

    @Test
    void matches_rfc6238_test_vectors() {
        // La RFC donne 8 chiffres ; nos codes en ont 6 : ce sont les 6 derniers chiffres.
        assertThat(totp.generate(RFC_SECRET, Instant.ofEpochSecond(59L))).isEqualTo("287082");
        assertThat(totp.generate(RFC_SECRET, Instant.ofEpochSecond(1111111109L))).isEqualTo("081804");
        assertThat(totp.generate(RFC_SECRET, Instant.ofEpochSecond(1111111111L))).isEqualTo("050471");
        assertThat(totp.generate(RFC_SECRET, Instant.ofEpochSecond(1234567890L))).isEqualTo("005924");
        assertThat(totp.generate(RFC_SECRET, Instant.ofEpochSecond(2000000000L))).isEqualTo("279037");
        assertThat(totp.generate(RFC_SECRET, Instant.ofEpochSecond(20000000000L))).isEqualTo("353130");
    }

    @Test
    void code_is_always_six_digits_with_leading_zeros() {
        // 1234567890 donne 005924 : les zéros de tête doivent être conservés
        assertThat(totp.generate(RFC_SECRET, Instant.ofEpochSecond(1234567890L))).hasSize(6).startsWith("00");
    }

    @Test
    void secret_has_160_bits_and_is_random() {
        byte[] a = totp.newSecret(), b = totp.newSecret();
        assertThat(a).hasSize(20);
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void base32_has_no_padding_and_round_trips() {
        byte[] s = totp.newSecret();
        String b32 = totp.toBase32(s);
        assertThat(b32).hasSize(32).doesNotContain("=").matches("[A-Z2-7]+");
        assertThat(new Base32().decode(b32)).isEqualTo(s);
    }

    @Test
    void otpauth_uri_is_well_formed_and_escaped() {
        String uri = totp.otpauthUri("DOP", "a.b+c@test.ma", "ABCDEFGH");
        assertThat(uri).startsWith("otpauth://totp/DOP:a.b%2Bc%40test.ma?")
            .contains("secret=ABCDEFGH").contains("issuer=DOP")
            .contains("algorithm=SHA1").contains("digits=6").contains("period=30");
    }

    @Test
    void accepts_current_and_adjacent_steps_only() {
        Instant now = Instant.ofEpochSecond(1_700_000_010L);
        long cur = totp.stepOf(now);
        assertThat(totp.match(RFC_SECRET, totp.generateForStep(RFC_SECRET, cur), now, 0)).isEqualTo(cur);
        assertThat(totp.match(RFC_SECRET, totp.generateForStep(RFC_SECRET, cur - 1), now, 0)).isEqualTo(cur - 1);
        assertThat(totp.match(RFC_SECRET, totp.generateForStep(RFC_SECRET, cur + 1), now, 0)).isEqualTo(cur + 1);
        assertThat(totp.match(RFC_SECRET, totp.generateForStep(RFC_SECRET, cur - 2), now, 0)).isEqualTo(-1);
        assertThat(totp.match(RFC_SECRET, totp.generateForStep(RFC_SECRET, cur + 2), now, 0)).isEqualTo(-1);
    }

    @Test
    void refuses_a_step_that_was_already_used() {
        Instant now = Instant.ofEpochSecond(1_700_000_010L);
        long cur = totp.stepOf(now);
        String code = totp.generateForStep(RFC_SECRET, cur);
        assertThat(totp.match(RFC_SECRET, code, now, cur)).isEqualTo(-1);       // même pas : rejeu
        assertThat(totp.match(RFC_SECRET, code, now, cur - 1)).isEqualTo(cur);  // pas encore consommé
        // un code plus ancien que le dernier pas consommé est refusé aussi
        assertThat(totp.match(RFC_SECRET, totp.generateForStep(RFC_SECRET, cur - 1), now, cur)).isEqualTo(-1);
    }

    @Test
    void rejects_garbage() {
        Instant now = Instant.ofEpochSecond(1_700_000_010L);
        assertThat(totp.match(RFC_SECRET, null, now, 0)).isEqualTo(-1);
        assertThat(totp.match(RFC_SECRET, "", now, 0)).isEqualTo(-1);
        assertThat(totp.match(RFC_SECRET, "abcdef", now, 0)).isEqualTo(-1);
        assertThat(totp.match(RFC_SECRET, "1234567", now, 0)).isEqualTo(-1);
    }
}
