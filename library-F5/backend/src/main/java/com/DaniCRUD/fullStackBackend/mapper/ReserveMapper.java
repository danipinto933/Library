package com.DaniCRUD.fullStackBackend.mapper;

import java.time.LocalDate;
import org.springframework.stereotype.Component;
import com.DaniCRUD.fullStackBackend.dto.response.ReserveDto;
import com.DaniCRUD.fullStackBackend.model.Reserve;

@Component
public class ReserveMapper
{
    public ReserveDto toDto (Reserve reserve)
    {
        if (reserve == null) return null;

        return ReserveDto.builder()
        .id(reserve.getId())
        .reserveDate(reserve.getReserveDate())
        .returnDate(reserve.getReturnDate())
        .books(reserve.getBooks())
        .user(reserve.getUser())
        .ampliated(reserve.isAmpliated())
        .build();
    }

    public Reserve toEntity (ReserveDto reserveDto)
    {
        if (reserveDto == null) return null;

        return Reserve.builder()
        .id(reserveDto.getId())
        .reserveDate(LocalDate.now())
        .returnDate(LocalDate.now().plusMonths(1))
        .books(reserveDto.getBooks())
        .user(reserveDto.getUser())
        .ampliated(reserveDto.isAmpliated())
        .build();
    }
}
