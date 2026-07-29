package com.library.reserveservice.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "reserves")
public class Reserve {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Builder.Default
    private LocalDate reserveDate = LocalDate.now();

    @Column(nullable = false)
    @Builder.Default
    private LocalDate returnDate = LocalDate.now().plusMonths(1);

    // FASE VI: Desacoplamiento. Solo guardamos el ID del usuario
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // FASE VI: Desacoplamiento. Solo guardamos los IDs de los libros
    @ElementCollection
    @CollectionTable(name = "reserve_books", joinColumns = @JoinColumn(name = "reserve_id"))
    @Column(name = "book_id")
    private Set<Long> bookIds = new HashSet<>();

    @NotNull
    @Column(name = "is_ampliated")
    private boolean ampliated = false;
}
