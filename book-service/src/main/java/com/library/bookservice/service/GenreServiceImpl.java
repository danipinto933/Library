package com.library.bookservice.service;

import com.library.bookservice.dto.GenreDto;
import com.library.bookservice.exception.GenreAlreadyExistsException;
import com.library.bookservice.exception.ResourceNotFoundException;
import com.library.bookservice.mapper.GenreMapper;
import com.library.bookservice.model.Genre;
import com.library.bookservice.repository.GenreRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreServiceImpl implements GenreService {
    
    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    public GenreServiceImpl(GenreRepository genreRepository, GenreMapper genreMapper) {
        this.genreRepository = genreRepository;
        this.genreMapper = genreMapper;
    }

    @Override
    public ResponseEntity<Genre> addGenre(GenreDto genreDto) {
        Genre existingGenre = genreRepository.findGenreByGenreName(genreDto.getGenreName());
        if (existingGenre != null) {
            // FASE VII: Ahora lanza la excepción semántica correcta
            throw new GenreAlreadyExistsException("El género con el nombre " + genreDto.getGenreName() + " ya existe");
        }
        Genre genre = genreMapper.toEntity(genreDto);
        genreRepository.save(genre);
        return new ResponseEntity<>(genre, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<GenreDto>> findAllGenres() {
        List<Genre> genres = genreRepository.findAll();
        List<GenreDto> genresDtos = genres.stream()
                .map(genreMapper::toDto)
                .toList();
        return new ResponseEntity<>(genresDtos, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<GenreDto> findGenreByGenreName(String genreName) {
        Genre genre = genreRepository.findGenreByGenreName(genreName);
        if (genre == null) {
            throw new ResourceNotFoundException("Género con el nombre " + genreName + " no encontrado");
        }
        GenreDto genreDto = genreMapper.toDto(genre);
        return new ResponseEntity<>(genreDto, HttpStatus.OK);
    }

    @Override
    public Genre findGenreByGenreId(Long id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Género con el ID " + id + " no encontrado"));
    }

    @Override
    public Genre findGenreByName(String genreName) {
        return genreRepository.findGenreByGenreName(genreName);
    }

    @Override
    public ResponseEntity<Genre> updateGenre(Long id, GenreDto genreDto) {
        Genre oldGenre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El genero no se ha encontrado"));
        
        Genre existingGenre = genreRepository.findGenreByGenreName(genreDto.getGenreName());
        if (existingGenre != null && !id.equals(existingGenre.getId())) {
            throw new GenreAlreadyExistsException("El género con el nombre " + genreDto.getGenreName() + " ya existe");
        }

        genreMapper.updateEntityFromDto(genreDto, oldGenre);
        Genre updatedGenre = genreRepository.save(oldGenre);
        return new ResponseEntity<>(updatedGenre, HttpStatus.OK);
    }

    @Override
    public String deleteGenre(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Género con el ID " + id + " no encontrado"));
        genreRepository.delete(genre);
        return "Género eliminado correctamente";
    }
}
