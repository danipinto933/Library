package com.DaniCRUD.fullStackBackend.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.DaniCRUD.fullStackBackend.dto.response.ReserveDto;
import com.DaniCRUD.fullStackBackend.exception.ResourceNotFoundException;
import com.DaniCRUD.fullStackBackend.exception.ValidationErrorsException;
import com.DaniCRUD.fullStackBackend.exception.BookNotAvailableException;
import com.DaniCRUD.fullStackBackend.exception.ReservationAlreadyExtendedException;
import com.DaniCRUD.fullStackBackend.exception.InvalidReservationDatesException;
import com.DaniCRUD.fullStackBackend.mapper.ReserveMapper;
import com.DaniCRUD.fullStackBackend.model.Book;
import com.DaniCRUD.fullStackBackend.model.Reserve;
import com.DaniCRUD.fullStackBackend.model.User;
import com.DaniCRUD.fullStackBackend.repository.ReserveRepository;

@Service
public class ReserveServiceImpl implements ReserveService
{
    private ReserveRepository reserveRepository;
    private UserServiceImpl userServiceImpl;
    private BookServiceImpl bookServiceImpl;
    private ReserveMapper reserveMapper;

    public ReserveServiceImpl (ReserveRepository reserveRepository, UserServiceImpl userServiceImpl, BookServiceImpl bookServiceImpl,
        ReserveMapper reserveMapper)
    {
        this.reserveRepository = reserveRepository;
        this.userServiceImpl = userServiceImpl;
        this.bookServiceImpl = bookServiceImpl;
        this.reserveMapper = reserveMapper;

    }

    @Override
    public ResponseEntity<Reserve> addReserve(ReserveDto reserveDto)
    {
        Reserve reserve = reserveMapper.toEntity(reserveDto);

        if (reserve.getReturnDate() != null && reserve.getReserveDate() != null)
        {
            if (reserve.getReturnDate().isBefore(reserve.getReserveDate()) || reserve.getReturnDate().isEqual(reserve.getReserveDate()))
            {
                throw new InvalidReservationDatesException("La fecha de devolución no puede ser anterior o igual a la fecha de reserva");
            }
        }

        Long selectedUserId = reserve.getUser().getId();
        User user = userServiceImpl.findByIdUser(selectedUserId);
        reserve.setUser(user);

        Set<Book> books = new HashSet<>();
        for (Book b : reserve.getBooks())
        {
            Book realBook = bookServiceImpl.findBookByid(b.getId());
            if (realBook == null)
            {
                throw new ResourceNotFoundException("Libros del repositorio con el ID " + b.getId() + " no encontrado");
            }
            if (!realBook.isAvailable())
            {
                throw new BookNotAvailableException("El libro con el ID " + b.getId() + " (" + realBook.getTitle() + ") no está disponible para reserva");
            }
            books.add(realBook);
            realBook.setAvailable(false);
        }
        reserve.setBooks(books);
        reserveRepository.save(reserve);
        return new ResponseEntity<>(reserve, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<ReserveDto>> findAllReserves()
    {
        List<Reserve> reserves = reserveRepository.findAll();

        List<ReserveDto> reservesDto = reserves.stream()
        .map(reserveMapper::toDto)
        .toList();

        return new ResponseEntity<>(reservesDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<ReserveDto>> findAllByReserveDate(LocalDate reserveDate)
    {
        List<Reserve> dateReserves = reserveRepository.findAllByReserveDate(reserveDate);

        if (dateReserves == null)
        {
            throw new ResourceNotFoundException("Reservas con la fecha " + reserveDate + " no encontradas");
        }

        List<ReserveDto> dateReservesDto = dateReserves.stream()
            .map(reserveMapper::toDto)
            .toList();

        return new ResponseEntity<>(dateReservesDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<ReserveDto>> findAllByReturnDate(LocalDate returnDate)
    {
        List<Reserve> dateReturnReserves = reserveRepository.findAllByReturnDate(returnDate);

        if (dateReturnReserves == null)
        {
            throw new ResourceNotFoundException("Reservas con la fecha " + returnDate + " no encontradas");
        }

        List<ReserveDto> dateReturnReservesDto = dateReturnReserves.stream()
            .map(reserveMapper::toDto)
            .toList();

        return new ResponseEntity<>(dateReturnReservesDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<ReserveDto>> findAllReservesByUserId(Long userId)
    {
        User user = userServiceImpl.findByIdUser(userId);

        if (user == null)
        {
            throw new ResourceNotFoundException("Usuario con el ID " + userId + " no encontrado");
        }
        
        List<Reserve> userReserves = reserveRepository.findAllByUser(user);

        List<ReserveDto> userReservesDto = userReserves.stream()
        .map(reserveMapper::toDto)
        .toList();
        return new ResponseEntity<>(userReservesDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ReserveDto> findReserveById(Long reserveId)
    {
        Reserve reserve = reserveRepository.findById(reserveId)
            .orElseThrow(() -> new ResourceNotFoundException("Reserva con el ID " + reserveId + " no encontrada"));
        ReserveDto reserveDTO = reserveMapper.toDto(reserve);

        return new ResponseEntity<>(reserveDTO, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Reserve> updateReserve(Long id, Reserve updatedReserveData) {
        Reserve oldReserve = reserveRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("La reserva con el ID " + id + " no encontrada"));

        if (oldReserve.isAmpliated())
        {
            throw new ReservationAlreadyExtendedException("La reserva ya fue ampliada y no puede ser extendida de nuevo");
        }

        Set<Long> bookIds = updatedReserveData.getBooks().stream()
            .map(Book::getId)
            .collect(Collectors.toSet());
        Set<Book> books = new HashSet<>();
        
        for (Long i : bookIds)
        {
            Book b = bookServiceImpl.findBookByid(i);
            books.add(b);
        }

        oldReserve.setReserveDate(oldReserve.getReserveDate());
        oldReserve.setReturnDate(oldReserve.getReturnDate().plusMonths(1));
        oldReserve.setAmpliated(true);
        oldReserve.setUser(updatedReserveData.getUser());
        oldReserve.setBooks(books);
        oldReserve.getBooks().forEach(book -> book.setAvailable(false));
        
        Reserve savedReserve = reserveRepository.save(oldReserve);
        return new ResponseEntity<>(savedReserve, HttpStatus.OK);
    }

    @Override
    public String deleteReserve(Long id)
    {
        Reserve reserve = reserveRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Reserva con el ID " + id + " no encontrada"));
        Set<Book> books = reserve.getBooks();
        books.forEach(book -> book.setAvailable(true));
        reserveRepository.delete(reserve);
        
        return "Reserva eliminada correctamente";
    }
}
