package ma.ensa.dop.audit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Journalisation immuable et traçabilité (FR-37, FR-38). Démonstrateur académique - données synthétiques uniquement. */
@SpringBootApplication
public class AuditServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuditServiceApplication.class, args);
    }
}
