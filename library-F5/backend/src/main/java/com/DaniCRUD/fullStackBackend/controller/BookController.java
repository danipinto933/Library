package com.DaniCRUD.fullStackBackend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.DaniCRUD.fullStackBackend.dto.response.BookDto;
import com.DaniCRUD.fullStackBackend.model.Book;
import com.DaniCRUD.fullStackBackend.service.BookService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/v1/books")
public class BookController
{
    private BookService bookService;

    public BookController (BookService bookService)
    {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<Book> addBook(@Valid @ModelAttribute BookDto bookDto, @RequestParam MultipartFile file)
    {
        return bookService.addBook(bookDto, file);
    }

    @GetMapping
    public ResponseEntity<List<BookDto>> findAllBooks()
    {
        return bookService.findAllBooks();
    }

    @GetMapping("/1/{bookTitle}")
    public ResponseEntity<BookDto> findByTitle(@PathVariable String bookTitle)
    {
        return bookService.findByTitle(bookTitle);
    }

    @GetMapping("/2/{bookIsbn}")
    public ResponseEntity<BookDto> findByIsbn(@PathVariable String bookIsbn)
    {
        return bookService.findByIsbn(bookIsbn);
    }

    @GetMapping("/3/{bookAuthor}")
    public ResponseEntity<List<BookDto>> findByAuthor(@PathVariable String bookAuthor)
    {
        return bookService.findByAuthor(bookAuthor);
    }

    @GetMapping("/4/{bookGenre}")
    public ResponseEntity<List<BookDto>> findByGenre(@PathVariable String bookGenre)
    {
        return bookService.findByGenre(bookGenre);
    }

    @GetMapping("/5")
    public ResponseEntity<List<BookDto>> findAllAvailableBooks()
    {
        return bookService.findAllAvailableBooks();
    }

    @GetMapping("/6")
    public ResponseEntity<List<BookDto>> findAllNotAvailableBooks()
    {
        return bookService.findAllNotAvailableBooks();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @Valid @ModelAttribute BookDto updatedBook, @RequestParam(required = false)  MultipartFile file) 
    {
        return bookService.updateBook(id, updatedBook, file);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) 
    {
        return new ResponseEntity<>(bookService.deleteBook(id), HttpStatus.OK);
    }
}
