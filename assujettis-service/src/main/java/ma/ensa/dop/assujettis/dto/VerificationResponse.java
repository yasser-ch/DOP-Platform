package ma.ensa.dop.assujettis.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import ma.ensa.dop.assujettis.service.EcheanceCalculator;

import java.time.LocalDate;

/** S12 - Réponse à "Est-ce que je dois déclarer ?". Les champs null sont omis du JSON. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record VerificationResponse(
    boolean assujetti,
    boolean accesFormulaire,
    String message,
    String formulaireUrl,
    LocalDate echeanceDeclaration,
    LocalDate echeanceRenouvellement,
    EcheanceCalculator.Statut statutEcheance) {}
