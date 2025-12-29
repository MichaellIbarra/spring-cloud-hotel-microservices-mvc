package dev.matichelo.service.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // deshabilitar CSRF ya que no es necesario para APIs RESTful, especialmente cuando se utiliza JWT ,
                .csrf(csrf -> csrf.disable())
                // metodo para autorizar solicitudes HTTP
                .authorizeHttpRequests(auth -> {
                    auth.anyRequest().permitAll();
                })
                // configurar la gestión de sesiones para que sea sin estado (stateless), se debe a que JWT se utiliza para autenticar cada solicitud de manera independiente.
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
