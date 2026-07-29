import { useState, useEffect } from 'react';
import './Author.css'
import '../books/Books.css'
import Card from '../../components/card/Card'
import axios from '../../api/axios'
import { useBookModal } from '../../hooks/useBookModal'
import BookModal from '../../components/bookModal/BookModal'
import Pagination from '../../components/pagination/Pagination'
import { usePagination } from '../../hooks/usePagination'

function Author() {
    const [books, setBooks] = useState([]);
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

        const getAuthors = async () => {
            try {
                const response = await axios.get("books", {
                    signal: controller.signal,
                    headers: { 'Authorization': token ? `Bearer ${token}` : '' }
                });
                if (isMounted) {
                    setBooks(response.data);
                    setAuthorBooks(response.data);
                }

            } catch (err) {
                console.error(err);
            }
        }

        getAuthors();

        return () => {
            isMounted = false;
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

    const uniqueAuthors = [...new Set(books.map(book => book.author))];

    const handleAuthorClick = (author) => {
        setSearchTerm('');
        if (selectedAuthor === author) {
            setSelectedAuthor(null);
            setAuthorBooks(books);
        } else {
            setSelectedAuthor(author);
            setAuthorBooks(books.filter(book => book.author === author));
        }
    }

    return (
        <div className="author-page">
            <h1>Autores</h1>
            <ul className="author-list">
                {uniqueAuthors.map((authorName, index) => (
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

            <BookModal 
                isOpen={isOpen} 
                onClose={closeModal} 
                book={selectedBook}
            />
        </div>
    )
}

export default Author;