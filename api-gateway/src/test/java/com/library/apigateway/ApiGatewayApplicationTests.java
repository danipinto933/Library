package com.library.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        // Evita conectarse al Config Server y Eureka durante los tests
        "spring.cloud.config.enabled=false",
        "spring.cloud.config.import-check.enabled=false",
        "spring.config.import=",
        "eureka.client.enabled=false",
        "app.jwt-secret=test-secret-key-only-for-tests-at-least-32-chars",
        "app.frontend-url=http://localhost:5173",
        // Rutas stub para que el contexto arranque sin servicios reales
        "spring.cloud.gateway.routes[0].id=test-route",
        "spring.cloud.gateway.routes[0].uri=http://localhost:9999",
        "spring.cloud.gateway.routes[0].predicates[0]=Path=/test/**"
})
class ApiGatewayApplicationTests {

    @Test
    void contextLoads() {
        // Verifica que el contexto Spring arranca correctamente
    }
}
