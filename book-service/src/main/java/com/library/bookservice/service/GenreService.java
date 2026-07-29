package com.library.bookservice.service;

import com.library.bookservice.dto.GenreDto;
import com.library.bookservice.model.Genre;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface GenreService {
    ResponseEntity<Genre> addGenre(GenreDto genreDto);
    ResponseEntity<List<GenreDto>> findAllGenres();
    ResponseEntity<GenreDto> findGenreByGenreName(String genreName);
    Genre findGenreByGenreId(Long id);
    Genre findGenreByName(String genreName);
    ResponseEntity<Genre> updateGenre(Long id, GenreDto genreDto);
    String deleteGenre(Long id);
}
