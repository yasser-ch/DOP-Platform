package ma.ensa.dop.auth.entity;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class MfaCredentialTest {
    private static final Instant T0 = Instant.parse("2026-01-01T00:00:00Z");
    private static final Duration LOCK = Duration.ofMinutes(15);

    @Test
    void locks_after_max_failures_then_unlocks_by_time() {
        MfaCredential c = new MfaCredential(1L, "enc");
        for (int i = 0; i < 4; i++) c.recordFailure(T0, 5, LOCK);
        assertThat(c.isLocked(T0)).isFalse();
        c.recordFailure(T0, 5, LOCK);
        assertThat(c.isLocked(T0)).isTrue();
        assertThat(c.isLocked(T0.plus(LOCK).minusSeconds(1))).isTrue();
        assertThat(c.isLocked(T0.plus(LOCK))).isFalse();
    }

    @Test
    void success_resets_failures_and_records_step() {
        MfaCredential c = new MfaCredential(1L, "enc");
        for (int i = 0; i < 4; i++) c.recordFailure(T0, 5, LOCK);
        c.recordSuccess(42);
        assertThat(c.getFailedAttempts()).isZero();
        assertThat(c.getLastUsedStep()).isEqualTo(42);
        c.recordFailure(T0, 5, LOCK);   // le compteur repart de zéro
        assertThat(c.isLocked(T0)).isFalse();
    }

    @Test
    void replacing_the_secret_resets_everything() {
        MfaCredential c = new MfaCredential(1L, "enc");
        c.activate();
        c.recordSuccess(42);
        for (int i = 0; i < 5; i++) c.recordFailure(T0, 5, LOCK);
        c.replaceSecret("new");
        assertThat(c.isEnabled()).isFalse();
        assertThat(c.getLastUsedStep()).isZero();
        assertThat(c.isLocked(T0)).isFalse();
        assertThat(c.getSecretEnc()).isEqualTo("new");
    }
}
