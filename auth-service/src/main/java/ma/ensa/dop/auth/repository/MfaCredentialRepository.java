package ma.ensa.dop.auth.repository;

import jakarta.persistence.LockModeType;
import ma.ensa.dop.auth.entity.MfaCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MfaCredentialRepository extends JpaRepository<MfaCredential, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from MfaCredential m where m.userId = :userId")
    Optional<MfaCredential> findForUpdate(@Param("userId") Long userId);

    Optional<MfaCredential> findByUserId(Long userId);

    boolean existsByUserIdAndEnabledTrue(Long userId);
}
