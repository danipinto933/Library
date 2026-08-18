import { useState, useEffect } from 'react';
import './Author.css'
import '../books/Books.css'
import Card from '../../components/card/Card'
import axios from '../../api/axios'
import { useBookModal } from '../../hooks/useBookModal'
import BookModal from '../../components/bookModal/BookModal'
import Pagination from '../../components/pagination/Pagination'
import { usePagination } from '../../hooks/usePagination'
import LoadingSpinner from '../../components/loadingSpinner/LoadingSpinner'

function Author() {
    const [books, setBooks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [topAuthors, setTopAuthors] = useState([]);
    const [selectedAuthor, setSelectedAuthor] = useState(null);
    const [authorBooks, setAuthorBooks] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const sessionString = localStorage.getItem('user_session');
    const token = sessionString ? JSON.parse(sessionString).accessToken : null;
    const { isOpen, selectedBook, openModal, closeModal } = useBookModal();
    const filteredBooks = authorBooks.filter(book => {
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

        const getAuthorsData = async () => {
            try {
                setLoading(true);
                const config = {
                    signal: controller.signal,
                    headers: { 'Authorization': token ? `Bearer ${token}` : '' }
                };

                const booksRes = await axios.get("books", config);
                let reservesData = [];
                try {
                    const reservesRes = await axios.get("reserves", config);
                    reservesData = reservesRes.data || [];
                } catch (resErr) {
                    console.warn("No se pudieron cargar las reservas para cálculo de demanda de autores:", resErr);
                }

                if (isMounted) {
                    const allBooks = booksRes.data || [];
                    setBooks(allBooks);
                    setAuthorBooks(allBooks);

                    const authorDemandMap = {};
                    reservesData.forEach(res => {
                        if (Array.isArray(res.books)) {
                            res.books.forEach(b => {
                                if (b.author) {
                                    authorDemandMap[b.author] = (authorDemandMap[b.author] || 0) + 1;
                                }
                            });
                        }
                    });

                    const hasReserveDemand = Object.keys(authorDemandMap).length > 0;
                    if (!hasReserveDemand) {
                        allBooks.forEach(b => {
                            if (b.author) {
                                authorDemandMap[b.author] = (authorDemandMap[b.author] || 0) + 1;
                            }
                        });
                    }

                    const uniqueAuthorsList = [...new Set(allBooks.map(b => b.author).filter(Boolean))];
                    uniqueAuthorsList.sort((a, b) => {
                        const countA = authorDemandMap[a] || 0;
                        const countB = authorDemandMap[b] || 0;
                        return countB - countA;
                    });

                    setTopAuthors(uniqueAuthorsList.slice(0, 5));
                }

            } catch (err) {
                if (err.name !== 'CanceledError') {
                    console.error("Error al obtener autores:", err);
                }
            } finally {
                if (isMounted) setLoading(false);
            }
        }

        getAuthorsData();

        return () => {
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
            setAuthorBooks(prevBooks => 
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

    const handleAuthorClick = (author) => {
        setSearchTerm('');
        if (selectedAuthor === author) {
            setSelectedAuthor(null);
            setAuthorBooks(books);
        } else {
            setSelectedAuthor(author);
            setAuthorBooks(books.filter(book => book.author?.toLowerCase() === author.toLowerCase()));
        }
    }

    return (
        <div className="author-page">
            <h1>Autores</h1>
            {loading ? (
                <LoadingSpinner message="exportando libros..." />
            ) : (
                <>
                    <ul className="author-list">
                        {topAuthors.map((authorName, index) => (
                            <li 
                                key={index} 
                                onClick={() => handleAuthorClick(authorName)}
                                className={selectedAuthor === authorName ? 'active' : ''}
                            >
                                {authorName}
                            </li> 
                        ))}
                    </ul>
                    <div className="search-bar-container">
                      <input
                        type="text"
                        placeholder="🔍 Buscar libros de este autor por título..."
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

export default Author;