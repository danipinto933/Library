package com.DaniCRUD.fullStackBackend.seeder;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import com.DaniCRUD.fullStackBackend.model.Book;
import com.DaniCRUD.fullStackBackend.model.FileData;
import com.DaniCRUD.fullStackBackend.model.Genre;
import com.DaniCRUD.fullStackBackend.model.Reserve;
import com.DaniCRUD.fullStackBackend.model.Role;
import com.DaniCRUD.fullStackBackend.model.User;
import com.DaniCRUD.fullStackBackend.repository.BookRepository;
import com.DaniCRUD.fullStackBackend.repository.FileDataRepository;
import com.DaniCRUD.fullStackBackend.repository.GenreRepository;
import com.DaniCRUD.fullStackBackend.repository.ReserveRepository;
import com.DaniCRUD.fullStackBackend.repository.RoleRepository;
import com.DaniCRUD.fullStackBackend.repository.UserRepository;

@Component
public class DataSeeder implements CommandLineRunner
{
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final BookRepository bookRepository;
    private final ReserveRepository reserveRepository;
    private final FileDataRepository fileDataRepository;
    private Role roleAdmin= new Role();
    private Role roleUser = new Role();
    private BCryptPasswordEncoder bcrypt;

    public DataSeeder(RoleRepository roleRepository, UserRepository userRepository, GenreRepository genreRepository,
        BookRepository bookRepository, ReserveRepository reserveRepository, FileDataRepository fileDataRepository,
        BCryptPasswordEncoder bcrypt)
    {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.genreRepository = genreRepository;
        this.bookRepository = bookRepository;
        this.reserveRepository = reserveRepository;
        this.fileDataRepository = fileDataRepository;
        this.bcrypt = bcrypt;
    }

    @Override
    public void run(String... args) throws Exception
    {
        if(roleRepository.count() == 0)
        {
            roleAdmin.setRole("ADMIN");
            roleUser.setRole("USER");

            roleRepository.saveAll(List.of(roleAdmin, roleUser));
        }

        if (fileDataRepository.count() == 0)
        {
            String pathQuijote = "uploads/covers/Don_Quijote.jpg";
            FileData coverDonQuijote = FileData.builder()
                .name("Don_Quijote.jpg")
                .type("image/jpeg")
                .filePath(pathQuijote)
                .build();

            String pathResplandor = "uploads/covers/El_Resplandor.jpg";
            FileData coverResplandor = FileData.builder()
                .name("El_Resplandor.jpg")
                .type("image/jpeg")
                .filePath(pathResplandor)
                .build();

            String pathHobbit = "uploads/covers/El_Hobbit.jpg";
            FileData coverHobbit = FileData.builder()
                .name("El_Hobbit.jpg")
                .type("image/jpeg")
                .filePath(pathHobbit)
                .build();

            String pathDune = "uploads/covers/Dune.jpg";
            FileData coverDune = FileData.builder()
                .name("Dune.jpg")
                .type("image/jpeg")
                .filePath(pathDune)
                .build();

            String pathSoledad = "uploads/covers/100_Anyos_De_Soledad.jpg";
            FileData coverSoledad = FileData.builder()
                .name("100_Anyos_De_Soledad.jpg")
                .type("image/jpeg")
                .filePath(pathSoledad)
                .build();

            String pathPresagios = "uploads/covers/Buenos_Presagios.jpg";
            FileData coverPresagios = FileData.builder()
                .name("Buenos_Presagios.jpg")
                .type("image/jpeg")
                .filePath(pathPresagios)
                .build();

            String pathHarry1 = "uploads/covers/harry1.jpg";
            FileData coverHarry1 = FileData.builder()
                    .name("harry1.jpg")
                    .type("image/jpeg")
                    .filePath(pathHarry1)
                    .build();

            String pathHarry2 = "uploads/covers/harry2.jpg";
            FileData coverHarry2 = FileData.builder()
                    .name("harry2.jpg")
                    .type("image/jpeg")
                    .filePath(pathHarry2)
                    .build();

            String pathHarry3 = "uploads/covers/harry3.jpg";
            FileData coverHarry3 = FileData.builder()
                    .name("harry3.jpg")
                    .type("image/jpeg")
                    .filePath(pathHarry3)
                    .build();

            String pathHarry4 = "uploads/covers/harry4.jpg";
            FileData coverHarry4 = FileData.builder()
                    .name("harry4.jpg")
                    .type("image/jpeg")
                    .filePath(pathHarry4)
                    .build();

            String pathHarry5 = "uploads/covers/harry5.jpg";
            FileData coverHarry5 = FileData.builder()
                    .name("harry5.jpg")
                    .type("image/jpeg")
                    .filePath(pathHarry5)
                    .build();

            String pathHarry6 = "uploads/covers/harry6.jpg";
            FileData coverHarry6 = FileData.builder()
                    .name("harry6.jpg")
                    .type("image/jpeg")
                    .filePath(pathHarry6)
                    .build();

            String pathHarry7 = "uploads/covers/harry7.jpg";
            FileData coverHarry7 = FileData.builder()
                    .name("harry7.jpg")
                    .type("image/jpeg")
                    .filePath(pathHarry7)
                    .build();

            fileDataRepository.saveAll(List.of(coverDonQuijote, coverResplandor, coverHobbit, coverDune, coverSoledad,
                    coverPresagios, coverHarry1, coverHarry2, coverHarry3, coverHarry4, coverHarry5, coverHarry6, coverHarry7));
        }

        if (userRepository.count() == 0)
        {
            User user1 = new User();
            user1.setUserName("Gato");
            user1.setName("Jack");
            user1.setEmail(("Jack@gmail.com"));
            user1.setPassword(bcrypt.encode("gato1234"));
            user1.setRole(roleRepository.findByRole(roleAdmin.getRole()));

            User user2 = new User();
            user2.setUserName("Dani");
            user2.setName("Daniel Fernández");
            user2.setEmail("danifdez@gmail.com");
            user2.setPassword(bcrypt.encode("dani1234"));
            user2.setRole(roleRepository.findByRole(roleUser.getRole()));

            User user3 = new User();
            user3.setUserName("Eva");
            user3.setName("Eva Guerrero");
            user3.setEmail("evague@gmail.com");
            user3.setPassword(bcrypt.encode("eva1234"));
            user3.setRole(roleRepository.findByRole(roleUser.getRole()));

            User user4 = new User();
            user4.setUserName("Kiwi");
            user4.setName("Kiwi el pajaro");
            user4.setEmail("Kiwi@gmail.com");
            user4.setPassword(bcrypt.encode("kiwi1234"));
            user4.setRole(roleRepository.findByRole(roleAdmin.getRole()));

            User user5 = new User();
            user5.setUserName("Javi");
            user5.setName("Javier Fernandez");
            user5.setEmail("javifdez@gmail.com");
            user5.setPassword(bcrypt.encode("javi1234"));
            user5.setRole(roleRepository.findByRole(roleUser.getRole()));

            User user6 = new User();
            user6.setUserName("Antonia");
            user6.setName("Antonia Pinto");
            user6.setEmail("antoniaP@gmail.com");
            user6.setPassword(bcrypt.encode("antonia1234"));
            user6.setRole(roleRepository.findByRole(roleUser.getRole()));

            User user7 = new User();
            user7.setUserName("Sergio");
            user7.setName("Sergio Pinto");
            user7.setEmail("sergioP@gmail.com");
            user7.setPassword(bcrypt.encode("sergio1234"));
            user7.setRole(roleRepository.findByRole(roleUser.getRole()));

            userRepository.saveAll(List.of(user1, user2, user3, user4, user5, user6, user7));
        }

        if(genreRepository.count() == 0)
        {
            Genre terrorGenre = new Genre();
            terrorGenre.setGenreName("Terror");

            Genre misteryGenre = new Genre();
            misteryGenre.setGenreName("Mistery");

            Genre comedyGenre = new Genre();
            comedyGenre.setGenreName("Comedy");

            Genre classicGenre = new Genre();
            classicGenre.setGenreName("Classic");

            Genre adventureGenre = new Genre();
            adventureGenre.setGenreName("Adventure");

            Genre fantasyGenre = new Genre();
            fantasyGenre.setGenreName("Fantasy");

            genreRepository.saveAll(List.of(terrorGenre, misteryGenre, comedyGenre,
                classicGenre, adventureGenre, fantasyGenre));
        }

        if(bookRepository.count() == 0)
        {

            Book book1 = new Book();
            Set<Genre> genresBook1 = new HashSet<>();
            book1.setTitle("Don Quijote");
            book1.setIsbn("1234-5678-AA");
            book1.setAuthor("Miguel de Cervantes");
            Genre genre1 = new Genre();
            genre1.setId(4L);
            genresBook1.add(genre1);
            book1.setGenres(genresBook1);
            book1.setAvailable(false);
            Optional<FileData> cover1 = fileDataRepository.findByName("Don_Quijote.jpg");
            if (cover1.isPresent())
            {
                book1.setImage(cover1.get());
            }

            Book book2 = new Book();
            Set<Genre> genresBook2 = new HashSet<>();
            book2.setTitle("El Resplandor");
            book2.setIsbn("1234-5678-BB");
            book2.setAuthor("Stephen King");
            Genre genre1Book2 = new Genre();
            Genre genre2Book2 = new Genre();
            genre1Book2.setId(1L);
            genre2Book2.setId(2L);
            genresBook2.add(genre1Book2);
            genresBook2.add(genre2Book2);
            book2.setGenres(genresBook2);
            book2.setAvailable(true);
            Optional<FileData> cover2 = fileDataRepository.findByName("El_Resplandor.jpg");
            if (cover2.isPresent())
            {
                book2.setImage(cover2.get());
            }

            Book book3 = new Book();
            Set<Genre> genresBook3 = new HashSet<>();
            book3.setTitle("El Hobbit");
            book3.setIsbn("1234-5678-CC");
            book3.setAuthor("J.R.R. Tolkien");
            Genre genre1Book3 = new Genre();
            Genre genre2Book3 = new Genre();
            genre1Book3.setId(5L);
            genre2Book3.setId(6L);
            genresBook3.add(genre1Book3);
            genresBook3.add(genre2Book3);
            book3.setGenres(genresBook3);
            book3.setAvailable(false);
            Optional<FileData> cover3 = fileDataRepository.findByName("El_Hobbit.jpg");
            if (cover3.isPresent())
            {
                book3.setImage(cover3.get());
            }

            Book book4 = new Book();
            Set<Genre> genresBook4 = new HashSet<>();
            book4.setTitle("Dune");
            book4.setIsbn("9876-5432-AA");
            book4.setAuthor("Frank Herbert");
            Genre genre1Book4 = new Genre();
            Genre genre2Book4 = new Genre();
            Genre genre3Book4 = new Genre();
            genre1Book4.setId(4L);
            genre2Book4.setId(5L);
            genre3Book4.setId(6L);
            genresBook4.add(genre1Book4);
            genresBook4.add(genre2Book4);
            genresBook4.add(genre3Book4);
            book4.setGenres(genresBook4);
            book4.setAvailable(true);
            Optional<FileData> cover4 = fileDataRepository.findByName("Dune.jpg");
            if (cover4.isPresent())
            {
                book4.setImage(cover4.get());
            }

            Book book5 = new Book();
            Set<Genre> genresBook5 = new HashSet<>();
            book5.setTitle("Cien años de soledad");
            book5.setIsbn("5555-2222-BB");
            book5.setAuthor("Gabriel García Márquez");
            Genre genre1Book5 = new Genre();
            genre1Book5.setId(4L);
            genresBook5.add(genre1Book5);
            book5.setGenres(genresBook5);
            book5.setAvailable(false);
            Optional<FileData> cover5 = fileDataRepository.findByName("100_Anyos_De_Soledad.jpg");
            if (cover5.isPresent())
            {
                book5.setImage(cover5.get());
            }

            Book book6 = new Book();
            Set<Genre> genresBook6 = new HashSet<>();
            book6.setTitle("Buenos presagios");
            book6.setIsbn("1122-3344-EE");
            book6.setAuthor("Terry Pratchett & Neil Gaiman");
            Genre genre1Book6 = new Genre();
            Genre genre2Book6 = new Genre();
            genre1Book6.setId(3L);
            genre2Book6.setId(6L);
            genresBook6.add(genre1Book6);
            genresBook6.add(genre2Book6);
            book6.setGenres(genresBook6);
            book6.setAvailable(true);
            Optional<FileData> cover6 = fileDataRepository.findByName("Buenos_Presagios.jpg");
            if (cover6.isPresent())
            {
                book6.setImage(cover6.get());
            }

            Book book7 = new Book();
            Set<Genre> genresBook7 = new HashSet<>();
            book7.setTitle("Harry Potter y la piedra filosofal");
            book7.setIsbn("1111-2222-33AA");
            book7.setAuthor("J.K.Rowling");
            Genre genre1Book7 = new Genre();
            Genre genre2Book7 = new Genre();
            genre1Book7.setId(2L);
            genre2Book7.setId(5L);
            genresBook7.add(genre1Book7);
            genresBook7.add(genre2Book7);
            book7.setGenres(genresBook7);
            book7.setAvailable(true);
            Optional<FileData> cover7 = fileDataRepository.findByName("harry1.jpg");
            if (cover7.isPresent())
            {
                book7.setImage(cover7.get());
            }

            Book book8 = new Book();
            Set<Genre> genresBook8 = new HashSet<>();
            book8.setTitle("Harry Potter y la camara secreta");
            book8.setIsbn("1111-2222-33BB");
            book8.setAuthor("J.K.Rowling");
            Genre genre1Book8 = new Genre();
            Genre genre2Book8 = new Genre();
            genre1Book8.setId(2L);
            genre2Book8.setId(5L);
            genresBook8.add(genre1Book8);
            genresBook8.add(genre2Book8);
            book8.setGenres(genresBook8);
            book8.setAvailable(true);
            Optional<FileData> cover8 = fileDataRepository.findByName("harry2.jpg");
            if (cover8.isPresent()) {
                book8.setImage(cover8.get());
            }

            Book book9 = new Book();
            Set<Genre> genresBook9 = new HashSet<>();
            book9.setTitle("Harry Potter y el prisionero de Azkaban");
            book9.setIsbn("1111-2222-33CC");
            book9.setAuthor("J.K.Rowling");
            Genre genre1Book9 = new Genre();
            Genre genre2Book9 = new Genre();
            genre1Book9.setId(2L);
            genre2Book9.setId(5L);
            genresBook9.add(genre1Book9);
            genresBook9.add(genre2Book9);
            book9.setGenres(genresBook9);
            book9.setAvailable(true);
            Optional<FileData> cover9 = fileDataRepository.findByName("harry3.jpg");
            if (cover9.isPresent()) {
                book9.setImage(cover9.get());
            }

            Book book10 = new Book();
            Set<Genre> genresBook10 = new HashSet<>();
            book10.setTitle("Harry Potter y el cáliz de fuego");
            book10.setIsbn("1111-2222-33DD");
            book10.setAuthor("J.K.Rowling");
            Genre genre1Book10 = new Genre();
            Genre genre2Book10 = new Genre();
            genre1Book10.setId(2L);
            genre2Book10.setId(5L);
            genresBook10.add(genre1Book10);
            genresBook10.add(genre2Book10);
            book10.setGenres(genresBook10);
            book10.setAvailable(true);
            Optional<FileData> cover10 = fileDataRepository.findByName("harry4.jpg");
            if (cover10.isPresent()) {
                book10.setImage(cover10.get());
            }

            Book book11 = new Book();
            Set<Genre> genresBook11 = new HashSet<>();
            book11.setTitle("Harry Potter y la Orden del Fénix");
            book11.setIsbn("1111-2222-33EE");
            book11.setAuthor("J.K.Rowling");
            Genre genre1Book11 = new Genre();
            Genre genre2Book11 = new Genre();
            genre1Book11.setId(2L);
            genre2Book11.setId(5L);
            genresBook11.add(genre1Book11);
            genresBook11.add(genre2Book11);
            book11.setGenres(genresBook11);
            book11.setAvailable(true);
            Optional<FileData> cover11 = fileDataRepository.findByName("harry5.jpg");
            if (cover11.isPresent()) {
                book11.setImage(cover11.get());
            }

            Book book12 = new Book();
            Set<Genre> genresBook12 = new HashSet<>();
            book12.setTitle("Harry Potter y el misterio del príncipe");
            book12.setIsbn("1111-2222-33FF");
            book12.setAuthor("J.K.Rowling");
            Genre genre1Book12 = new Genre();
            Genre genre2Book12 = new Genre();
            genre1Book12.setId(2L);
            genre2Book12.setId(5L);
            genresBook12.add(genre1Book12);
            genresBook12.add(genre2Book12);
            book12.setGenres(genresBook12);
            book12.setAvailable(true);
            Optional<FileData> cover12 = fileDataRepository.findByName("harry6.jpg");
            if (cover12.isPresent()) {
                book12.setImage(cover12.get());
            }

            Book book13 = new Book();
            Set<Genre> genresBook13 = new HashSet<>();
            book13.setTitle("Harry Potter y las Reliquias de la Muerte");
            book13.setIsbn("1111-2222-33GG");
            book13.setAuthor("J.K.Rowling");
            Genre genre1Book13 = new Genre();
            Genre genre2Book13 = new Genre();
            genre1Book13.setId(2L);
            genre2Book13.setId(5L);
            genresBook13.add(genre1Book13);
            genresBook13.add(genre2Book13);
            book13.setGenres(genresBook13);
            book13.setAvailable(true);
            Optional<FileData> cover13 = fileDataRepository.findByName("harry7.jpg");
            if (cover13.isPresent()) {
                book13.setImage(cover13.get());
            }

            bookRepository.saveAll(List.of(book1, book2, book3, book4, book5, book6, book7, book8, book9, book10, book11, book12, book13));
        }

        if(reserveRepository.count() == 0)
        {

            Reserve reserve1 = new Reserve();
            User user1 = new User();
            user1.setId(1L);
            reserve1.setUser(user1);
            reserve1.setReserveDate(LocalDate.now());
            reserve1.setReturnDate(LocalDate.now().plusMonths(1));
            Set<Book> reservedBooks1 = new HashSet<>();
            Book book3 = new Book();
            Book book5 = new Book();
            book3.setId(3L);
            book5.setId(5L);
            reservedBooks1.add(book3);
            reservedBooks1.add(book5);
            reserve1.setBooks(reservedBooks1);

            Reserve reserve2 = new Reserve();
            User user6 = new User();
            user6.setId(6L);
            reserve2.setUser(user6);
            reserve2.setReserveDate(LocalDate.parse("2025-11-26"));
            reserve2.setReturnDate(LocalDate.now().plusMonths(1));
            Set<Book> reservedBooks2 = new HashSet<>();
            Book book1 = new Book();
            book1.setId(1L);
            reservedBooks2.add(book1);
            reserve2.setBooks(reservedBooks2);

            reserveRepository.saveAll(List.of(reserve1, reserve2));
        }
    }
}