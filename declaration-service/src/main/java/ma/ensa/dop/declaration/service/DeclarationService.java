package ma.ensa.dop.declaration.service;

import ma.ensa.dop.declaration.dto.DeclarationResponse;
import ma.ensa.dop.declaration.dto.UpdateBrouillonRequest;
import ma.ensa.dop.declaration.entity.Declaration;
import ma.ensa.dop.declaration.entity.StatutDeclaration;
import ma.ensa.dop.declaration.entity.TypeDeclaration;
import ma.ensa.dop.declaration.exception.BrouillonDejaExistantException;
import ma.ensa.dop.declaration.exception.ConflitVersionException;
import ma.ensa.dop.declaration.exception.DeclarationNotFoundException;
import ma.ensa.dop.declaration.exception.DeclarationVerrouilleeException;
import ma.ensa.dop.declaration.repository.DeclarationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;
import java.util.Locale;

/** S14 - Création, sauvegarde automatique et reprise des brouillons. */
@Service
public class DeclarationService {
    private final DeclarationRepository declarations;
    private final Clock clock;

    public DeclarationService(DeclarationRepository declarations, Clock clock) {
        this.declarations = declarations; this.clock = clock;
    }

    /** Crée un brouillon du type choisi (initiale, renouvellement ou cessation). */
    @Transactional
    public DeclarationResponse creer(String email, TypeDeclaration type) {
        String proprietaire = normaliser(email);
        declarations.findFirstByDeclarantEmailAndTypeAndStatut(proprietaire, type, StatutDeclaration.BROUILLON)
            .ifPresent(existant -> { throw new BrouillonDejaExistantException(existant.getId()); });
        Declaration d = declarations.save(new Declaration(proprietaire, type, clock.instant()));
        return DeclarationResponse.from(d);
    }

    /** Reprise : liste de MES déclarations, la plus récente en premier. */
    @Transactional(readOnly = true)
    public List<DeclarationResponse> lister(String email) {
        return declarations.findByDeclarantEmailOrderByUpdatedAtDesc(normaliser(email))
            .stream().map(DeclarationResponse::from).toList();
    }

    /** Reprise : recharge un brouillon pour continuer à le remplir. */
    @Transactional(readOnly = true)
    public DeclarationResponse obtenir(String email, Long id) {
        return DeclarationResponse.from(trouver(email, id));
    }

    /** Sauvegarde automatique : appelée régulièrement par le front, même si le brouillon est incomplet. */
    @Transactional
    public DeclarationResponse sauvegarder(String email, Long id, UpdateBrouillonRequest req) {
        Declaration d = trouver(email, id);
        if (!d.estBrouillon()) throw new DeclarationVerrouilleeException();
        if (req.version() != null && !req.version().equals(d.getVersion())) throw new ConflitVersionException();
        d.sauvegarder(req.anneeReference(), req.observations(), clock.instant());
        return DeclarationResponse.from(declarations.saveAndFlush(d));   // flush : la nouvelle version est renvoyée
    }

    private Declaration trouver(String email, Long id) {
        return declarations.findByIdAndDeclarantEmail(id, normaliser(email))
            .orElseThrow(DeclarationNotFoundException::new);
    }

    private static String normaliser(String email) { return email.trim().toLowerCase(Locale.ROOT); }
}
