package ma.ensa.dop.assujettis.entity;

import jakarta.validation.constraints.*;
import ma.ensa.dop.declaration.entity.ModeAcquisition;

import java.math.BigDecimal;

/** Saisie d'un bien immobilier : les 4 champs sont obligatoires (critère d'acceptation de S15). */
public record BienImmobilierRequest(
    @NotBlank @Size(max = 300)
    String adresse,

    @NotNull @DecimalMin(value = "0.0", inclusive = false, message = "doit être supérieure à 0")
    @Digits(integer = 10, fraction = 2)
    BigDecimal superficie,                 // m²

    @NotNull @DecimalMin(value = "0.0", inclusive = false, message = "doit être supérieure à 0")
    @Digits(integer = 15, fraction = 2)
    BigDecimal valeur,                     // MAD

    @NotNull
    ModeAcquisition modeAcquisition) {}
