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

import com.DaniCRUD.fullStackBackend.dto.response.UserDto;
import com.DaniCRUD.fullStackBackend.model.Role;
import com.DaniCRUD.fullStackBackend.model.User;
import com.DaniCRUD.fullStackBackend.repository.RoleRepository;
import com.DaniCRUD.fullStackBackend.repository.UserRepository;
import com.DaniCRUD.fullStackBackend.security.SecurityConstants;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureWebTestClient
public class UserControllerWebTestClientTests {

    @Autowired
    private WebTestClient webTestClient;

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
            uniqueUserName = "user_web_" + timestamp;
            uniqueName = "Name Web " + timestamp;
            uniqueEmail = "web_" + timestamp + "@example.com";
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

        User responseUser = webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(User.class)
                .value(user -> {
                    assertNotNull(user.getId());
                    assertEquals(uniqueUserName, user.getUserName());
                })
                .returnResult()
                .getResponseBody();

        assertNotNull(responseUser);
        savedUserId = responseUser.getId();
    }

    @Test
    @Order(2)
    void testSaveUser_Duplicate() {
        UserDto dto = UserDto.builder()
                .userName(uniqueUserName) // Duplicado
                .name("Other Name Web")
                .email("other_web@example.com")
                .password("password")
                .build();

        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(3)
    void testSaveUser_ValidationError() {
        UserDto invalidDto = UserDto.builder().userName(null).build();

        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(4)
    void testFindAllUsers() {
        webTestClient.get()
                .uri("/api/v1/users")
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(UserDto.class)
                .value(list -> {
                    assertTrue(list.size() > 0);
                    assertTrue(list.stream().anyMatch(u -> u.getUserName().equals(uniqueUserName)));
                });
    }

    @Test
    @Order(5)
    void testFindByUserName() {
        webTestClient.get()
                .uri("/api/v1/users/1/{userName}", uniqueUserName)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(UserDto.class)
                .value(dto -> {
                    assertEquals(uniqueUserName, dto.getUserName());
                });
    }

    @Test
    @Order(6)
    void testFindByUserName_NotFound() {
        webTestClient.get()
                .uri("/api/v1/users/1/UserInexistenteWeb")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(7)
    void testFindByName() {
        webTestClient.get()
                .uri("/api/v1/users/2/{name}", uniqueName)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(UserDto.class)
                .value(dto -> {
                    assertEquals(uniqueName, dto.getName());
                });
    }

    @Test
    @Order(8)
    void testFindByEmail() {
        webTestClient.get()
                .uri("/api/v1/users/3/{email}", uniqueEmail)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(UserDto.class)
                .value(dto -> {
                    assertEquals(uniqueEmail, dto.getEmail());
                });
    }

    @Test
    @Order(9)
    void testFindUsersByRole() {
        webTestClient.get()
                .uri("/api/v1/users/4/USER")
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(UserDto.class)
                .value(list -> {
                    assertTrue(list.size() > 0);
                });
    }

    @Test
    @Order(10)
    void testUpdateUser() {
        UserDto updateDto = UserDto.builder()
                .userName(uniqueUserName + "_MOD_WEB")
                .name(uniqueName + " Mod Web")
                .email(uniqueEmail)
                .password("newpassword123Web")
                .role("USER")
                .build();

        webTestClient.put()
                .uri("/api/v1/users/{id}", savedUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .value(user -> {
                    assertEquals(uniqueUserName + "_MOD_WEB", user.getUserName());
                });
    }

    @Test
    @Order(11)
    void testDeleteUser() {
        webTestClient.delete()
                .uri("/api/v1/users/{id}", savedUserId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(res -> {
                    assertEquals("Usuario eliminado correctamente", res);
                });
    }

    @Test
    @Order(12)
    void testDeleteUser_NotFound() {
        webTestClient.delete()
                .uri("/api/v1/users/99999")
                .exchange()
                .expectStatus().isNotFound();
    }
}
