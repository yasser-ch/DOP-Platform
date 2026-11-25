package ma.ensa.dop.auth.security;

import ma.ensa.dop.auth.Ressort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component("ressortPolicy")
public class RessortPolicy {
    public boolean canAccess(Authentication auth, Ressort dossierRessort) {
        if (auth == null || dossierRessort == null) return false;
        Set<String> a = AuthorityUtils.authorityListToSet(auth.getAuthorities());
        return a.contains("ROLE_MAGISTRAT") && a.contains("RESSORT_" + dossierRessort.name());
    }
}
