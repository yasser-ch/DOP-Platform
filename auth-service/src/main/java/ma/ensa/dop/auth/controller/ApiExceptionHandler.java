package ma.ensa.dop.auth.controller;

import ma.ensa.dop.auth.exception.EmailAlreadyUsedException;
import ma.ensa.dop.auth.exception.InvalidCredentialsException;
import ma.ensa.dop.auth.exception.SelfRoleChangeException;
import ma.ensa.dop.auth.exception.UserNotFoundException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(EmailAlreadyUsedException.class)
    ResponseEntity<Map<String, String>> conflict() {
        return ResponseEntity.status(409).body(Map.of("error", "Inscription impossible avec cet email"));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    ResponseEntity<Map<String, String>> unauthorized() {
        return ResponseEntity.status(401).body(Map.of("error", "Identifiants invalides"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, String>> invalid() {
        return ResponseEntity.badRequest().body(Map.of("error", "Données invalides"));
    }

    @ExceptionHandler(UserNotFoundException.class)
    ResponseEntity<Map<String, String>> notFound() {
        return ResponseEntity.status(404).body(Map.of("error", "Utilisateur introuvable"));
    }

    @ExceptionHandler(SelfRoleChangeException.class)
    ResponseEntity<Map<String, String>> selfChange() {
        return ResponseEntity.status(409).body(Map.of("error", "Vous ne pouvez pas modifier votre propre rôle"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<Map<String, String>> unreadable() {
        return ResponseEntity.badRequest().body(Map.of("error", "Corps de requête invalide (rôle inconnu ?)"));
    }
}
