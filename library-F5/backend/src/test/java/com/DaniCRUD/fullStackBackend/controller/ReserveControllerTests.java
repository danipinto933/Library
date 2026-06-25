package com.DaniCRUD.fullStackBackend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import com.DaniCRUD.fullStackBackend.dto.response.ReserveDto;
import com.DaniCRUD.fullStackBackend.exception.ResourceNotFoundException;
import com.DaniCRUD.fullStackBackend.model.Book;
import com.DaniCRUD.fullStackBackend.model.Reserve;
import com.DaniCRUD.fullStackBackend.model.User;
import com.DaniCRUD.fullStackBackend.service.ReserveService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = ReserveController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
})
public class ReserveControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReserveService reserveService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user1;
    private Book book1;
    private Reserve reserve1;
    private ReserveDto reserveDto1;
    private LocalDate today;
    private LocalDate returnDate;

    @BeforeEach
    void setup() {
        today = LocalDate.now();
        returnDate = today.plusMonths(1);

        user1 = User.builder().id(1L).userName("user1").build();
        book1 = Book.builder().id(1L).title("El Quijote").build();

        reserve1 = Reserve.builder()
                .id(1L)
                .reserveDate(today)
                .returnDate(returnDate)
                .user(user1)
                .books(new HashSet<>(Set.of(book1)))
                .build();

        reserveDto1 = ReserveDto.builder()
                .id(1L)
                .reserveDate(today)
                .returnDate(returnDate)
                .user(user1)
                .books(new HashSet<>(Set.of(book1)))
                .build();
    }

    @Test
    void testSaveReserve() throws Exception {
        given(reserveService.addReserve(any(ReserveDto.class)))
                .willReturn(new ResponseEntity<>(reserve1, HttpStatus.CREATED));

        mockMvc.perform(post("/api/v1/reserves")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reserveDto1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testFindAllReserves() throws Exception {
        given(reserveService.findAllReserves())
                .willReturn(new ResponseEntity<>(List.of(reserveDto1), HttpStatus.OK));

        mockMvc.perform(get("/api/v1/reserves")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void testFindAllByReserveDate() throws Exception {
        given(reserveService.findAllByReserveDate(today))
                .willReturn(new ResponseEntity<>(List.of(reserveDto1), HttpStatus.OK));

        mockMvc.perform(get("/api/v1/reserves/1/" + today)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void testFindAllByReturnDate() throws Exception {
        given(reserveService.findAllByReturnDate(returnDate))
                .willReturn(new ResponseEntity<>(List.of(reserveDto1), HttpStatus.OK));

        mockMvc.perform(get("/api/v1/reserves/2/" + returnDate)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void testFindAllReservesByUser() throws Exception {
        given(reserveService.findAllReservesByUserId(1L))
                .willReturn(new ResponseEntity<>(List.of(reserveDto1), HttpStatus.OK));

        mockMvc.perform(get("/api/v1/reserves/3/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void testFindReserveById() throws Exception {
        given(reserveService.findReserveById(1L))
                .willReturn(new ResponseEntity<>(reserveDto1, HttpStatus.OK));

        mockMvc.perform(get("/api/v1/reserves/4/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testFindReserveById_NotFound() throws Exception {
        given(reserveService.findReserveById(999L))
                .willThrow(new ResourceNotFoundException("Reserva no encontrada"));

        mockMvc.perform(get("/api/v1/reserves/4/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateReserve() throws Exception {
        given(reserveService.updateReserve(eq(1L), any(Reserve.class)))
                .willReturn(new ResponseEntity<>(reserve1, HttpStatus.OK));

        mockMvc.perform(put("/api/v1/reserves/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reserve1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testDeleteReserve() throws Exception {
        given(reserveService.deleteReserve(1L))
                .willReturn("Reserva eliminada correctamente");

        mockMvc.perform(delete("/api/v1/reserves/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Reserva eliminada correctamente"));
    }

    @Test
    void testDeleteReserve_NotFound() throws Exception {
        given(reserveService.deleteReserve(999L))
                .willThrow(new ResourceNotFoundException("Reserva no encontrada"));

        mockMvc.perform(delete("/api/v1/reserves/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
