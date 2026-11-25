package ma.ensa.dop.declaration.dto;

import ma.ensa.dop.declaration.entity.Declaration;
import ma.ensa.dop.declaration.entity.StatutDeclaration;
import ma.ensa.dop.declaration.entity.TypeDeclaration;

import java.time.Instant;

public record DeclarationResponse(
    Long id, TypeDeclaration type, StatutDeclaration statut,
    Integer anneeReference, String observations,
    Instant createdAt, Instant derniereSauvegarde, Long version) {

    public static DeclarationResponse from(Declaration d) {
        return new DeclarationResponse(d.getId(), d.getType(), d.getStatut(),
            d.getAnneeReference(), d.getObservations(),
            d.getCreatedAt(), d.getUpdatedAt(), d.getVersion());
    }
}
