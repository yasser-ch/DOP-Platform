package ma.ensa.dop.auth.service;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import ma.ensa.dop.auth.util.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.security.oauth2.jwt.JwtEncoder;

@Service
public class JwtService {
    private final JwtEncoder encoder;
    private final String issuer;
    private final long ttlMinutes;

    public JwtService(@Value("${dop.jwt.secret}") String secret,
                      @Value("${dop.jwt.issuer}") String issuer,
                      @Value("${dop.jwt.ttl-minutes}") long ttlMinutes) {
        byte[] key = secret.getBytes(StandardCharsets.UTF_8);
        if (key.length < 32)
            throw new IllegalStateException("JWT_SECRET manquant ou trop court (32 octets min)");
        this.encoder = new NimbusJwtEncoder(
            new ImmutableSecret<>(new SecretKeySpec(key, "HmacSHA256")));
        this.issuer = issuer;
        this.ttlMinutes = ttlMinutes;
    }

    public String generate(String email, Role role) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer(issuer).subject(email)
            .issuedAt(now).expiresAt(now.plus(ttlMinutes, ChronoUnit.MINUTES))
            .claim("role", role.name())
            .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public long ttlSeconds() { return ttlMinutes * 60; }
}
