package com.library.apigateway.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Filtro JWT global del API Gateway.
 *
 * Responsabilidades:
 *   1. Identificar si el endpoint es público o requiere autenticación.
 *   2. Validar el token JWT usando HMAC512 (mismo algoritmo que el monolito).
 *   3. Extraer username y roles y propagarlos como headers al microservicio destino.
 *   4. Rechazar con 401 si el token falta o es inválido en rutas protegidas.
 *
 * Token JWT esperado (generado por Auth-Service):
 *   - Subject: username
 *   - Claim "roles": List<String> e.g. ["ROLE_ADMIN"] o ["ROLE_USER"]
 *   - Algorithm: HMAC512
 */
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    // Rutas siempre públicas (sin importar el método HTTP)
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/v1/login",
            "/uploads/"
    );

    // Rutas públicas SOLO para GET (visibles sin autenticación)
    private static final List<String> PUBLIC_GET_PATHS = List.of(
            "/api/v1/books",
            "/api/v1/genres"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        HttpMethod method = request.getMethod();

        // ── 1. Verificar si la ruta es pública ──────────────────────────────
        if (isPublicPath(path, method)) {
            return chain.filter(exchange);
        }

        // ── 2. Obtener el header de autorización ────────────────────────────
        String authHeader = request.getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Acceso denegado a '{}' — falta header Authorization", path);
            return buildUnauthorizedResponse(exchange, "Token JWT ausente o malformado");
        }

        String token = authHeader.substring(7).trim();

        // ── 3. Validar el token JWT ──────────────────────────────────────────
        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(jwtSecret))
                    .build()
                    .verify(token);

            String username = decodedJWT.getSubject();
            List<String> roles = decodedJWT.getClaim("roles").asList(String.class);
            String rolesHeader = (roles != null) ? String.join(",", roles) : "";

            log.debug("Token válido — usuario: '{}', roles: '{}'", username, rolesHeader);

            // ── 4. Propagar identidad al microservicio destino ───────────────
            ServerHttpRequest enrichedRequest = request.mutate()
                    .header("X-User-Name", username)
                    .header("X-User-Roles", rolesHeader)
                    .build();

            return chain.filter(exchange.mutate().request(enrichedRequest).build());

        } catch (JWTVerificationException ex) {
            log.warn("Token JWT inválido para '{}': {}", path, ex.getMessage());
            return buildUnauthorizedResponse(exchange, "Token JWT inválido o expirado");
        }
    }

    /**
     * Determina si una ruta es pública y puede omitir la validación JWT.
     */
    private boolean isPublicPath(String path, HttpMethod method) {
        // Siempre públicas (login, imágenes estáticas)
        for (String publicPath : PUBLIC_PATHS) {
            if (path.startsWith(publicPath)) {
                return true;
            }
        }
        // Públicas solo para GET (catálogo de libros y géneros)
        if (HttpMethod.GET.equals(method)) {
            for (String publicGetPath : PUBLIC_GET_PATHS) {
                if (path.startsWith(publicGetPath)) {
                    return true;
                }
            }
        }
        // El registro de un nuevo usuario es público
        if (HttpMethod.POST.equals(method) && path.equals("/api/v1/users")) {
            return true;
        }
        return false;
    }

    /**
     * Construye una respuesta 401 con cuerpo JSON.
     */
    private Mono<Void> buildUnauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");

        String body = String.format(
                "{\"status\": 401, \"error\": \"Unauthorized\", \"message\": \"%s\"}",
                message
        );

        DataBuffer buffer = response.bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }

    /**
     * Prioridad alta — este filtro se ejecuta antes que cualquier otro filtro del Gateway.
     */
    @Override
    public int getOrder() {
        return -1;
    }
}
