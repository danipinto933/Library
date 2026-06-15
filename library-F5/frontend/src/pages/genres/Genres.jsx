import { useState, useEffect } from 'react';
import './Genres.css'
import '../books/Books.css'
import axios from '../../api/axios'
import Card from '../../components/card/Card'
import { useBookModal } from '../../hooks/useBookModal'
import BookModal from '../../components/bookModal/BookModal'
import Pagination from '../../components/pagination/Pagination'
import { usePagination } from '../../hooks/usePagination'


function Genres() {
    const [genres, setGenres] = useState([]);
    const [books, setBooks] = useState([]);
    const [selectedGenre, setSelectedGenre] = useState(null);
    const [genreBooks, setGenreBooks] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const sessionString = localStorage.getItem('user_session');
    const token = sessionString ? JSON.parse(sessionString).accessToken : null;
    const { isOpen, selectedBook, openModal, closeModal } = useBookModal();
    const filteredBooks = genreBooks.filter(book => {
        const term = searchTerm.toLowerCase();
        const matchesTitle = book.title?.toLowerCase().includes(term);
        const matchesAuthor = book.author?.toLowerCase().includes(term);
        const matchesGenre = book.genres?.some(g => g.toLowerCase().includes(term));
        return matchesTitle || matchesAuthor || matchesGenre;
    });
    const { 
        currentPage, 
        totalPages, 
        currentItems: paginatedBooks, 
        nextPage, 
        prevPage 
      } = usePagination(filteredBooks, 8);
    

    useEffect(() => {
      let isMounted = true;
      const controller = new AbortController();

      const fetchData = async () => {
        try {
          const config = {
            signal: controller.signal,
            headers: { 'Authorization': token ? `Bearer ${token}` : '' }
          };
          const [responseGenres, responseBooks] = await Promise.all([
            axios.get('genres', config),
            axios.get('books', config)
          ]);
          if (isMounted) {
            setGenres(responseGenres.data);
            setBooks(responseBooks.data);
            setGenreBooks(responseBooks.data);
          }
        }
        catch (err){
          if (err.name !== 'CanceledError') { 
            console.error("Error al obtener datos:", err);
          }
        }
      }

      fetchData();

      return() => {
        isMounted = false;
        controller.abort();
      }
    }, [token])

    useEffect(() => {
        const handleReservaExitosa = (event) => {
            const librosReservados = event.detail; 
            const idsReservados = librosReservados.map(libro => libro.id);

            setBooks(prevBooks => 
                prevBooks.map(book => 
                    idsReservados.includes(book.id) ? { ...book, available: false } : book
                )
            );
            setGenreBooks(prevBooks => 
                prevBooks.map(book => 
                    idsReservados.includes(book.id) ? { ...book, available: false } : book
                )
            );
        };

        window.addEventListener('reservaConfirmada', handleReservaExitosa);

        return () => {
            window.removeEventListener('reservaConfirmada', handleReservaExitosa);
        };
    }, []);

    const handleGenreClick = (genre) => {
      setSearchTerm('');
      if (selectedGenre?.id === genre.id) {
        setSelectedGenre(null);
        setGenreBooks(books);
      } else {
        setSelectedGenre(genre);
        setGenreBooks(books.filter(book => book.genres?.includes(genre.genreName)));
      }
    }

  return (
    <div className="genre-page">
            <h1>Géneros</h1>
            <ul className="genre-list">
                {genres.map((g, index) => (
                    <li 
                      key={index} 
                      onClick={() => handleGenreClick(g)}
                      className={selectedGenre?.id === g.id ? 'active' : ''}
                    >
                      {g.genreName}
                    </li> 
                ))}
            </ul>
            <div className="search-bar-container">
              <input
                type="text"
                placeholder="🔍 Buscar libros en este género por título..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="book-search-input"
              />
            </div>
            <div className="books-grid">
              {paginatedBooks.map((book) => (
                <Card 
                key={book.id} 
                book={book}
                onClick={() => openModal(book)}
                />
              ))}
            </div>

            <Pagination 
              currentPage={currentPage}
              totalPages={totalPages}
              prevPage={prevPage}
              nextPage={nextPage}
            />

            <BookModal 
              isOpen={isOpen} 
              onClose={closeModal} 
              book={selectedBook}
            />
        </div>
  )
}

export default Genres;