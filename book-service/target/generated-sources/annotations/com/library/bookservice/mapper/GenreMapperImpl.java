package com.library.bookservice.mapper;

import com.library.bookservice.dto.GenreDto;
import com.library.bookservice.model.Genre;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-30T19:32:15+0200",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class GenreMapperImpl implements GenreMapper {

    @Override
    public GenreDto toDto(Genre genre) {
        if ( genre == null ) {
            return null;
        }

        GenreDto.GenreDtoBuilder genreDto = GenreDto.builder();

        genreDto.genreName( genre.getGenreName() );
        genreDto.id( genre.getId() );

        return genreDto.build();
    }

    @Override
    public Genre toEntity(GenreDto genreDto) {
        if ( genreDto == null ) {
            return null;
        }

        Genre.GenreBuilder genre = Genre.builder();

        genre.genreName( genreDto.getGenreName() );
        genre.id( genreDto.getId() );

        return genre.build();
    }

    @Override
    public void updateEntityFromDto(GenreDto dto, Genre genre) {
        if ( dto == null ) {
            return;
        }

        genre.setGenreName( dto.getGenreName() );
    }
}
