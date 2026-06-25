package com.DaniCRUD.fullStackBackend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.DaniCRUD.fullStackBackend.dto.response.RoleDto;
import com.DaniCRUD.fullStackBackend.model.Role;
import com.DaniCRUD.fullStackBackend.repository.RoleRepository;
import com.DaniCRUD.fullStackBackend.security.SecurityConstants;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureWebTestClient
public class RoleControllerWebTestClientTests {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RoleRepository roleRepository;

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    private String token;
    private static Long savedRoleId;
    private static String uniqueRoleName;

    @BeforeEach
    void setup() {
        if (uniqueRoleName == null) {
            uniqueRoleName = "ROLE_WEB_" + System.currentTimeMillis();
        }

        token = JWT.create()
                .withSubject("admin")
                .withClaim("roles", List.of("ROLE_ADMIN"))
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(jwtSecret));
    }

    @Test
    @Order(1)
    void testSaveRole() {
        RoleDto newRole = RoleDto.builder().role(uniqueRoleName).build();

        Role responseRole = webTestClient.post()
                .uri("/api/v1/roles")
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newRole)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Role.class)
                .value(role -> {
                    assertNotNull(role.getId());
                    assertEquals(uniqueRoleName, role.getRole());
                })
                .returnResult()
                .getResponseBody();

        assertNotNull(responseRole);
        savedRoleId = responseRole.getId();
    }

    @Test
    @Order(2)
    void testSaveRole_ValidationError() {
        RoleDto invalidRole = RoleDto.builder().role(null).build();

        webTestClient.post()
                .uri("/api/v1/roles")
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRole)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(3)
    void testFindAllRoles() {
        webTestClient.get()
                .uri("/api/v1/roles")
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(RoleDto.class)
                .value(list -> {
                    assertTrue(list.size() > 0);
                    assertTrue(list.stream().anyMatch(r -> r.getRole().equals(uniqueRoleName)));
                });
    }


    @Test
    @Order(4)
    void testUpdateRole() {
        RoleDto updateDto = RoleDto.builder().role(uniqueRoleName + "_MODIFIED_WEB").build();

        webTestClient.put()
                .uri("/api/v1/roles/{id}", savedRoleId)
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Role.class)
                .value(role -> {
                    assertEquals(uniqueRoleName + "_MODIFIED_WEB", role.getRole());
                });
    }

    @Test
    @Order(5)
    void testDeleteRole() {
        webTestClient.delete()
                .uri("/api/v1/roles/{id}", savedRoleId)
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(res -> {
                    assertEquals("Rol eliminado correctamente", res);
                });
    }

    @Test
    @Order(6)
    void testDeleteRole_NotFound() {
        webTestClient.delete()
                .uri("/api/v1/roles/99999")
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token)
                .exchange()
                .expectStatus().isNotFound();
    }
}
