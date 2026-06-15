package com.DaniCRUD.fullStackBackend.service;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.DaniCRUD.fullStackBackend.dto.response.BookDto;
import com.DaniCRUD.fullStackBackend.model.Book;

@Service
public interface BookService
{
    ResponseEntity<Book> addBook (BookDto bookDto, MultipartFile file);

    ResponseEntity<List<BookDto>> findAllBooks();
    ResponseEntity<List<BookDto>> findAllAvailableBooks();
    ResponseEntity<List<BookDto>> findAllNotAvailableBooks();
    ResponseEntity<BookDto> findByTitle(String bookTitle);
    ResponseEntity<BookDto> findByIsbn(String bookIsbn);
    ResponseEntity<List<BookDto>> findByAuthor(String bookAuthor);
    ResponseEntity<List<BookDto>> findByGenre(String bookGenre);
    Book findBookByid (Long id);

    ResponseEntity<Book> updateBook(Long id, BookDto bookDto, MultipartFile file);

    String deleteBook(Long id);

}
