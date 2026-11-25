package ma.ensa.dop.auth.config;

import ma.ensa.dop.auth.repository.MfaCredentialRepository;
import ma.ensa.dop.auth.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/** Utilisé par l'authentification HTTP Basic (mot de passe seul). */
@Service
public class DopUserDetailsService implements UserDetailsService {

    private final UserRepository users;
    private final MfaCredentialRepository mfaCredentials;

    public DopUserDetailsService(UserRepository users, MfaCredentialRepository mfaCredentials) {
        this.users = users;
        this.mfaCredentials = mfaCredentials;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return users.findByEmail(email.trim().toLowerCase())
            .map(u -> {
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + u.getRole().name()));
                if (u.getRessort() != null)
                    authorities.add(new SimpleGrantedAuthority("RESSORT_" + u.getRessort().name()));

                // Un compte protégé par MFA ne doit jamais s'authentifier avec le seul mot de passe :
                // il doit passer par /auth/login + /auth/mfa/verify puis utiliser le jeton Bearer.
                boolean mfaEnabled = mfaCredentials.existsByUserIdAndEnabledTrue(u.getId());

                return org.springframework.security.core.userdetails.User
                    .withUsername(u.getEmail())
                    .password(u.getPasswordHash())
                    .authorities(authorities)
                    .disabled(mfaEnabled)
                    .build();
            })
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));
    }
}
