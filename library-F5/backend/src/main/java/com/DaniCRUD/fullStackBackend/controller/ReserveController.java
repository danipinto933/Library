package com.DaniCRUD.fullStackBackend.controller;

import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.DaniCRUD.fullStackBackend.dto.response.ReserveDto;
import com.DaniCRUD.fullStackBackend.model.Reserve;
import com.DaniCRUD.fullStackBackend.service.ReserveService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/reserves")
public class ReserveController
{
    private ReserveService reserveService;

    public ReserveController (ReserveService reserveService)
    {
        this.reserveService = reserveService;
    }

    @PostMapping("")
    public ResponseEntity<Reserve> addReserve(@Valid @RequestBody ReserveDto reserveDto)
    {
        return reserveService.addReserve(reserveDto);
    }

    @GetMapping("")
    public ResponseEntity<List<ReserveDto>> findAllReserves() 
    {
        return reserveService.findAllReserves();
    }

    @GetMapping("1/{reserveDate}")
    public ResponseEntity<List<ReserveDto>> findAllByReserveDate(@PathVariable LocalDate reserveDate)
    {
        return reserveService.findAllByReserveDate(reserveDate);
    }

    @GetMapping("2/{returnDate}")
    public ResponseEntity<List<ReserveDto>> findAllByReturnDate(@PathVariable LocalDate returnDate) 
    {
        return reserveService.findAllByReturnDate(returnDate);
    }

    @GetMapping("3/{idUser}")
    public ResponseEntity<List<ReserveDto>> findAllReservesByUser(@PathVariable Long idUser)
    {
        return reserveService.findAllReservesByUserId(idUser);
    }

    @GetMapping("4/{reserveId}")
    public ResponseEntity<ReserveDto> findReserveById(@PathVariable Long reserveId)
    {
        return reserveService.findReserveById(reserveId);
    }

    @PutMapping("{id}")
    public ResponseEntity<Reserve> updateReserve(@PathVariable Long id, @RequestBody Reserve updatedReserveData)
    {
        return reserveService.updateReserve(id, updatedReserveData);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReserve(@PathVariable Long id)
    {
        return new ResponseEntity<>(reserveService.deleteReserve(id), HttpStatus.OK);
    }
}
