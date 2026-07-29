package com.library.authservice.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.authservice.model.User;
import com.library.authservice.security.CustomAuthenticationManager;
import com.library.authservice.security.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Filtro de autenticación JWT.
 *
 * Se registra en /api/v1/login (configurado en SpringConfig).
 * Flujo:
 *   1. Lee credenciales del body JSON {userName, password}
 *   2. Autentica via CustomAuthenticationManager (BCrypt)
 *   3. Si OK → genera JWT HMAC512 con claims {subject=userName, roles=[...]}
 *   4. Devuelve el token en el header Authorization y en el body JSON
 *
 * Algoritmo: HMAC512 — compatible con el filtro JwtAuthenticationFilter del Gateway.
 */
public class JWTAuthentication extends UsernamePasswordAuthenticationFilter {

    private final CustomAuthenticationManager customAuthenticationManager;
    private final String secretKey;

    public JWTAuthentication(CustomAuthenticationManager customAuthenticationManager, String secretKey) {
        this.customAuthenticationManager = customAuthenticationManager;
        this.secretKey = secretKey;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            User user = new ObjectMapper().readValue(request.getInputStream(), User.class);
            return customAuthenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword())
            );
        } catch (IOException e) {
            throw new RuntimeException("Error al leer las credenciales de login", e);
        }
    }

    @Override
    public void successfulAuthentication(HttpServletRequest request,
                                         HttpServletResponse response,
                                         FilterChain chain,
                                         Authentication authResult) throws IOException, ServletException {

        List<String> roles = authResult.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.toList());

        String token = JWT.create()
                .withSubject(authResult.getName())
                .withClaim("roles", roles)           // ["ROLE_ADMIN"] o ["ROLE_USER"]
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(secretKey)); // mismo algoritmo que el monolito

        response.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);

        Map<String, Object> httpResponse = new HashMap<>();
        httpResponse.put("token", token);
        httpResponse.put("user", authResult.getName());

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(httpResponse));
        response.getWriter().flush();
    }

    @Override
    public void unsuccessfulAuthentication(HttpServletRequest request,
                                           HttpServletResponse response,
                                           AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("status", 401);
        errorBody.put("error", "Unauthorized");
        errorBody.put("message", "Credenciales incorrectas");

        response.getWriter().write(new ObjectMapper().writeValueAsString(errorBody));
        response.getWriter().flush();
    }
}
