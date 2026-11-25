package ma.ensa.dop.assujettis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

/** Horloge injectable : "aujourd'hui" = date au Maroc. Les tests peuvent la remplacer par une horloge fixe. */
@Configuration
public class ClockConfig {
    @Bean
    Clock clock() { return Clock.system(ZoneId.of("Africa/Casablanca")); }
}
