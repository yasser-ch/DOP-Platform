package ma.ensa.dop.audit.service;

/**
 * Résultat du contrôle d'intégrité du journal.
 *
 * @param valid                   true si toute la chaîne est cohérente
 * @param checked                 nombre d'entrées vérifiées avant la première anomalie (ou au total)
 * @param firstBrokenSequence     numéro de la première entrée en anomalie (null si valide)
 * @param reason                  explication de l'anomalie (null si valide)
 * @param headHash                empreinte de la dernière entrée valide : à conserver ailleurs pour détecter
 *                                aussi une suppression des dernières entrées
 */
public record VerificationResult(boolean valid, long checked, Long firstBrokenSequence,
                                 String reason, String headHash) {

    public static VerificationResult ok(long checked, String headHash) {
        return new VerificationResult(true, checked, null, null, headHash);
    }

    public static VerificationResult broken(long checked, long sequence, String reason, String headHash) {
        return new VerificationResult(false, checked, sequence, reason, headHash);
    }
}
