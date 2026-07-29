package com.library.reserveservice.repository;

import com.library.reserveservice.model.Reserve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReserveRepository extends JpaRepository<Reserve, Long> {
    List<Reserve> findAllByReserveDate(LocalDate reserveDate);
    List<Reserve> findAllByReturnDate(LocalDate returnDate);
    List<Reserve> findAllByUserId(Long userId);
}
