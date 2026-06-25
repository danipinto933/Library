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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.MultiValueMap;

import com.DaniCRUD.fullStackBackend.dto.response.BookDto;
import com.DaniCRUD.fullStackBackend.model.Book;
import com.DaniCRUD.fullStackBackend.model.Genre;
import com.DaniCRUD.fullStackBackend.repository.BookRepository;
import com.DaniCRUD.fullStackBackend.repository.GenreRepository;
import com.DaniCRUD.fullStackBackend.security.SecurityConstants;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureWebTestClient
public class BookControllerWebTestClientTests {

    @Autowired
    private WebTestClient webTestClient;

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
            uniqueTitle = "Book WebClient " + timestamp;
            uniqueIsbn = "ISBN-WEB-" + timestamp;
            genreName = "Genre Web " + timestamp;

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
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("title", uniqueTitle);
        builder.part("isbn", uniqueIsbn);
        builder.part("author", "Autor WebClient");
        builder.part("genres", genreName);
        builder.part("available", true);
        builder.part("file", new NamedByteArrayResource("cover.jpg", "image content".getBytes()))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE);

        MultiValueMap<String, HttpEntity<?>> multipartBody = builder.build();

        Book responseBook = webTestClient.post()
                .uri("/api/v1/books")
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(multipartBody)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Book.class)
                .value(book -> {
                    assertNotNull(book.getId());
                    assertEquals(uniqueTitle, book.getTitle());
                })
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBook);
        savedBookId = responseBook.getId();
    }

    @Test
    @Order(2)
    void testSaveBook_Duplicate() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("title", uniqueTitle); // Duplicado
        builder.part("isbn", "ISBN-OTHER-WEB");
        builder.part("author", "Autor WebClient");
        builder.part("genres", genreName);
        builder.part("available", true);
        builder.part("file", new NamedByteArrayResource("cover.jpg", "image content".getBytes()))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE);

        webTestClient.post()
                .uri("/api/v1/books")
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(builder.build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(3)
    void testSaveBook_ValidationError() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        // Faltan campos para forzar error
        builder.part("isbn", "ISBN-ERROR-WEB");
        builder.part("file", new NamedByteArrayResource("cover.jpg", "image content".getBytes()))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE);

        webTestClient.post()
                .uri("/api/v1/books")
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(builder.build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(4)
    void testFindAllBooks() {
        webTestClient.get()
                .uri("/api/v1/books")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(BookDto.class)
                .value(list -> {
                    assertTrue(list.size() > 0);
                    assertTrue(list.stream().anyMatch(b -> b.getTitle().equals(uniqueTitle)));
                });
    }

    @Test
    @Order(5)
    void testFindByTitle() {
        webTestClient.get()
                .uri("/api/v1/books/1/{bookTitle}", uniqueTitle)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(BookDto.class)
                .value(dto -> {
                    assertEquals(uniqueTitle, dto.getTitle());
                });
    }

    @Test
    @Order(6)
    void testFindByTitle_NotFound() {
        webTestClient.get()
                .uri("/api/v1/books/1/InexistenteTitleWeb")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(7)
    void testFindByIsbn() {
        webTestClient.get()
                .uri("/api/v1/books/2/{bookIsbn}", uniqueIsbn)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(BookDto.class)
                .value(dto -> {
                    assertEquals(uniqueIsbn, dto.getIsbn());
                });
    }

    @Test
    @Order(8)
    void testFindByIsbn_NotFound() {
        webTestClient.get()
                .uri("/api/v1/books/2/ISBN-INEXISTENTE-WEB")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(9)
    void testFindByAuthor() {
        webTestClient.get()
                .uri("/api/v1/books/3/{bookAuthor}", "Autor WebClient")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(BookDto.class)
                .value(list -> {
                    assertTrue(list.size() > 0);
                });
    }

    @Test
    @Order(10)
    void testFindByGenre() {
        webTestClient.get()
                .uri("/api/v1/books/4/{bookGenre}", genreName)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(BookDto.class)
                .value(list -> {
                    assertTrue(list.size() > 0);
                });
    }

    @Test
    @Order(11)
    void testUpdateBook() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("title", uniqueTitle + " Modificado Web");
        builder.part("isbn", uniqueIsbn);
        builder.part("author", "Autor WebClient Modificado");
        builder.part("genres", genreName);
        builder.part("available", false);
        builder.part("file", new NamedByteArrayResource("cover.jpg", "image content updated web".getBytes()))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE);

        webTestClient.put()
                .uri("/api/v1/books/{id}", savedBookId)
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(builder.build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Book.class)
                .value(book -> {
                    assertEquals(uniqueTitle + " Modificado Web", book.getTitle());
                    assertEquals(false, book.isAvailable());
                });
    }

    @Test
    @Order(12)
    void testDeleteBook() {
        webTestClient.delete()
                .uri("/api/v1/books/{id}", savedBookId)
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(res -> {
                    assertEquals("Libro eliminado correctamente", res);
                });
    }

    @Test
    @Order(13)
    void testDeleteBook_NotFound() {
        webTestClient.delete()
                .uri("/api/v1/books/99999")
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token)
                .exchange()
                .expectStatus().isNotFound();
    }
}
