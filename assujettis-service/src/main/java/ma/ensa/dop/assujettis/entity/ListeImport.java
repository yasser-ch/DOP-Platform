package ma.ensa.dop.assujettis.entity;

import jakarta.persistence.*;

import java.time.Instant;

/** Historique : une ligne par import (fichier CSV ou saisie via formulaire), avec horodatage. */
@Entity
@Table(name = "liste_imports")
public class ListeImport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String source;          // "CSV:nom-du-fichier.csv" ou "FORMULAIRE"

    @Column(name = "imported_at", nullable = false)
    private Instant importedAt;     // l'horodatage exigé par S11

    @Column(name = "imported_by", nullable = false)
    private String importedBy;      // e-mail (subject du JWT) de l'autorité de tutelle

    @Column(name = "total_lignes", nullable = false) private int totalLignes;
    @Column(nullable = false) private int crees;
    @Column(name = "mis_a_jour", nullable = false) private int misAJour;

    protected ListeImport() {}

    public ListeImport(String source, Instant importedAt, String importedBy, int totalLignes) {
        this.source = source; this.importedAt = importedAt; this.importedBy = importedBy;
        this.totalLignes = totalLignes;
    }

    /** Renseigne le bilan une fois l'import terminé. */
    public void bilan(int crees, int misAJour) { this.crees = crees; this.misAJour = misAJour; }

    public Long getId() { return id; }
    public String getSource() { return source; }
    public Instant getImportedAt() { return importedAt; }
    public String getImportedBy() { return importedBy; }
    public int getTotalLignes() { return totalLignes; }
    public int getCrees() { return crees; }
    public int getMisAJour() { return misAJour; }
}
