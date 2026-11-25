package ma.ensa.dop.audit.dto;

import ma.ensa.dop.audit.entity.AuditEvent;

import java.time.Instant;

public record AuditEventResponse(long sequence, Instant occurredAt, String actor, String action,
                                 String resource, String details, String previousHash, String hash) {

    public static AuditEventResponse from(AuditEvent e) {
        return new AuditEventResponse(e.getSeqNo(), e.getOccurredAt(), e.getActor(), e.getAction(),
                e.getResource(), e.getDetails(), e.getPreviousHash(), e.getHash());
    }
}
