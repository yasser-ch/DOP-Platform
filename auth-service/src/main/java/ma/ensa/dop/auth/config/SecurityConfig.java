package ma.ensa.dop.auth.config;

import ma.ensa.dop.auth.service.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, JwtService jwt) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a -> a
                .requestMatchers(HttpMethod.POST, "/auth/register", "/auth/login", "/auth/mfa/verify").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .anyRequest().authenticated())
            // Bearer : seul moyen d'appeler l'API pour un compte MFA (cf. DopUserDetailsService)
            .oauth2ResourceServer(o -> o.jwt(j -> j
                .decoder(jwt.accessTokenDecoder())
                .jwtAuthenticationConverter(jwtConverter())))
            .httpBasic(Customizer.withDefaults())
            .exceptionHandling(e -> e
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
        return http.build();
    }

    /** Traduit les claims role / ressort du jeton en autorités ROLE_* / RESSORT_* (comme DopUserDetailsService). */
    private static Converter<Jwt, AbstractAuthenticationToken> jwtConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<GrantedAuthority> authorities = new ArrayList<>();
            String role = jwt.getClaimAsString("role");
            if (role != null) authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            String ressort = jwt.getClaimAsString("ressort");
            if (ressort != null) authorities.add(new SimpleGrantedAuthority("RESSORT_" + ressort));
            return authorities;
        });
        return converter;
    }
}
