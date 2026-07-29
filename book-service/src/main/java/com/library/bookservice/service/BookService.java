package com.library.bookservice.service;

import com.library.bookservice.dto.BookDto;
import com.library.bookservice.model.Book;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BookService {
    ResponseEntity<Book> addBook(BookDto bookDto, MultipartFile file);
    ResponseEntity<List<BookDto>> findAllBooks();
    ResponseEntity<List<BookDto>> findAllAvailableBooks();
    ResponseEntity<List<BookDto>> findAllNotAvailableBooks();
    ResponseEntity<BookDto> findByTitle(String bookTitle);
    ResponseEntity<BookDto> findByIdDto(Long id);
    ResponseEntity<BookDto> findByIsbn(String bookIsbn);
    ResponseEntity<List<BookDto>> findByAuthor(String bookAuthor);
    ResponseEntity<List<BookDto>> findByGenre(String bookGenre);
    Book findBookByid(Long id);
    ResponseEntity<Book> updateBook(Long id, BookDto bookDto, MultipartFile file);
    String deleteBook(Long id);
}
