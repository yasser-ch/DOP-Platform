package ma.ensa.dop.declaration.entity;

import jakarta.persistence.*;

import java.time.Instant;

/**
 * Une déclaration de patrimoine d'un agent. S14 : on crée un BROUILLON que l'agent complète progressivement.
 * Les sections (biens immobiliers S15, mobiliers S16, revenus S17...) s'ajouteront à cette entité.
 */
@Entity
@Table(name = "declarations")
public class Declaration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "declarant_email", nullable = false, length = 254)
    private String declarantEmail;              // propriétaire = "sub" du JWT

    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private TypeDeclaration type;

    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private StatutDeclaration statut;

    @Column(name = "annee_reference")
    private Integer anneeReference;             // facultatif tant que c'est un brouillon

    @Column(length = 2000)
    private String observations;                // facultatif

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;                  // date de la dernière sauvegarde automatique

    @Version
    private Long version;                       // verrou optimiste : détecte deux onglets qui s'écrasent

    protected Declaration() {}                  // exigé par JPA

    public Declaration(String declarantEmail, TypeDeclaration type, Instant maintenant) {
        this.declarantEmail = declarantEmail;
        this.type = type;
        this.statut = StatutDeclaration.BROUILLON;
        this.createdAt = maintenant;
        this.updatedAt = maintenant;
    }

    /** Sauvegarde partielle : un champ à null signifie "inchangé". */
    public void sauvegarder(Integer anneeReference, String observations, Instant maintenant) {
        if (anneeReference != null) this.anneeReference = anneeReference;
        if (observations != null) this.observations = observations;
        this.updatedAt = maintenant;
    }

    /** Préparation de S19 (soumission) : une fois soumise, la déclaration n'est plus modifiable. */
    public void soumettre(Instant maintenant) {
        this.statut = StatutDeclaration.SOUMISE;
        this.updatedAt = maintenant;
    }

    public boolean estBrouillon() { return statut == StatutDeclaration.BROUILLON; }

    public Long getId() { return id; }
    public String getDeclarantEmail() { return declarantEmail; }
    public TypeDeclaration getType() { return type; }
    public StatutDeclaration getStatut() { return statut; }
    public Integer getAnneeReference() { return anneeReference; }
    public String getObservations() { return observations; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }
}
