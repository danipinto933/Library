package com.DaniCRUD.fullStackBackend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.DaniCRUD.fullStackBackend.dto.response.GenreDto;
import com.DaniCRUD.fullStackBackend.exception.ResourceNotFoundException;
import com.DaniCRUD.fullStackBackend.mapper.GenreMapper;
import com.DaniCRUD.fullStackBackend.model.Genre;
import com.DaniCRUD.fullStackBackend.repository.GenreRepository;

@Service
public class GenreServiceImpl implements GenreService {
    private GenreRepository genreRepository;
    private GenreMapper genreMapper;

    public GenreServiceImpl(GenreRepository genreRepository, GenreMapper genreMapper) {
        this.genreRepository = genreRepository;
        this.genreMapper = genreMapper;
    }

    @Override
    public ResponseEntity<Genre> addGenre(GenreDto genreDto) {
        Genre existingGenre = genreRepository.findGenreByGenreName(genreDto.getGenreName());
        if (existingGenre != null) {
            throw new ResourceNotFoundException("El género con el nombre " + genreDto.getGenreName() + " ya existe");
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
            throw new ResourceNotFoundException("Génerop con el nombre " + genreName + " no encontrado");
        }

        GenreDto genreDto = genreMapper.toDto(genre);
        return new ResponseEntity<>(genreDto, HttpStatus.OK);
    }

    @Override
    public Genre findGenreByGenreId(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Género con el ID " + id + " no encontrado"));
        return genre;
    }

    @Override
    public Genre findGenreByName(String genreName) {
        Genre genre = genreRepository.findGenreByGenreName(genreName);
        return genre;
    }

    @Override
    public ResponseEntity<Genre> updateGenre(Long id, GenreDto genreDto) {
        Genre oldGenre = genreRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("El genero no se ha encontrado")); //Obtenemos el usuario antiguo a través del ID
        
        Genre existingGenre = genreRepository.findGenreByGenreName(genreDto.getGenreName());
        if (existingGenre != null && !id.equals(existingGenre.getId())) {
            throw new ResourceNotFoundException("El género con el nombre " + genreDto.getGenreName() + " ya existe");
        }

        genreMapper.updateEntityFromDto(genreDto, oldGenre);
        Genre updatedGenre = genreMapper.toEntity(genreDto);
        updatedGenre = genreRepository.save(oldGenre); // Guardamos en un nuevo usuario los datos del antiguo usuario (ya tiene el nombre cambiado)
        return new ResponseEntity<>(updatedGenre, HttpStatus.OK); // Devolvemos un response con un estado de HTTP
    }

    @Override
    public String deleteGenre(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Género con el ID " + id + " no encontrado"));
        genreRepository.delete(genre);
        return "Género eliminado correctamente";
    }
}
