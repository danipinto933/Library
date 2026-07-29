package com.library.reserveservice.dto;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReserveDto {
    private Long id;
    private LocalDate reserveDate;
    private LocalDate returnDate;
    
    // FASE VI: En lugar de un Entity User y Book, usamos los DTOs externos
    private UserExternalDto user;
    private Set<BookExternalDto> books = new HashSet<>();
    
    private boolean ampliated;
}
