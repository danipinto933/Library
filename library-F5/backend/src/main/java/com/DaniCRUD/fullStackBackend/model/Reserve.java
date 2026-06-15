package com.DaniCRUD.fullStackBackend.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.DaniCRUD.fullStackBackend.dto.response.BookDto;
import com.DaniCRUD.fullStackBackend.dto.response.UserDto;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table (name = "reserves")
public class Reserve
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Builder.Default
    private LocalDate reserveDate = LocalDate.now();

    @Column(nullable = false)
    @Builder.Default
    private LocalDate returnDate = LocalDate.now().plusMonths(1);

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "reserve_book", joinColumns = @JoinColumn(name="reserve_id"), inverseJoinColumns=@JoinColumn(name="book_id"))
    private Set<Book> books = new HashSet<>();

    @NotNull
    @Column(name = "is_ampliated")
    private boolean ampliated = false;
    
}