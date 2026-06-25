package com.DaniCRUD.fullStackBackend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import com.DaniCRUD.fullStackBackend.dto.response.BookDto;
import com.DaniCRUD.fullStackBackend.exception.BookAlreadyExistsException;
import com.DaniCRUD.fullStackBackend.exception.ResourceNotFoundException;
import com.DaniCRUD.fullStackBackend.model.Book;
import com.DaniCRUD.fullStackBackend.model.FileData;
import com.DaniCRUD.fullStackBackend.service.BookService;

@WebMvcTest(controllers = BookController.class, excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
})
public class BookControllerTests {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private BookService bookService;

        private Book book1;
        private BookDto bookDto1;
        private BookDto bookDto2;
        private MockMultipartFile mockFile;

        @BeforeEach
        void setup() {
                FileData fileData = FileData.builder()
                                .id(1L)
                                .name("cover.jpg")
                                .type("image/jpeg")
                                .filePath("uploads/El_Quijote.jpg")
                                .build();

                book1 = Book.builder()
                                .id(1L)
                                .title("El Quijote")
                                .isbn("978-1234567890")
                                .author("Miguel de Cervantes")
                                .image(fileData)
                                .available(true)
                                .build();

                bookDto1 = BookDto.builder()
                                .id(1L)
                                .title("El Quijote")
                                .isbn("978-1234567890")
                                .author("Miguel de Cervantes")
                                .image(fileData)
                                .genres(new HashSet<>(Set.of("Ficción")))
                                .available(true)
                                .build();

                bookDto2 = BookDto.builder()
                                .id(2L)
                                .title("La Odisea")
                                .isbn("978-0987654321")
                                .author("Homero")
                                .image(fileData)
                                .genres(new HashSet<>(Set.of("Aventura")))
                                .available(false)
                                .build();

                mockFile = new MockMultipartFile("file", "El_Quijote.jpg", "image/jpeg", "content".getBytes());
        }

        @Test
        void testSaveBook() throws Exception {
                given(bookService.addBook(any(BookDto.class), any(MultipartFile.class)))
                                .willReturn(new ResponseEntity<>(book1, HttpStatus.CREATED));

                mockMvc.perform(multipart("/api/v1/books")
                                .file(mockFile)
                                .param("title", "El Quijote")
                                .param("isbn", "978-1234567890")
                                .param("author", "Miguel de Cervantes")
                                .param("genres", "Ficción")
                                .param("available", "true"))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.title").value("El Quijote"));
        }

        @Test
        void testSaveBook_ValidationError() throws Exception {
                // Enviar un título vacío para forzar un error de validación
                mockMvc.perform(multipart("/api/v1/books")
                                .file(mockFile)
                                .param("isbn", "978-1234567890")
                                .param("author", "Miguel de Cervantes"))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void testSaveBook_DuplicateBook() throws Exception {
                given(bookService.addBook(any(BookDto.class), any(MultipartFile.class)))
                                .willThrow(new BookAlreadyExistsException("El libro ya existe"));

                mockMvc.perform(multipart("/api/v1/books")
                                .file(mockFile)
                                .param("title", "El Quijote")
                                .param("isbn", "978-1234567890")
                                .param("author", "Miguel de Cervantes"))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void testFindAllBooks() throws Exception {
                given(bookService.findAllBooks())
                                .willReturn(new ResponseEntity<>(List.of(bookDto1, bookDto2), HttpStatus.OK));

                mockMvc.perform(get("/api/v1/books"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.size()").value(2))
                                .andExpect(jsonPath("$[0].title").value("El Quijote"))
                                .andExpect(jsonPath("$[1].title").value("La Odisea"));
        }

        @Test
        void testFindByTitle() throws Exception {
                given(bookService.findByTitle("El Quijote"))
                                .willReturn(new ResponseEntity<>(bookDto1, HttpStatus.OK));

                mockMvc.perform(get("/api/v1/books/1/El Quijote"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.title").value("El Quijote"));
        }

        @Test
        void testFindByTitle_NotFound() throws Exception {
                given(bookService.findByTitle("Inexistente"))
                                .willThrow(new ResourceNotFoundException("Libro no encontrado"));

                mockMvc.perform(get("/api/v1/books/1/Inexistente"))
                                .andExpect(status().isNotFound());
        }

        @Test
        void testFindByIsbn() throws Exception {
                given(bookService.findByIsbn("978-1234567890"))
                                .willReturn(new ResponseEntity<>(bookDto1, HttpStatus.OK));

                mockMvc.perform(get("/api/v1/books/2/978-1234567890"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.isbn").value("978-1234567890"));
        }

        @Test
        void testFindByIsbn_NotFound() throws Exception {
                given(bookService.findByIsbn("0000"))
                                .willThrow(new ResourceNotFoundException("Libro no encontrado"));

                mockMvc.perform(get("/api/v1/books/2/0000"))
                                .andExpect(status().isNotFound());
        }

        @Test
        void testFindByAuthor() throws Exception {
                given(bookService.findByAuthor("Miguel de Cervantes"))
                                .willReturn(new ResponseEntity<>(List.of(bookDto1), HttpStatus.OK));

                mockMvc.perform(get("/api/v1/books/3/Miguel de Cervantes"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.size()").value(1))
                                .andExpect(jsonPath("$[0].author").value("Miguel de Cervantes"));
        }

        @Test
        void testFindByAuthor_NotFound() throws Exception {
                given(bookService.findByAuthor("Inexistente"))
                                .willThrow(new ResourceNotFoundException("Libros no encontrados"));

                mockMvc.perform(get("/api/v1/books/3/Inexistente"))
                                .andExpect(status().isNotFound());
        }

        @Test
        void testFindByGenre() throws Exception {
                given(bookService.findByGenre("Ficción"))
                                .willReturn(new ResponseEntity<>(List.of(bookDto1), HttpStatus.OK));

                mockMvc.perform(get("/api/v1/books/4/Ficción"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.size()").value(1));
        }

        @Test
        void testFindByGenre_NotFound() throws Exception {
                given(bookService.findByGenre("Terror"))
                                .willThrow(new ResourceNotFoundException("Libros no encontrados"));

                mockMvc.perform(get("/api/v1/books/4/Terror"))
                                .andExpect(status().isNotFound());
        }

        @Test
        void testFindAllAvailableBooks() throws Exception {
                given(bookService.findAllAvailableBooks())
                                .willReturn(new ResponseEntity<>(List.of(bookDto1), HttpStatus.OK));

                mockMvc.perform(get("/api/v1/books/5"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.size()").value(1))
                                .andExpect(jsonPath("$[0].available").value(true));
        }

        @Test
        void testFindAllNotAvailableBooks() throws Exception {
                given(bookService.findAllNotAvailableBooks())
                                .willReturn(new ResponseEntity<>(List.of(bookDto2), HttpStatus.OK));

                mockMvc.perform(get("/api/v1/books/6"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.size()").value(1))
                                .andExpect(jsonPath("$[0].available").value(false));
        }

        @Test
        void testUpdateBook() throws Exception {
                given(bookService.updateBook(eq(1L), any(BookDto.class), any(MultipartFile.class)))
                                .willReturn(new ResponseEntity<>(book1, HttpStatus.OK));

                // Para simular PUT con multipart en MockMvc:
                mockMvc.perform(multipart("/api/v1/books/1")
                                .file(mockFile)
                                .with(request -> {
                                        request.setMethod("PUT");
                                        return request;
                                })
                                .param("title", "El Quijote Modificado")
                                .param("isbn", "978-1234567890")
                                .param("author", "Miguel de Cervantes")
                                .param("available", "true"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1L));
        }

        @Test
        void testUpdateBook_NotFound() throws Exception {
                given(bookService.updateBook(eq(999L), any(BookDto.class), any(MultipartFile.class)))
                                .willThrow(new ResourceNotFoundException("Libro no encontrado"));

                mockMvc.perform(multipart("/api/v1/books/999")
                                .file(mockFile)
                                .with(request -> {
                                        request.setMethod("PUT");
                                        return request;
                                })
                                .param("title", "El Quijote Modificado")
                                .param("isbn", "978-1234567890")
                                .param("author", "Miguel de Cervantes"))
                                .andExpect(status().isNotFound());
        }

        @Test
        void testDeleteBook() throws Exception {
                given(bookService.deleteBook(1L))
                                .willReturn("Libro eliminado correctamente");

                mockMvc.perform(delete("/api/v1/books/1"))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Libro eliminado correctamente"));
        }

        @Test
        void testDeleteBook_NotFound() throws Exception {
                given(bookService.deleteBook(999L))
                                .willThrow(new ResourceNotFoundException("Libro no encontrado"));

                mockMvc.perform(delete("/api/v1/books/999"))
                                .andExpect(status().isNotFound());
        }
}
