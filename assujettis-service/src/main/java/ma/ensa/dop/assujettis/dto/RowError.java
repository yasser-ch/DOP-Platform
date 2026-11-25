package ma.ensa.dop.assujettis.dto;

/** Erreur de validation sur une ligne du CSV (ligne = n° de la ligne de données : 1 = première ligne après l'en-tête). */
public record RowError(int ligne, String champ, String message) {}
