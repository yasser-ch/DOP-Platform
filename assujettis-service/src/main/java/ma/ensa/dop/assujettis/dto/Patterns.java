package ma.ensa.dop.assujettis.dto;

/** Expressions régulières partagées par les DTO. */
final class Patterns {
    private Patterns() {}

    /**
     * Anti "CSV injection" : un champ texte ne doit pas commencer par = + - @ tab ou retour chariot,
     * sinon Excel/LibreOffice pourrait l'interpréter comme une formule quand la liste est exportée.
     */
    static final String TEXTE_SUR = "^[^=+\\-@\\t\\r].*$";
}
