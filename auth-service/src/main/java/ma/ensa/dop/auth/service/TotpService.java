package ma.ensa.dop.auth.service;

import org.apache.commons.codec.binary.Base32;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * TOTP conforme à la RFC 6238 : HMAC-SHA1, pas de 30 s, 6 chiffres
 * (paramètres compatibles avec Google Authenticator, Microsoft Authenticator, Authy…).
 */
@Component
public class TotpService {
    private static final long STEP_SECONDS = 30;
    private static final int DIGITS = 6;
    private static final int MODULO = 1_000_000;
    private static final String HMAC = "HmacSHA1";

    private final SecureRandom random = new SecureRandom();

    /** Secret brut de 160 bits (RFC 4226), partagé avec l'application d'authentification. */
    public byte[] newSecret() {
        byte[] s = new byte[20];
        random.nextBytes(s);
        return s;
    }

    public String toBase32(byte[] secret) {
        return new Base32().encodeToString(secret).replace("=", "");
    }

    /** URI {@code otpauth://} utilisée pour générer le QR code d'enrôlement. */
    public String otpauthUri(String issuer, String email, String base32Secret) {
        String label = enc(issuer) + ":" + enc(email);
        return "otpauth://totp/" + label + "?secret=" + base32Secret + "&issuer=" + enc(issuer)
            + "&algorithm=SHA1&digits=" + DIGITS + "&period=" + STEP_SECONDS;
    }

    private static String enc(String s) {
        return URLEncoder.encode(s, UTF_8).replace("+", "%20");
    }

    /** Numéro du pas de 30 s qui contient l'instant donné. */
    public long stepOf(Instant at) {
        return Math.floorDiv(at.getEpochSecond(), STEP_SECONDS);
    }

    /** Code à 6 chiffres pour un instant précis. */
    public String generate(byte[] secret, Instant at) {
        return generateForStep(secret, stepOf(at));
    }

    /** Code à 6 chiffres pour un numéro de pas donné (HOTP, RFC 4226 §5.3). */
    public String generateForStep(byte[] secret, long step) {
        try {
            Mac mac = Mac.getInstance(HMAC);
            mac.init(new SecretKeySpec(secret, HMAC));
            byte[] hash = mac.doFinal(ByteBuffer.allocate(Long.BYTES).putLong(step).array());
            int offset = hash[hash.length - 1] & 0x0F;
            int binary = ((hash[offset] & 0x7F) << 24)
                | ((hash[offset + 1] & 0xFF) << 16)
                | ((hash[offset + 2] & 0xFF) << 8)
                | (hash[offset + 3] & 0xFF);
            return String.format("%0" + DIGITS + "d", binary % MODULO);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Retourne le pas de temps qui correspond au code, ou -1.
     * Tolère ±1 pas (dérive d'horloge) et refuse tout pas déjà consommé (anti-rejeu).
     */
    public long match(byte[] secret, String code, Instant now, long lastUsedStep) {
        if (code == null) return -1;
        byte[] given = code.getBytes(UTF_8);
        long current = stepOf(now);
        long found = -1;
        for (long s = current - 1; s <= current + 1; s++) {
            if (s <= lastUsedStep) continue;
            byte[] expected = generateForStep(secret, s).getBytes(UTF_8);
            // comparaison en temps constant ; on parcourt toutes les fenêtres sans sortie anticipée
            if (MessageDigest.isEqual(expected, given) && found < 0) found = s;
        }
        return found;
    }
}
