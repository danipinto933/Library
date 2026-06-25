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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

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
import com.DaniCRUD.fullStackBackend.repository.ReserveRepository;
import com.DaniCRUD.fullStackBackend.repository.RoleRepository;
import com.DaniCRUD.fullStackBackend.repository.UserRepository;
import com.DaniCRUD.fullStackBackend.security.SecurityConstants;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReserveControllerTestRestTemplateTests {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ReserveRepository reserveRepository;

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

            // Aseguramos rol
            Role roleUser = roleRepository.findByRole("USER");
            if (roleUser == null) {
                roleUser = Role.builder().role("USER").build();
                roleRepository.save(roleUser);
            }

            testUser = User.builder()
                    .userName("reserve_user_" + timestamp)
                    .name("Reserve User " + timestamp)
                    .email("reserve_" + timestamp + "@example.com")
                    .password("pass")
                    .role(roleUser)
                    .build();
            userRepository.save(testUser);

            // Aseguramos género
            Genre g = Genre.builder().genreName("Genre Reserve " + timestamp).build();
            genreRepository.save(g);

            // Aseguramos imagen
            FileData img = FileData.builder()
                    .name("img_" + timestamp + ".jpg")
                    .type("image/jpeg")
                    .filePath("uploads/img_" + timestamp + ".jpg")
                    .build();
            fileDataRepository.save(img);

            testBook = Book.builder()
                    .title("Book Reserve " + timestamp)
                    .isbn("ISBN-RES-" + timestamp)
                    .author("Author Reserve")
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
        HttpHeaders headers = new HttpHeaders();
        headers.set(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ReserveDto dto = ReserveDto.builder()
                .user(testUser)
                .books(new HashSet<>(Set.of(testBook)))
                .build();

        HttpEntity<ReserveDto> requestEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<Reserve> response = testRestTemplate.exchange(
                "/api/v1/reserves",
                HttpMethod.POST,
                requestEntity,
                Reserve.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());

        savedReserveId = response.getBody().getId();
    }

    @Test
    @Order(2)
    void testSaveReserve_InvalidDates() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Fecha de devolución anterior a reserva
        ReserveDto dto = ReserveDto.builder()
                .user(testUser)
                .books(new HashSet<>(Set.of(testBook)))
                .reserveDate(today)
                .returnDate(today.minusDays(1))
                .build();

        HttpEntity<ReserveDto> requestEntity = new HttpEntity<>(dto, headers);

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/v1/reserves",
                HttpMethod.POST,
                requestEntity,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(3)
    void testFindAllReserves() {
        ResponseEntity<List<ReserveDto>> response = testRestTemplate.exchange(
                "/api/v1/reserves",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ReserveDto>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().size() > 0);
    }

    @Test
    @Order(4)
    void testFindAllByReserveDate() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<List<ReserveDto>> response = testRestTemplate.exchange(
                "/api/v1/reserves/1/" + today,
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<ReserveDto>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(5)
    void testFindAllByReturnDate() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<List<ReserveDto>> response = testRestTemplate.exchange(
                "/api/v1/reserves/2/" + returnDate,
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<ReserveDto>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(6)
    void testFindAllReservesByUser() {
        ResponseEntity<List<ReserveDto>> response = testRestTemplate.exchange(
                "/api/v1/reserves/3/" + testUser.getId(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ReserveDto>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(7)
    void testFindReserveById() {
        ResponseEntity<ReserveDto> response = testRestTemplate.exchange(
                "/api/v1/reserves/4/" + savedReserveId,
                HttpMethod.GET,
                null,
                ReserveDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(savedReserveId, response.getBody().getId());
    }

    @Test
    @Order(8)
    void testUpdateReserve() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Reserve updateData = Reserve.builder()
                .user(testUser)
                .books(new HashSet<>(Set.of(testBook)))
                .build();

        HttpEntity<Reserve> requestEntity = new HttpEntity<>(updateData, headers);

        ResponseEntity<Reserve> response = testRestTemplate.exchange(
                "/api/v1/reserves/" + savedReserveId,
                HttpMethod.PUT,
                requestEntity,
                Reserve.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isAmpliated());
    }

    @Test
    @Order(9)
    void testDeleteReserve() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/v1/reserves/" + savedReserveId,
                HttpMethod.DELETE,
                requestEntity,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Reserva eliminada correctamente", response.getBody());
    }
}
