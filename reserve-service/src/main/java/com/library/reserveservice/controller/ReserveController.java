package com.library.reserveservice.controller;

import com.library.reserveservice.dto.ReserveDto;
import com.library.reserveservice.model.Reserve;
import com.library.reserveservice.service.ReserveService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reserves")
public class ReserveController {

    private final ReserveService reserveService;

    public ReserveController(ReserveService reserveService) {
        this.reserveService = reserveService;
    }

    @PostMapping("")
    public ResponseEntity<ReserveDto> addReserve(@Valid @RequestBody ReserveDto reserveDto) {
        return reserveService.addReserve(reserveDto);
    }

    @GetMapping("")
    public ResponseEntity<List<ReserveDto>> findAllReserves() {
        return reserveService.findAllReserves();
    }

    @GetMapping("1/{reserveDate}")
    public ResponseEntity<List<ReserveDto>> findAllByReserveDate(@PathVariable LocalDate reserveDate) {
        return reserveService.findAllByReserveDate(reserveDate);
    }

    @GetMapping("2/{returnDate}")
    public ResponseEntity<List<ReserveDto>> findAllByReturnDate(@PathVariable LocalDate returnDate) {
        return reserveService.findAllByReturnDate(returnDate);
    }

    @GetMapping("3/{idUser}")
    public ResponseEntity<List<ReserveDto>> findAllReservesByUser(@PathVariable Long idUser) {
        return reserveService.findAllReservesByUserId(idUser);
    }

    @GetMapping("4/{reserveId}")
    public ResponseEntity<ReserveDto> findReserveById(@PathVariable Long reserveId) {
        return reserveService.findReserveById(reserveId);
    }

    @PutMapping("{id}")
    public ResponseEntity<ReserveDto> updateReserve(@PathVariable Long id, @RequestBody Reserve updatedReserveData) {
        return reserveService.updateReserve(id, updatedReserveData);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReserve(@PathVariable Long id) {
        return new ResponseEntity<>(reserveService.deleteReserve(id), HttpStatus.OK);
    }
}
