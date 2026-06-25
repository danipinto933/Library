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

import com.DaniCRUD.fullStackBackend.dto.response.GenreDto;
import com.DaniCRUD.fullStackBackend.model.Genre;
import com.DaniCRUD.fullStackBackend.security.SecurityConstants;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GenreControllerTestRestTemplateTests {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    private Genre genre1;
    private GenreDto genreDto1;
    private String token;

    @BeforeEach
    void setup() {
        System.out.println("Ejecutando tests desde cero...");
        String uniqueGenreName = "Género " + System.currentTimeMillis();
        genre1 = Genre.builder().id(1L).genreName(uniqueGenreName).build();
        genreDto1 = GenreDto.builder().id(1L).genreName(uniqueGenreName).build();

        token = JWT.create()
                .withSubject("admin")
                .withClaim("roles", List.of("ROLE_ADMIN"))
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(jwtSecret));
    }

    private Genre createTestGenre(String name) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);

        GenreDto dto = GenreDto.builder().genreName(name).build();
        HttpEntity<GenreDto> requestEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<Genre> response = testRestTemplate.exchange(
                "/api/v1/genres",
                HttpMethod.POST,
                requestEntity,
                Genre.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        return response.getBody();
    }

    @Test
    @Order(1)
    void testSaveGenre() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);

        HttpEntity<GenreDto> requestEntity = new HttpEntity<>(genreDto1, headers);

        ResponseEntity<GenreDto> response = testRestTemplate.exchange(
                "/api/v1/genres",
                HttpMethod.POST,
                requestEntity,
                GenreDto.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(genreDto1.getGenreName(), response.getBody().getGenreName());
    }

    @Test
    @Order(2)
    void testSaveGenre_DuplicateName() {
        String name = "Genre Dup " + System.currentTimeMillis();
        createTestGenre(name);

        HttpHeaders headers = new HttpHeaders();
        headers.set(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        GenreDto duplicateDto = GenreDto.builder().genreName(name).build();
        HttpEntity<GenreDto> requestEntity = new HttpEntity<>(duplicateDto, headers);

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/v1/genres",
                HttpMethod.POST,
                requestEntity,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(3)
    void testSaveGenre_ValidationError() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);

        GenreDto invalidDto = GenreDto.builder().genreName(null).build();
        HttpEntity<GenreDto> requestEntity = new HttpEntity<>(invalidDto, headers);

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/v1/genres",
                HttpMethod.POST,
                requestEntity,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(4)
    void testFindAllGenres() {
        String name1 = "All Genre 1 " + System.currentTimeMillis();
        String name2 = "All Genre 2 " + System.currentTimeMillis();
        Genre g1 = createTestGenre(name1);
        Genre g2 = createTestGenre(name2);

        HttpHeaders headers = new HttpHeaders();
        headers.set(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<List<GenreDto>> response = testRestTemplate.exchange(
                "/api/v1/genres",
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<GenreDto>>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<GenreDto> body = response.getBody();
        assertNotNull(body);

        boolean foundG1 = body.stream().anyMatch(dto -> dto.getGenreName().equals(name1));
        boolean foundG2 = body.stream().anyMatch(dto -> dto.getGenreName().equals(name2));
        assertTrue(foundG1);
        assertTrue(foundG2);
    }

    @Test
    @Order(5)
    void testFindGenreByGenreName() {
        String name = "Find Genre By Name " + System.currentTimeMillis();
        Genre created = createTestGenre(name);

        HttpHeaders headers = new HttpHeaders();
        headers.set(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<GenreDto> response = testRestTemplate.exchange(
                "/api/v1/genres/1/" + name,
                HttpMethod.GET,
                requestEntity,
                GenreDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(created.getId(), response.getBody().getId());
        assertEquals(name, response.getBody().getGenreName());
    }

    @Test
    @Order(6)
    void testFindGenreByGenreName_NotFound() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/v1/genres/1/NonExistentGenre_" + System.currentTimeMillis(),
                HttpMethod.GET,
                requestEntity,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(7)
    void testUpdateGenre() {
        String originalName = "Update Genre " + System.currentTimeMillis();
        Genre created = createTestGenre(originalName);

        String updatedName = "Updated Genre " + System.currentTimeMillis();
        GenreDto updateDto = GenreDto.builder().genreName(updatedName).build();

        HttpHeaders headers = new HttpHeaders();
        headers.set(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        HttpEntity<GenreDto> requestEntity = new HttpEntity<>(updateDto, headers);

        ResponseEntity<Genre> response = testRestTemplate.exchange(
                "/api/v1/genres/" + created.getId(),
                HttpMethod.PUT,
                requestEntity,
                Genre.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(created.getId(), response.getBody().getId());
        assertEquals(updatedName, response.getBody().getGenreName());
    }

    @Test
    @Order(8)
    void testUpdateGenre_NotFound() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        GenreDto updateDto = GenreDto.builder().genreName("New Name").build();
        HttpEntity<GenreDto> requestEntity = new HttpEntity<>(updateDto, headers);

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/v1/genres/999999",
                HttpMethod.PUT,
                requestEntity,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(9)
    void testUpdateGenre_DuplicateName() {
        String name1 = "Dup Update 1 " + System.currentTimeMillis();
        String name2 = "Dup Update 2 " + System.currentTimeMillis();
        Genre created1 = createTestGenre(name1);
        Genre created2 = createTestGenre(name2);

        GenreDto updateDto = GenreDto.builder().genreName(name1).build();

        HttpHeaders headers = new HttpHeaders();
        headers.set(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        HttpEntity<GenreDto> requestEntity = new HttpEntity<>(updateDto, headers);

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/v1/genres/" + created2.getId(),
                HttpMethod.PUT,
                requestEntity,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(10)
    void testDeleteGenre() {
        String name = "Delete Genre " + System.currentTimeMillis();
        Genre created = createTestGenre(name);

        HttpHeaders headers = new HttpHeaders();
        headers.set(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/v1/genres/" + created.getId(),
                HttpMethod.DELETE,
                requestEntity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Género eliminado correctamente", response.getBody());

        ResponseEntity<String> getResponse = testRestTemplate.exchange(
                "/api/v1/genres/1/" + name,
                HttpMethod.GET,
                requestEntity,
                String.class);
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    @Order(11)
    void testDeleteGenre_NotFound() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/v1/genres/999999",
                HttpMethod.DELETE,
                requestEntity,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
