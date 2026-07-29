package com.library.bookservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationEvent {
    private Long reserveId;
    private Long userId;
    private Set<Long> bookIds;
    private LocalDate reserveDate;
    private String status;
}
