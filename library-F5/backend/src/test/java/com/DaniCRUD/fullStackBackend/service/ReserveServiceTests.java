package com.DaniCRUD.fullStackBackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.DaniCRUD.fullStackBackend.dto.response.ReserveDto;
import com.DaniCRUD.fullStackBackend.exception.BookNotAvailableException;
import com.DaniCRUD.fullStackBackend.exception.InvalidReservationDatesException;
import com.DaniCRUD.fullStackBackend.exception.ReservationAlreadyExtendedException;
import com.DaniCRUD.fullStackBackend.exception.ResourceNotFoundException;
import com.DaniCRUD.fullStackBackend.mapper.ReserveMapper;
import com.DaniCRUD.fullStackBackend.model.Book;
import com.DaniCRUD.fullStackBackend.model.Reserve;
import com.DaniCRUD.fullStackBackend.model.User;
import com.DaniCRUD.fullStackBackend.repository.ReserveRepository;

@ExtendWith(MockitoExtension.class)
public class ReserveServiceTests {

    @Mock
    private ReserveRepository reserveRepository;

    @Mock
    private UserServiceImpl userServiceImpl;

    @Mock
    private BookServiceImpl bookServiceImpl;

    @Mock
    private ReserveMapper reserveMapper;

    @InjectMocks
    private ReserveServiceImpl reserveServiceImpl;

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
        book1 = Book.builder().id(1L).title("El Quijote").available(true).build();

        reserve1 = Reserve.builder()
                .id(1L)
                .reserveDate(today)
                .returnDate(returnDate)
                .user(user1)
                .books(new HashSet<>(Set.of(book1)))
                .ampliated(false)
                .build();

        reserveDto1 = ReserveDto.builder()
                .id(1L)
                .reserveDate(today)
                .returnDate(returnDate)
                .user(user1)
                .books(new HashSet<>(Set.of(book1)))
                .ampliated(false)
                .build();
    }

    @Test
    void testAddReserve() {
        given(reserveMapper.toEntity(any(ReserveDto.class))).willReturn(reserve1);
        given(userServiceImpl.findByIdUser(1L)).willReturn(user1);
        given(bookServiceImpl.findBookByid(1L)).willReturn(book1);
        given(reserveRepository.save(any(Reserve.class))).willReturn(reserve1);

        ResponseEntity<Reserve> response = reserveServiceImpl.addReserve(reserveDto1);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(reserveRepository).save(any(Reserve.class));
        assertTrue(!book1.isAvailable()); // Se pone en false al reservar
    }

    @Test
    void testAddReserve_InvalidDates() {
        reserve1.setReturnDate(today.minusDays(1)); // Fecha incorrecta
        given(reserveMapper.toEntity(any(ReserveDto.class))).willReturn(reserve1);

        assertThrows(InvalidReservationDatesException.class, () -> reserveServiceImpl.addReserve(reserveDto1));
        verify(reserveRepository, never()).save(any(Reserve.class));
    }

    @Test
    void testAddReserve_BookNotFound() {
        given(reserveMapper.toEntity(any(ReserveDto.class))).willReturn(reserve1);
        given(userServiceImpl.findByIdUser(1L)).willReturn(user1);
        given(bookServiceImpl.findBookByid(1L)).willReturn(null); // Libro inexistente

        assertThrows(ResourceNotFoundException.class, () -> reserveServiceImpl.addReserve(reserveDto1));
        verify(reserveRepository, never()).save(any(Reserve.class));
    }

    @Test
    void testAddReserve_BookNotAvailable() {
        book1.setAvailable(false); // No disponible
        given(reserveMapper.toEntity(any(ReserveDto.class))).willReturn(reserve1);
        given(userServiceImpl.findByIdUser(1L)).willReturn(user1);
        given(bookServiceImpl.findBookByid(1L)).willReturn(book1);

        assertThrows(BookNotAvailableException.class, () -> reserveServiceImpl.addReserve(reserveDto1));
        verify(reserveRepository, never()).save(any(Reserve.class));
    }

    @Test
    void testFindAllReserves() {
        given(reserveRepository.findAll()).willReturn(List.of(reserve1));
        given(reserveMapper.toDto(reserve1)).willReturn(reserveDto1);

        ResponseEntity<List<ReserveDto>> response = reserveServiceImpl.findAllReserves();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testFindAllByReserveDate() {
        given(reserveRepository.findAllByReserveDate(today)).willReturn(List.of(reserve1));
        given(reserveMapper.toDto(reserve1)).willReturn(reserveDto1);

        ResponseEntity<List<ReserveDto>> response = reserveServiceImpl.findAllByReserveDate(today);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testFindAllByReserveDate_NotFound() {
        given(reserveRepository.findAllByReserveDate(today)).willReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> reserveServiceImpl.findAllByReserveDate(today));
    }

    @Test
    void testFindAllByReturnDate() {
        given(reserveRepository.findAllByReturnDate(returnDate)).willReturn(List.of(reserve1));
        given(reserveMapper.toDto(reserve1)).willReturn(reserveDto1);

        ResponseEntity<List<ReserveDto>> response = reserveServiceImpl.findAllByReturnDate(returnDate);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testFindAllByReturnDate_NotFound() {
        given(reserveRepository.findAllByReturnDate(returnDate)).willReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> reserveServiceImpl.findAllByReturnDate(returnDate));
    }

    @Test
    void testFindAllReservesByUserId() {
        given(userServiceImpl.findByIdUser(1L)).willReturn(user1);
        given(reserveRepository.findAllByUser(user1)).willReturn(List.of(reserve1));
        given(reserveMapper.toDto(reserve1)).willReturn(reserveDto1);

        ResponseEntity<List<ReserveDto>> response = reserveServiceImpl.findAllReservesByUserId(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testFindAllReservesByUserId_UserNotFound() {
        given(userServiceImpl.findByIdUser(999L)).willReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> reserveServiceImpl.findAllReservesByUserId(999L));
    }

    @Test
    void testFindReserveById() {
        given(reserveRepository.findById(1L)).willReturn(Optional.of(reserve1));
        given(reserveMapper.toDto(reserve1)).willReturn(reserveDto1);

        ResponseEntity<ReserveDto> response = reserveServiceImpl.findReserveById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void testFindReserveById_NotFound() {
        given(reserveRepository.findById(999L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reserveServiceImpl.findReserveById(999L));
    }

    @Test
    void testUpdateReserve() {
        given(reserveRepository.findById(1L)).willReturn(Optional.of(reserve1));
        given(bookServiceImpl.findBookByid(1L)).willReturn(book1);
        given(reserveRepository.save(any(Reserve.class))).willReturn(reserve1);

        ResponseEntity<Reserve> response = reserveServiceImpl.updateReserve(1L, reserve1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(reserveRepository).save(any(Reserve.class));
    }

    @Test
    void testUpdateReserve_NotFound() {
        given(reserveRepository.findById(999L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reserveServiceImpl.updateReserve(999L, reserve1));
    }

    @Test
    void testUpdateReserve_AlreadyExtended() {
        reserve1.setAmpliated(true); // Ya ampliada
        given(reserveRepository.findById(1L)).willReturn(Optional.of(reserve1));

        assertThrows(ReservationAlreadyExtendedException.class, () -> reserveServiceImpl.updateReserve(1L, reserve1));
    }

    @Test
    void testDeleteReserve() {
        given(reserveRepository.findById(1L)).willReturn(Optional.of(reserve1));

        String result = reserveServiceImpl.deleteReserve(1L);

        assertEquals("Reserva eliminada correctamente", result);
        verify(reserveRepository).delete(reserve1);
        assertTrue(book1.isAvailable()); // Se vuelve a poner en true al eliminar
    }


    @Test
    void testDeleteReserve_NotFound() {
        given(reserveRepository.findById(999L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reserveServiceImpl.deleteReserve(999L));
    }
}
