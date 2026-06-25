package com.DaniCRUD.fullStackBackend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import com.DaniCRUD.fullStackBackend.dto.response.ReserveDto;
import com.DaniCRUD.fullStackBackend.model.Book;
import com.DaniCRUD.fullStackBackend.model.FileData;
import com.DaniCRUD.fullStackBackend.model.Genre;
import com.DaniCRUD.fullStackBackend.model.Reserve;
import com.DaniCRUD.fullStackBackend.model.Role;
import com.DaniCRUD.fullStackBackend.model.User;
import com.DaniCRUD.fullStackBackend.repository.BookRepository;
import com.DaniCRUD.fullStackBackend.repository.FileDataRepository;
import com.DaniCRUD.fullStackBackend.repository.GenreRepository;
import com.DaniCRUD.fullStackBackend.repository.RoleRepository;
import com.DaniCRUD.fullStackBackend.repository.UserRepository;
import com.DaniCRUD.fullStackBackend.security.SecurityConstants;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureWebTestClient
public class ReserveControllerWebTestClientTests {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private FileDataRepository fileDataRepository;

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    private String token;
    private static Long savedReserveId;
    private static User testUser;
    private static Book testBook;
    private static LocalDate today;
    private static LocalDate returnDate;

    @BeforeEach
    void setup() {
        today = LocalDate.now();
        returnDate = today.plusMonths(1);

        if (testUser == null) {
            long timestamp = System.currentTimeMillis();

            Role roleUser = roleRepository.findByRole("USER");
            if (roleUser == null) {
                roleUser = Role.builder().role("USER").build();
                roleRepository.save(roleUser);
            }

            testUser = User.builder()
                    .userName("reserve_user_web_" + timestamp)
                    .name("Reserve User Web " + timestamp)
                    .email("reserve_web_" + timestamp + "@example.com")
                    .password("pass")
                    .role(roleUser)
                    .build();
            userRepository.save(testUser);

            Genre g = Genre.builder().genreName("Genre Reserve Web " + timestamp).build();
            genreRepository.save(g);

            FileData img = FileData.builder()
                    .name("img_web_" + timestamp + ".jpg")
                    .type("image/jpeg")
                    .filePath("uploads/img_web_" + timestamp + ".jpg")
                    .build();
            fileDataRepository.save(img);

            testBook = Book.builder()
                    .title("Book Reserve Web " + timestamp)
                    .isbn("ISBN-RES-WEB-" + timestamp)
                    .author("Author Reserve Web")
                    .image(img)
                    .genres(new HashSet<>(Set.of(g)))
                    .available(true)
                    .build();
            bookRepository.save(testBook);
        }

        token = JWT.create()
                .withSubject("admin")
                .withClaim("roles", List.of("ROLE_ADMIN"))
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(jwtSecret));
    }

    @Test
    @Order(1)
    void testSaveReserve() {
        ReserveDto dto = ReserveDto.builder()
                .user(testUser)
                .books(new HashSet<>(Set.of(testBook)))
                .build();

        Reserve response = webTestClient.post()
                .uri("/api/v1/reserves")
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Reserve.class)
                .value(res -> {
                    assertNotNull(res.getId());
                    assertEquals(testUser.getId(), res.getUser().getId());
                })
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        savedReserveId = response.getId();
    }

    @Test
    @Order(2)
    void testSaveReserve_InvalidDates() {
        ReserveDto dto = ReserveDto.builder()
                .user(testUser)
                .books(new HashSet<>(Set.of(testBook)))
                .reserveDate(today)
                .returnDate(today.minusDays(1)) // Invalido
                .build();

        webTestClient.post()
                .uri("/api/v1/reserves")
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(3)
    void testFindAllReserves() {
        webTestClient.get()
                .uri("/api/v1/reserves")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(ReserveDto.class)
                .value(list -> {
                    assertTrue(list.size() > 0);
                });
    }

    @Test
    @Order(4)
    void testFindAllByReserveDate() {
        webTestClient.get()
                .uri("/api/v1/reserves/1/{date}", today)
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(ReserveDto.class);
    }

    @Test
    @Order(5)
    void testFindAllByReturnDate() {
        webTestClient.get()
                .uri("/api/v1/reserves/2/{date}", returnDate)
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(ReserveDto.class);
    }

    @Test
    @Order(6)
    void testFindAllReservesByUser() {
        webTestClient.get()
                .uri("/api/v1/reserves/3/{idUser}", testUser.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(ReserveDto.class);
    }

    @Test
    @Order(7)
    void testFindReserveById() {
        webTestClient.get()
                .uri("/api/v1/reserves/4/{reserveId}", savedReserveId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(ReserveDto.class)
                .value(dto -> {
                    assertEquals(savedReserveId, dto.getId());
                });
    }

    @Test
    @Order(8)
    void testUpdateReserve() {
        Reserve updateData = Reserve.builder()
                .user(testUser)
                .books(new HashSet<>(Set.of(testBook)))
                .build();

        webTestClient.put()
                .uri("/api/v1/reserves/{id}", savedReserveId)
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateData)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Reserve.class)
                .value(res -> {
                    assertTrue(res.isAmpliated());
                });
    }

    @Test
    @Order(9)
    void testDeleteReserve() {
        webTestClient.delete()
                .uri("/api/v1/reserves/{id}", savedReserveId)
                .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(res -> {
                    assertEquals("Reserva eliminada correctamente", res);
                });
    }
}
