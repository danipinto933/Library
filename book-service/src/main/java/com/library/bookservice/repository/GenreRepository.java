package com.library.bookservice.repository;

import com.library.bookservice.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    Genre findGenreByGenreName(String genreName);
}
