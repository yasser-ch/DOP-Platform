package ma.ensa.dop.assujettis.repository;

import ma.ensa.dop.declaration.entity.BienImmobilier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BienImmobilierRepository extends JpaRepository<BienImmobilier, Long> {
    List<BienImmobilier> findByDeclarationIdOrderByIdAsc(Long declarationId);

    /** Un bien n'est accessible QUE via sa propre déclaration (on ne peut pas "emprunter" l'id d'un autre). */
    Optional<BienImmobilier> findByIdAndDeclarationId(Long id, Long declarationId);

    long countByDeclarationId(Long declarationId);
}
