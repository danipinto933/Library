package com.DaniCRUD.fullStackBackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.DaniCRUD.fullStackBackend.dto.response.BookDto;
import com.DaniCRUD.fullStackBackend.exception.BookAlreadyExistsException;
import com.DaniCRUD.fullStackBackend.exception.ResourceNotFoundException;
import com.DaniCRUD.fullStackBackend.mapper.BookMapper;
import com.DaniCRUD.fullStackBackend.model.Book;
import com.DaniCRUD.fullStackBackend.model.FileData;
import com.DaniCRUD.fullStackBackend.model.Genre;
import com.DaniCRUD.fullStackBackend.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
public class BookServiceTests {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private GenreServiceImpl genreServiceImpl;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private StorageServiceImpl storageServiceImpl;

    @InjectMocks
    private BookServiceImpl bookServiceImpl;

    private Book book1;
    private Book book2;
    private BookDto bookDto1;
    private BookDto bookDto2;
    private Genre genre1;
    private Genre genre2;
    private FileData fileData1;
    private FileData fileData2;
    private MockMultipartFile mockFile1;
    private MockMultipartFile mockFile2;

    @BeforeEach
    void setup() {
        genre1 = Genre.builder().id(1L).genreName("Ficción").build();
        genre2 = Genre.builder().id(2L).genreName("Clasico").build();

        fileData1 = FileData.builder()
                .id(1L)
                .name("Don_Quijote.jpg")
                .type("image/jpeg")
                .filePath("uploads/Don_Quijote.jpg")
                .build();

        fileData2 = FileData.builder()
                .id(1L)
                .name("La_Celestina.jpg")
                .type("image/jpeg")
                .filePath("uploads/Celestina.jpg")
                .build();

        book1 = Book.builder()
                .id(1L)
                .title("El Quijote")
                .isbn("978-1234567890")
                .author("Miguel de Cervantes")
                .image(fileData1)
                .genres(new HashSet<Genre>(Set.of(genre1, genre2)))
                .available(true)
                .build();

        book2 = Book.builder()
                .id(2L)
                .title("La Celestina")
                .isbn("978-0987654321")
                .author("Fernando de Rojas")
                .image(fileData2)
                .genres(new HashSet<Genre>(Set.of(genre1)))
                .available(false)
                .build();

        bookDto1 = BookDto.builder()
                .id(1L)
                .title("El Quijote")
                .isbn("978-1234567890")
                .author("Miguel de Cervantes")
                .image(fileData1)
                .genres(new HashSet<>(Set.of("Ficción", "Clasico")))
                .available(true)
                .build();

        bookDto2 = BookDto.builder()
                .id(2L)
                .title("La Celestina")
                .isbn("978-0987654321")
                .author("Fernando de Rojas")
                .image(fileData2)
                .genres(new HashSet<>(Set.of("Ficción")))
                .available(false)
                .build();

        mockFile1 = new MockMultipartFile("file", "Don_Quijote.jpg", "image/jpeg", "content".getBytes());
    }

    @Test
    void testSaveBook() {
        given(bookMapper.toEntity(any(BookDto.class))).willReturn(book1);
        given(bookRepository.findByTitle(book1.getTitle())).willReturn(null);
        given(bookRepository.findByIsbn(book1.getIsbn())).willReturn(null);
        given(genreServiceImpl.findGenreByGenreId(1L)).willReturn(genre1);
        given(genreServiceImpl.findGenreByGenreId(2L)).willReturn(genre2);
        given(storageServiceImpl.uploadImageToFileSystem(any(MultipartFile.class))).willReturn(fileData1);

        given(bookRepository.save(any(Book.class))).willReturn(book1);

        ResponseEntity<Book> response = bookServiceImpl.addBook(bookDto1, mockFile1);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("El Quijote", response.getBody().getTitle());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void testSaveBook_AlreadyExistsByTitle() {
        given(bookMapper.toEntity(any(BookDto.class))).willReturn(book1);
        given(bookRepository.findByTitle(book1.getTitle())).willReturn(book2);

        assertThrows(BookAlreadyExistsException.class, () -> bookServiceImpl.addBook(bookDto1, mockFile1));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void testSaveBook_AlreadyExistsByIsbn() {
        given(bookMapper.toEntity(any(BookDto.class))).willReturn(book1);
        given(bookRepository.findByTitle(book1.getTitle())).willReturn(null);
        given(bookRepository.findByIsbn(book1.getIsbn())).willReturn(book2);

        assertThrows(BookAlreadyExistsException.class, () -> bookServiceImpl.addBook(bookDto1, mockFile1));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void testSaveBook_GenreNotFound() {
        given(bookMapper.toEntity(any(BookDto.class))).willReturn(book1);
        given(bookRepository.findByTitle(book1.getTitle())).willReturn(null);
        given(bookRepository.findByIsbn(book1.getIsbn())).willReturn(null);
        given(genreServiceImpl.findGenreByGenreId(any(Long.class))).willReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> bookServiceImpl.addBook(bookDto1, mockFile1));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void testFindAllBooks() {
        given(bookRepository.findAll()).willReturn(List.of(book1, book2));
        given(bookMapper.toDto(book1)).willReturn(bookDto1);
        given(bookMapper.toDto(book2)).willReturn(bookDto2);

        ResponseEntity<List<BookDto>> response = bookServiceImpl.findAllBooks();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testFindAllAvailableBooks() {
        given(bookRepository.findAll()).willReturn(List.of(book1, book2));
        given(bookMapper.toDto(book1)).willReturn(bookDto1);

        ResponseEntity<List<BookDto>> response = bookServiceImpl.findAllAvailableBooks();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertTrue(response.getBody().get(0).isAvailable());
    }

    @Test
    void testFindAllNotAvailableBooks() {
        given(bookRepository.findAll()).willReturn(List.of(book1, book2));
        given(bookMapper.toDto(book2)).willReturn(bookDto2);

        ResponseEntity<List<BookDto>> response = bookServiceImpl.findAllNotAvailableBooks();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertTrue(!response.getBody().get(0).isAvailable());
    }

    @Test
    void testFindByTitle() {
        given(bookRepository.findByTitle("El Quijote")).willReturn(book1);
        given(bookMapper.toDto(book1)).willReturn(bookDto1);

        ResponseEntity<BookDto> response = bookServiceImpl.findByTitle("El Quijote");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("El Quijote", response.getBody().getTitle());
    }

    @Test
    void testFindByTitle_NotFound() {
        given(bookRepository.findByTitle("Inexistente")).willReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> bookServiceImpl.findByTitle("Inexistente"));
    }

    @Test
    void testFindByIsbn() {
        given(bookRepository.findByIsbn("978-1234567890")).willReturn(book1);
        given(bookMapper.toDto(book1)).willReturn(bookDto1);

        ResponseEntity<BookDto> response = bookServiceImpl.findByIsbn("978-1234567890");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("978-1234567890", response.getBody().getIsbn());
    }

    @Test
    void testFindByIsbn_NotFound() {
        given(bookRepository.findByIsbn("000-0000000000")).willReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> bookServiceImpl.findByIsbn("000-0000000000"));
    }

    @Test
    void testFindByAuthor() {
        given(bookRepository.findByAuthor("Miguel de Cervantes")).willReturn(List.of(book1));
        given(bookMapper.toDto(book1)).willReturn(bookDto1);

        ResponseEntity<List<BookDto>> response = bookServiceImpl.findByAuthor("Miguel de Cervantes");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testFindByAuthor_NotFound() {
        given(bookRepository.findByAuthor("Inexistente")).willReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> bookServiceImpl.findByAuthor("Inexistente"));
    }

    @Test
    void testFindByGenre() {
        given(bookRepository.findByGenres_GenreName("Ficción")).willReturn(List.of(book1));
        given(bookMapper.toDto(book1)).willReturn(bookDto1);

        ResponseEntity<List<BookDto>> response = bookServiceImpl.findByGenre("Ficción");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testFindByGenre_NotFound() {
        given(bookRepository.findByGenres_GenreName("Terror")).willReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> bookServiceImpl.findByGenre("Terror"));
    }

    @Test
    void testFindBookById() {
        given(bookRepository.findById(1L)).willReturn(Optional.of(book1));

        Book foundBook = bookServiceImpl.findBookByid(1L);

        assertNotNull(foundBook);
        assertEquals("El Quijote", foundBook.getTitle());
    }

    @Test
    void testFindBookById_NotFound() {
        given(bookRepository.findById(999L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookServiceImpl.findBookByid(999L));
    }

    @Test
    void testUpdateBook() {
        given(bookRepository.findById(1L)).willReturn(Optional.of(book1));
        given(genreServiceImpl.findGenreByName("Ficción")).willReturn(genre1);
        given(genreServiceImpl.findGenreByName("Clasico")).willReturn(genre2);
        given(storageServiceImpl.uploadImageToFileSystem(any(MultipartFile.class))).willReturn(fileData1);
        given(bookRepository.save(any(Book.class))).willReturn(book1);

        ResponseEntity<Book> response = bookServiceImpl.updateBook(1L, bookDto1, mockFile1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void testUpdateBook_NotFound() {
        given(bookRepository.findById(999L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookServiceImpl.updateBook(999L, bookDto1, mockFile1));
    }

    @Test
    void testUpdateBook_GenreNotFound() {
        given(bookRepository.findById(1L)).willReturn(Optional.of(book1));
        given(genreServiceImpl.findGenreByName(any(String.class))).willReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> bookServiceImpl.updateBook(1L, bookDto1, mockFile1));
    }

    @Test
    void testDeleteBook() {
        given(bookRepository.findById(1L)).willReturn(Optional.of(book1));

        String result = bookServiceImpl.deleteBook(1L);

        assertEquals("Libro eliminado correctamente", result);
        verify(bookRepository).delete(book1);
    }

    @Test
    void testDeleteBook_NotFound() {
        given(bookRepository.findById(999L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookServiceImpl.deleteBook(999L));
    }
}
