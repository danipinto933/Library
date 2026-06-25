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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.DaniCRUD.fullStackBackend.dto.response.RoleDto;
import com.DaniCRUD.fullStackBackend.model.Role;
import com.DaniCRUD.fullStackBackend.repository.RoleRepository;
import com.DaniCRUD.fullStackBackend.security.SecurityConstants;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RoleControllerTestRestTemplateTests {

    @Autowired
    private TestRestTemplate testRestTemplate;

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
            uniqueRoleName = "ROLE_TEST_" + System.currentTimeMillis();
        }

        token = JWT.create()
                .withSubject("admin")
                .withClaim("roles", List.of("ROLE_ADMIN"))
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(jwtSecret));
    }

    private Role createTestRole(String name) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        RoleDto dto = RoleDto.builder().role(name).build();
        HttpEntity<RoleDto> requestEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<Role> response = testRestTemplate.exchange(
                "/api/v1/roles",
                HttpMethod.POST,
                requestEntity,
                Role.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        return response.getBody();
    }

    @Test
    @Order(1)
    void testSaveRole() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        RoleDto dto = RoleDto.builder().role(uniqueRoleName).build();
        HttpEntity<RoleDto> requestEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<Role> response = testRestTemplate.exchange(
                "/api/v1/roles",
                HttpMethod.POST,
                requestEntity,
                Role.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals(uniqueRoleName, response.getBody().getRole());

        savedRoleId = response.getBody().getId();
    }

    @Test
    @Order(2)
    void testSaveRole_ValidationError() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        RoleDto invalidDto = RoleDto.builder().role(null).build();
        HttpEntity<RoleDto> requestEntity = new HttpEntity<>(invalidDto, headers);

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/v1/roles",
                HttpMethod.POST,
                requestEntity,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(3)
    void testFindAllRoles() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<List<RoleDto>> response = testRestTemplate.exchange(
                "/api/v1/roles",
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<RoleDto>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().size() > 0);
        assertTrue(response.getBody().stream().anyMatch(r -> r.getRole().equals(uniqueRoleName)));
    }


    @Test
    @Order(4)
    void testUpdateRole() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        RoleDto updateDto = RoleDto.builder().role(uniqueRoleName + "_MODIFIED").build();
        HttpEntity<RoleDto> requestEntity = new HttpEntity<>(updateDto, headers);

        ResponseEntity<Role> response = testRestTemplate.exchange(
                "/api/v1/roles/" + savedRoleId,
                HttpMethod.PUT,
                requestEntity,
                Role.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(uniqueRoleName + "_MODIFIED", response.getBody().getRole());
    }

    @Test
    @Order(5)
    void testDeleteRole() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/v1/roles/" + savedRoleId,
                HttpMethod.DELETE,
                requestEntity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Rol eliminado correctamente", response.getBody());
    }

    @Test
    @Order(6)
    void testDeleteRole_NotFound() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/v1/roles/9999",
                HttpMethod.DELETE,
                requestEntity,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
