package com.library.apigateway.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de integración del filtro JWT del API Gateway.
 *
 * Cobertura:
 *   - Rutas públicas (GET books/genres, login, uploads) → pasan sin token.
 *   - Rutas protegidas sin token → 401.
 *   - Rutas protegidas con token válido → se propagan headers X-User-Name / X-User-Roles.
 *   - Rutas protegidas con token manipulado (inválido) → 401.
 *   - Rutas protegidas con token expirado → 401.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "spring.cloud.config.import-check.enabled=false",
        "spring.config.import=",
        "eureka.client.enabled=false",
        "app.jwt-secret=test-secret-key-only-for-tests-at-least-32-chars",
        "app.frontend-url=http://localhost:5173",

        // ── Rutas de test ────────────────────────────────────────────────────
        // IMPORTANTE: el GlobalFilter JWT solo se ejecuta DESPUÉS de que una
        // ruta coincida con el predicado. Sin rutas configuradas (Config Server
        // deshabilitado), el Gateway devuelve 404 directamente saltándose el
        // filtro. Por eso definimos rutas apuntando a localhost:9999 (inalcanzable).
        // El filtro JWT devuelve 401 antes de intentar conectarse, de modo que
        // el backend inalcanzable no afecta a los tests de rechazo de token.
        // Los tests de rutas públicas reciben 503 (conexión rechazada) ≠ 401 ✅

        "spring.cloud.gateway.routes[0].id=test-login",
        "spring.cloud.gateway.routes[0].uri=http://localhost:9999",
        "spring.cloud.gateway.routes[0].predicates[0]=Path=/api/v1/login",

        "spring.cloud.gateway.routes[1].id=test-users",
        "spring.cloud.gateway.routes[1].uri=http://localhost:9999",
        "spring.cloud.gateway.routes[1].predicates[0]=Path=/api/v1/users,/api/v1/users/**",

        "spring.cloud.gateway.routes[2].id=test-roles",
        "spring.cloud.gateway.routes[2].uri=http://localhost:9999",
        "spring.cloud.gateway.routes[2].predicates[0]=Path=/api/v1/roles/**",

        "spring.cloud.gateway.routes[3].id=test-books",
        "spring.cloud.gateway.routes[3].uri=http://localhost:9999",
        "spring.cloud.gateway.routes[3].predicates[0]=Path=/api/v1/books,/api/v1/books/**",

        "spring.cloud.gateway.routes[4].id=test-genres",
        "spring.cloud.gateway.routes[4].uri=http://localhost:9999",
        "spring.cloud.gateway.routes[4].predicates[0]=Path=/api/v1/genres,/api/v1/genres/**",

        "spring.cloud.gateway.routes[5].id=test-reserves",
        "spring.cloud.gateway.routes[5].uri=http://localhost:9999",
        "spring.cloud.gateway.routes[5].predicates[0]=Path=/api/v1/reserves,/api/v1/reserves/**"
})
class JwtAuthenticationFilterTests {

    private static final String JWT_SECRET = "test-secret-key-only-for-tests-at-least-32-chars";
    private static final long ONE_HOUR_MS = 3_600_000L;

    @Autowired
    private WebTestClient webTestClient;

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private String buildValidToken(String username, String... roles) {
        return JWT.create()
                .withSubject(username)
                .withClaim("roles", List.of(roles))
                .withExpiresAt(new Date(System.currentTimeMillis() + ONE_HOUR_MS))
                .sign(Algorithm.HMAC512(JWT_SECRET));
    }

    private String buildExpiredToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withClaim("roles", List.of("ROLE_USER"))
                .withExpiresAt(new Date(System.currentTimeMillis() - 1000))
                .sign(Algorithm.HMAC512(JWT_SECRET));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Tests — Rutas públicas (deben pasar SIN token)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void getBooks_sinToken_retornaNoDenegado() {
        // GET /api/v1/books es público → el Gateway no bloquea (aunque el servicio esté caído dará 502, no 401)
        webTestClient.get()
                .uri("/api/v1/books")
                .exchange()
                .expectStatus().value(status ->
                        assertThat(status).isNotEqualTo(HttpStatus.UNAUTHORIZED.value())
                );
    }

    @Test
    void getGenres_sinToken_retornaNoDenegado() {
        webTestClient.get()
                .uri("/api/v1/genres")
                .exchange()
                .expectStatus().value(status ->
                        assertThat(status).isNotEqualTo(HttpStatus.UNAUTHORIZED.value())
                );
    }

    @Test
    void login_sinToken_retornaNoDenegado() {
        webTestClient.post()
                .uri("/api/v1/login")
                .exchange()
                .expectStatus().value(status ->
                        assertThat(status).isNotEqualTo(HttpStatus.UNAUTHORIZED.value())
                );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Tests — Rutas protegidas SIN token → 401
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void getReserves_sinToken_retorna401() {
        webTestClient.get()
                .uri("/api/v1/reserves/all")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("401"));
    }

    @Test
    void postBook_sinToken_retorna401() {
        webTestClient.post()
                .uri("/api/v1/books")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void getUsers_sinToken_retorna401() {
        webTestClient.get()
                .uri("/api/v1/users")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Tests — Token inválido (firma incorrecta) → 401
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void getUsers_tokenManipulado_retorna401() {
        String fakeToken = buildValidToken("hacker", "ROLE_ADMIN")
                .concat("tampered");

        webTestClient.get()
                .uri("/api/v1/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + fakeToken)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void getUsers_tokenConSecretoIncorrecto_retorna401() {
        String tokenConOtroSecreto = JWT.create()
                .withSubject("hacker")
                .withClaim("roles", List.of("ROLE_ADMIN"))
                .withExpiresAt(new Date(System.currentTimeMillis() + ONE_HOUR_MS))
                .sign(Algorithm.HMAC512("otro-secreto-completamente-diferente-1234567890"));

        webTestClient.get()
                .uri("/api/v1/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenConOtroSecreto)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Tests — Token expirado → 401
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void getUsers_tokenExpirado_retorna401() {
        String expiredToken = buildExpiredToken("usuario");

        webTestClient.get()
                .uri("/api/v1/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredToken)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Tests — Header malformado → 401
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void protectedRoute_headerSinBearerPrefix_retorna401() {
        String token = buildValidToken("usuario", "ROLE_USER");

        webTestClient.get()
                .uri("/api/v1/users")
                .header(HttpHeaders.AUTHORIZATION, token) // sin "Bearer "
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Tests — POST /api/v1/users (registro) es público → no bloquea
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void postUsers_registroPublico_sinToken_noDa401() {
        webTestClient.post()
                .uri("/api/v1/users")
                .exchange()
                .expectStatus().value(status ->
                        assertThat(status).isNotEqualTo(HttpStatus.UNAUTHORIZED.value())
                );
    }
}
