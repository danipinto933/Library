package com.DaniCRUD.fullStackBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.DaniCRUD.fullStackBackend.model.Genre;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long>
{
    public Genre findGenreByGenreName (String genreName);
}
