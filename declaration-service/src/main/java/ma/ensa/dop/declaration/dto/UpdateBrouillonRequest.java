package ma.ensa.dop.declaration.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * Sauvegarde automatique : tous les champs sont facultatifs (un brouillon peut être incomplet).
 * Champ absent ou null = inchangé. "version" (facultatif) = celle que le client a reçue ;
 * si elle ne correspond plus, le brouillon a été modifié ailleurs (409).
 */
public record UpdateBrouillonRequest(
    @Min(2000) @Max(2100) Integer anneeReference,
    @Size(max = 2000) String observations,
    Long version) {}
