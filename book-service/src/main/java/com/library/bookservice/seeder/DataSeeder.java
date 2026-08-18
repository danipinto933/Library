package com.library.bookservice.seeder;

import com.library.bookservice.model.Book;
import com.library.bookservice.model.FileData;
import com.library.bookservice.model.Genre;
import com.library.bookservice.repository.BookRepository;
import com.library.bookservice.repository.FileDataRepository;
import com.library.bookservice.repository.GenreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final GenreRepository genreRepository;
    private final BookRepository bookRepository;
    private final FileDataRepository fileDataRepository;

    public DataSeeder(GenreRepository genreRepository, BookRepository bookRepository, FileDataRepository fileDataRepository) {
        this.genreRepository = genreRepository;
        this.bookRepository = bookRepository;
        this.fileDataRepository = fileDataRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Starting DataSeeder execution...");

        // 1. Ensure all genres exist
        Genre sciFi = getOrCreateGenre("Ciencia Ficción");
        Genre fantasy = getOrCreateGenre("Fantasía");
        Genre horror = getOrCreateGenre("Terror");
        Genre adventure = getOrCreateGenre("Aventura");
        Genre classics = getOrCreateGenre("Clásicos");

        // Cleanup legacy placeholder book if present
        Book legacyBook = bookRepository.findByTitleIgnoreCase("El Señor de los Anillos");
        if (legacyBook != null) {
            bookRepository.delete(legacyBook);
            fileDataRepository.findByName("el-senor-de-los-anillos.jpg").ifPresent(fileDataRepository::delete);
            log.info("Removed legacy placeholder book 'El Señor de los Anillos'");
        }

        // 2. Seed Books and FileData idempotently
        seedBook("1984", "9780451524935", "George Orwell", "1984.jpg", genresOf(sciFi), true);
        seedBook("Amanecer", "9788420473543", "Stephenie Meyer", "amanecer.jpg", genresOf(fantasy), true);
        seedBook("Crepúsculo", "9788420469287", "Stephenie Meyer", "crepusculo.jpg", genresOf(fantasy), true);
        seedBook("Dune", "9780441172719", "Frank Herbert", "dune.jpg", genresOf(sciFi), true);
        seedBook("Eclipse", "9788420472140", "Stephenie Meyer", "eclipse.jpg", genresOf(fantasy), true);
        seedBook("El Buscón", "9788437601618", "Francisco de Quevedo", "el-buscon.jpg", genresOf(classics, adventure), true);
        seedBook("El Cuervo", "9788491052173", "Edgar Allan Poe", "el-cuervo.jpg", genresOf(horror), true);
        seedBook("El Resplandor", "9788497593724", "Stephen King", "el-resplandor.jpg", genresOf(horror), true);
        seedBook("El Señor de los Anillos: El Retorno del Rey", "9788445071786", "J.R.R. Tolkien", "el-retorno-del-rey.jpg", genresOf(fantasy, adventure), true);
        seedBook("Fahrenheit 451", "9788497596671", "Ray Bradbury", "fahrenheit-451.jpg", genresOf(sciFi), true);
        seedBook("Fundación", "9780553293357", "Isaac Asimov", "fundacion.jpg", genresOf(sciFi), true);
        seedBook("El Ingenioso Caballero Don Quijote de la Mancha", "9788424116491", "Miguel de Cervantes", "ingenioso-caballero.jpg", genresOf(classics, adventure), true);
        seedBook("El Ingenioso Hidalgo Don Quijote de la Mancha", "9788420412146", "Miguel de Cervantes", "ingenioso-hidalgo.jpg", genresOf(classics, adventure), true);
        seedBook("El Señor de los Anillos: La Comunidad del Anillo", "9788445071762", "J.R.R. Tolkien", "la-comunidad-del-anillo.jpg", genresOf(fantasy, adventure), true);
        seedBook("La llamada de Cthulhu", "9788415618843", "H.P. Lovecraft", "la-llamada-de-cthulhu.jpg", genresOf(horror), true);
        seedBook("La vida es sueño", "9788437600922", "Pedro Calderón de la Barca", "la-vida-es-sueño.jpg", genresOf(classics), true);
        seedBook("El Señor de los Anillos: Las Dos Torres", "9788445071779", "J.R.R. Tolkien", "las-dos-torres.jpg", genresOf(fantasy, adventure), true);
        seedBook("Luna nueva", "9788420469614", "Stephenie Meyer", "luna-nueva.jpg", genresOf(fantasy), true);
        seedBook("Rebelión en la granja", "9788499890951", "George Orwell", "rebelion-en-la-granja.jpg", genresOf(sciFi, classics), true);

        log.info("DataSeeder completed successfully. Total books in database: {}", bookRepository.count());
    }

    private Set<Genre> genresOf(Genre... genres) {
        return new HashSet<>(Arrays.asList(genres));
    }

    private Genre getOrCreateGenre(String genreName) {
        Genre genre = genreRepository.findGenreByGenreName(genreName);
        if (genre == null) {
            genre = genreRepository.save(new Genre(null, genreName));
            log.info("Created new genre: {}", genreName);
        }
        return genre;
    }

    private FileData getOrCreateFileData(String filename) {
        String path = "uploads/covers/" + filename;
        return fileDataRepository.findByName(filename)
                .orElseGet(() -> fileDataRepository.save(
                        FileData.builder()
                                .name(filename)
                                .type("image/jpeg")
                                .filePath(path)
                                .build()
                ));
    }

    private void seedBook(String title, String isbn, String author, String coverFilename, Set<Genre> genres, boolean available) {
        FileData image = getOrCreateFileData(coverFilename);
        
        Book existingBook = bookRepository.findByTitleIgnoreCase(title);
        if (existingBook == null) {
            existingBook = bookRepository.findByIsbn(isbn);
        }

        if (existingBook == null) {
            Book newBook = Book.builder()
                    .title(title)
                    .isbn(isbn)
                    .author(author)
                    .image(image)
                    .genres(genres)
                    .available(available)
                    .build();
            bookRepository.save(newBook);
            log.info("Seeded new book: {}", title);
        } else {
            existingBook.setTitle(title);
            existingBook.setIsbn(isbn);
            existingBook.setAuthor(author);
            existingBook.setImage(image);
            existingBook.setGenres(genres);
            existingBook.setAvailable(available);
            bookRepository.save(existingBook);
            log.info("Updated existing book: {}", title);
        }
    }
}




