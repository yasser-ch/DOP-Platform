package ma.ensa.dop.assujettis.dto;

import ma.ensa.dop.assujettis.entity.Assujetti;
import ma.ensa.dop.assujettis.service.EcheanceCalculator;

import java.time.Instant;
import java.time.LocalDate;

public record AssujettiResponse(
    Long id, String cin, String nom, String prenom, String email, String fonction, String organisme,
    LocalDate dateEntreeFonction,
    LocalDate echeanceDeclaration, LocalDate echeanceRenouvellement,
    EcheanceCalculator.Statut statutEcheance, long joursRestants,
    Instant importedAt, String importedBy) {

    public static AssujettiResponse from(Assujetti a, LocalDate aujourdHui) {
        return new AssujettiResponse(
            a.getId(), a.getCin(), a.getNom(), a.getPrenom(), a.getEmail(), a.getFonction(), a.getOrganisme(),
            a.getDateEntreeFonction(),
            a.getEcheanceDeclaration(), a.getEcheanceRenouvellement(),
            EcheanceCalculator.statut(a.getEcheanceDeclaration(), aujourdHui),
            EcheanceCalculator.joursRestants(a.getEcheanceDeclaration(), aujourdHui),
            a.getImportedAt(), a.getImportedBy());
    }
}
