package com.library.bookservice.controller;

import com.library.bookservice.dto.GenreDto;
import com.library.bookservice.model.Genre;
import com.library.bookservice.service.GenreService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/genres")
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @PostMapping("")
    public ResponseEntity<Genre> addGenre(@Valid @RequestBody GenreDto genreDto) {
        return genreService.addGenre(genreDto);
    }

    @GetMapping("")
    public ResponseEntity<List<GenreDto>> findAllGenres() {
        return genreService.findAllGenres();
    }

    @GetMapping("/1/{genreName}")
    public ResponseEntity<GenreDto> findGenreByGenreName(@PathVariable String genreName) {
        return genreService.findGenreByGenreName(genreName);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Genre> updateGenre(@PathVariable Long id, @Valid @RequestBody GenreDto genreDto) {
        return genreService.updateGenre(id, genreDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGenre(@PathVariable Long id) {
        return new ResponseEntity<>(genreService.deleteGenre(id), HttpStatus.OK);
    }
}
