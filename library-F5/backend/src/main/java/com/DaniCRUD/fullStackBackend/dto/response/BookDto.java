package com.DaniCRUD.fullStackBackend.dto.response;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.DaniCRUD.fullStackBackend.model.FileData;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDto //Esto es lo que ve el usuario al pedir datos
{
    private Long id;

    @NotNull(message = "Introduzca un título")
    private String title;

    @NotNull(message = "Introduzca un ISBN")
    private String isbn;

    @NotNull(message = "Introduzca un autor")
    private String author;

    private FileData image;
    
    //private Set<GenreDto> genres = new LinkedHashSet<>();
    private Set<String> genres = new HashSet<>();

    private boolean available;
}