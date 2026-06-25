package com.DaniCRUD.fullStackBackend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import java.util.List;

import com.DaniCRUD.fullStackBackend.model.Genre;
import com.DaniCRUD.fullStackBackend.dto.response.GenreDto;
import com.DaniCRUD.fullStackBackend.service.GenreService;
import com.DaniCRUD.fullStackBackend.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = GenreController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
})
public class GenreControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenreService genreService;

    @Autowired
    private ObjectMapper objectMapper;

    private Genre genre1;
    private GenreDto genreDto1;
    private GenreDto genreDto2;

    @BeforeEach
    void setup() {
        System.out.println("Ejecutando tests desde cero...");
        genre1 = Genre.builder().id(1L).genreName("Género 1").build();
        genreDto1 = GenreDto.builder().id(1L).genreName("Género 1").build();
        genreDto2 = GenreDto.builder().id(2L).genreName("Género 2").build();
    }

    @Test
    void testSaveGenre() throws Exception {
        given(genreService.addGenre(any(GenreDto.class)))
                .willReturn(new ResponseEntity<>(genre1, HttpStatus.CREATED));

        mockMvc.perform(post("/api/v1/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(genreDto1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.genreName").value("Género 1"));
    }

    @Test
    void testSaveGenre_ValidationError() throws Exception {
        GenreDto invalidDto = GenreDto.builder().genreName(null).build();

        mockMvc.perform(post("/api/v1/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSaveGenre_DuplicateName() throws Exception {
        given(genreService.addGenre(any(GenreDto.class)))
                .willThrow(new ResourceNotFoundException("El género con el nombre Género 1 ya existe"));

        mockMvc.perform(post("/api/v1/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(genreDto1)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testFindAllGenres() throws Exception {
        given(genreService.findAllGenres())
                .willReturn(new ResponseEntity<>(List.of(genreDto1, genreDto2), HttpStatus.OK));

        mockMvc.perform(get("/api/v1/genres")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].genreName").value("Género 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].genreName").value("Género 2"));
    }

    @Test
    void testFindGenreByGenreName() throws Exception {
        given(genreService.findGenreByGenreName("Género 1"))
                .willReturn(new ResponseEntity<>(genreDto1, HttpStatus.OK));

        mockMvc.perform(get("/api/v1/genres/1/Género 1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.genreName").value("Género 1"));
    }

    @Test
    void testFindGenreByGenreName_NotFound() throws Exception {
        given(genreService.findGenreByGenreName("NoExiste"))
                .willThrow(new ResourceNotFoundException("Génerop con el nombre NoExiste no encontrado"));

        mockMvc.perform(get("/api/v1/genres/1/NoExiste")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateGenre() throws Exception {
        Genre updatedGenre = Genre.builder().id(1L).genreName("Género 1 Modificado").build();
        GenreDto updateDto = GenreDto.builder().genreName("Género 1 Modificado").build();

        given(genreService.updateGenre(eq(1L), any(GenreDto.class)))
                .willReturn(new ResponseEntity<>(updatedGenre, HttpStatus.OK));

        mockMvc.perform(put("/api/v1/genres/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.genreName").value("Género 1 Modificado"));
    }

    @Test
    void testUpdateGenre_NotFound() throws Exception {
        given(genreService.updateGenre(eq(999L), any(GenreDto.class)))
                .willThrow(new ResourceNotFoundException("El genero no se ha encontrado"));

        mockMvc.perform(put("/api/v1/genres/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(genreDto1)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteGenre() throws Exception {
        given(genreService.deleteGenre(1L))
                .willReturn("Género eliminado correctamente");

        mockMvc.perform(delete("/api/v1/genres/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Género eliminado correctamente"));
    }

    @Test
    void testDeleteGenre_NotFound() throws Exception {
        given(genreService.deleteGenre(999L))
                .willThrow(new ResourceNotFoundException("Género con el ID 999 no encontrado"));

        mockMvc.perform(delete("/api/v1/genres/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
