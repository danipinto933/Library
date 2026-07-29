package com.library.bookservice.seeder;

import com.library.bookservice.model.Book;
import com.library.bookservice.model.Genre;
import com.library.bookservice.repository.BookRepository;
import com.library.bookservice.repository.GenreRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private final GenreRepository genreRepository;
    private final BookRepository bookRepository;

    public DataSeeder(GenreRepository genreRepository, BookRepository bookRepository) {
        this.genreRepository = genreRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (genreRepository.count() == 0) {
            genreRepository.save(new Genre(null, "Ciencia Ficción"));
            genreRepository.save(new Genre(null, "Fantasía"));
            genreRepository.save(new Genre(null, "Terror"));
            genreRepository.save(new Genre(null, "Aventura"));
            System.out.println("Genres created successfully.");
        }

        if (bookRepository.count() == 0) {
            Genre sciFi = genreRepository.findGenreByGenreName("Ciencia Ficción");
            Genre fantasy = genreRepository.findGenreByGenreName("Fantasía");

            Book book1 = new Book(null, "Dune", "9780441172719", "Frank Herbert", null, Set.of(sciFi), true);
            Book book2 = new Book(null, "El Señor de los Anillos", "9780261102385", "J.R.R. Tolkien", null, Set.of(fantasy), true);
            Book book3 = new Book(null, "Fundación", "9780553293357", "Isaac Asimov", null, Set.of(sciFi), true);

            bookRepository.save(book1);
            bookRepository.save(book2);
            bookRepository.save(book3);
            System.out.println("Books created successfully.");
        }
    }
}
