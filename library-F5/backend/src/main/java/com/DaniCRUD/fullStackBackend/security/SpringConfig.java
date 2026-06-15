package com.DaniCRUD.fullStackBackend.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.DaniCRUD.fullStackBackend.security.filter.JWTAuthentication;
import com.DaniCRUD.fullStackBackend.security.filter.JWTAuthorization;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

@Configuration
public class SpringConfig
{

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    private CustomAuthenticationManager customAuthenticationManager;

    public SpringConfig(CustomAuthenticationManager customAuthenticationManager)
    {
        this.customAuthenticationManager = customAuthenticationManager;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        JWTAuthentication jwtAuthentication = new JWTAuthentication(customAuthenticationManager, jwtSecret);
        jwtAuthentication.setFilterProcessesUrl("/api/v1/login"); //Crea automaticamente un metodo para hacer login

        http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(request -> request
            .requestMatchers(HttpMethod.GET,"/api/v1/users").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET,"/api/v1/users/4/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET,"/api/v1/users/5/**").hasRole("ADMIN")
            .requestMatchers("/api/v1/roles/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET,"/api/v1/reserves/all").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET,"/api/v1/reserves/1/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET,"/api/v1/reserves/2/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST,"/api/v1/books").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST,"/api/v1/genres").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT,"/api/v1/genres").hasRole("ADMIN")
            .anyRequest().permitAll()
            )
        .addFilter(jwtAuthentication)
        .addFilterAfter(new JWTAuthorization(jwtSecret), JWTAuthentication.class)
        .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() //Cross Origin Resource Sharing
    {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of(frontendUrl)); 
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Cache-Control"));
        config.setAllowCredentials(true);
        config.setExposedHeaders(List.of("Authorization"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/uploads/covers/**");
    }
}
