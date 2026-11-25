package ma.ensa.dop.auth.service;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import ma.ensa.dop.auth.Ressort;
import ma.ensa.dop.auth.Role;
import ma.ensa.dop.auth.exception.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Service
public class JwtService {
    private static final Duration MFA_TTL = Duration.ofMinutes(5);
    private static final String PURPOSE = "purpose";
    private static final String PURPOSE_MFA = "mfa";

    private final JwtEncoder encoder;
    private final SecretKeySpec hmac;
    private final String issuer;
    private final long ttlMinutes;

    public JwtService(@Value("${dop.jwt.secret}") String secret,
                      @Value("${dop.jwt.issuer}") String issuer,
                      @Value("${dop.jwt.ttl-minutes}") long ttlMinutes) {
        byte[] key = secret.getBytes(StandardCharsets.UTF_8);
        if (key.length < 32)
            throw new IllegalStateException("JWT_SECRET manquant ou trop court (32 octets min)");
        this.hmac = new SecretKeySpec(key, "HmacSHA256");
        this.encoder = new NimbusJwtEncoder(new ImmutableSecret<>(hmac));
        this.issuer = issuer;
        this.ttlMinutes = ttlMinutes;
    }

    /** Jeton d'accès (délivré uniquement après mot de passe, et code MFA si activé). */
    public String generate(String email, Role role, Ressort ressort) {
        Instant now = Instant.now();
        JwtClaimsSet.Builder b = JwtClaimsSet.builder()
            .issuer(issuer).subject(email)
            .issuedAt(now).expiresAt(now.plus(ttlMinutes, ChronoUnit.MINUTES))
            .claim("role", role.name());
        if (ressort != null) b.claim("ressort", ressort.name());
        return sign(b.build());
    }

    public long ttlSeconds() { return ttlMinutes * 60; }

    /** Jeton intermédiaire (5 min) émis après le mot de passe : ne donne accès à aucune ressource. */
    public String generateMfaToken(String email) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder().issuer(issuer).subject(email)
            .issuedAt(now).expiresAt(now.plus(MFA_TTL)).claim(PURPOSE, PURPOSE_MFA).build();   // pas de "role"
        return sign(claims);
    }

    public long mfaTtlSeconds() { return MFA_TTL.toSeconds(); }

    public String emailFromMfaToken(String token) {
        try {
            Jwt jwt = mfaDecoder().decode(token);
            if (!PURPOSE_MFA.equals(jwt.getClaimAsString(PURPOSE))) throw new InvalidCredentialsException();
            return jwt.getSubject();
        } catch (JwtException e) {
            throw new InvalidCredentialsException();
        }
    }

    /**
     * Décodeur des jetons d'accès (Bearer). Rejette explicitement le jeton intermédiaire MFA :
     * sans ce contrôle, un mfaToken (mot de passe seul) donnerait accès aux endpoints protégés
     * et contournerait le second facteur.
     */
    public JwtDecoder accessTokenDecoder() {
        NimbusJwtDecoder d = baseDecoder();
        OAuth2TokenValidator<Jwt> noPurpose = new JwtClaimValidator<Object>(PURPOSE, Objects::isNull);
        d.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
            JwtValidators.createDefaultWithIssuer(issuer), noPurpose));
        return d;
    }

    private JwtDecoder mfaDecoder() {
        NimbusJwtDecoder d = baseDecoder();
        d.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));
        return d;
    }

    private NimbusJwtDecoder baseDecoder() {
        return NimbusJwtDecoder.withSecretKey(hmac).macAlgorithm(MacAlgorithm.HS256).build();
    }

    private String sign(JwtClaimsSet claims) {
        return encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
            .getTokenValue();
    }
}
