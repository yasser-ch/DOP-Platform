package ma.ensa.dop.auth.controller;

import jakarta.validation.Valid;
import ma.ensa.dop.auth.dto.LoginRequest;
import ma.ensa.dop.auth.dto.LoginResponse;
import ma.ensa.dop.auth.dto.RegisterRequest;
import ma.ensa.dop.auth.dto.UserResponse;
import ma.ensa.dop.auth.service.AuthService;
import ma.ensa.dop.auth.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {
    private final AuthService service;
    private final JwtService jwt;
    public UserController(AuthService service, JwtService jwt) {
        this.service = service;
        this.jwt = jwt;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest req) { return service.register(req); }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        UserResponse u = service.login(req);
        return new LoginResponse(jwt.generate(u.email(), u.role()), "Bearer", jwt.ttlSeconds());
    }
}
