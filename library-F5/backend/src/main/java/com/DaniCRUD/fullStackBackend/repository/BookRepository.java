package com.DaniCRUD.fullStackBackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.DaniCRUD.fullStackBackend.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>
{
    public Book findByTitle (String bookTitle);
    public Book findByIsbn (String bookISBN);
    public List<Book> findByAuthor(String bookAuthor);
    public List<Book> findByGenres_GenreName(String genreName);

}