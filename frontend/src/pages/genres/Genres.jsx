import { useState, useEffect } from 'react';
import './Genres.css'
import '../books/Books.css'
import axios from '../../api/axios'
import Card from '../../components/card/Card'
import { useBookModal } from '../../hooks/useBookModal'
import BookModal from '../../components/bookModal/BookModal'
import Pagination from '../../components/pagination/Pagination'
import { usePagination } from '../../hooks/usePagination'
import LoadingSpinner from '../../components/loadingSpinner/LoadingSpinner'


function Genres() {
    const [genres, setGenres] = useState([]);
    const [books, setBooks] = useState([]);
    const [loading, setLoading] = useState(true);
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
        const matchesGenre = book.genres?.some(g => (typeof g === 'string' ? g : g?.genreName || g?.name || '').toLowerCase().includes(term));
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
          setLoading(true);
          const config = {
            signal: controller.signal,
            headers: { 'Authorization': token ? `Bearer ${token}` : '' }
          };
          const [responseGenres, responseBooks] = await Promise.all([
            axios.get('genres', config),
            axios.get('books', config)
          ]);

          let reservesData = [];
          try {
            const reservesRes = await axios.get('reserves', config);
            reservesData = reservesRes.data || [];
          } catch (resErr) {
            console.warn("No se pudieron cargar las reservas para cálculo de demanda:", resErr);
          }

          if (isMounted) {
            const allGenres = responseGenres.data || [];
            const allBooks = responseBooks.data || [];
            setBooks(allBooks);
            setGenreBooks(allBooks);

            const genreDemandMap = {};
            reservesData.forEach(res => {
              if (Array.isArray(res.books)) {
                res.books.forEach(b => {
                  if (Array.isArray(b.genres)) {
                    b.genres.forEach(gName => {
                      genreDemandMap[gName] = (genreDemandMap[gName] || 0) + 1;
                    });
                  }
                });
              }
            });

            const hasReserveDemand = Object.keys(genreDemandMap).length > 0;
            if (!hasReserveDemand) {
              allBooks.forEach(b => {
                if (Array.isArray(b.genres)) {
                  b.genres.forEach(gName => {
                    genreDemandMap[gName] = (genreDemandMap[gName] || 0) + 1;
                  });
                }
              });
            }

            const sortedGenres = [...allGenres].sort((a, b) => {
              const countA = genreDemandMap[a.genreName] || 0;
              const countB = genreDemandMap[b.genreName] || 0;
              return countB - countA;
            });

            setGenres(sortedGenres.slice(0, 5));
          }
        }
        catch (err){
          if (err.name !== 'CanceledError') { 
            console.error("Error al obtener datos:", err);
          }
        } finally {
          if (isMounted) setLoading(false);
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
        setGenreBooks(books.filter(book => book.genres?.some(g => (typeof g === 'string' ? g : g?.genreName || g?.name || '').toLowerCase() === genre.genreName.toLowerCase())));
      }
    }

  return (
    <div className="genre-page">
            <h1>Géneros</h1>
            {loading ? (
              <LoadingSpinner message="exportando libros..." />
            ) : (
              <>
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
              </>
            )}

            <BookModal 
              isOpen={isOpen} 
              onClose={closeModal} 
              book={selectedBook}
            />
        </div>
  )
}

export default Genres;