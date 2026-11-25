package ma.ensa.dop.audit.controller;

import jakarta.validation.Valid;
import ma.ensa.dop.audit.dto.AuditEventRequest;
import ma.ensa.dop.audit.dto.AuditEventResponse;
import ma.ensa.dop.audit.service.AuditService;
import ma.ensa.dop.audit.service.VerificationResult;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * S40 : journal d'audit en ajout seul. Il n'existe volontairement AUCUNE route PUT, PATCH ou DELETE.
 */
@RestController
@RequestMapping("/audit")
public class AuditController {

    private final AuditService service;

    public AuditController(AuditService service) { this.service = service; }

    /** Ajoute une entrée. Tout utilisateur authentifié peut journaliser, mais seulement en son propre nom. */
    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public AuditEventResponse enregistrer(@Valid @RequestBody AuditEventRequest req, Authentication auth) {
        return AuditEventResponse.from(service.append(auth.getName(), req.action(), req.resource(), req.details()));
    }

    /** Consultation réservée à l'administrateur. */
    @GetMapping("/events")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AuditEventResponse> lister(@RequestParam(required = false) String actor,
                                           @RequestParam(required = false) String action,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "50") int size) {
        int safePage = Math.max(0, page);
        int safeSize = Math.min(Math.max(1, size), 200);
        return service.list(actor, action, safePage, safeSize).stream().map(AuditEventResponse::from).toList();
    }

    /** Recalcule la chaîne de hachages et signale la première anomalie. */
    @GetMapping("/events/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public VerificationResult verifier() { return service.verify(); }
}
