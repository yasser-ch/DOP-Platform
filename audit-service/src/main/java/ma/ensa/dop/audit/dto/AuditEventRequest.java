package ma.ensa.dop.audit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Corps d'un enregistrement. Il ne contient volontairement PAS d'acteur : celui-ci est lu dans
 * le JWT, pour qu'un utilisateur ne puisse pas écrire dans le journal au nom d'un autre.
 * Ne jamais mettre de données personnelles dans "details" (identifiants techniques uniquement).
 */
public record AuditEventRequest(
        @NotBlank @Size(max = 100) String action,
        @NotBlank @Size(max = 255) String resource,
        @Size(max = 2000) String details) {
}
