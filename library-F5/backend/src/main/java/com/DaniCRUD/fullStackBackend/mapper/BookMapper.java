package com.DaniCRUD.fullStackBackend.mapper;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.DaniCRUD.fullStackBackend.dto.response.BookDto;
import com.DaniCRUD.fullStackBackend.dto.response.GenreDto;
import com.DaniCRUD.fullStackBackend.model.Book;
import com.DaniCRUD.fullStackBackend.model.Genre;
import com.DaniCRUD.fullStackBackend.repository.GenreRepository;

@Component

public class BookMapper
{
    @Autowired
    private GenreMapper genreMapper;

    @Autowired
    private GenreRepository genreRepository;

    public BookDto toDto (Book book)
    {
        if (book == null) return null;

        Set<GenreDto> genres = null;
        Set<String> genresName = new HashSet<>();
        if (book.getGenres() != null)
        {
            genres = book.getGenres().stream()
            .map(genreMapper::toDto)
            .collect(Collectors.toSet());

            for (GenreDto genre : genres)
            {
                genresName.add(genre.getGenreName());
            }
        }
        
        return BookDto.builder()
        .id(book.getId())
        .title(book.getTitle())
        .isbn(book.getIsbn())
        .author(book.getAuthor())
        .genres(genresName)
        .image(book.getImage())
        .available(book.isAvailable())
        .build();
    }

    public Book toEntity (BookDto bookDto)
    {
        if (bookDto == null) return null;
        
        Set<Genre> genres = new HashSet<>();

        if (bookDto.getGenres() != null)
        {
            for (String gDto : bookDto.getGenres())
            {
                Genre g = genreRepository.findGenreByGenreName(gDto);
                genres.add(g);
            }
        }

        return Book.builder()
        .id(bookDto.getId())
        .title(bookDto.getTitle())
        .isbn(bookDto.getIsbn())
        .author(bookDto.getAuthor())
        .genres(genres)
        .image(bookDto.getImage())
        .available(bookDto.isAvailable())
        .build();

    }

    public void updateEntityFromDto(BookDto dto, Book book)
    {
        Set<Genre> genres = null;

        if (dto.getGenres() != null)
        {
            genres = dto.getGenres().stream()
            .map(genreName -> genreRepository.findGenreByGenreName(genreName))
            .collect(Collectors.toSet());
        }

        if (dto.getTitle() != null)   book.setTitle(dto.getTitle());
        if (dto.getIsbn() != null)    book.setIsbn(dto.getIsbn());
        if (dto.getAuthor() != null)  book.setAuthor(dto.getAuthor());
        if (dto.getGenres() != null)  book.setGenres(genres);
        book.setAvailable(dto.isAvailable());

    }
}
