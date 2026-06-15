package com.DaniCRUD.fullStackBackend.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.DaniCRUD.fullStackBackend.dto.response.GenreDto;
import com.DaniCRUD.fullStackBackend.model.Genre;

@Service
public interface GenreService
{
    ResponseEntity<Genre> addGenre (GenreDto genreDto);

    ResponseEntity<List<GenreDto>> findAllGenres();
    ResponseEntity<GenreDto> findGenreByGenreName (String genreName);
    Genre findGenreByGenreId(Long id);
    Genre findGenreByName(String genreName);

    ResponseEntity<Genre> updateGenre(Long id, GenreDto genreDto);

    String deleteGenre(Long id);
}
