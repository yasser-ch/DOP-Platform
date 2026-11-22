package ma.ensa.dop.assujettis.controller;

import ma.ensa.dop.assujettis.exception.CsvImportException;
import ma.ensa.dop.assujettis.exception.InvalidFileException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.List;
import java.util.Map;

/** Traduit les erreurs métier de assujettis-service en réponses HTTP (jamais de valeur saisie dans les messages). */
@RestControllerAdvice
public class ApiExceptionHandler {

    /** CSV lisible mais lignes invalides : 422, rien n'est importé. */
    @ExceptionHandler(CsvImportException.class)
    ResponseEntity<Map<String, Object>> csvInvalide(CsvImportException e) {
        List<Map<String, Object>> details = e.getErreurs().stream()
            .map(r -> Map.<String, Object>of("ligne", r.ligne(), "champ", r.champ(), "message", r.message()))
            .toList();
        return ResponseEntity.unprocessableEntity().body(Map.of("error", "Import CSV refusé", "details", details));
    }

    /** Fichier inutilisable (vide, mauvaise extension, colonnes manquantes...). */
    @ExceptionHandler(InvalidFileException.class)
    ResponseEntity<Map<String, String>> fichierInvalide(InvalidFileException e) {
        return ResponseEntity.badRequest().body(Map.of("error", String.valueOf(e.getMessage())));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ResponseEntity<Map<String, String>> tropGros() {
        return ResponseEntity.status(413).body(Map.of("error", "Fichier trop volumineux (2 Mo maximum)"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> invalide(MethodArgumentNotValidException e) {
        List<Map<String, String>> details = e.getBindingResult().getFieldErrors().stream()
            .map(f -> Map.of("champ", f.getField(), "message", String.valueOf(f.getDefaultMessage()))).toList();
        return ResponseEntity.badRequest().body(Map.of("error", "Données invalides", "details", details));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<Map<String, String>> illisible() {
        return ResponseEntity.badRequest().body(Map.of("error", "Corps de requête invalide"));
    }
}
