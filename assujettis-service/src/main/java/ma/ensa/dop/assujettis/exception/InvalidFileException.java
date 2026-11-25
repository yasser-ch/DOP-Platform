package ma.ensa.dop.assujettis.exception;

/** Fichier inutilisable : vide, mauvaise extension, colonnes manquantes, trop de lignes... */
public class InvalidFileException extends RuntimeException {
    public InvalidFileException(String message) { super(message); }
}
