package ma.ensa.dop.assujettis.entity;

import ma.ensa.dop.declaration.entity.BienImmobilier;
import ma.ensa.dop.declaration.entity.ModeAcquisition;

import java.math.BigDecimal;
import java.time.Instant;

public record BienImmobilierResponse(
    Long id, String adresse, BigDecimal superficie, BigDecimal valeur,
    ModeAcquisition modeAcquisition, Instant createdAt, Instant updatedAt) {

    public static BienImmobilierResponse from(BienImmobilier b) {
        return new BienImmobilierResponse(b.getId(), b.getAdresse(), b.getSuperficie(), b.getValeur(),
            b.getModeAcquisition(), b.getCreatedAt(), b.getUpdatedAt());
    }
}
