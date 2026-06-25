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

import com.DaniCRUD.fullStackBackend.dto.response.GenreDto;
import com.DaniCRUD.fullStackBackend.security.SecurityConstants;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureWebTestClient
public class GenreControllerWebTestClientTests {
    @Autowired
    private WebTestClient webTestClient;

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    private String token;

    private static GenreDto savedGenre1;
    private static GenreDto savedGenre2;
    private static String genreName1;
    private static String genreName2;

    @BeforeEach
    void setup() {
        System.out.println("Ejecutando tests desde cero...");
        if (genreName1 == null) {
            genreName1 = "Género Web 1 " + System.currentTimeMillis();
            genreName2 = "Género Web 2 " + System.currentTimeMillis();
        }

        token = JWT.create()
                .withSubject("admin")
                .withClaim("roles", List.of("ROLE_ADMIN"))
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(jwtSecret));
    }

    @Test
    @Order(1)
    void testSaveGenre() {
        GenreDto newGenre = GenreDto.builder().genreName(genreName1).build();

        savedGenre1 = webTestClient.post()
                .uri("/api/v1/genres")
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newGenre)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(GenreDto.class)
                .value(genreDto -> {
                    assertNotNull(genreDto.getId());
                    assertEquals(genreName1, genreDto.getGenreName());
                })
                .returnResult()
                .getResponseBody();
    }

    @Test
    @Order(2)
    void testSaveGenre_DuplicateName() {
        GenreDto duplicateGenre = GenreDto.builder().genreName(genreName1).build();

        webTestClient.post()
                .uri("/api/v1/genres")
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(duplicateGenre)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(3)
    void testSaveGenre_ValidationError() {
        GenreDto invalidDto = GenreDto.builder().genreName(null).build();

        webTestClient.post()
                .uri("/api/v1/genres")
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(4)
    void testFindAllGenres() {
        GenreDto newGenre2 = GenreDto.builder().genreName(genreName2).build();
        savedGenre2 = webTestClient.post()
                .uri("/api/v1/genres")
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newGenre2)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(GenreDto.class)
                .returnResult()
                .getResponseBody();

        webTestClient.get()
                .uri("/api/v1/genres")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(GenreDto.class)
                .value(list -> {
                    assertTrue(list.stream().anyMatch(g -> g.getGenreName().equals(genreName1)));
                    assertTrue(list.stream().anyMatch(g -> g.getGenreName().equals(genreName2)));
                });
    }

    @Test
    @Order(5)
    void testFindGenreByGenreName() {
        webTestClient.get()
                .uri("/api/v1/genres/1/{genreName}", genreName1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(GenreDto.class)
                .value(genreDto -> {
                    assertEquals(savedGenre1.getId(), genreDto.getId());
                    assertEquals(genreName1, genreDto.getGenreName());
                });
    }

    @Test
    @Order(6)
    void testFindGenreByGenreName_NotFound() {
        webTestClient.get()
                .uri("/api/v1/genres/1/{genreName}", "NoExiste_" + System.currentTimeMillis())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(7)
    void testUpdateGenre() {
        String updatedName = "Género Web 1 Modificado " + System.currentTimeMillis();
        GenreDto updateDto = GenreDto.builder().genreName(updatedName).build();

        webTestClient.put()
                .uri("/api/v1/genres/{id}", savedGenre1.getId())
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateDto)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(GenreDto.class)
                .value(genreDto -> {
                    assertEquals(savedGenre1.getId(), genreDto.getId());
                    assertEquals(updatedName, genreDto.getGenreName());
                });

        genreName1 = updatedName;
    }

    @Test
    @Order(8)
    void testUpdateGenre_NotFound() {
        GenreDto updateDto = GenreDto.builder().genreName("Género Web Modificado NotFound").build();

        webTestClient.put()
                .uri("/api/v1/genres/{id}", 999999L)
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateDto)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(9)
    void testUpdateGenre_ValidationError() {
        GenreDto invalidDto = GenreDto.builder().genreName(null).build();

        webTestClient.put()
                .uri("/api/v1/genres/{id}", savedGenre1.getId())
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(10)
    void testDeleteGenre() {
        webTestClient.delete()
                .uri("/api/v1/genres/{id}", savedGenre1.getId())
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @Order(11)
    void testDeleteGenre_NotFound() {
        webTestClient.delete()
                .uri("/api/v1/genres/{id}", 999999L)
                .exchange()
                .expectStatus().isNotFound();
    }

}
