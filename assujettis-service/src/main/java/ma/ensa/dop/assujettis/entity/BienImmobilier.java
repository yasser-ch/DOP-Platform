package ma.ensa.dop.assujettis.entity;

import jakarta.persistence.*;
import ma.ensa.dop.declaration.entity.ModeAcquisition;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;

/** S15 - Un bien immobilier déclaré. Une déclaration peut en contenir plusieurs. */
@Entity
@Table(name = "biens_immobiliers")
public class BienImmobilier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Le bien appartient à UNE déclaration. Si la déclaration est supprimée, ses biens le sont aussi (ON DELETE CASCADE). */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "declaration_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Declaration declaration;

    @Column(nullable = false, length = 300)
    private String adresse;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal superficie;               // en m²

    @Column(nullable = false, precision = 17, scale = 2)
    private BigDecimal valeur;                   // en dirhams (MAD)

    @Enumerated(EnumType.STRING)
    @Column(name = "mode_acquisition", nullable = false, length = 20)
    private ModeAcquisition modeAcquisition;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected BienImmobilier() {}                // exigé par JPA

    public BienImmobilier(Declaration declaration, String adresse, BigDecimal superficie, BigDecimal valeur,
                          ModeAcquisition modeAcquisition, Instant maintenant) {
        this.declaration = declaration;
        this.createdAt = maintenant;
        modifier(adresse, superficie, valeur, modeAcquisition, maintenant);
    }

    public void modifier(String adresse, BigDecimal superficie, BigDecimal valeur,
                         ModeAcquisition modeAcquisition, Instant maintenant) {
        this.adresse = adresse;
        this.superficie = superficie;
        this.valeur = valeur;
        this.modeAcquisition = modeAcquisition;
        this.updatedAt = maintenant;
    }

    public Long getId() { return id; }
    public String getAdresse() { return adresse; }
    public BigDecimal getSuperficie() { return superficie; }
    public BigDecimal getValeur() { return valeur; }
    public ModeAcquisition getModeAcquisition() { return modeAcquisition; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
