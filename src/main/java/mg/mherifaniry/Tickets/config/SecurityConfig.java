package mg.mherifaniry.Tickets.config;

import mg.mherifaniry.Tickets.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserServiceImpl userService;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
                .authorizeHttpRequests(request -> request
                        // Autoriser l'accès public aux ressources Swagger et OpenAPI
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/users").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/users/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/{id}/ticket").permitAll()
                        .requestMatchers("/tickets").authenticated()
                        .requestMatchers( HttpMethod.POST, "/tickets").authenticated()
                        .requestMatchers( "/tickets/{id}").authenticated()
                        .requestMatchers( HttpMethod.DELETE, "/tickets/{id}").authenticated()
                        .requestMatchers( HttpMethod.PUT,"/tickets/{id}/assign/{userId}").authenticated()
                )
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults())
                .build();

    }

    @Bean // Authentification Base de données
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder(5));
        return provider;
    }


}
