
package ma.ensa.dop.auth.config;

import ma.ensa.dop.auth.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DopUserDetailsService implements UserDetailsService {

    private final UserRepository users;

    public DopUserDetailsService(UserRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
        throws UsernameNotFoundException {

        return users.findByEmail(email.trim().toLowerCase())
            .map(u -> {

                List<GrantedAuthority> authorities = new ArrayList<>();

                // 1. Ajouter le rôle de l'utilisateur
                authorities.add(
                    new SimpleGrantedAuthority("ROLE_" + u.getRole().name())
                );

                // 2. Ajouter le ressort uniquement s'il existe
                if (u.getRessort() != null) {
                    authorities.add(
                        new SimpleGrantedAuthority("RESSORT_" + u.getRessort().name())
                    );
                }

                // 3. Construire le UserDetails
                return org.springframework.security.core.userdetails.User
                    .withUsername(u.getEmail())
                    .password(u.getPasswordHash())
                    .authorities(authorities)
                    .build();
            })
            .orElseThrow(() ->
                new UsernameNotFoundException("Utilisateur introuvable")
            );
    }
}
