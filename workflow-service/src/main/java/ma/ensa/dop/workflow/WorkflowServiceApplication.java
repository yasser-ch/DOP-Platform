package ma.ensa.dop.workflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Cycle de contrôle : examen, observation, mise en demeure (FR-20 à FR-26). Démonstrateur académique - données synthétiques uniquement. */
@SpringBootApplication
public class WorkflowServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkflowServiceApplication.class, args);
    }
}
