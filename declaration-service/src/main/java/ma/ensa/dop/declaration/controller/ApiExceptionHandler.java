package ma.ensa.dop.declaration.controller;

import ma.ensa.dop.declaration.exception.BrouillonDejaExistantException;
import ma.ensa.dop.declaration.exception.ConflitVersionException;
import ma.ensa.dop.declaration.exception.DeclarationNotFoundException;
import ma.ensa.dop.declaration.exception.DeclarationVerrouilleeException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(DeclarationNotFoundException.class)
    ResponseEntity<Map<String, String>> introuvable() {
        return ResponseEntity.status(404).body(Map.of("error", "Déclaration introuvable"));
    }

    @ExceptionHandler(BrouillonDejaExistantException.class)
    ResponseEntity<Map<String, Object>> brouillonExistant(BrouillonDejaExistantException e) {
        // On renvoie l'id du brouillon existant : le front peut proposer "reprendre ce brouillon".
        return ResponseEntity.status(409).body(Map.of(
            "error", "Un brouillon de ce type existe déjà : reprenez-le",
            "declarationId", e.getDeclarationId()));
    }

    @ExceptionHandler(DeclarationVerrouilleeException.class)
    ResponseEntity<Map<String, String>> verrouillee() {
        return ResponseEntity.status(409).body(Map.of("error", "La déclaration est soumise : modification impossible"));
    }

    @ExceptionHandler({ConflitVersionException.class, ObjectOptimisticLockingFailureException.class})
    ResponseEntity<Map<String, String>> conflit() {
        return ResponseEntity.status(409).body(Map.of(
            "error", "Le brouillon a été modifié ailleurs : rechargez-le avant de continuer"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> invalide(MethodArgumentNotValidException e) {
        // Seulement le nom du champ et le message, jamais la valeur saisie.
        List<Map<String, String>> details = e.getBindingResult().getFieldErrors().stream()
            .map(f -> Map.of("champ", f.getField(), "message", String.valueOf(f.getDefaultMessage()))).toList();
        return ResponseEntity.badRequest().body(Map.of("error", "Données invalides", "details", details));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<Map<String, String>> illisible() {
        return ResponseEntity.badRequest().body(Map.of(
            "error", "Corps de requête invalide (type inconnu ? valeurs acceptées : INITIALE, RENOUVELLEMENT, CESSATION)"));
    }
}
