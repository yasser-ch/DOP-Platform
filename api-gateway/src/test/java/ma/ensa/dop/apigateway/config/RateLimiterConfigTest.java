package ma.ensa.dop.apigateway.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.InetSocketAddress;

import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

class RateLimiterConfigTest {

    private final RateLimiterConfig config = new RateLimiterConfig();

    @Test
    void utiliseLIpDuClientCommeCle() {
        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/assujettis/x")
                        .remoteAddress(new InetSocketAddress("10.1.2.3", 4321)).build());

        assertEquals("10.1.2.3", config.ipKeyResolver().resolve(exchange).block());
    }

    @Test
    void cleParDefautSansAdresse() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/x").build());

        assertEquals("unknown", config.ipKeyResolver().resolve(exchange).block());
    }
}