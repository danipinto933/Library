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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.DaniCRUD.fullStackBackend.dto.response.BookDto;
import com.DaniCRUD.fullStackBackend.model.Book;
import com.DaniCRUD.fullStackBackend.model.Genre;
import com.DaniCRUD.fullStackBackend.repository.BookRepository;
import com.DaniCRUD.fullStackBackend.repository.GenreRepository;
import com.DaniCRUD.fullStackBackend.security.SecurityConstants;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTestRestTemplateTests {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private BookRepository bookRepository;

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    private String token;
    private static Long savedBookId;
    private static String uniqueTitle;
    private static String uniqueIsbn;
    private static String genreName;

    // Clase auxiliar para simular la carga de un archivo en las pruebas HTTP
    public static class NamedByteArrayResource extends ByteArrayResource {
        private final String filename;

        public NamedByteArrayResource(String filename, byte[] byteArray) {
            super(byteArray);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return this.filename;
        }
    }

    @BeforeEach
    void setup() {
        if (uniqueTitle == null) {
            long timestamp = System.currentTimeMillis();
            uniqueTitle = "Book RestTemplate " + timestamp;
            uniqueIsbn = "ISBN-REST-" + timestamp;
            genreName = "Genre Rest " + timestamp;

            // Pre-guardamos el género necesario para vincular al libro
            Genre g = Genre.builder().genreName(genreName).build();
            genreRepository.save(g);
        }

        token = JWT.create()
                .withSubject("admin")
                .withClaim("roles", List.of("ROLE_ADMIN"))
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(jwtSecret));
    }

    @Test
    @Order(1)
    void testSaveBook() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("title", uniqueTitle);
        body.add("isbn", uniqueIsbn);
        body.add("author", "Autor RestTemplate");
        body.add("genres", genreName);
        body.add("available", true);

        NamedByteArrayResource fileResource = new NamedByteArrayResource("cover.jpg", "image content".getBytes());
        body.add("file", fileResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Book> response = testRestTemplate.exchange(
                "/api/v1/books",
                HttpMethod.POST,
                requestEntity,
                Book.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals(uniqueTitle, response.getBody().getTitle());
        
        savedBookId = response.getBody().getId();
    }

    @Test
    @Order(2)
    void testSaveBook_Duplicate() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("title", uniqueTitle); // Título duplicado
        body.add("isbn", "ISBN-OTHER");
        body.add("author", "Autor");
        body.add("genres", genreName);
        body.add("available", true);

        NamedByteArrayResource fileResource = new NamedByteArrayResource("cover.jpg", "image content".getBytes());
        body.add("file", fileResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/v1/books",
                HttpMethod.POST,
                requestEntity,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(3)
    void testSaveBook_ValidationError() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        // Falta título y autor para forzar el error de validación
        body.add("isbn", "ISBN-ERROR");
        body.add("genres", genreName);

        NamedByteArrayResource fileResource = new NamedByteArrayResource("cover.jpg", "image content".getBytes());
        body.add("file", fileResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/v1/books",
                HttpMethod.POST,
                requestEntity,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(4)
    void testFindAllBooks() {
        ResponseEntity<List<BookDto>> response = testRestTemplate.exchange(
                "/api/v1/books",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<BookDto>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().size() > 0);
        assertTrue(response.getBody().stream().anyMatch(b -> b.getTitle().equals(uniqueTitle)));
    }

    @Test
    @Order(5)
    void testFindByTitle() {
        ResponseEntity<BookDto> response = testRestTemplate.exchange(
                "/api/v1/books/1/" + uniqueTitle,
                HttpMethod.GET,
                null,
                BookDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(uniqueTitle, response.getBody().getTitle());
    }

    @Test
    @Order(6)
    void testFindByTitle_NotFound() {
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/v1/books/1/TituloInexistente",
                HttpMethod.GET,
                null,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(7)
    void testFindByIsbn() {
        ResponseEntity<BookDto> response = testRestTemplate.exchange(
                "/api/v1/books/2/" + uniqueIsbn,
                HttpMethod.GET,
                null,
                BookDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(uniqueIsbn, response.getBody().getIsbn());
    }

    @Test
    @Order(8)
    void testFindByIsbn_NotFound() {
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/v1/books/2/ISBN-INEXISTENTE",
                HttpMethod.GET,
                null,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(9)
    void testFindByAuthor() {
        ResponseEntity<List<BookDto>> response = testRestTemplate.exchange(
                "/api/v1/books/3/Autor RestTemplate",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<BookDto>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().size() > 0);
    }

    @Test
    @Order(10)
    void testFindByGenre() {
        ResponseEntity<List<BookDto>> response = testRestTemplate.exchange(
                "/api/v1/books/4/" + genreName,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<BookDto>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().size() > 0);
    }

    @Test
    @Order(11)
    void testUpdateBook() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("title", uniqueTitle + " Modificado");
        body.add("isbn", uniqueIsbn);
        body.add("author", "Autor RestTemplate Modificado");
        body.add("genres", genreName);
        body.add("available", false);

        NamedByteArrayResource fileResource = new NamedByteArrayResource("cover.jpg", "image content updated".getBytes());
        body.add("file", fileResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Book> response = testRestTemplate.exchange(
                "/api/v1/books/" + savedBookId,
                HttpMethod.PUT,
                requestEntity,
                Book.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(uniqueTitle + " Modificado", response.getBody().getTitle());
        assertEquals(false, response.getBody().isAvailable());
    }

    @Test
    @Order(12)
    void testDeleteBook() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/v1/books/" + savedBookId,
                HttpMethod.DELETE,
                requestEntity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Libro eliminado correctamente", response.getBody());
    }

    @Test
    @Order(13)
    void testDeleteBook_NotFound() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/v1/books/9999",
                HttpMethod.DELETE,
                requestEntity,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
