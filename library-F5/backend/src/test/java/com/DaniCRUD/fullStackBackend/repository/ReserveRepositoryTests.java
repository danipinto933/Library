package com.DaniCRUD.fullStackBackend.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.DaniCRUD.fullStackBackend.model.Book;
import com.DaniCRUD.fullStackBackend.model.FileData;
import com.DaniCRUD.fullStackBackend.model.Genre;
import com.DaniCRUD.fullStackBackend.model.Reserve;
import com.DaniCRUD.fullStackBackend.model.Role;
import com.DaniCRUD.fullStackBackend.model.User;

@DataJpaTest
public class ReserveRepositoryTests {

    @Autowired
    private ReserveRepository reserveRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private FileDataRepository fileDataRepository;

    @Autowired
    private GenreRepository genreRepository;

    private User user1;
    private Book book1;
    private Reserve reserve1;
    private LocalDate today;
    private LocalDate returnDate;

    @BeforeEach
    void setup() {
        System.out.println("Ejecutando tests de repository de Reserve desde cero...");

        today = LocalDate.now();
        returnDate = today.plusMonths(1);

        Role role1 = Role.builder().role("ROLE_USER").build();
        roleRepository.save(role1);

        user1 = User.builder()
                .userName("user1")
                .name("User One")
                .email("user1@example.com")
                .password("pass")
                .role(role1)
                .build();
        userRepository.save(user1);

        Genre genre1 = Genre.builder().genreName("Ficción").build();
        genreRepository.save(genre1);

        FileData fileData1 = FileData.builder()
                .name("cover.jpg")
                .type("image/jpeg")
                .filePath("uploads/cover.jpg")
                .build();
        fileDataRepository.save(fileData1);

        book1 = Book.builder()
                .title("El Quijote")
                .isbn("978-1234567890")
                .author("Cervantes")
                .image(fileData1)
                .genres(new HashSet<>(Set.of(genre1)))
                .available(true)
                .build();
        bookRepository.save(book1);

        reserve1 = Reserve.builder()
                .reserveDate(today)
                .returnDate(returnDate)
                .user(user1)
                .books(new HashSet<>(Set.of(book1)))
                .ampliated(false)
                .build();
    }

    @Test
    void testSaveReserve() {
        Reserve savedReserve = reserveRepository.save(reserve1);
        assertNotNull(savedReserve);
        assertNotNull(savedReserve.getId());
        assertEquals(user1.getId(), savedReserve.getUser().getId());
    }

    @Test
    void testFindAllReserves() {
        reserveRepository.save(reserve1);

        List<Reserve> reserves = reserveRepository.findAll();
        assertNotNull(reserves);
        assertTrue(reserves.size() > 0);
    }

    @Test
    void testFindReserveById() {
        Reserve savedReserve = reserveRepository.save(reserve1);
        Reserve foundReserve = reserveRepository.findById(savedReserve.getId()).orElse(null);

        assertNotNull(foundReserve);
        assertEquals(savedReserve.getId(), foundReserve.getId());
    }

    @Test
    void testFindAllByReserveDate() {
        reserveRepository.save(reserve1);

        List<Reserve> reserves = reserveRepository.findAllByReserveDate(today);
        assertNotNull(reserves);
        assertTrue(reserves.size() > 0);
        assertEquals(today, reserves.get(0).getReserveDate());
    }

    @Test
    void testFindAllByReturnDate() {
        reserveRepository.save(reserve1);

        List<Reserve> reserves = reserveRepository.findAllByReturnDate(returnDate);
        assertNotNull(reserves);
        assertTrue(reserves.size() > 0);
        assertEquals(returnDate, reserves.get(0).getReturnDate());
    }

    @Test
    void testFindByUser_Id() {
        reserveRepository.save(reserve1);

        List<Reserve> reserves = reserveRepository.findByUser_Id(user1.getId());
        assertNotNull(reserves);
        assertTrue(reserves.size() > 0);
        assertEquals(user1.getId(), reserves.get(0).getUser().getId());
    }

    @Test
    void testFindAllByUser() {
        reserveRepository.save(reserve1);

        List<Reserve> reserves = reserveRepository.findAllByUser(user1);
        assertNotNull(reserves);
        assertTrue(reserves.size() > 0);
        assertEquals(user1.getId(), reserves.get(0).getUser().getId());
    }

    @Test
    void testDeleteReserve() {
        Reserve savedReserve = reserveRepository.save(reserve1);
        assertNotNull(savedReserve);

        reserveRepository.delete(savedReserve);
        Reserve foundReserve = reserveRepository.findById(savedReserve.getId()).orElse(null);
        assertNull(foundReserve);
    }
}
