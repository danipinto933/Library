package com.library.authservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "spring.cloud.config.import-check.enabled=false",
        "spring.config.import=",
        "eureka.client.enabled=false",
        "app.jwt-secret=test-secret-key-only-for-tests-at-least-32-chars",
        "app.frontend-url=http://localhost:5173",
        // H2 en modo PostgreSQL para los tests
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class AuthServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
