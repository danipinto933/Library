package com.DaniCRUD.fullStackBackend.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.DaniCRUD.fullStackBackend.dto.response.ReserveDto;
import com.DaniCRUD.fullStackBackend.model.Reserve;

@Service
public interface ReserveService
{
    ResponseEntity<Reserve> addReserve (ReserveDto reserveDto);

    ResponseEntity<List<ReserveDto>> findAllReserves();
    ResponseEntity<List<ReserveDto>> findAllByReserveDate(LocalDate reserveDate);
    ResponseEntity<List<ReserveDto>> findAllByReturnDate(LocalDate returnDate);
    ResponseEntity<List<ReserveDto>> findAllReservesByUserId(Long userId);
    ResponseEntity<ReserveDto> findReserveById(Long reserveId);

    ResponseEntity<Reserve> updateReserve(Long id, Reserve updatedReserveData);

    String deleteReserve(Long id);
}