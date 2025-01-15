package com.aluracursos.literalura.repository;

import com.aluracursos.literalura.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    @Query("SELECT a FROM Author a WHERE a.birth_year <= :year AND (a.death_year > :year OR a.death_year IS NULL)")
    List<Author> getAuthorsAliveInYear(Integer year);
    @Query("SELECT a FROM Author a WHERE a.name = :name")
    List<Author> getAuthorByName(String name);
}
