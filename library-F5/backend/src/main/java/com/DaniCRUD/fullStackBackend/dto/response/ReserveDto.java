package com.DaniCRUD.fullStackBackend.dto.response;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import com.DaniCRUD.fullStackBackend.model.Book;
import com.DaniCRUD.fullStackBackend.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReserveDto
{
    private Long id;
    private LocalDate reserveDate;
    private LocalDate returnDate;
    private User user;
    private Set<Book> books = new HashSet<>();
    private boolean ampliated;
}
