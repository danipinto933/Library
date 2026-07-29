package com.library.bookservice.mapper;

import com.library.bookservice.dto.BookDto;
import com.library.bookservice.model.Book;
import com.library.bookservice.model.Genre;
import com.library.bookservice.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BookMapper {

    @Autowired
    private GenreMapper genreMapper;

    @Autowired
    private GenreRepository genreRepository;

    public BookDto toDto(Book book) {
        if (book == null) return null;

        Set<String> genresName = new HashSet<>();
        if (book.getGenres() != null) {
            genresName = book.getGenres().stream()
                    .map(Genre::getGenreName)
                    .collect(Collectors.toSet());
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

    public Book toEntity(BookDto bookDto) {
        if (bookDto == null) return null;
        
        Set<Genre> genres = new HashSet<>();
        if (bookDto.getGenres() != null) {
            for (String gDto : bookDto.getGenres()) {
                Genre g = genreRepository.findGenreByGenreName(gDto);
                if (g != null) {
                    genres.add(g);
                }
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

    public void updateEntityFromDto(BookDto dto, Book book) {
        if (dto.getTitle() != null) book.setTitle(dto.getTitle());
        if (dto.getIsbn() != null) book.setIsbn(dto.getIsbn());
        if (dto.getAuthor() != null) book.setAuthor(dto.getAuthor());
        
        if (dto.getGenres() != null) {
            Set<Genre> genres = dto.getGenres().stream()
                    .map(genreRepository::findGenreByGenreName)
                    .filter(g -> g != null)
                    .collect(Collectors.toSet());
            book.setGenres(genres);
        }
        
        book.setAvailable(dto.isAvailable());
    }
}
