package ma.ensa.dop.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(properties = {"dop.admin.email=admin@test.ma", "dop.admin.password=Admin12345"})
@TestPropertySource(
    properties = "dop.jwt.secret=test-only-secret-0123456789-abcdefghij"

)
class AuthServiceApplicationTests {
    @Test
    void contextLoads() {
    }
}
