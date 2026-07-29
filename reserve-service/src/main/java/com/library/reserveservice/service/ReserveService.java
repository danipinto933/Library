package com.library.reserveservice.service;

import com.library.reserveservice.dto.ReserveDto;
import com.library.reserveservice.model.Reserve;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

public interface ReserveService {
    ResponseEntity<ReserveDto> addReserve(ReserveDto reserveDto);
    ResponseEntity<List<ReserveDto>> findAllReserves();
    ResponseEntity<List<ReserveDto>> findAllByReserveDate(LocalDate reserveDate);
    ResponseEntity<List<ReserveDto>> findAllByReturnDate(LocalDate returnDate);
    ResponseEntity<List<ReserveDto>> findAllReservesByUserId(Long userId);
    ResponseEntity<ReserveDto> findReserveById(Long reserveId);
    ResponseEntity<ReserveDto> updateReserve(Long id, Reserve updatedReserveData);
    String deleteReserve(Long id);
}
