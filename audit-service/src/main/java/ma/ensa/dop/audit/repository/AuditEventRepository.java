package ma.ensa.dop.audit.repository;

import ma.ensa.dop.audit.entity.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Dépôt volontairement limité : il étend Repository (et non JpaRepository) pour n'exposer
 * que l'ajout et la lecture. Ni delete, ni update : l'immuabilité est dans le type même.
 */
public interface AuditEventRepository extends Repository<AuditEvent, Long> {

    <S extends AuditEvent> S save(S event);

    long count();

    Optional<AuditEvent> findTopByOrderBySeqNoDesc();

    List<AuditEvent> findAllByOrderBySeqNoAsc();

    Page<AuditEvent> findAll(Pageable pageable);

    Page<AuditEvent> findByActor(String actor, Pageable pageable);

    Page<AuditEvent> findByAction(String action, Pageable pageable);
}
