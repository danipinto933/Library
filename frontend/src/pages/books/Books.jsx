import './Books.css'
import Card from '../../components/card/Card'
import axios from '../../api/axios'
import { useEffect, useState } from 'react'
import { useBookModal } from '../../hooks/useBookModal'
import BookModal from '../../components/bookModal/BookModal'
import Pagination from '../../components/pagination/Pagination'
import { usePagination } from '../../hooks/usePagination'
import LoadingSpinner from '../../components/loadingSpinner/LoadingSpinner'

function Books() {
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const { isOpen, selectedBook, openModal, closeModal } = useBookModal();
  const filteredBooks = books.filter(book => {
    const term = searchTerm.toLowerCase();
    const matchesTitle = book.title?.toLowerCase().includes(term);
    const matchesAuthor = book.author?.toLowerCase().includes(term);
    const matchesGenre = book.genres?.some(g => (typeof g === 'string' ? g : g?.genreName || g?.name || '').toLowerCase().includes(term));
    return matchesTitle || matchesAuthor || matchesGenre;
  });
  const {
    currentPage,
    totalPages,
    currentItems: currentBooks,
    nextPage,
    prevPage
  } = usePagination(filteredBooks, 8);

  useEffect(() => {
    let isMounted = true;
    const controller = new AbortController();

    const getBooks = async () => {
      try {
        setLoading(true);
        const response = await axios.get('books/5', {
          signal: controller.signal
        });
        if (isMounted) setBooks(response.data);
      }
      catch (err) {
        console.error(err);
      } finally {
        if (isMounted) setLoading(false);
      }
    }

    getBooks();

    return () => {
      isMounted = false;
      controller.abort();
    }

  }, [])

  return (
    <div className="books-container">
      <div className="search-bar-container">
        <input
          type="text"
          placeholder="🔍 Buscar libros por título, género o autor..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="book-search-input"
        />
      </div>

      {loading ? (
        <LoadingSpinner message="exportando libros..." />
      ) : (
        <>
          <div className="books-grid">
            {currentBooks.map((book) => (
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

export default Books;