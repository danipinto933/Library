package com.DaniCRUD.fullStackBackend.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.DaniCRUD.fullStackBackend.model.Genre;

@DataJpaTest
public class GenreRepositoryTests {
    @Autowired
    private GenreRepository genreRepository;
    private Genre genre1;
    private Genre genre2;

    @BeforeEach
    void setup() {
        System.out.println("Ejecutando tests desde cero...");
        genre1 = Genre.builder().genreName("Género 1").build();
        genre2 = Genre.builder().genreName("Género 2").build();
    }

    @Test
    void testSaveGenre() {
        Genre savedGenre = genreRepository.save(genre1);

        assertNotNull(savedGenre);
    }

    @Test
    void testFindAllGenres() {
        genreRepository.save(genre1);
        genreRepository.save(genre2);
        List<Genre> genres = genreRepository.findAll();
        assertNotNull(genres);
        assertTrue(genres.size() > 0);
    }

    @Test
    void testFindGenreById() {
        genreRepository.save(genre1);
        genreRepository.save(genre2);

        Genre genre = genreRepository.findById(genre1.getId()).get();

        assertNotNull(genre);
    }

    @Test
    void testFindGenreByGenreName() {
        genreRepository.save(genre1);

        Genre genre = genreRepository.findGenreByGenreName("Género 1");

        assertNotNull(genre);
        assertEquals("Género 1", genre.getGenreName());
    }

    @Test
    void testFindGenreByGenreName_NotFound() {
        Genre genre = genreRepository.findGenreByGenreName("NoExiste");

        assertNull(genre);
    }

    @Test
    void testUpdateGenre() {
        genreRepository.save(genre1);
        genre1.setGenreName("Género actualizado");

        Genre updatedGenre = genreRepository.save(genre1);

        assertNotNull(updatedGenre);
        assertEquals("Género actualizado", updatedGenre.getGenreName());
    }

    @Test
    void testDeleteGenre() {
        genreRepository.save(genre1);

        genreRepository.delete(genre1);

        List<Genre> genres = genreRepository.findAll();
        assertEquals(0, genres.size());
    }
}
