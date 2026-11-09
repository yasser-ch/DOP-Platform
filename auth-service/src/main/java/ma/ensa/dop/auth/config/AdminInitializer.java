package ma.ensa.dop.auth.config;

import ma.ensa.dop.auth.Role;
import ma.ensa.dop.auth.entity.User;
import ma.ensa.dop.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements ApplicationRunner {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final String adminEmail;
    private final String adminPassword;

    public AdminInitializer(UserRepository users, PasswordEncoder encoder,
                            @Value("${dop.admin.email}") String adminEmail,
                            @Value("${dop.admin.password}") String adminPassword) {
        this.users = users; this.encoder = encoder;
        this.adminEmail = adminEmail.trim().toLowerCase();
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!users.existsByEmail(adminEmail)) {
            users.save(new User(adminEmail, encoder.encode(adminPassword), Role.ADMIN));
        }
    }
}
