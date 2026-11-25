package ma.ensa.dop.assujettis.repository;

import ma.ensa.dop.assujettis.entity.Assujetti;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AssujettiRepository extends JpaRepository<Assujetti, Long> {
    Optional<Assujetti> findByCin(String cin);                       // upsert à l'import (S11)
    Optional<Assujetti> findFirstByEmailIgnoreCase(String email);    // vérification de l'agent (S12)
    List<Assujetti> findAllByOrderByNomAscPrenomAsc();               // liste visible (S11)
    List<Assujetti> findByEcheanceDeclarationLessThanEqualOrderByEcheanceDeclarationAsc(LocalDate limite); // S13
}
