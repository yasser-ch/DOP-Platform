package ma.ensa.dop.auth.entity;

import jakarta.persistence.*;
import ma.ensa.dop.auth.util.Role;

// User.java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private Role role;

    protected User() {}  // exigé par JPA

    public User(String email, String passwordHash, Role role) {
        this.email = email; this.passwordHash = passwordHash; this.role = role;
    }
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }
}
