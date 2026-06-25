package com.DaniCRUD.fullStackBackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.DaniCRUD.fullStackBackend.dto.response.GenreDto;
import com.DaniCRUD.fullStackBackend.exception.ResourceNotFoundException;
import com.DaniCRUD.fullStackBackend.mapper.GenreMapper;
import com.DaniCRUD.fullStackBackend.model.Genre;
import com.DaniCRUD.fullStackBackend.repository.GenreRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class GenreServiceTests {
    @Mock
    private GenreRepository genreRepository;
    @Spy
    private GenreMapper genreMapper;
    @InjectMocks
    private GenreServiceImpl genreServiceImpl;

    private Genre genre1;
    private Genre genre2;

    @BeforeEach
    void setup() {
        System.out.println("Ejecutando tests desde cero...");
        genre1 = Genre.builder().id(1L).genreName("Género 1").build();
        genre2 = Genre.builder().id(2L).genreName("Género 2").build();
    }

    @Test
    void testSaveGenre() {
        given(genreRepository.save(any(Genre.class))).willReturn(genre1);

        GenreDto genreDto = genreMapper.toDto(genre1);
        genreServiceImpl.addGenre(genreDto);

        assertNotNull(genreDto);
    }

    @Test
    void testSaveGenreWithThrowException() {
        given(genreRepository.findGenreByGenreName(genre1.getGenreName())).willReturn(genre1);

        GenreDto genreDto = genreMapper.toDto(genre1);

        assertThrows(ResourceNotFoundException.class, () -> genreServiceImpl.addGenre(genreDto));
        verify(genreRepository, never()).save(any(Genre.class));
    }

    @Test
    void testFindAllGenres() {
        List<Genre> genres = List.of(genre1, genre2);
        given(genreRepository.findAll()).willReturn(genres);

        List<GenreDto> genresDtos = genreServiceImpl.findAllGenres().getBody();

        assertNotNull(genresDtos);
        assertEquals(2, genresDtos.size());
    }

    @Test
    void testFindAllGenresWithEmptyList() {
        given(genreRepository.findAll()).willReturn(Collections.emptyList());

        List<GenreDto> genresDtos = genreServiceImpl.findAllGenres().getBody();

        assertTrue(genresDtos.isEmpty());
    }

    @Test
    void testFindGenreById() {
        given(genreRepository.findById(1L)).willReturn(Optional.of(genre1));

        Genre genre = genreServiceImpl.findGenreByGenreId(1L);

        assertNotNull(genre);
        assertEquals(genre1.getGenreName(), genre.getGenreName());
    }

    @Test
    void testFindGenreByIdWithThrowException() {
        Long id = 1L;
        given(genreRepository.findById(id)).willThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> genreServiceImpl.findGenreByGenreId(id));
    }

    @Test
    void testFindGenreByGenreName() {
        given(genreRepository.findGenreByGenreName("Género 1")).willReturn(genre1);

        ResponseEntity<GenreDto> response = genreServiceImpl.findGenreByGenreName("Género 1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Género 1", response.getBody().getGenreName());
    }

    @Test
    void testFindGenreByGenreNameWithThrowException() {
        given(genreRepository.findGenreByGenreName("NoExiste")).willReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> genreServiceImpl.findGenreByGenreName("NoExiste"));
    }

    @Test
    void testFindGenreByName() {
        given(genreRepository.findGenreByGenreName("Género 1")).willReturn(genre1);

        Genre genre = genreServiceImpl.findGenreByName("Género 1");

        assertNotNull(genre);
        assertEquals("Género 1", genre.getGenreName());
    }

    @Test
    void testFindGenreByName_NotFound() {
        given(genreRepository.findGenreByGenreName("NoExiste")).willReturn(null);

        Genre genre = genreServiceImpl.findGenreByName("NoExiste");

        assertNull(genre);
    }

    @Test
    void testUpdateGenre() {
        given(genreRepository.findById(1L)).willReturn(Optional.of(genre1));
        given(genreRepository.save(any(Genre.class))).willReturn(genre1);

        GenreDto genreDto = genreMapper.toDto(genre1);
        genreServiceImpl.updateGenre(1L, genreDto);

        assertNotNull(genreDto);
    }

    @Test
    void testUpdateGenreWithThrowException() {
        given(genreRepository.findById(1L)).willThrow(ResourceNotFoundException.class);

        GenreDto genreDto = genreMapper.toDto(genre1);

        assertThrows(ResourceNotFoundException.class, () -> genreServiceImpl.updateGenre(1L, genreDto));
    }

    @Test
    void testUpdateGenreWithThrowExceptionWhenGenreNameAlreadyExists() {
        given(genreRepository.findById(1L)).willReturn(Optional.of(genre1));
        given(genreRepository.findGenreByGenreName(any(String.class))).willReturn(genre2);

        GenreDto genreDto = genreMapper.toDto(genre1);
        genreDto.setGenreName("Género de prueba");

        assertThrows(ResourceNotFoundException.class, () -> genreServiceImpl.updateGenre(1L, genreDto));
    }

    @Test
    void testDeleteGenre() {
        given(genreRepository.findById(1L)).willReturn(Optional.of(genre1));

        String result = genreServiceImpl.deleteGenre(1L);

        assertEquals("Género eliminado correctamente", result);
    }

    @Test
    void testDeleteGenreWithThrowException() {
        given(genreRepository.findById(1L)).willThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> genreServiceImpl.deleteGenre(1L));
    }
}
