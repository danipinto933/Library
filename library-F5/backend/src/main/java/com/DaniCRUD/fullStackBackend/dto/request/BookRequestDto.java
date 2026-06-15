package com.DaniCRUD.fullStackBackend.dto.request;

import java.util.Set;
import com.DaniCRUD.fullStackBackend.model.FileData;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookRequestDto
{
    @NotBlank(message="El título no puede estar vacío")
    private String title;

    @NotBlank(message="El ISBN no puede estar vacío")
    private String isbn;

    @NotBlank(message="El autor no puede estar vacío")
    private String author;

    private FileData image;

    @NotBlank(message="Los géneros no puede estar vacío")
    private Set<Long> genres;
}