package ma.ensa.dop.apigateway.config;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Optional;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * Cle de rate limiting : l'adresse IP du client. Chaque IP dispose de son propre seuil
 * (voir application-docker.yml : redis-rate-limiter.replenishRate / burstCapacity).
 */
@Configuration
public class RateLimiterConfig {

    @Bean
    KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(
                Optional.ofNullable(exchange.getRequest().getRemoteAddress())
                        .map(InetSocketAddress::getAddress)
                        .map(InetAddress::getHostAddress)
                        .orElse("unknown"));
    }
}