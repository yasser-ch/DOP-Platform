package ma.ensa.dop.auth.controller;

import jakarta.validation.Valid;
import ma.ensa.dop.auth.dto.CodeRequest;
import ma.ensa.dop.auth.dto.LoginResponse;
import ma.ensa.dop.auth.dto.MfaSetupResponse;
import ma.ensa.dop.auth.dto.MfaVerifyRequest;
import ma.ensa.dop.auth.dto.UserResponse;
import ma.ensa.dop.auth.service.JwtService;
import ma.ensa.dop.auth.service.MfaService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/mfa")
public class MfaController {
    private final MfaService service;
    private final JwtService jwt;

    public MfaController(MfaService service, JwtService jwt) {
        this.service = service;
        this.jwt = jwt;
    }

    /** Démarre l'enrôlement : renvoie le secret et l'URI du QR code. */
    @PostMapping("/setup")
    public MfaSetupResponse setup(Authentication a) {
        return service.setup(a.getName());
    }

    /** Confirme l'enrôlement avec un premier code valide. */
    @PostMapping("/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void enable(@Valid @RequestBody CodeRequest r, Authentication a) {
        service.enable(a.getName(), r.code());
    }

    @PostMapping("/disable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disable(@Valid @RequestBody CodeRequest r, Authentication a) {
        service.disable(a.getName(), r.code());
    }

    /** 2ᵉ étape de la connexion : échange mfaToken + code contre le jeton d'accès. */
    @PostMapping("/verify")
    public LoginResponse verify(@Valid @RequestBody MfaVerifyRequest r) {
        UserResponse u = service.verifyLogin(r.mfaToken(), r.code());
        return LoginResponse.token(jwt.generate(u.email(), u.role(), u.ressort()), jwt.ttlSeconds());
    }
}
