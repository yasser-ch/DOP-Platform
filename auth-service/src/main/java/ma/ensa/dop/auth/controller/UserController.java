package ma.ensa.dop.auth.controller;

import jakarta.validation.Valid;
import ma.ensa.dop.auth.dto.LoginRequest;
import ma.ensa.dop.auth.dto.LoginResponse;
import ma.ensa.dop.auth.dto.RegisterRequest;
import ma.ensa.dop.auth.dto.UserResponse;
import ma.ensa.dop.auth.service.AuthService;
import ma.ensa.dop.auth.service.JwtService;
import ma.ensa.dop.auth.service.MfaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UserController {
    private final AuthService service;
    private final JwtService jwt;
    private final MfaService mfa;

    public UserController(AuthService service, JwtService jwt, MfaService mfa) {
        this.service = service;
        this.jwt = jwt;
        this.mfa = mfa;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest req) {
        return service.register(req);
    }

    /** Sans MFA : jeton d'accès. Avec MFA : défi {@code mfaRequired} + jeton intermédiaire. */
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        UserResponse u = service.login(req);
        if (mfa.isEnabled(u.id()))
            return LoginResponse.mfaChallenge(jwt.generateMfaToken(u.email()), jwt.mfaTtlSeconds());
        return LoginResponse.token(jwt.generate(u.email(), u.role(), u.ressort()), jwt.ttlSeconds());
    }
}
