package ma.ensa.dop.assujettis.entity;

import jakarta.persistence.*;
import ma.ensa.dop.assujettis.service.EcheanceCalculator;

import java.time.Instant;
import java.time.LocalDate;

/** Un agent public soumis à l'obligation de déclaration (une ligne de la liste nominative). */
@Entity
@Table(name = "assujettis")
public class Assujetti {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String cin;                       // identifiant métier : un agent = un CIN

    @Column(nullable = false, length = 100) private String nom;
    @Column(nullable = false, length = 100) private String prenom;
    @Column(nullable = false, length = 254) private String email;      // sert au rapprochement avec le compte (S12)
    @Column(nullable = false, length = 150) private String fonction;
    @Column(nullable = false, length = 150) private String organisme;

    @Column(name = "date_entree_fonction", nullable = false)
    private LocalDate dateEntreeFonction;

    // --- S13 : échéances calculées automatiquement ---
    @Column(name = "echeance_declaration", nullable = false)
    private LocalDate echeanceDeclaration;     // date d'entrée + 3 mois

    @Column(name = "echeance_renouvellement", nullable = false)
    private LocalDate echeanceRenouvellement;  // février, +3 ans

    // --- S11 : traçabilité / horodatage ---
    @Column(name = "imported_at", nullable = false)
    private Instant importedAt;

    @Column(name = "imported_by", nullable = false)
    private String importedBy;

    @Column(name = "import_id", nullable = false)
    private Long importId;

    protected Assujetti() {}  // exigé par JPA

    public Assujetti(String cin) { this.cin = cin; }

    /** Remplit / met à jour la fiche et recalcule les échéances (S13) à chaque modification. */
    public void remplir(String nom, String prenom, String email, String fonction, String organisme,
                        LocalDate dateEntreeFonction, Instant importedAt, String importedBy, Long importId) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.fonction = fonction;
        this.organisme = organisme;
        this.dateEntreeFonction = dateEntreeFonction;
        this.echeanceDeclaration = EcheanceCalculator.echeanceDeclaration(dateEntreeFonction);
        this.echeanceRenouvellement = EcheanceCalculator.echeanceRenouvellement(dateEntreeFonction);
        this.importedAt = importedAt;
        this.importedBy = importedBy;
        this.importId = importId;
    }

    public Long getId() { return id; }
    public String getCin() { return cin; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getEmail() { return email; }
    public String getFonction() { return fonction; }
    public String getOrganisme() { return organisme; }
    public LocalDate getDateEntreeFonction() { return dateEntreeFonction; }
    public LocalDate getEcheanceDeclaration() { return echeanceDeclaration; }
    public LocalDate getEcheanceRenouvellement() { return echeanceRenouvellement; }
    public Instant getImportedAt() { return importedAt; }
    public String getImportedBy() { return importedBy; }
    public Long getImportId() { return importId; }
}
