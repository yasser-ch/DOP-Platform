package ma.ensa.dop.auth.service;

import ma.ensa.dop.auth.dto.MfaSetupResponse;
import ma.ensa.dop.auth.dto.UserResponse;
import ma.ensa.dop.auth.entity.MfaCredential;
import ma.ensa.dop.auth.entity.User;
import ma.ensa.dop.auth.exception.InvalidCredentialsException;
import ma.ensa.dop.auth.exception.MfaInvalidCodeException;
import ma.ensa.dop.auth.exception.MfaLockedException;
import ma.ensa.dop.auth.exception.MfaStateException;
import ma.ensa.dop.auth.exception.UserNotFoundException;
import ma.ensa.dop.auth.repository.MfaCredentialRepository;
import ma.ensa.dop.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

/**
 * Cycle de vie du MFA TOTP : setup → enable → (verify à chaque connexion) → disable.
 * Chaque opération qui vérifie un code verrouille la ligne (SELECT ... FOR UPDATE) pour que deux
 * requêtes concurrentes ne puissent pas rejouer le même code, et conserve le compteur d'échecs
 * même quand le code est faux ({@code noRollbackFor}).
 */
@Service
public class MfaService {
    static final int MAX_ATTEMPTS = 5;
    static final Duration LOCK = Duration.ofMinutes(15);

    private final UserRepository users;
    private final MfaCredentialRepository creds;
    private final TotpService totp;
    private final SecretCipher cipher;
    private final JwtService jwt;
    private final Clock clock;
    private final String issuer;

    public MfaService(UserRepository users, MfaCredentialRepository creds, TotpService totp,
                      SecretCipher cipher, JwtService jwt, Clock clock,
                      @Value("${dop.mfa.issuer}") String issuer) {
        this.users = users;
        this.creds = creds;
        this.totp = totp;
        this.cipher = cipher;
        this.jwt = jwt;
        this.clock = clock;
        this.issuer = issuer;
    }

    @Transactional(readOnly = true)
    public boolean isEnabled(Long userId) {
        return creds.existsByUserIdAndEnabledTrue(userId);
    }

    /** Génère (ou régénère) un secret non encore activé. Refusé si le MFA est déjà actif. */
    @Transactional
    public MfaSetupResponse setup(String email) {
        User u = user(email);
        MfaCredential c = creds.findForUpdate(u.getId()).orElse(null);
        if (c != null && c.isEnabled()) throw new MfaStateException();   // désactiver d'abord (avec un code)

        byte[] secret = totp.newSecret();
        String enc = cipher.encrypt(secret);
        if (c == null) c = new MfaCredential(u.getId(), enc);
        else c.replaceSecret(enc);
        creds.save(c);

        String b32 = totp.toBase32(secret);
        return new MfaSetupResponse(b32, totp.otpauthUri(issuer, u.getEmail(), b32));
    }

    /** Active le MFA après preuve que l'utilisateur possède bien le secret. */
    @Transactional(noRollbackFor = {MfaInvalidCodeException.class, MfaLockedException.class})
    public void enable(String email, String code) {
        MfaCredential c = creds.findForUpdate(user(email).getId())
            .filter(x -> !x.isEnabled()).orElseThrow(MfaStateException::new);
        checkCode(c, code);
        c.activate();
        creds.save(c);
    }

    /** 2ᵉ étape de la connexion : valide le mfaToken (mot de passe déjà vérifié) + le code TOTP. */
    @Transactional(noRollbackFor = {MfaInvalidCodeException.class, MfaLockedException.class})
    public UserResponse verifyLogin(String mfaToken, String code) {
        User u = users.findByEmail(jwt.emailFromMfaToken(mfaToken))
            .orElseThrow(InvalidCredentialsException::new);
        MfaCredential c = creds.findForUpdate(u.getId())
            .filter(MfaCredential::isEnabled).orElseThrow(InvalidCredentialsException::new);
        checkCode(c, code);
        creds.save(c);
        return UserResponse.from(u);
    }

    @Transactional(noRollbackFor = {MfaInvalidCodeException.class, MfaLockedException.class})
    public void disable(String email, String code) {
        MfaCredential c = creds.findForUpdate(user(email).getId())
            .filter(MfaCredential::isEnabled).orElseThrow(MfaStateException::new);
        checkCode(c, code);
        creds.delete(c);
    }

    private void checkCode(MfaCredential c, String code) {
        Instant now = clock.instant();
        if (c.isLocked(now)) throw new MfaLockedException();
        long step = totp.match(cipher.decrypt(c.getSecretEnc()), code, now, c.getLastUsedStep());
        if (step < 0) {
            c.recordFailure(now, MAX_ATTEMPTS, LOCK);
            creds.save(c);
            throw new MfaInvalidCodeException();
        }
        c.recordSuccess(step);
    }

    private User user(String email) {
        return users.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }
}
