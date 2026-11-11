package ma.ensa.dop.assujettis.controller;

import jakarta.validation.ConstraintViolationException;
import ma.ensa.dop.assujettis.exception.CsvImportException;
import ma.ensa.dop.assujettis.exception.InvalidFileException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(CsvImportException.class)
    ResponseEntity<Map<String, Object>> csvInvalide(CsvImportException e) {
        return ResponseEntity.unprocessableEntity()
            .body(Map.of("error", "Import refusé : des lignes sont invalides", "details", e.getErreurs()));
    }

    @ExceptionHandler(InvalidFileException.class)
    ResponseEntity<Map<String, String>> fichierInvalide(InvalidFileException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ResponseEntity<Map<String, String>> tropGros() {
        return ResponseEntity.status(413).body(Map.of("error", "Fichier trop volumineux (2 Mo maximum)"));
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    ResponseEntity<Map<String, String>> fichierAbsent() {
        return ResponseEntity.badRequest().body(Map.of("error", "Fichier manquant (champ multipart « file »)"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> formulaireInvalide(MethodArgumentNotValidException e) {
        // On renvoie seulement le nom du champ + le message, jamais la valeur saisie.
        List<Map<String, String>> details = e.getBindingResult().getFieldErrors().stream()
            .map(f -> Map.of("champ", f.getField(), "message", String.valueOf(f.getDefaultMessage()))).toList();
        return ResponseEntity.badRequest().body(Map.of("error", "Données invalides", "details", details));
    }

    @ExceptionHandler({ConstraintViolationException.class, HttpMessageNotReadableException.class})
    ResponseEntity<Map<String, String>> requeteInvalide() {
        return ResponseEntity.badRequest().body(Map.of("error", "Requête invalide (paramètre ou format de date incorrect ?)"));
    }
}
