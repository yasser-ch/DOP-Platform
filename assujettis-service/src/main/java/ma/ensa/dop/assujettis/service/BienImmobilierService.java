package ma.ensa.dop.assujettis.service;

import ma.ensa.dop.declaration.dto.BienImmobilierRequest;
import ma.ensa.dop.declaration.dto.BienImmobilierResponse;
import ma.ensa.dop.declaration.entity.BienImmobilier;
import ma.ensa.dop.declaration.entity.Declaration;
import ma.ensa.dop.declaration.exception.BienNotFoundException;
import ma.ensa.dop.declaration.exception.DeclarationNotFoundException;
import ma.ensa.dop.declaration.exception.DeclarationVerrouilleeException;
import ma.ensa.dop.declaration.exception.LimiteBiensAtteinteException;
import ma.ensa.dop.declaration.repository.BienImmobilierRepository;
import ma.ensa.dop.declaration.repository.DeclarationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;
import java.util.Locale;

/** S15 - Biens immobiliers d'une déclaration en brouillon. */
@Service
public class BienImmobilierService {
    /** Garde-fou contre les abus : 100 biens immobiliers maximum par déclaration. */
    static final int MAX_BIENS = 100;

    private final DeclarationRepository declarations;
    private final BienImmobilierRepository biens;
    private final Clock clock;

    public BienImmobilierService(DeclarationRepository declarations, BienImmobilierRepository biens, Clock clock) {
        this.declarations = declarations; this.biens = biens; this.clock = clock;
    }

    /** "Plusieurs biens ajoutables" : chaque appel ajoute un bien de plus. */
    @Transactional
    public BienImmobilierResponse ajouter(String email, Long declarationId, BienImmobilierRequest req) {
        Declaration d = declarationModifiable(email, declarationId);
        if (biens.countByDeclarationId(d.getId()) >= MAX_BIENS) throw new LimiteBiensAtteinteException();
        BienImmobilier b = biens.save(new BienImmobilier(
            d, req.adresse().trim(), req.superficie(), req.valeur(), req.modeAcquisition(), clock.instant()));
        return BienImmobilierResponse.from(b);
    }

    /** Lecture autorisée même après soumission (seule la modification est verrouillée). */
    @Transactional(readOnly = true)
    public List<BienImmobilierResponse> lister(String email, Long declarationId) {
        Declaration d = declarationDeLAgent(email, declarationId);
        return biens.findByDeclarationIdOrderByIdAsc(d.getId()).stream().map(BienImmobilierResponse::from).toList();
    }

    @Transactional
    public BienImmobilierResponse modifier(String email, Long declarationId, Long bienId, BienImmobilierRequest req) {
        Declaration d = declarationModifiable(email, declarationId);
        BienImmobilier b = biens.findByIdAndDeclarationId(bienId, d.getId()).orElseThrow(BienNotFoundException::new);
        b.modifier(req.adresse().trim(), req.superficie(), req.valeur(), req.modeAcquisition(), clock.instant());
        return BienImmobilierResponse.from(b);          // l'entité est suivie par JPA : sauvegardée à la fin de la transaction
    }

    @Transactional
    public void supprimer(String email, Long declarationId, Long bienId) {
        Declaration d = declarationModifiable(email, declarationId);
        BienImmobilier b = biens.findByIdAndDeclarationId(bienId, d.getId()).orElseThrow(BienNotFoundException::new);
        biens.delete(b);
    }

    // ---------- règles communes ----------

    /** La déclaration existe ET m'appartient (sinon 404, on ne révèle rien). */
    private Declaration declarationDeLAgent(String email, Long declarationId) {
        return declarations.findByIdAndDeclarantEmail(declarationId, email.trim().toLowerCase(Locale.ROOT))
            .orElseThrow(DeclarationNotFoundException::new);
    }

    /** ... ET c'est encore un brouillon (sinon 409 : déclaration soumise = verrouillée). */
    private Declaration declarationModifiable(String email, Long declarationId) {
        Declaration d = declarationDeLAgent(email, declarationId);
        if (!d.estBrouillon()) throw new DeclarationVerrouilleeException();
        return d;
    }
}
