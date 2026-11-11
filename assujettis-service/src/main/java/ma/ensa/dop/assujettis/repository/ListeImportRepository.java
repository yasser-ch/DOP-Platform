package ma.ensa.dop.assujettis.repository;

import ma.ensa.dop.assujettis.entity.ListeImport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListeImportRepository extends JpaRepository<ListeImport, Long> {
    List<ListeImport> findAllByOrderByImportedAtDesc();
}
