package com.library.reserveservice.service;

import com.library.reserveservice.client.BookServiceClient;
import com.library.reserveservice.client.UserServiceClient;
import com.library.reserveservice.dto.BookExternalDto;
import com.library.reserveservice.dto.ReserveDto;
import com.library.reserveservice.dto.UserExternalDto;
import com.library.reserveservice.event.ReservationEvent;
import com.library.reserveservice.event.ReservationEventPublisher;
import com.library.reserveservice.exception.BookNotAvailableException;
import com.library.reserveservice.exception.InvalidReservationDatesException;
import com.library.reserveservice.exception.ReservationAlreadyExtendedException;
import com.library.reserveservice.exception.ResourceNotFoundException;
import com.library.reserveservice.mapper.ReserveMapper;
import com.library.reserveservice.model.Reserve;
import com.library.reserveservice.repository.ReserveRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReserveServiceImpl implements ReserveService {

    private final ReserveRepository reserveRepository;
    private final ReserveMapper reserveMapper;
    private final UserServiceClient userServiceClient;
    private final BookServiceClient bookServiceClient;
    private final ReservationEventPublisher eventPublisher;

    public ReserveServiceImpl(ReserveRepository reserveRepository, ReserveMapper reserveMapper, UserServiceClient userServiceClient, BookServiceClient bookServiceClient, ReservationEventPublisher eventPublisher) {
        this.reserveRepository = reserveRepository;
        this.reserveMapper = reserveMapper;
        this.userServiceClient = userServiceClient;
        this.bookServiceClient = bookServiceClient;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public ResponseEntity<ReserveDto> addReserve(ReserveDto reserveDto) {
        Reserve reserve = reserveMapper.toEntity(reserveDto);

        if (reserve.getReturnDate() != null && reserve.getReserveDate() != null) {
            if (reserve.getReturnDate().isBefore(reserve.getReserveDate()) || reserve.getReturnDate().isEqual(reserve.getReserveDate())) {
                throw new InvalidReservationDatesException("La fecha de devolución no puede ser anterior o igual a la fecha de reserva");
            }
        }

        // FASE VI: Verificar usuario remotamente con CB
        UserExternalDto userDto = userServiceClient.getUserById(reserve.getUserId());
        if (userDto == null || userDto.getName().contains("no disponible")) {
            throw new ResourceNotFoundException("Usuario con el ID " + reserve.getUserId() + " no encontrado o servicio no disponible");
        }

        // FASE VI: Verificar libros remotamente con CB
        for (Long bookId : reserve.getBookIds()) {
            BookExternalDto bookDto = bookServiceClient.getBookById(bookId);
            if (bookDto == null || !bookDto.isAvailable()) {
                throw new BookNotAvailableException("El libro con el ID " + bookId + " no está disponible para reserva");
            }
        }

        Reserve savedReserve = reserveRepository.save(reserve);

        // FASE VI: Publicar evento asíncrono para notificar a book-service
        ReservationEvent event = ReservationEvent.builder()
                .reserveId(savedReserve.getId())
                .userId(savedReserve.getUserId())
                .bookIds(savedReserve.getBookIds())
                .reserveDate(savedReserve.getReserveDate())
                .status("CREATED")
                .build();
        eventPublisher.publishReservationCreatedEvent(event);

        return new ResponseEntity<>(reserveMapper.toDto(savedReserve), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<ReserveDto>> findAllReserves() {
        List<Reserve> reserves = reserveRepository.findAll();
        List<ReserveDto> dtos = reserves.stream().map(reserveMapper::toDto).toList();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<ReserveDto>> findAllByReserveDate(LocalDate reserveDate) {
        List<Reserve> reserves = reserveRepository.findAllByReserveDate(reserveDate);
        if (reserves.isEmpty()) {
            throw new ResourceNotFoundException("Reservas con la fecha " + reserveDate + " no encontradas");
        }
        return new ResponseEntity<>(reserves.stream().map(reserveMapper::toDto).toList(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<ReserveDto>> findAllByReturnDate(LocalDate returnDate) {
        List<Reserve> reserves = reserveRepository.findAllByReturnDate(returnDate);
        if (reserves.isEmpty()) {
            throw new ResourceNotFoundException("Reservas con la fecha " + returnDate + " no encontradas");
        }
        return new ResponseEntity<>(reserves.stream().map(reserveMapper::toDto).toList(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<ReserveDto>> findAllReservesByUserId(Long userId) {
        List<Reserve> reserves = reserveRepository.findAllByUserId(userId);
        if (reserves.isEmpty()) {
            // Throw exception or return empty
            // throw new ResourceNotFoundException("Reservas para el usuario con ID " + userId + " no encontradas");
        }
        return new ResponseEntity<>(reserves.stream().map(reserveMapper::toDto).toList(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ReserveDto> findReserveById(Long reserveId) {
        Reserve reserve = reserveRepository.findById(reserveId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva con el ID " + reserveId + " no encontrada"));
        return new ResponseEntity<>(reserveMapper.toDto(reserve), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ReserveDto> updateReserve(Long id, Reserve updatedReserveData) {
        Reserve oldReserve = reserveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La reserva con el ID " + id + " no encontrada"));

        if (oldReserve.isAmpliated()) {
            throw new ReservationAlreadyExtendedException("La reserva ya fue ampliada y no puede ser extendida de nuevo");
        }

        oldReserve.setReturnDate(oldReserve.getReturnDate().plusMonths(1));
        oldReserve.setAmpliated(true);
        if (updatedReserveData.getUserId() != null) {
            oldReserve.setUserId(updatedReserveData.getUserId());
        }
        if (updatedReserveData.getBookIds() != null && !updatedReserveData.getBookIds().isEmpty()) {
            oldReserve.setBookIds(updatedReserveData.getBookIds());
        }

        Reserve savedReserve = reserveRepository.save(oldReserve);
        return new ResponseEntity<>(reserveMapper.toDto(savedReserve), HttpStatus.OK);
    }

    @Override
    public String deleteReserve(Long id) {
        Reserve reserve = reserveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva con el ID " + id + " no encontrada"));
        
        // Aquí deberíamos llamar a book-service para volver a poner los libros como disponibles
        // FASE VI: O publicar un evento de "RESERVATION_DELETED"
        ReservationEvent event = ReservationEvent.builder()
                .reserveId(reserve.getId())
                .userId(reserve.getUserId())
                .bookIds(reserve.getBookIds())
                .reserveDate(reserve.getReserveDate())
                .status("DELETED")
                .build();
        eventPublisher.publishReservationCreatedEvent(event);

        reserveRepository.delete(reserve);
        return "Reserva eliminada correctamente";
    }
}
