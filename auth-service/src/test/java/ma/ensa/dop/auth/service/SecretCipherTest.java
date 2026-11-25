package ma.ensa.dop.auth.service;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SecretCipherTest {
    private static final String KEY = Base64.getEncoder().encodeToString(new byte[32]);
    private final SecretCipher cipher = new SecretCipher(KEY);

    @Test
    void round_trip() {
        byte[] secret = new TotpService().newSecret();
        assertThat(cipher.decrypt(cipher.encrypt(secret))).isEqualTo(secret);
    }

    @Test
    void same_plaintext_gives_different_ciphertexts() {
        byte[] secret = new TotpService().newSecret();
        assertThat(cipher.encrypt(secret)).isNotEqualTo(cipher.encrypt(secret));
    }

    @Test
    void tampered_ciphertext_is_detected() {
        byte[] raw = Base64.getDecoder().decode(cipher.encrypt(new byte[20]));
        raw[raw.length - 1] ^= 1;
        String tampered = Base64.getEncoder().encodeToString(raw);
        assertThatThrownBy(() -> cipher.decrypt(tampered)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void wrong_key_cannot_decrypt() {
        byte[] otherKey = new byte[32];
        Arrays.fill(otherKey, (byte) 7);
        SecretCipher other = new SecretCipher(Base64.getEncoder().encodeToString(otherKey));
        String enc = cipher.encrypt(new byte[20]);
        assertThatThrownBy(() -> other.decrypt(enc)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void missing_or_invalid_key_fails_fast() {
        assertThatThrownBy(() -> new SecretCipher("")).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> new SecretCipher("pas-du-base64!!")).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> new SecretCipher(Base64.getEncoder().encodeToString(new byte[16])))
            .isInstanceOf(IllegalStateException.class);
    }
}
