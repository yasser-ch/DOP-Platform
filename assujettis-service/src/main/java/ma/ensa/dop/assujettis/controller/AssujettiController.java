package ma.ensa.dop.assujettis.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import ma.ensa.dop.assujettis.dto.AssujettiRequest;
import ma.ensa.dop.assujettis.dto.AssujettiResponse;
import ma.ensa.dop.assujettis.dto.ImportResultResponse;
import ma.ensa.dop.assujettis.service.AssujettiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/** S11 (import + liste) et S13 (échéances). */
@RestController
@RequestMapping("/assujettis")
@Validated
public class AssujettiController {
    private final AssujettiService service;

    public AssujettiController(AssujettiService service) { this.service = service; }

    // ----- S11 : import CSV -----
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('AUTORITE_TUTELLE')")
    public ImportResultResponse importerCsv(@RequestParam("file") MultipartFile file, Authentication auth) {
        return service.importerCsv(file, auth.getName());
    }

    // ----- S11 : saisie via formulaire (un assujetti à la fois) -----
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('AUTORITE_TUTELLE')")
    public ImportResultResponse ajouter(@Valid @RequestBody AssujettiRequest req, Authentication auth) {
        return service.ajouterParFormulaire(req, auth.getName());
    }

    // ----- S11 : liste visible et horodatée -----
    @GetMapping
    @PreAuthorize("hasAnyRole('AUTORITE_TUTELLE','ADMIN')")
    public List<AssujettiResponse> lister() { return service.lister(); }

    @GetMapping("/imports")
    @PreAuthorize("hasAnyRole('AUTORITE_TUTELLE','ADMIN')")
    public List<ImportResultResponse> historique() { return service.historiqueImports(); }

    // ----- S13 : échéances à venir / dépassées (utile pour les notifications) -----
    @GetMapping("/echeances")
    @PreAuthorize("hasAnyRole('AUTORITE_TUTELLE','ADMIN')")
    public List<AssujettiResponse> echeances(@RequestParam(defaultValue = "30") @Min(0) @Max(365) int jours) {
        return service.echeancesAvant(jours);
    }
}
