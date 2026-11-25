package ma.ensa.dop.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * @param mfaToken jeton intermédiaire prouvant que l'étape mot de passe est passée
 * @param code     code TOTP (6 chiffres) prouvant la possession de l'application d'authentification
 */
public record MfaVerifyRequest(
    @NotBlank String mfaToken,
    @NotBlank @Pattern(regexp = "\\d{6}") String code
) {}
