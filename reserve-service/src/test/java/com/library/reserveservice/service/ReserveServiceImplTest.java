package com.library.reserveservice.service;

import com.library.reserveservice.client.BookServiceClient;
import com.library.reserveservice.client.UserServiceClient;
import com.library.reserveservice.dto.BookExternalDto;
import com.library.reserveservice.dto.ReserveDto;
import com.library.reserveservice.dto.UserExternalDto;
import com.library.reserveservice.event.ReservationEventPublisher;
import com.library.reserveservice.exception.MaxReservationLimitExceededException;
import com.library.reserveservice.mapper.ReserveMapper;
import com.library.reserveservice.model.Reserve;
import com.library.reserveservice.repository.ReserveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReserveServiceImplTest {

    @Mock
    private ReserveRepository reserveRepository;

    @Mock
    private ReserveMapper reserveMapper;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private BookServiceClient bookServiceClient;

    @Mock
    private ReservationEventPublisher eventPublisher;

    @InjectMocks
    private ReserveServiceImpl reserveService;

    private UserExternalDto userDto;
    private BookExternalDto bookDto;

    @BeforeEach
    void setUp() {
        userDto = new UserExternalDto();
        userDto.setId(1L);
        userDto.setName("Test User");

        bookDto = new BookExternalDto();
        bookDto.setId(101L);
        bookDto.setAvailable(true);
    }

    @Test
    void addReserve_ExceedsSingleReservationLimit_ThrowsException() {
        Reserve reserve = Reserve.builder()
                .userId(1L)
                .bookIds(Set.of(101L, 102L, 103L, 104L))
                .build();

        ReserveDto reserveDto = new ReserveDto();
        when(reserveMapper.toEntity(any(ReserveDto.class))).thenReturn(reserve);
        when(userServiceClient.getUserById(1L)).thenReturn(userDto);

        assertThrows(MaxReservationLimitExceededException.class, () -> {
            reserveService.addReserve(reserveDto);
        });

        verify(reserveRepository, never()).save(any());
    }

    @Test
    void addReserve_ExceedsAccumulatedLimit_ThrowsException() {
        Reserve existingReserve = Reserve.builder()
                .id(1L)
                .userId(1L)
                .bookIds(Set.of(201L, 202L))
                .build();

        Reserve newReserve = Reserve.builder()
                .userId(1L)
                .bookIds(Set.of(101L, 102L))
                .build();

        ReserveDto reserveDto = new ReserveDto();
        when(reserveMapper.toEntity(any(ReserveDto.class))).thenReturn(newReserve);
        when(userServiceClient.getUserById(1L)).thenReturn(userDto);
        when(reserveRepository.findAllByUserId(1L)).thenReturn(List.of(existingReserve));

        assertThrows(MaxReservationLimitExceededException.class, () -> {
            reserveService.addReserve(reserveDto);
        });

        verify(reserveRepository, never()).save(any());
    }
}
