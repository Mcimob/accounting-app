package ch.pfaditools.accounting.security;

import ch.pfaditools.accounting.security.service.SecurityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_LOGIN;
import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_LOGOUT;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String[] ALLOWED_GET_URLS = {
            "/",
            "/login/**",
            "/register/**",
            "/frontend/**",
            "/VAADIN/**",
            "/favicon.ico"
    };

    private final SecurityService securityService;

    public SecurityConfig(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(ALLOWED_GET_URLS).permitAll()
                .anyRequest().authenticated()
            ).authenticationProvider(securityService)
            .formLogin(form -> form
                .loginPage("/" + ROUTE_LOGIN)
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/" + ROUTE_LOGIN + "?" + ROUTE_LOGOUT)
                .permitAll()
            )
            .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(SecurityService securityService) {
        return securityService; // Ensure SecurityService is registered
    }
}
