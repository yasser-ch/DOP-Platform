package ma.ensa.dop.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** Code TOTP saisi par l'utilisateur : exactement 6 chiffres. */
public record CodeRequest(
    @NotBlank @Pattern(regexp = "\\d{6}") String code
) {}
