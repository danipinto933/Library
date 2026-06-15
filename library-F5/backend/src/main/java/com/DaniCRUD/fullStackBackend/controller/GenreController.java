package com.DaniCRUD.fullStackBackend.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.DaniCRUD.fullStackBackend.dto.response.GenreDto;
import com.DaniCRUD.fullStackBackend.model.Genre;
import com.DaniCRUD.fullStackBackend.service.GenreService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/genres")
public class GenreController
{
    private GenreService genreService;

    public GenreController (GenreService genreService)
    {
        this.genreService = genreService;
    }

    @PostMapping("")
    public ResponseEntity<Genre> addGenre(@Valid @RequestBody GenreDto genreDto) //@RequestBody permite interpretar la informacion del cliente en el frontend, que esta deba transformarse en un JSON y lo podra leer
    {
        return genreService.addGenre(genreDto);
    }

    @GetMapping("")
    public ResponseEntity <List<GenreDto>> findAllGenres()
    {
        return genreService.findAllGenres();
    }

    @GetMapping("/1/{genreName}")
    public ResponseEntity <GenreDto> findGenreByGenreName(@PathVariable String genreName) 
    {
        return genreService.findGenreByGenreName(genreName);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Genre> updateGenre(@PathVariable Long id, @RequestBody GenreDto genreDto)
    {
        return genreService.updateGenre(id, genreDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGenre(@PathVariable Long id)
    {
        return new ResponseEntity<>(genreService.deleteGenre(id), HttpStatus.OK);
    }
}
