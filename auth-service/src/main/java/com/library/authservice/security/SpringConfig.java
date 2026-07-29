package com.library.authservice.security;

import com.library.authservice.security.filter.JWTAuthentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Spring Security para Auth-Service.
 *
 * Este servicio es minimalista: solo expone /api/v1/login (manejado por el filtro JWT).
 * El resto de rutas de la aplicación no existen en este servicio, por lo que
 * cualquier petición fuera de /api/v1/login queda bloqueada (denyAll).
 *
 * CORS: no configurado aquí — el API Gateway centraliza toda la política CORS.
 */
@Configuration
public class SpringConfig {

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    private final CustomAuthenticationManager customAuthenticationManager;

    public SpringConfig(CustomAuthenticationManager customAuthenticationManager) {
        this.customAuthenticationManager = customAuthenticationManager;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JWTAuthentication jwtAuthentication = new JWTAuthentication(customAuthenticationManager, jwtSecret);
        jwtAuthentication.setFilterProcessesUrl("/api/v1/login");

        http
            .cors(cors -> cors.disable())   // CORS delegado al API Gateway
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(request -> request
                .requestMatchers("/api/v1/login").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().denyAll()      // Auth-Service no expone más endpoints
            )
            .addFilter(jwtAuthentication)
            .sessionManagement(management ->
                management.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }
}
