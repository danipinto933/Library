package com.library.bookservice.service;

import com.library.bookservice.dto.BookDto;
import com.library.bookservice.exception.BookAlreadyExistsException;
import com.library.bookservice.exception.ResourceNotFoundException;
import com.library.bookservice.mapper.BookMapper;
import com.library.bookservice.model.Book;
import com.library.bookservice.model.FileData;
import com.library.bookservice.model.Genre;
import com.library.bookservice.repository.BookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BookServiceImpl implements BookService {
    
    private final BookRepository bookRepository;
    private final GenreService genreService;
    private final BookMapper bookMapper;
    private final StorageService storageService;

    public BookServiceImpl(BookRepository bookRepository, GenreService genreService,
                           BookMapper bookMapper, StorageService storageService) {
        this.bookRepository = bookRepository;
        this.genreService = genreService;
        this.bookMapper = bookMapper;
        this.storageService = storageService;
    }

    @Override
    public ResponseEntity<Book> addBook(BookDto bookDto, MultipartFile file) {
        Book book = bookMapper.toEntity(bookDto);

        if (bookRepository.findByTitleIgnoreCase(book.getTitle()) != null) {
            throw new BookAlreadyExistsException("El libro con el título '" + book.getTitle() + "' ya está registrado");
        }
        if (bookRepository.findByIsbn(book.getIsbn()) != null) {
            throw new BookAlreadyExistsException("El libro con el ISBN '" + book.getIsbn() + "' ya está registrado");
        }

        Set<Genre> genres = new HashSet<>();
        for (Genre g : book.getGenres()) {
            Genre realGenre = genreService.findGenreByName(g.getGenreName());
            if (realGenre == null) {
                throw new ResourceNotFoundException("Género con el nombre " + g.getGenreName() + " no encontrado");
            }
            genres.add(realGenre);
        }
        book.setGenres(genres);

        if (file != null && !file.isEmpty()) {
            FileData image = storageService.uploadImageToFileSystem(file);
            book.setImage(image);
        }

        bookRepository.save(book);
        return new ResponseEntity<>(book, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<BookDto>> findAllBooks() {
        List<Book> books = bookRepository.findAll();
        List<BookDto> booksDto = books.stream().map(bookMapper::toDto).toList();
        return new ResponseEntity<>(booksDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<BookDto>> findAllAvailableBooks() {
        // FASE VII: Uso de consulta directa a BBDD en vez de findAll + stream filter
        List<Book> availableBooks = bookRepository.findByAvailable(true);
        List<BookDto> booksDto = availableBooks.stream().map(bookMapper::toDto).toList();
        return new ResponseEntity<>(booksDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<BookDto>> findAllNotAvailableBooks() {
        // FASE VII: Uso de consulta directa a BBDD en vez de findAll + stream filter
        List<Book> notAvailableBooks = bookRepository.findByAvailable(false);
        List<BookDto> booksDto = notAvailableBooks.stream().map(bookMapper::toDto).toList();
        return new ResponseEntity<>(booksDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BookDto> findByTitle(String bookTitle) {
        Book book = bookRepository.findByTitleIgnoreCase(bookTitle);
        if (book == null) {
            List<Book> books = bookRepository.findByTitleContainingIgnoreCase(bookTitle);
            if (books != null && !books.isEmpty()) {
                book = books.get(0);
            }
        }
        if (book == null) {
            throw new ResourceNotFoundException("Libro con el título " + bookTitle + " no encontrado");
        }
        return new ResponseEntity<>(bookMapper.toDto(book), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BookDto> findByIsbn(String bookIsbn) {
        Book book = bookRepository.findByIsbnIgnoreCase(bookIsbn);
        if (book == null) {
            book = bookRepository.findByIsbn(bookIsbn);
        }
        if (book == null) {
            throw new ResourceNotFoundException("Libro con el ISBN " + bookIsbn + " no encontrado");
        }
        return new ResponseEntity<>(bookMapper.toDto(book), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<BookDto>> findByAuthor(String bookAuthor) {
        List<Book> books = bookRepository.findByAuthorIgnoreCase(bookAuthor);
        if (books == null || books.isEmpty()) {
            books = bookRepository.findByAuthorContainingIgnoreCase(bookAuthor);
        }
        if (books == null || books.isEmpty()) {
            throw new ResourceNotFoundException("Libros con el autor " + bookAuthor + " no encontrado");
        }
        List<BookDto> booksDto = books.stream().map(bookMapper::toDto).toList();
        return new ResponseEntity<>(booksDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<BookDto>> findByGenre(String bookGenre) {
        List<Book> books = bookRepository.findByGenres_GenreNameIgnoreCase(bookGenre);
        if (books == null || books.isEmpty()) {
            books = bookRepository.findByGenres_GenreNameContainingIgnoreCase(bookGenre);
        }
        if (books == null || books.isEmpty()) {
            throw new ResourceNotFoundException("Libros con el genero " + bookGenre + " no encontrado");
        }
        List<BookDto> booksDto = books.stream().map(bookMapper::toDto).toList();
        return new ResponseEntity<>(booksDto, HttpStatus.OK);
    }

    @Override
    public Book findBookByid(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro con el ID " + id + " no encontrado"));
    }

    @Override
    public ResponseEntity<BookDto> findByIdDto(Long id) {
        Book book = findBookByid(id);
        return new ResponseEntity<>(bookMapper.toDto(book), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Book> updateBook(Long id, BookDto bookDto, MultipartFile file) {
        Book oldBook = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro con el ID " + id + " no encontrado"));
        
        bookMapper.updateEntityFromDto(bookDto, oldBook);
        
        Set<Genre> newGenres = new HashSet<>();
        if (bookDto.getGenres() != null) {
            for (String g : bookDto.getGenres()) {
                Genre realGenre = genreService.findGenreByName(g);
                if (realGenre == null) {
                    throw new ResourceNotFoundException("Género con el nombre " + g + " no encontrado");
                }
                newGenres.add(realGenre);
            }
            oldBook.setGenres(newGenres);
        }
        
        if (file != null && !file.isEmpty()) {
            FileData newImage = storageService.uploadImageToFileSystem(file);
            oldBook.setImage(newImage);
        }

        Book updateBook = bookRepository.save(oldBook);
        return new ResponseEntity<>(updateBook, HttpStatus.OK);
    }

    @Override
    public String deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro con el ID " + id + " no encontrado"));
        bookRepository.delete(book);
        return "Libro eliminado correctamente";
    }
}
