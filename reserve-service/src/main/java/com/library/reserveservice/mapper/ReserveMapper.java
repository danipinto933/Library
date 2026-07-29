package com.library.reserveservice.mapper;

import com.library.reserveservice.client.BookServiceClient;
import com.library.reserveservice.client.UserServiceClient;
import com.library.reserveservice.dto.BookExternalDto;
import com.library.reserveservice.dto.ReserveDto;
import com.library.reserveservice.dto.UserExternalDto;
import com.library.reserveservice.model.Reserve;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ReserveMapper {

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private BookServiceClient bookServiceClient;

    public ReserveDto toDto(Reserve reserve) {
        if (reserve == null) return null;

        ReserveDto dto = ReserveDto.builder()
                .id(reserve.getId())
                .reserveDate(reserve.getReserveDate())
                .returnDate(reserve.getReturnDate())
                .ampliated(reserve.isAmpliated())
                .build();

        // Obtener usuario desde user-service con Circuit Breaker
        if (reserve.getUserId() != null) {
            UserExternalDto userDto = userServiceClient.getUserById(reserve.getUserId());
            dto.setUser(userDto);
        }

        // Obtener libros desde book-service con Circuit Breaker
        Set<BookExternalDto> books = new HashSet<>();
        if (reserve.getBookIds() != null) {
            for (Long bookId : reserve.getBookIds()) {
                BookExternalDto bookDto = bookServiceClient.getBookById(bookId);
                if (bookDto != null) {
                    books.add(bookDto);
                }
            }
        }
        dto.setBooks(books);

        return dto;
    }

    public Reserve toEntity(ReserveDto reserveDto) {
        if (reserveDto == null) return null;

        Reserve reserve = Reserve.builder()
                .id(reserveDto.getId())
                .reserveDate(reserveDto.getReserveDate())
                .returnDate(reserveDto.getReturnDate())
                .ampliated(reserveDto.isAmpliated())
                .build();

        if (reserveDto.getUser() != null) {
            reserve.setUserId(reserveDto.getUser().getId());
        }

        if (reserveDto.getBooks() != null) {
            Set<Long> bookIds = reserveDto.getBooks().stream()
                    .map(BookExternalDto::getId)
                    .collect(Collectors.toSet());
            reserve.setBookIds(bookIds);
        } else {
            reserve.setBookIds(new HashSet<>());
        }

        return reserve;
    }
}
