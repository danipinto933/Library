package com.DaniCRUD.fullStackBackend.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.DaniCRUD.fullStackBackend.dto.response.BookDto;
import com.DaniCRUD.fullStackBackend.exception.ResourceNotFoundException;
import com.DaniCRUD.fullStackBackend.exception.BookAlreadyExistsException;
import com.DaniCRUD.fullStackBackend.mapper.BookMapper;
import com.DaniCRUD.fullStackBackend.model.Book;
import com.DaniCRUD.fullStackBackend.model.FileData;
import com.DaniCRUD.fullStackBackend.model.Genre;
import com.DaniCRUD.fullStackBackend.repository.BookRepository;


@Service
public class BookServiceImpl implements BookService
{
    private BookRepository bookRepository;
    private GenreServiceImpl genreServiceImpl;
    private BookMapper bookMapper;
    private StorageServiceImpl storageServiceImpl;

    public BookServiceImpl(BookRepository bookRepository, GenreServiceImpl genreServiceImpl,
        BookMapper bookMapper, StorageServiceImpl storageServiceImpl)
    {
        this.bookRepository = bookRepository;
        this.genreServiceImpl = genreServiceImpl;
        this.bookMapper = bookMapper;
        this.storageServiceImpl= storageServiceImpl;
    }
    
    @Override
    public ResponseEntity<Book> addBook (BookDto bookDto, MultipartFile file)
    {
        Book book = bookMapper.toEntity(bookDto);

        if (bookRepository.findByTitle(book.getTitle()) != null)
        {
            throw new BookAlreadyExistsException("El libro con el título '" + book.getTitle() + "' ya está registrado");
        }
        if (bookRepository.findByIsbn(book.getIsbn()) != null)
        {
            throw new BookAlreadyExistsException("El libro con el ISBN '" + book.getIsbn() + "' ya está registrado");
        }

        Set<Genre> genres = new HashSet<>();
        for (Genre g : book.getGenres()) 
        {
            Genre realGenre = genreServiceImpl.findGenreByGenreId(g.getId());
                if(realGenre == null)
                {
                    throw new ResourceNotFoundException("Género con el nombre " + g.getGenreName() + " no encontrado");
                }
            genres.add(realGenre);
        }
        book.setGenres(genres);

        FileData image = storageServiceImpl.uploadImageToFileSystem(file);
        book.setImage(image);

        bookRepository.save(book);
        return new ResponseEntity<>(book, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<BookDto>> findAllBooks() 
    {
        List<Book> books = bookRepository.findAll();

        List<BookDto> booksDto = books.stream()
        .map(bookMapper::toDto)
        .toList();

        return new ResponseEntity<>(booksDto, HttpStatus.OK);  
    }
        
    @Override
    public ResponseEntity<List<BookDto>> findAllAvailableBooks() 
    {
        List<Book> books = bookRepository.findAll();

        List<BookDto> availableBooks = books.stream()
            .filter(Book::isAvailable)
            .map(bookMapper::toDto)
            .toList();

        return new ResponseEntity<>(availableBooks, HttpStatus.OK);
    }

    public ResponseEntity<List<BookDto>> findAllNotAvailableBooks()
    {
        List<Book> books = bookRepository.findAll();

        List<BookDto> notAvailableBooks = books.stream()
            .filter(book -> !book.isAvailable())
            .map(bookMapper::toDto)
            .toList();

        return new ResponseEntity<>(notAvailableBooks, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BookDto> findByTitle (String bookTitle)
    {
        Book book = bookRepository.findByTitle(bookTitle);

        if (book == null)
        {
            throw new ResourceNotFoundException("Libro con el título " + bookTitle + " no encontrado");
        }

        BookDto bookDto = bookMapper.toDto(book);
        return new ResponseEntity<>(bookDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BookDto> findByIsbn(String bookIsbn)
    {
        Book book = bookRepository.findByIsbn(bookIsbn);

        if (book == null)
        {
            throw new ResourceNotFoundException("Libro con el ISBN " + bookIsbn + " no encontrado");
        }

        BookDto bookDto = bookMapper.toDto(book);
        return new ResponseEntity<>(bookDto, HttpStatus.OK);
    }
    
    @Override
    public ResponseEntity<List<BookDto>> findByAuthor(String bookAuthor)
    {
        List<Book> books = bookRepository.findByAuthor(bookAuthor);

        if (books == null)
        {
            throw new ResourceNotFoundException("Libros con el autor " + bookAuthor + " no encontrado");
        }

        List<BookDto> booksDto = books.stream()
        .map(bookMapper::toDto)
        .toList();
        return new ResponseEntity<>(booksDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<BookDto>> findByGenre(String bookGenre)
    {
        List<Book> books = bookRepository.findByGenres_GenreName(bookGenre);

        if (books == null)
        {
            throw new ResourceNotFoundException("Libros con el genero " + bookGenre + " no encontrado");
        }

        List<BookDto> booksDto = books.stream()
        .map(bookMapper::toDto)
        .toList();
        return new ResponseEntity<>(booksDto, HttpStatus.OK);
    }

    @Override
    public Book findBookByid (Long id)
    {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Libro con el ID " + id + " no encontrado"));
        return book;
    }

    @Override
    public ResponseEntity<Book> updateBook(Long id, BookDto bookDto, MultipartFile file)
    {
        Book oldBook = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Libro con el ID " + id + " no encontrado"));
        System.out.println("el estado del oldBook: "+oldBook.isAvailable());
        bookMapper.updateEntityFromDto(bookDto, oldBook);
        System.out.println("el estado del bookDto: "+bookDto.isAvailable());
        Set<Genre> newGenres = new HashSet<>();
        for (String g : bookDto.getGenres()) 
        {
            Genre realGenre = genreServiceImpl.findGenreByName(g);
                if(realGenre == null)
                {
                    throw new ResourceNotFoundException("Género con el nombre " + g + " no encontrado");
                }
            newGenres.add(realGenre);
        }
        oldBook.setGenres(newGenres);
        
        if (file != null && !file.isEmpty())
        {
            FileData newImage = storageServiceImpl.uploadImageToFileSystem(file);
            oldBook.setImage(newImage);
        }

        Book updateBook = bookRepository.save(oldBook);
        return new ResponseEntity<>(updateBook, HttpStatus.OK);
    }

    @Override
    public String deleteBook(Long id)
    {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Libro con el ID " + id + " no encontrado"));
        bookRepository.delete(book);
        return "Libro eliminado correctamente";
    }
}