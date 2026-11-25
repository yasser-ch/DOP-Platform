package ma.ensa.dop.audit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "dop.jwt.secret=test-only-secret-0123456789-abcdefghij")
class AuditServiceApplicationTests {
    @Test
    void contextLoads() {
    }
}
