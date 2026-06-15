package com.DaniCRUD.fullStackBackend.mapper;

import org.springframework.stereotype.Component;
import com.DaniCRUD.fullStackBackend.dto.response.GenreDto;
import com.DaniCRUD.fullStackBackend.model.Genre;

@Component
public class GenreMapper
{
    public GenreDto toDto (Genre genre)
    {
        if (genre == null) return null;
        
        return GenreDto.builder()
        .id(genre.getId())
        .genreName(genre.getGenreName())
        .build();
    }

    public Genre toEntity (GenreDto genreDto)
    {
        if (genreDto == null) return null;

        return Genre.builder()
        .id(genreDto.getId())
        .genreName(genreDto.getGenreName())
        .build();
    }

    public void updateEntityFromDto (GenreDto dto, Genre genre)
    {
        if (dto.getGenreName() != null)     genre.setGenreName(dto.getGenreName());
    }


}
