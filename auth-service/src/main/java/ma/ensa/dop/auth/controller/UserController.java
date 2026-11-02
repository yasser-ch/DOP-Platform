package ma.ensa.dop.auth.controller;

import jakarta.validation.Valid;
import ma.ensa.dop.auth.dto.LoginRequest;
import ma.ensa.dop.auth.dto.RegisterRequest;
import ma.ensa.dop.auth.dto.UserResponse;
import ma.ensa.dop.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {
    private final AuthService service;
    public UserController(AuthService service) { this.service = service; }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest req) { return service.register(req); }

    @PostMapping("/login")
    public UserResponse login(@Valid @RequestBody LoginRequest req) { return service.login(req); }
}
