package ma.ensa.dop.auth.service;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import ma.ensa.dop.auth.Ressort;
import ma.ensa.dop.auth.Role;
import ma.ensa.dop.auth.exception.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class JwtService {
    private static final Duration MFA_TTL = Duration.ofMinutes(5);

    private final JwtEncoder encoder;
    private final JwtDecoder decoder;
    private final String issuer;
    private final long ttlMinutes;

    public JwtService(@Value("${dop.jwt.secret}") String secret, JwtDecoder decoder,
                      @Value("${dop.jwt.issuer}") String issuer,
                      @Value("${dop.jwt.ttl-minutes}") long ttlMinutes) {
        byte[] key = secret.getBytes(StandardCharsets.UTF_8);
        if (key.length < 32)
            throw new IllegalStateException("JWT_SECRET manquant ou trop court (32 octets min)");

        SecretKeySpec hmac = new SecretKeySpec(key, "HmacSHA256");
        this.encoder = new NimbusJwtEncoder(new ImmutableSecret<>(hmac));

        NimbusJwtDecoder d = NimbusJwtDecoder.withSecretKey(hmac)
            .macAlgorithm(MacAlgorithm.HS256)
            .build();
        d.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));
        this.decoder = d;
        this.issuer = issuer;
        this.ttlMinutes = ttlMinutes;
    }

    public String generate(String email, Role role, Ressort ressort) {
        Instant now = Instant.now();
        JwtClaimsSet.Builder b = JwtClaimsSet.builder()
            .issuer(issuer).subject(email)
            .issuedAt(now).expiresAt(now.plus(ttlMinutes, ChronoUnit.MINUTES))
            .claim("role", role.name());
        if (ressort != null) b.claim("ressort", ressort.name());
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, b.build())).getTokenValue();
    }

    public long ttlSeconds() { return ttlMinutes * 60; }

    public String generateMfaToken(String email) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder().issuer(issuer).subject(email)
            .issuedAt(now).expiresAt(now.plus(MFA_TTL)).claim("purpose", "mfa").build();   // pas de "role"
        return encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
    }
    public long mfaTtlSeconds() { return MFA_TTL.toSeconds(); }

    public String emailFromMfaToken(String token) {
        try {
            Jwt jwt = decoder.decode(token);
            if (!"mfa".equals(jwt.getClaimAsString("purpose"))) throw new InvalidCredentialsException();
            return jwt.getSubject();
        } catch (JwtException e) { throw new InvalidCredentialsException(); }
    }
}
