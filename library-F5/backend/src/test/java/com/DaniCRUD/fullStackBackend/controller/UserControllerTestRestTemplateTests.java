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

import com.DaniCRUD.fullStackBackend.dto.response.UserDto;
import com.DaniCRUD.fullStackBackend.model.Role;
import com.DaniCRUD.fullStackBackend.model.User;
import com.DaniCRUD.fullStackBackend.repository.RoleRepository;
import com.DaniCRUD.fullStackBackend.repository.UserRepository;
import com.DaniCRUD.fullStackBackend.security.SecurityConstants;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTestRestTemplateTests {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    private String token;
    private static Long savedUserId;
    private static String uniqueUserName;
    private static String uniqueName;
    private static String uniqueEmail;

    @BeforeEach
    void setup() {
        // Aseguramos que existan roles, especialmente el de ID 2 (ROLE_USER/USER)
        if (roleRepository.count() == 0) {
            Role roleAdmin = Role.builder().role("ADMIN").build();
            Role roleUser = Role.builder().role("USER").build();
            roleRepository.saveAll(List.of(roleAdmin, roleUser));
        } else if (roleRepository.findById(2L).isEmpty()) {
            Role roleUser = Role.builder().role("USER").build();
            roleRepository.save(roleUser);
        }

        if (uniqueUserName == null) {
            long timestamp = System.currentTimeMillis();
            uniqueUserName = "user_rest_" + timestamp;
            uniqueName = "Name Rest " + timestamp;
            uniqueEmail = "rest_" + timestamp + "@example.com";
        }

        token = JWT.create()
                .withSubject("admin")
                .withClaim("roles", List.of("ROLE_ADMIN"))
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(jwtSecret));
    }

    @Test
    @Order(1)
    void testSaveUser() {
        UserDto dto = UserDto.builder()
                .userName(uniqueUserName)
                .name(uniqueName)
                .email(uniqueEmail)
                .password("password123")
                .role("USER")
                .build();

        ResponseEntity<User> response = testRestTemplate.postForEntity(
                "/api/v1/users",
                dto,
                User.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals(uniqueUserName, response.getBody().getUserName());

        savedUserId = response.getBody().getId();
    }

    @Test
    @Order(2)
    void testSaveUser_Duplicate() {
        UserDto dto = UserDto.builder()
                .userName(uniqueUserName) // Duplicado
                .name("Other Name")
                .email("other@example.com")
                .password("password")
                .build();

        ResponseEntity<String> response = testRestTemplate.postForEntity(
                "/api/v1/users",
                dto,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(3)
    void testSaveUser_ValidationError() {
        UserDto invalidDto = UserDto.builder().userName(null).build();

        ResponseEntity<String> response = testRestTemplate.postForEntity(
                "/api/v1/users",
                invalidDto,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(4)
    void testFindAllUsers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<List<UserDto>> response = testRestTemplate.exchange(
                "/api/v1/users",
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<UserDto>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().size() > 0);
        assertTrue(response.getBody().stream().anyMatch(u -> u.getUserName().equals(uniqueUserName)));
    }

    @Test
    @Order(5)
    void testFindByUserName() {
        ResponseEntity<UserDto> response = testRestTemplate.exchange(
                "/api/v1/users/1/" + uniqueUserName,
                HttpMethod.GET,
                null,
                UserDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(uniqueUserName, response.getBody().getUserName());
    }

    @Test
    @Order(6)
    void testFindByUserName_NotFound() {
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/v1/users/1/UserInexistente",
                HttpMethod.GET,
                null,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(7)
    void testFindByName() {
        ResponseEntity<UserDto> response = testRestTemplate.exchange(
                "/api/v1/users/2/" + uniqueName,
                HttpMethod.GET,
                null,
                UserDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(uniqueName, response.getBody().getName());
    }

    @Test
    @Order(8)
    void testFindByEmail() {
        ResponseEntity<UserDto> response = testRestTemplate.exchange(
                "/api/v1/users/3/" + uniqueEmail,
                HttpMethod.GET,
                null,
                UserDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(uniqueEmail, response.getBody().getEmail());
    }

    @Test
    @Order(9)
    void testFindUsersByRole() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<List<UserDto>> response = testRestTemplate.exchange(
                "/api/v1/users/4/USER",
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<UserDto>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().size() > 0);
    }

    @Test
    @Order(10)
    void testUpdateUser() {
        UserDto updateDto = UserDto.builder()
                .userName(uniqueUserName + "_MOD")
                .name(uniqueName + " Mod")
                .email(uniqueEmail)
                .password("newpassword123")
                .role("USER")
                .build();

        HttpEntity<UserDto> requestEntity = new HttpEntity<>(updateDto);

        ResponseEntity<User> response = testRestTemplate.exchange(
                "/api/v1/users/" + savedUserId,
                HttpMethod.PUT,
                requestEntity,
                User.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(uniqueUserName + "_MOD", response.getBody().getUserName());
    }

    @Test
    @Order(11)
    void testDeleteUser() {
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/v1/users/" + savedUserId,
                HttpMethod.DELETE,
                null,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Usuario eliminado correctamente", response.getBody());
    }

    @Test
    @Order(12)
    void testDeleteUser_NotFound() {
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/v1/users/9999",
                HttpMethod.DELETE,
                null,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
