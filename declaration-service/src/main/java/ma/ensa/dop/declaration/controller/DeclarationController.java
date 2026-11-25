package ma.ensa.dop.declaration.controller;

import jakarta.validation.Valid;
import ma.ensa.dop.declaration.dto.CreateDeclarationRequest;
import ma.ensa.dop.declaration.dto.DeclarationResponse;
import ma.ensa.dop.declaration.dto.UpdateBrouillonRequest;
import ma.ensa.dop.declaration.service.DeclarationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** S14 - Brouillons de déclaration. Réservé aux agents déclarants. */
@RestController
@RequestMapping("/declarations")
@PreAuthorize("hasRole('DECLARANT')")
public class DeclarationController {
    private final DeclarationService service;

    public DeclarationController(DeclarationService service) { this.service = service; }

    // auth.getName() = "sub" du JWT = e-mail du compte (voir JwtService dans auth-service)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeclarationResponse creer(@Valid @RequestBody CreateDeclarationRequest req, Authentication auth) {
        return service.creer(auth.getName(), req.type());
    }

    @GetMapping
    public List<DeclarationResponse> mesDeclarations(Authentication auth) {
        return service.lister(auth.getName());
    }

    @GetMapping("/{id}")
    public DeclarationResponse reprendre(@PathVariable Long id, Authentication auth) {
        return service.obtenir(auth.getName(), id);
    }

    @PatchMapping("/{id}/brouillon")
    public DeclarationResponse sauvegarderBrouillon(@PathVariable Long id,
                                                    @Valid @RequestBody UpdateBrouillonRequest req,
                                                    Authentication auth) {
        return service.sauvegarder(auth.getName(), id, req);
    }
}
