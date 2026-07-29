package com.library.reserveservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookExternalDto {
    private Long id;
    private String title;
    private String isbn;
    private String author;
    private boolean available;
    private Set<String> genres;
}
