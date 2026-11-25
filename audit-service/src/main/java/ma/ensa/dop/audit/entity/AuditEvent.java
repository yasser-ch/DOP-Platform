package ma.ensa.dop.audit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.Immutable;

import java.time.Instant;

/**
 * Une entrée du journal d'audit. Elle ne peut qu'être ajoutée : l'entité est @Immutable
 * (Hibernate n'émet jamais d'UPDATE), aucun setter n'existe et le dépôt n'expose ni
 * modification ni suppression. Chaque entrée porte l'empreinte de la précédente
 * (chaîne de hachages) : toute altération ultérieure casse la chaîne et se détecte.
 */
@Entity
@Immutable
@Table(name = "audit_event",
       uniqueConstraints = @UniqueConstraint(name = "uk_audit_event_seq", columnNames = "seq_no"))
public class AuditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Numéro d'ordre, 1, 2, 3... sans trou : un trou révèle une suppression. */
    @Column(name = "seq_no", nullable = false, updatable = false)
    private long seqNo;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;

    @Column(nullable = false, updatable = false, length = 255)
    private String actor;

    @Column(nullable = false, updatable = false, length = 100)
    private String action;

    @Column(nullable = false, updatable = false, length = 255)
    private String resource;

    @Column(updatable = false, length = 2000)
    private String details;

    @Column(name = "previous_hash", nullable = false, updatable = false, length = 64)
    private String previousHash;

    @Column(name = "entry_hash", nullable = false, updatable = false, length = 64)
    private String hash;

    protected AuditEvent() { }   // requis par JPA

    public AuditEvent(long seqNo, Instant occurredAt, String actor, String action, String resource,
                      String details, String previousHash, String hash) {
        this.seqNo = seqNo;
        this.occurredAt = occurredAt;
        this.actor = actor;
        this.action = action;
        this.resource = resource;
        this.details = details;
        this.previousHash = previousHash;
        this.hash = hash;
    }

    public Long getId() { return id; }
    public long getSeqNo() { return seqNo; }
    public Instant getOccurredAt() { return occurredAt; }
    public String getActor() { return actor; }
    public String getAction() { return action; }
    public String getResource() { return resource; }
    public String getDetails() { return details; }
    public String getPreviousHash() { return previousHash; }
    public String getHash() { return hash; }
}
