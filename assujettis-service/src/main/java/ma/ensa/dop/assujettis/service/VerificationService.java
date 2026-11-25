package ma.ensa.dop.assujettis.service;

import ma.ensa.dop.assujettis.dto.VerificationResponse;
import ma.ensa.dop.assujettis.entity.Assujetti;
import ma.ensa.dop.assujettis.repository.AssujettiRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;

/** S12 - L'agent vérifie s'il figure sur la liste (rapprochement par e-mail du compte). */
@Service
public class VerificationService {
    private final AssujettiRepository assujettis;
    private final Clock clock;
    private final String formulaireUrl;

    public VerificationService(AssujettiRepository assujettis, Clock clock,
                               @Value("${dop.assujettis.formulaire-url}") String formulaireUrl) {
        this.assujettis = assujettis; this.clock = clock; this.formulaireUrl = formulaireUrl;
    }

    @Transactional(readOnly = true)
    public VerificationResponse verifier(String emailDuCompte) {
        return assujettis.findFirstByEmailIgnoreCase(emailDuCompte.trim())
            .map(this::present)
            .orElseGet(this::absent);
    }

    private VerificationResponse present(Assujetti a) {
        return new VerificationResponse(
            true, true,
            "Vous figurez sur la liste des assujettis : vous devez déposer votre déclaration avant le "
                + a.getEcheanceDeclaration() + ".",
            formulaireUrl,
            a.getEcheanceDeclaration(), a.getEcheanceRenouvellement(),
            EcheanceCalculator.statut(a.getEcheanceDeclaration(), LocalDate.now(clock)));
    }

    private VerificationResponse absent() {
        return new VerificationResponse(
            false, false,
            "Vous ne figurez pas sur la liste des assujettis : aucune déclaration n'est requise pour le moment. "
                + "Si vous pensez qu'il s'agit d'une erreur, contactez l'autorité de tutelle.",
            null, null, null, null);
    }
}
