package ma.ensa.dop.declaration.repository;

import ma.ensa.dop.declaration.entity.Declaration;
import ma.ensa.dop.declaration.entity.StatutDeclaration;
import ma.ensa.dop.declaration.entity.TypeDeclaration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeclarationRepository extends JpaRepository<Declaration, Long> {
    /** Reprise : toutes les déclarations de l'agent, la plus récemment sauvegardée d'abord. */
    List<Declaration> findByDeclarantEmailOrderByUpdatedAtDesc(String declarantEmail);

    /** Filtre par propriétaire : un agent ne voit JAMAIS la déclaration d'un autre. */
    Optional<Declaration> findByIdAndDeclarantEmail(Long id, String declarantEmail);

    /** Règle : un seul brouillon ouvert par type et par agent. */
    Optional<Declaration> findFirstByDeclarantEmailAndTypeAndStatut(
        String declarantEmail, TypeDeclaration type, StatutDeclaration statut);
}
