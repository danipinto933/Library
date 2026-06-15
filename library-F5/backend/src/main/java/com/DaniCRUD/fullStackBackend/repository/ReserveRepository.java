package com.DaniCRUD.fullStackBackend.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.DaniCRUD.fullStackBackend.model.Reserve;
import com.DaniCRUD.fullStackBackend.model.User;

@Repository
public interface ReserveRepository extends JpaRepository<Reserve,Long>
{
    public List<Reserve> findAllByReserveDate(LocalDate reserveDate);
    public List<Reserve> findAllByReturnDate(LocalDate returnDate);
    public List<Reserve> findByUser_Id(Long idUser);
    public List<Reserve> findAllByUser(User user);
}