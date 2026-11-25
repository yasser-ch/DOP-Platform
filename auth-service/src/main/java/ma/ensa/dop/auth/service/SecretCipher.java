package ma.ensa.dop.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

/** Chiffrement AES-256-GCM des secrets TOTP au repos (IV aléatoire de 12 octets préfixé au chiffré). */
@Component
public class SecretCipher {
    private static final int IV_LEN = 12;
    private static final int TAG_BITS = 128;
    private static final String INVALID_KEY = "MFA_ENCRYPTION_KEY manquante ou invalide (32 octets en Base64)";

    private final SecretKeySpec key;
    private final SecureRandom random = new SecureRandom();

    public SecretCipher(@Value("${dop.mfa.encryption-key}") String base64Key) {
        byte[] k;
        try {
            k = base64Key.isBlank() ? new byte[0] : Base64.getDecoder().decode(base64Key.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(INVALID_KEY, e);
        }
        if (k.length != 32) throw new IllegalStateException(INVALID_KEY);
        this.key = new SecretKeySpec(k, "AES");
    }

    public String encrypt(byte[] plain) {
        try {
            byte[] iv = new byte[IV_LEN];
            random.nextBytes(iv);
            Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
            c.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
            byte[] ct = c.doFinal(plain);
            return Base64.getEncoder().encodeToString(
                ByteBuffer.allocate(iv.length + ct.length).put(iv).put(ct).array());
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    public byte[] decrypt(String token) {
        try {
            byte[] all = Base64.getDecoder().decode(token);
            Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
            c.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, all, 0, IV_LEN));
            return c.doFinal(all, IV_LEN, all.length - IV_LEN);
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            throw new IllegalStateException("Secret MFA illisible", e);
        }
    }
}
