package ma.ensa.dop.declaration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Cœur métier : création et soumission des déclarations (FR-09 à FR-16). Démonstrateur académique - données synthétiques uniquement. */
@SpringBootApplication
public class DeclarationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeclarationServiceApplication.class, args);
    }
}
