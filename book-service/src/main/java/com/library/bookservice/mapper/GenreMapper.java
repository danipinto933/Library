package com.library.bookservice.mapper;

import com.library.bookservice.dto.GenreDto;
import com.library.bookservice.model.Genre;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface GenreMapper {

    GenreDto toDto(Genre genre);

    Genre toEntity(GenreDto genreDto);

    void updateEntityFromDto(GenreDto dto, @MappingTarget Genre genre);
}
