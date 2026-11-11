package ma.ensa.dop.assujettis.controller;

import ma.ensa.dop.assujettis.dto.VerificationResponse;
import ma.ensa.dop.assujettis.service.VerificationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** S12 - "Est-ce que je figure sur la liste ?" */
@RestController
@RequestMapping("/assujettis/me")
public class VerificationController {
    private final VerificationService service;

    public VerificationController(VerificationService service) { this.service = service; }

    @GetMapping("/verification")
    @PreAuthorize("hasRole('DECLARANT')")
    public VerificationResponse verifier(Authentication auth) {
        // auth.getName() = "sub" du JWT = e-mail du compte (voir JwtService dans auth-service)
        return service.verifier(auth.getName());
    }
}
