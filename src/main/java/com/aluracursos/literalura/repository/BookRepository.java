package com.aluracursos.literalura.repository;

import com.aluracursos.literalura.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;


@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("SELECT b FROM Book b WHERE b.languages = :languages")
    List<Book> getBookByLanguage(String languages);
    @Query(value = "SELECT b FROM Book b ORDER BY b.download_count DESC")
    List<Book> getTop10MostDownloadedBooks();
}
