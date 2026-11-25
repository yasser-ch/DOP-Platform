package ma.ensa.dop.assujettis.exception;

import ma.ensa.dop.assujettis.dto.RowError;

import java.util.List;

/** Le fichier est lisible mais certaines lignes sont invalides : rien n'est importé (tout ou rien). */
public class CsvImportException extends RuntimeException {
    private final transient List<RowError> erreurs;

    public CsvImportException(List<RowError> erreurs) {
        super("Import CSV refusé : " + erreurs.size() + " erreur(s)");
        this.erreurs = erreurs;
    }
    public List<RowError> getErreurs() { return erreurs; }
}
