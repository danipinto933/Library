package com.library.bookservice.dto;

import com.library.bookservice.model.FileData;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDto {
    private Long id;

    @NotNull(message = "Introduzca un título")
    private String title;

    @NotNull(message = "Introduzca un ISBN")
    private String isbn;

    @NotNull(message = "Introduzca un autor")
    private String author;

    private FileData image;
    
    private Set<String> genres = new HashSet<>();

    private boolean available;
}
