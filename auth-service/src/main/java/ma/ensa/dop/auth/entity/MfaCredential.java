package ma.ensa.dop.auth.entity;

import jakarta.persistence.*;

import java.time.Duration;
import java.time.Instant;

@Entity
@Table(name = "mfa_credentials")
public class MfaCredential {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "secret_enc", nullable = false, length = 255)
    private String secretEnc;

    @Column(nullable = false)
    private boolean enabled;

    /** Dernier pas TOTP consommé : empêche le rejeu d'un code déjà utilisé. */
    @Column(name = "last_used_step", nullable = false)
    private long lastUsedStep;

    @Column(name = "failed_attempts", nullable = false)
    private int failedAttempts;

    @Column(name = "locked_until")
    private Instant lockedUntil;

    protected MfaCredential() {}

    public MfaCredential(Long userId, String secretEnc) {
        this.userId = userId;
        this.secretEnc = secretEnc;
    }

    public void replaceSecret(String enc) {
        secretEnc = enc;
        enabled = false;
        lastUsedStep = 0;
        failedAttempts = 0;
        lockedUntil = null;
    }

    public void activate() { enabled = true; }

    public boolean isLocked(Instant now) { return lockedUntil != null && now.isBefore(lockedUntil); }

    public void recordFailure(Instant now, int max, Duration lock) {
        if (++failedAttempts >= max) {
            lockedUntil = now.plus(lock);
            failedAttempts = 0;
        }
    }

    public void recordSuccess(long step) {
        failedAttempts = 0;
        lockedUntil = null;
        lastUsedStep = step;
    }

    public String getSecretEnc() { return secretEnc; }
    public boolean isEnabled() { return enabled; }
    public long getLastUsedStep() { return lastUsedStep; }
    public int getFailedAttempts() { return failedAttempts; }
    public Instant getLockedUntil() { return lockedUntil; }
}
