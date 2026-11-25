package ma.ensa.dop.audit.service;

import ma.ensa.dop.audit.entity.AuditEvent;
import ma.ensa.dop.audit.repository.AuditEventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class AuditService {

    /** Empreinte "précédente" de la toute première entrée. */
    public static final String GENESIS = "0".repeat(64);

    private final AuditEventRepository repository;
    private final TransactionTemplate transaction;
    private final Clock clock;
    /** Les ajouts sont sérialisés : chaque entrée doit connaître l'empreinte de la précédente. */
    private final ReentrantLock appendLock = new ReentrantLock();

    public AuditService(AuditEventRepository repository, PlatformTransactionManager txManager, Clock clock) {
        this.repository = repository;
        this.transaction = new TransactionTemplate(txManager);
        this.clock = clock;
    }

    /** Ajoute une entrée à la fin du journal. L'acteur vient du JWT, jamais du corps de la requête. */
    public AuditEvent append(String actor, String action, String resource, String details) {
        appendLock.lock();
        try {
            return transaction.execute(status -> {
                Optional<AuditEvent> last = repository.findTopByOrderBySeqNoDesc();
                long seqNo = last.map(e -> e.getSeqNo() + 1).orElse(1L);
                String previousHash = last.map(AuditEvent::getHash).orElse(GENESIS);
                Instant at = clock.instant().truncatedTo(ChronoUnit.MILLIS);   // précision stable en base
                String hash = AuditHasher.hash(seqNo, at, actor, action, resource, details, previousHash);
                return repository.save(new AuditEvent(seqNo, at, actor, action, resource, details, previousHash, hash));
            });
        } finally {
            appendLock.unlock();
        }
    }

    /** Entrées les plus récentes d'abord, avec filtre optionnel par acteur ou par action. */
    @Transactional(readOnly = true)
    public List<AuditEvent> list(String actor, String action, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "seqNo"));
        Page<AuditEvent> result;
        if (actor != null && !actor.isBlank()) {
            result = repository.findByActor(actor, pageable);
        } else if (action != null && !action.isBlank()) {
            result = repository.findByAction(action, pageable);
        } else {
            result = repository.findAll(pageable);
        }
        return result.getContent();
    }

    /**
     * Recalcule toute la chaîne depuis la première entrée. Détecte : une entrée modifiée,
     * une entrée supprimée au milieu (trou de numérotation), ou une chaîne réécrite à moitié.
     */
    @Transactional(readOnly = true)
    public VerificationResult verify() {
        String previousHash = GENESIS;
        long expectedSeq = 1;
        long checked = 0;
        for (AuditEvent e : repository.findAllByOrderBySeqNoAsc()) {
            if (e.getSeqNo() != expectedSeq) {
                return VerificationResult.broken(checked, expectedSeq,
                        "Entrée manquante ou dupliquée : le numéro " + expectedSeq + " était attendu.", previousHash);
            }
            if (!previousHash.equals(e.getPreviousHash())) {
                return VerificationResult.broken(checked, e.getSeqNo(),
                        "Chaînage rompu : l'empreinte précédente ne correspond pas.", previousHash);
            }
            String expectedHash = AuditHasher.hash(e.getSeqNo(), e.getOccurredAt(), e.getActor(),
                    e.getAction(), e.getResource(), e.getDetails(), e.getPreviousHash());
            if (!expectedHash.equals(e.getHash())) {
                return VerificationResult.broken(checked, e.getSeqNo(),
                        "Contenu modifié : l'empreinte recalculée diffère de l'empreinte enregistrée.", previousHash);
            }
            previousHash = e.getHash();
            expectedSeq++;
            checked++;
        }
        return VerificationResult.ok(checked, previousHash);
    }
}
