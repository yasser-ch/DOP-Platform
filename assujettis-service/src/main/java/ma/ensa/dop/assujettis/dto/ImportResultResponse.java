package ma.ensa.dop.assujettis.dto;

import ma.ensa.dop.assujettis.entity.ListeImport;

import java.time.Instant;

/** Résultat d'un import (et ligne de l'historique des imports). */
public record ImportResultResponse(
    Long importId, String source, Instant importedAt, String importedBy,
    int totalLignes, int crees, int misAJour) {

    public static ImportResultResponse from(ListeImport i) {
        return new ImportResultResponse(i.getId(), i.getSource(), i.getImportedAt(), i.getImportedBy(),
            i.getTotalLignes(), i.getCrees(), i.getMisAJour());
    }
}
