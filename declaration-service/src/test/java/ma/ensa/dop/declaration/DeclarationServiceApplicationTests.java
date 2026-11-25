package ma.ensa.dop.declaration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// AJOUTÉ : le service refuse de démarrer sans JWT_SECRET (>= 32 octets), donc on en fournit un pour les tests.
@SpringBootTest(properties = "dop.jwt.secret=test-only-secret-0123456789-abcdefghij")
class DeclarationServiceApplicationTests {
    @Test
    void contextLoads() {
    }
}
