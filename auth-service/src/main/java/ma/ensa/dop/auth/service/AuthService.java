package ma.ensa.dop.auth.service;

import org.springframework.transaction.annotation.Transactional;
import ma.ensa.dop.auth.util.Role;
import ma.ensa.dop.auth.dto.LoginRequest;
import ma.ensa.dop.auth.dto.RegisterRequest;
import ma.ensa.dop.auth.dto.UserResponse;
import ma.ensa.dop.auth.entity.User;
import ma.ensa.dop.auth.exception.EmailAlreadyUsedException;
import ma.ensa.dop.auth.exception.InvalidCredentialsException;
import ma.ensa.dop.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;

    public AuthService(UserRepository users, PasswordEncoder encoder) {
        this.users = users; this.encoder = encoder;
    }

    @Transactional
    public UserResponse register(RegisterRequest req) {
        String email = req.email().trim().toLowerCase();
        if (users.existsByEmail(email)) throw new EmailAlreadyUsedException();
        User saved = users.save(new User(email, encoder.encode(req.password()), Role.DECLARANT));
        return new UserResponse(saved.getId(), saved.getEmail(), saved.getRole());
    }

    @Transactional(readOnly = true)
    public UserResponse login(LoginRequest req) {
        User user = users.findByEmail(req.email().trim().toLowerCase())
            .filter(u -> encoder.matches(req.password(), u.getPasswordHash()))
            .orElseThrow(InvalidCredentialsException::new);
        return new UserResponse(user.getId(), user.getEmail(), user.getRole());
    }
}
