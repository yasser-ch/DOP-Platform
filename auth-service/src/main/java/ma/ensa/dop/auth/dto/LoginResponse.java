package ma.ensa.dop.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginResponse(String accessToken, String tokenType, Long expiresIn,
                            Boolean mfaRequired, String mfaToken) {
    public static LoginResponse token(String t, long ttl) { return new LoginResponse(t, "Bearer", ttl, null, null); }
    public static LoginResponse mfaChallenge(String t, long ttl) { return new LoginResponse(null, null, ttl, true, t); }
}
