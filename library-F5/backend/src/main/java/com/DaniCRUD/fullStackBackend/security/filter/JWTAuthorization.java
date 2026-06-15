package com.DaniCRUD.fullStackBackend.security.filter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import com.DaniCRUD.fullStackBackend.security.SecurityConstants;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTAuthorization extends OncePerRequestFilter {

    private String secretKey;

    public JWTAuthorization(String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String header = request.getHeader(SecurityConstants.HEADER_STRING); // Usa constante

        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) { // Usa constante
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.replace(SecurityConstants.TOKEN_PREFIX, "").trim(); // Usa constante

        if (token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(token);

            String userName = decodedJWT.getSubject();
            
            // OJO: Asegúrate de que en el Login guardaste el claim como "roles"
            List<String> roles = decodedJWT.getClaim("roles").asList(String.class); 
            
            // Si roles es null (porque el token no los trae), esto fallaría. Agregamos protección:
            if (roles == null) roles = List.of(); 

            List<GrantedAuthority> authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority(role))
                    .collect(Collectors.toList());

            Authentication authentication = new UsernamePasswordAuthenticationToken(userName, null, authorities);
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            // ⚠️ ESTO ES VITAL: Imprime el error para que sepamos por qué falla ⚠️
            System.out.println("Error validando token: " + e.getMessage());
            e.printStackTrace(); 
            
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token invalido: " + e.getMessage()); // Devuelve el error al Postman
        }
    }
}