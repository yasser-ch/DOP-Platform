package ma.ensa.dop.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/** Horloge injectable (remplaçable dans les tests pour avancer le temps TOTP). */
@Configuration
public class ClockConfig {
    @Bean
    Clock clock() { return Clock.systemUTC(); }
}
