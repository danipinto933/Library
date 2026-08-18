package com.library.bookservice.repository;

import com.library.bookservice.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Book findByTitle(String title);

    Book findByTitleIgnoreCase(String title);

    List<Book> findByTitleContainingIgnoreCase(String title);

    Book findByIsbn(String isbn);

    Book findByIsbnIgnoreCase(String isbn);

    List<Book> findByAuthor(String author);

    List<Book> findByAuthorIgnoreCase(String author);

    List<Book> findByAuthorContainingIgnoreCase(String author);

    List<Book> findByGenres_GenreName(String genreName);

    List<Book> findByGenres_GenreNameIgnoreCase(String genreName);

    List<Book> findByGenres_GenreNameContainingIgnoreCase(String genreName);
    
    // FASE VII: Añadido para evitar findAll en memoria cuando se filtran disponibles
    List<Book> findByAvailable(boolean available);
}
