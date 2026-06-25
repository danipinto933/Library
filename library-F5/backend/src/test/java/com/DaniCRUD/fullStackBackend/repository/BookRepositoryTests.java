package com.DaniCRUD.fullStackBackend.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

@DataJpaTest
public class BookRepositoryTests {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private FileDataRepository fileDataRepository;

    private Genre genre1;
    private Genre genre2;
    private FileData fileData1;
    private FileData fileData2;
    private Book book1;
    private Book book2;

    @BeforeEach
    void setup() {
        System.out.println("Ejecutando tests de repository desde cero...");

        genre1 = Genre.builder().genreName("Ficción").build();
        genre2 = Genre.builder().genreName("Aventura").build();
        genreRepository.save(genre1);
        genreRepository.save(genre2);

        fileData1 = FileData.builder()
                .name("Don_Quijote.jpg")
                .type("image/jpeg")
                .filePath("uploads/Don_Quijote.jpg")
                .build();
        fileData2 = FileData.builder()
                .name("Celestina.jpg")
                .type("image/jpeg")
                .filePath("uploads/Celestina.jpg")
                .build();
        fileDataRepository.save(fileData1);
        fileDataRepository.save(fileData2);

        book1 = Book.builder()
                .title("El Quijote")
                .isbn("978-1234567890")
                .author("Miguel de Cervantes")
                .image(fileData1)
                .genres(new HashSet<>(Set.of(genre1)))
                .available(true)
                .build();

        book2 = Book.builder()
                .title("La Celestina")
                .isbn("978-0987654321")
                .author("Fernando de Rojas")
                .image(fileData2)
                .genres(new HashSet<>(Set.of(genre2)))
                .available(false)
                .build();
    }

    @Test
    void testSaveBook() {
        Book savedBook = bookRepository.save(book1);
        assertNotNull(savedBook);
        assertNotNull(savedBook.getId());
        assertEquals("El Quijote", savedBook.getTitle());
    }

    @Test
    void testFindAllBooks() {
        bookRepository.save(book1);
        bookRepository.save(book2);

        List<Book> books = bookRepository.findAll();
        assertNotNull(books);
        assertEquals(2, books.size());
    }

    @Test
    void testFindBookById() {
        Book savedBook = bookRepository.save(book1);
        Book foundBook = bookRepository.findById(savedBook.getId()).orElse(null);

        assertNotNull(foundBook);
        assertEquals(savedBook.getId(), foundBook.getId());
    }

    @Test
    void testFindByTitle() {
        bookRepository.save(book1);

        Book foundBook = bookRepository.findByTitle("El Quijote");
        assertNotNull(foundBook);
        assertEquals("El Quijote", foundBook.getTitle());
    }

    @Test
    void testFindByTitle_NotFound() {
        Book foundBook = bookRepository.findByTitle("Libro Inexistente");
        assertNull(foundBook);
    }

    @Test
    void testFindByIsbn() {
        bookRepository.save(book1);

        Book foundBook = bookRepository.findByIsbn("978-1234567890");
        assertNotNull(foundBook);
        assertEquals("978-1234567890", foundBook.getIsbn());
    }

    @Test
    void testFindByIsbn_NotFound() {
        Book foundBook = bookRepository.findByIsbn("000-0000000000");
        assertNull(foundBook);
    }

    @Test
    void testFindByAuthor() {
        bookRepository.save(book1);

        List<Book> foundBooks = bookRepository.findByAuthor("Miguel de Cervantes");
        assertNotNull(foundBooks);
        assertEquals(1, foundBooks.size());
        assertEquals("El Quijote", foundBooks.get(0).getTitle());
    }

    @Test
    void testFindByAuthor_NotFound() {
        List<Book> foundBooks = bookRepository.findByAuthor("Autor Inexistente");
        assertTrue(foundBooks.isEmpty());
    }

    @Test
    void testFindByGenre() {
        bookRepository.save(book1);
        bookRepository.save(book2);

        List<Book> foundBooks = bookRepository.findByGenres_GenreName("Ficción");
        assertNotNull(foundBooks);
        assertEquals(1, foundBooks.size());
        assertEquals("El Quijote", foundBooks.get(0).getTitle());
    }

    @Test
    void testFindByGenre_NotFound() {
        List<Book> foundBooks = bookRepository.findByGenres_GenreName("Terror");
        assertTrue(foundBooks.isEmpty());
    }

    @Test
    void testUpdateBook() {
        Book savedBook = bookRepository.save(book1);
        savedBook.setTitle("El Quijote Edición Especial");
        savedBook.setAvailable(false);

        Book updatedBook = bookRepository.save(savedBook);

        assertNotNull(updatedBook);
        assertEquals("El Quijote Edición Especial", updatedBook.getTitle());
        assertEquals(false, updatedBook.isAvailable());
    }

    @Test
    void testDeleteBook() {
        Book savedBook = bookRepository.save(book1);
        assertNotNull(savedBook);

        bookRepository.delete(savedBook);
        Book foundBook = bookRepository.findById(savedBook.getId()).orElse(null);
        assertNull(foundBook);
    }
}
