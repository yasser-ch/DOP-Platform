package ma.ensa.dop.assujettis.controller;

import jakarta.validation.Valid;
import ma.ensa.dop.declaration.dto.BienImmobilierRequest;
import ma.ensa.dop.declaration.dto.BienImmobilierResponse;
import ma.ensa.dop.declaration.service.BienImmobilierService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** S15 - Biens immobiliers d'une déclaration. Réservé aux agents déclarants. */
@RestController
@RequestMapping("/declarations/{declarationId}/biens-immobiliers")
@PreAuthorize("hasRole('DECLARANT')")
public class BienImmobilierController {
    private final BienImmobilierService service;

    public BienImmobilierController(BienImmobilierService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BienImmobilierResponse ajouter(@PathVariable Long declarationId,
                                          @Valid @RequestBody BienImmobilierRequest req, Authentication auth) {
        return service.ajouter(auth.getName(), declarationId, req);
    }

    @GetMapping
    public List<BienImmobilierResponse> lister(@PathVariable Long declarationId, Authentication auth) {
        return service.lister(auth.getName(), declarationId);
    }

    @PutMapping("/{bienId}")
    public BienImmobilierResponse modifier(@PathVariable Long declarationId, @PathVariable Long bienId,
                                           @Valid @RequestBody BienImmobilierRequest req, Authentication auth) {
        return service.modifier(auth.getName(), declarationId, bienId, req);
    }

    @DeleteMapping("/{bienId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long declarationId, @PathVariable Long bienId, Authentication auth) {
        service.supprimer(auth.getName(), declarationId, bienId);
    }
}
