package com.aluracursos.literalura.principal;

import com.aluracursos.literalura.model.Author;
import com.aluracursos.literalura.model.Book;
import com.aluracursos.literalura.model.DataBook;
import com.aluracursos.literalura.repository.AuthorRepository;
import com.aluracursos.literalura.repository.BookRepository;
import com.aluracursos.literalura.service.ConsumeAPI;
import com.aluracursos.literalura.service.ConvierteDatos;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumeAPI consumeApi = new ConsumeAPI();
    private final String URL_BASE = "https://gutendex.com/books/";
    private ConvierteDatos convierteDatos = new ConvierteDatos();
    private BookRepository bookRepository;
    private AuthorRepository authorRepository;
    private List<Book> books;
    private List<Author> authors;

    public Principal(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Búsqueda de libro por título
                    2 - Lista de todos los libros
                    3 - Búsqueda de autor por nombre
                    4 - Lista de autores
                    5 - Listar autores vivos en determinado año
                    6 - Búsqueda de libros por lenguaje
                    7 - Top 10 libros más descargados
                                  
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2:
                    listarTodosLosLibros();
                    break;
                case 3:
                    buscarAutorPorNombre();
                    break;
                case 4:
                    listarTodosLosAutores();
                    break;
                case 5:
                    listarAutoresVivosEnAno();
                    break;
                case 6:
                    buscarLibroPorLenguaje();
                    break;
                case 7:
                    obtenerTop10LibrosDescargados();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }

    private DataBook getBookData() {
        System.out.println("Ingrese el nombre del libro ...");
        var search = teclado.nextLine().toLowerCase().replace(" ", "%20");
        var json = consumeApi.obtenerDatos(URL_BASE +
                "?search=" +
                search);

        DataBook dataBook = convierteDatos.obtenerDatos(json, DataBook.class);
        return dataBook;
    }

    private void buscarLibroPorTitulo() {
        DataBook dataBook = getBookData();

        try {
            Book book = new Book(dataBook.results().get(0));
            Author author = new Author(dataBook.results().get(0).authorList().get(0));

            System.out.println("""
                    Libro[
                        Título: %s
                        Autor: %s
                        Lenguaje: %s
                        Descargas: %s
                    ]
                    """.formatted(book.getTitle(),
                    book.getAuthor_name(),
                    book.getLanguages(),
                    book.getDownload_count().toString()));

            authorRepository.save(author);
            bookRepository.save(book);

        } catch (Exception e) {
            System.out.println("No se encontró ese libro o ocurrió un error: " + e.getMessage());
        }
    }

    private void listarTodosLosLibros() {
        books = bookRepository.findAll();
        books.stream().forEach(
                b -> {
                    System.out.println("""
                            Título: %s
                            Autor: %s
                            Lenguaje: %s
                            Descargas: %s                                
                                """.formatted(
                            b.getTitle(),
                            b.getAuthor_name(),
                            b.getLanguages(),
                            b.getDownload_count().toString()
                    ));
                });
    }

    private void buscarAutorPorNombre() {
        System.out.println("Ingrese el nombre del autor ...");
        var nombreAutor = teclado.nextLine().toLowerCase();
        List<Author> authors = authorRepository.getAuthorByName(nombreAutor);

        if (authors.isEmpty()) {
            System.out.println("No se encontraron autores con ese nombre.");
        } else {
            authors.forEach(
                    a -> {
                        System.out.println("""
                                Autor: %s
                                Año de nacimiento: %s
                                Año de defunción: %s
                                """.formatted(
                                a.getName(),
                                a.getBirth_year(),
                                a.getDeath_year()
                        ));
                    });
        }
    }

    private void listarTodosLosAutores() {
        authors = authorRepository.findAll();
        authors.stream().forEach(
                a -> {
                    System.out.println("""
                            Autor: %s
                            Año de nacimiento: %s
                            Año de defunción: %s
                            """.formatted(
                            a.getName(),
                            a.getBirth_year(),
                            a.getDeath_year()
                    ));
                });
    }

    private void listarAutoresVivosEnAno() {
        System.out.println("Ingrese el año ...");
        Integer year = teclado.nextInt();
        List<Author> authorsAlive = authorRepository.getAuthorsAliveInYear(year);
        authorsAlive.forEach(a -> {
            System.out.println("""
                    Autor: %s
                    Año de nacimiento: %s
                    Año de defunción: %s
                    """.formatted(
                    a.getName(),
                    a.getBirth_year(),
                    a.getDeath_year() != null ? a.getDeath_year() : "Actualmente vivo"
            ));
        });
    }

    private void buscarLibroPorLenguaje() {
        System.out.println("""
                Seleccione el lenguaje del libro a buscar:                
                1 - En (Ingles)
                2 - Es (Español)
                """);
        try {

            var lenOption = teclado.nextInt();
            teclado.nextLine();

            switch (lenOption) {
                case 1:
                    books = bookRepository.getBookByLanguage("en");
                    break;
                case 2:
                    books = bookRepository.getBookByLanguage("es");
                    break;

                default:
                    System.out.println("Opción inválida");
            }

            books.stream().forEach(
                    b -> {
                        System.out.println("""
                                Título: %s
                                Autor: %s
                                Lenguaje: %s
                                Descargas: %s
                                    """.formatted(
                                b.getTitle(),
                                b.getAuthor_name(),
                                b.getLanguages(),
                                b.getDownload_count().toString()
                        ));
                    });
        } catch (
                Exception e) {
            System.out.println("Opción inválida");
        }
    }

    public void obtenerTop10LibrosDescargados() {
        List<Book> books = bookRepository.getTop10MostDownloadedBooks();

        List<Book> top10 = books.stream()
                .limit(10)
                .collect(Collectors.toList());

        top10.forEach(
                b -> {
                    System.out.println("""
                            Título: %s
                            Autor: %s
                            Lenguaje: %s
                            Descargas: %s
                                """.formatted(
                            b.getTitle(),
                            b.getAuthor_name(),
                            b.getLanguages(),
                            b.getDownload_count().toString()
                    ));
                });
    }
}
