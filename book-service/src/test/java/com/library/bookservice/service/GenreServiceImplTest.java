package com.library.bookservice.service;

import com.library.bookservice.dto.GenreDto;
import com.library.bookservice.model.Genre;
import com.library.bookservice.repository.GenreRepository;
import com.library.bookservice.mapper.GenreMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mapstruct.factory.Mappers;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreServiceImplTest {

    @Mock
    private GenreRepository genreRepository;

    @Spy
    private GenreMapper genreMapper = Mappers.getMapper(GenreMapper.class);

    @InjectMocks
    private GenreServiceImpl genreService;

    private Genre existingGenre;

    @BeforeEach
    void setUp() {
        existingGenre = Genre.builder()
                .id(1L)
                .genreName("Fantasía")
                .build();
    }

    @Test
    void updateGenre_ShouldPreserveId_AndReturnUpdatedGenre() {
        // Arrange: DTO sem ID explícito no payload (como llega desde el frontend)
        GenreDto updateDto = GenreDto.builder()
                .genreName("Fantasía Épica")
                .build();

        when(genreRepository.findById(1L)).thenReturn(Optional.of(existingGenre));
        when(genreRepository.findGenreByGenreNameIgnoreCase("Fantasía Épica")).thenReturn(null);
        when(genreRepository.save(any(Genre.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ResponseEntity<Genre> response = genreService.updateGenre(1L, updateDto);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId(), "El ID debe ser preservado y no ser nulo");
        assertEquals("Fantasía Épica", response.getBody().getGenreName());
    }
}
