import { useState, useEffect } from 'react';
import axios from '../api/axios';
import Button from '../components/Button';
import './BooksDashboard.css';
import LoadingSpinner from './loadingSpinner/LoadingSpinner';

const BooksSection = ({ activeAction, handleSectionClick, token }) => {
    const [bookTitle, setBookTitle] = useState('');
    const [bookIsbn, setBookIsbn] = useState('');
    const [bookAuthor, setBookAuthor] = useState('');
    const [bookImage, setBookImage] = useState(null);
    const [bookGenres, setBookGenres] = useState([]);
    const [bookAvailable, setBookAvailable] = useState(false);
    const [displayedBooks, setDisplayedBooks] = useState([]);
    const [loading, setLoading] = useState(true);

    const [genresBook, setGenresBook] = useState([]);
    const [books, setBooks] = useState([]);
    const [selectedBook, setSelectedBook] = useState(null);
    const [selectedBookForView, setSelectedBookForView] = useState(null);
    const [updatedBookTitle, setUpdatedBookTitle] = useState('');
    const [updatedBookIsbn, setUpdatedBookIsbn] = useState('');
    const [updatedBookAuthor, setUpdatedBookAuthor] = useState('');
    const [updatedBookImage, setUpdatedBookImage] = useState(null);
    const [updatedBookGenres, setUpdatedBookGenres] = useState([]);
    const [updatedBookAvailable, setUpdatedBookAvailable] = useState(null);

    useEffect(() => {
        let isMounted = true;
        const controller = new AbortController();
    
        const getBooks = async () => {
            try {
                setLoading(true);
                const response = await axios.get('books', {
                signal: controller.signal,
                headers: { 'Authorization': token ? `Bearer ${token}` : '' }
            });
            if (isMounted) {
                setBooks(response.data);
                setDisplayedBooks(response.data);
            }
            }
            catch (err){
                console.error(err);
            } finally {
                if (isMounted) setLoading(false);
            }
        }
        const getGenres = async () => {
            try {
                    const response = await axios.get('genres', {
                    signal: controller.signal,
                    headers: { 'Authorization': token ? `Bearer ${token}` : '' }
                    });
                    if (isMounted) setGenresBook(response.data);
                }
                catch (err){
                    console.error(err);
                }
            }
        
        getBooks();
        getGenres();
    
        return() => {
            isMounted = false;
            controller.abort();
        }}, [token])

        const handleSubmitCreateBook = async (e) => {
            e.preventDefault();

            const formData = new FormData();
            formData.append('title', bookTitle);
            formData.append('isbn', bookIsbn);
            formData.append('author', bookAuthor);
            bookGenres.forEach(genreName => {
                formData.append('genres', genreName);
            });
            formData.append('file', bookImage);
            formData.append('available', true);


            try {
                await axios.post('books', formData, {
                    headers: {
                    'Content-Type': 'multipart/form-data',
                    'Authorization': token ? `Bearer ${token}` : '' },
                withCredentials: true
                })
            } catch (error) {
                console.log(error)
            }
            
            setBookTitle('');
            setBookIsbn('');
            setBookAuthor('');
            setBookImage(null);
            setBookGenres([]);
            setBookAvailable(false);
            handleSectionClick('books');
            alert("¡Registro del libro completo!")

        }

        const handleGenreChange = (genreId) => {
            if (bookGenres.includes(genreId)) {
                setBookGenres(bookGenres.filter(id => id !== genreId));
            } else {
                setBookGenres([...bookGenres, genreId]);
            }
        };

        const handleUpdateGenreChange = (genreName) => {
            if (updatedBookGenres.includes(genreName)) {
                setUpdatedBookGenres(updatedBookGenres.filter(name => name !== genreName));
            } else {
                setUpdatedBookGenres([...updatedBookGenres, genreName]);
            }
        };

        const handleBookClickForView = (book) => {
            if (selectedBookForView && selectedBookForView.id === book.id) {
                setSelectedBookForView(null);
            } else {
                setSelectedBookForView(book);
            }
        };
        
        const handleBookClickForUpdate = (book) => {
            setSelectedBook(book);
            setUpdatedBookTitle(book.title);
            setUpdatedBookIsbn(book.isbn);
            setUpdatedBookAuthor(book.author);
            setUpdatedBookImage(book.image);
            setUpdatedBookGenres(book.genres);
            setUpdatedBookAvailable(book.available);
        }
        
        const handleUpdateBook = async (e) => {
            e.preventDefault();
            if (!selectedBook) return;

            const formDataUpdate = new FormData();
            formDataUpdate.append('title', updatedBookTitle);
            formDataUpdate.append('isbn', updatedBookIsbn);
            formDataUpdate.append('author', updatedBookAuthor);
            formDataUpdate.append('available', updatedBookAvailable);
            updatedBookGenres.forEach((genreName) => {
                formDataUpdate.append(`genres`, genreName);
            });
            if (updatedBookImage instanceof File) {
                formDataUpdate.append('file', updatedBookImage);
            }
            

            try {
            await axios.put(`books/${selectedBook.id}`, formDataUpdate,
                { headers:
                {
                    'Authorization': token ? `Bearer ${token}` : ''
                },
                withCredentials: true
                }
            );
        alert("¡Libro actualizado!");
        setSelectedBook(null);
        setUpdatedBookTitle('');
        setUpdatedBookIsbn('');
        setUpdatedBookAuthor('');
        setUpdatedBookGenres([]);
        setUpdatedBookImage(null);
        setUpdatedBookAvailable(false);
            } catch (err) {
            console.error(err);
            alert("Error al actualizar el libro");
            }
        }
        
        const handleDeleteBook = async (book) => {
            if (window.confirm(`¿Estás seguro de que quieres borrar el libro "${book.title}"?`)) {
            try {
                await axios.delete(`books/${book.id}`,
                { headers:
                { 'Content-Type': 'application/json',
                    'Authorization': token ? `Bearer ${token}` : ''
                },
                withCredentials: true 
                });
                alert("¡Libro eliminado!");
            } catch (err) {
                console.error(err);
                alert("Error al eliminar el libro");
            }
            }
        }

        const handleResetList = () => {
            setDisplayedBooks(books);
        };

        const handleSearchBook = async (searchType, promptText) => {
            let url = '';

            if (searchType !== 5 && searchType !== 6) {
                const searchTerm = window.prompt(promptText);
                if (!searchTerm || searchTerm.trim() === "") return; 
                
                url = `books/${searchType}/${searchTerm.trim()}`;
            } else {
                url = `books/${searchType}`;
            }

            try {
                const response = await axios.get(url, {
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': token ? `Bearer ${token}` : ''
                    },
                    withCredentials: true
                });
                
                const data = response.data;
                if (data && (!Array.isArray(data) || data.length > 0)) {
                    const booksArray = Array.isArray(data) ? data : [data];
                    setDisplayedBooks(booksArray);
                } else {
                    alert(searchType === 5 ? "No hay libros alquilados en este momento." : "Libro no encontrado");
                    setDisplayedBooks([]);
                }
            } catch (error) {
                console.error("Error en la búsqueda:", error);
                alert("Ocurrió un error o no se encontró el libro.");
                setDisplayedBooks([]);
            }
        };

    return (
        <>
        <div className="action-content books-dashboard">
                
                {/* Vista de CREAR */}
                {activeAction === 'create' && (
                    <div>
                    <h3>Crear nuevo libro</h3>
                        <form onSubmit={handleSubmitCreateBook}>
                            <label htmlFor= "bookTitle">
                                Titulo del libro:
                            </label>
                            <input
                                type="text"
                                id="bookTitle"
                                onChange={(e) => setBookTitle(e.target.value)}
                                value={bookTitle}
                                autoComplete="off"
                                required
                            />
                            
                            <br />
                            <label htmlFor= "bookIsbn">
                                ISBN del libro:
                            </label>
                            <input
                                type="text"
                                id="bookIsbn"
                                onChange={(e) => setBookIsbn(e.target.value)}
                                value={bookIsbn}
                                autoComplete="off"
                                required
                            />

                            <br />
                            <label htmlFor= "bookAuthor">
                                Autor del libro:
                            </label>
                            <input
                                type="text"
                                id="bookAuthor"
                                onChange={(e) => setBookAuthor(e.target.value)}
                                value={bookAuthor}
                                autoComplete="off"
                                required
                            />

                            <br />
                            <label htmlFor= "bookGenre">
                                Géneros del libro:
                            </label>
                            <div style={{ display: 'flex', flexDirection: 'column', gap: '5px', marginTop: '5px' }}>
                                {genresBook.map((genre) => (
                                    <div key={genre.id} style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                                        <input
                                            type="checkbox"
                                            id={`genre-${genre.genreName}`}
                                            value={genre.genreName}
                                            checked={bookGenres.includes(genre.genreName)} 
                                            onChange={() => handleGenreChange(genre.genreName)}
                                        />
                                        <label htmlFor={`genre${genre.id}`} style={{ margin: 0 }}>
                                            {genre.genreName}
                                        </label>
                                    </div>
                                ))}
                            </div>

                            <br />
                            <label htmlFor= "bookImage">
                                Portada del libro:
                            </label>
                            <input
                                type="file"
                                id="bookImage"
                                accept="image/png, image/jpeg, image/jpg"
                                onChange={(e) => setBookImage(e.target.files[0])}
                                required
                            />

                            <br />
                            <Button type="submit" disabled={!bookTitle || !bookIsbn || !bookAuthor || !bookImage || bookGenres.length === 0}>
                                Crear libro
                            </Button>
                        </form>
                    </div>
                )}

                {/* Vista de LEER */}
                {activeAction === 'read' && (
                    <div>
                    <h3>Lista de libros</h3>
                        {loading ? (
                            <LoadingSpinner message="exportando libros..." />
                        ) : (
                            <>
                                <div>
                                    <Button onClick={handleResetList}>
                                        Mostrar Todos
                                    </Button>

                                    <Button onClick={() => handleSearchBook(1, "Introduce el título del libro a buscar:")}>
                                        Por titulo
                                    </Button>

                                    <Button onClick={() => handleSearchBook(2, "Introduce el ISBN del libro a buscar:")}>
                                        Por ISBN
                                    </Button>

                                    <Button onClick={() => handleSearchBook(3, "Introduce el autor del libro a buscar:")}>
                                        Por autor
                                    </Button>

                                    <Button onClick={() => handleSearchBook(4, "Introduce el género del libro a buscar:")}>
                                        Por género
                                    </Button>

                                    <Button onClick={() => handleSearchBook(5, "")}>
                                        Disponibles
                                    </Button>

                                    <Button onClick={() => handleSearchBook(6, "")}>
                                        Alquilados
                                    </Button>
                                </div>

                                {displayedBooks.length > 0 ? (
                                    <ul>
                                        {displayedBooks.map((book, index) => {
                                            const isSelected = selectedBookForView && selectedBookForView.id === book.id;
                                            const showDetails = isSelected || displayedBooks.length === 1;
                                            const coversBaseUrl = (import.meta.env.VITE_COVERS_URL || "http://localhost:8085/uploads/covers/").replace(/\/$/, '');
                                            const imageName = book.image?.name || (typeof book.image === 'string' ? book.image : null);
                                            const imageUrl = imageName ? `${coversBaseUrl}/${imageName}` : null;
                                            const genresFormatted = Array.isArray(book.genres)
                                                ? book.genres.map(g => (typeof g === 'object' && g !== null ? g.genreName || g.name || '' : g)).filter(Boolean).join(', ')
                                                : book.genres || 'Sin género';

                                            return (
                                                <li
                                                    key={book.id || index}
                                                    onClick={() => handleBookClickForView(book)}
                                                    className={`book-item ${showDetails ? 'selected' : ''}`}
                                                    style={{ cursor: 'pointer' }}
                                                >
                                                    <div className="book-item-header">
                                                        <span className="book-item-title">{book.title}</span>
                                                        <span className="book-item-indicator">
                                                            {showDetails ? '▲ Ocultar datos' : '▼ Ver datos'}
                                                        </span>
                                                    </div>

                                                    {showDetails && (
                                                        <div className="book-details-card" onClick={(e) => e.stopPropagation()}>
                                                            <p><strong>Título:</strong> {book.title}</p>
                                                            <p><strong>ISBN:</strong> {book.isbn}</p>
                                                            <p><strong>Autor:</strong> {book.author}</p>
                                                            <p><strong>Géneros:</strong> {genresFormatted}</p>
                                                            <p><strong>Estado:</strong> {book.available ? "Disponible" : "Alquilado"}</p>
                                                            {imageUrl && (
                                                                <div>
                                                                    <p><strong>Portada:</strong> {imageName}</p>
                                                                    <img
                                                                        src={imageUrl}
                                                                        alt={`Portada de ${book.title}`}
                                                                        className="book-details-cover"
                                                                    />
                                                                </div>
                                                            )}
                                                        </div>
                                                    )}
                                                </li>
                                            );
                                        })}
                                    </ul>
                                ) : (
                                    <p>No hay libros cargados.</p>
                                )}
                            </>
                        )}
                    </div>
                )}

                {/* Vista de ACTUALIZAR */}
                {activeAction === 'update' && (
                    <div>
                    <h3>Actualizar libro</h3>
                        {books.length > 0 ? (
                        <ul>
                        {books.map((book, index) => (
                            <li key={index}
                            onClick={() => handleBookClickForUpdate(book)}
                            style={{ cursor: 'pointer'}}
                            >
                            {book.title}
                            
                            {selectedBook && selectedBook.id === book.id && (
                                <div 
                                    onClick={(e) => e.stopPropagation()} 
                                    style={{ marginTop: '10px', padding: '15px', border: '1px solid rgba(255, 255, 255, 0.2)', borderRadius: '8px', cursor: 'default' }}
                                >
                                    <form onSubmit={handleUpdateBook}>
                                        <label htmlFor="updatedBookTitle">Nuevo nombre:</label>
                                        <input
                                        type="text"
                                        id="updatedBookTitle"
                                        value={updatedBookTitle}
                                        onChange={(e) => setUpdatedBookTitle(e.target.value)}
                                        required
                                        />
                                        <br/>

                                        <label htmlFor="updatedBookIsbn">Nuevo isbn:</label>
                                        <input
                                        type="text"
                                        id="updatedBookIsbn"
                                        value={updatedBookIsbn}
                                        onChange={(e) => setUpdatedBookIsbn(e.target.value)}
                                        required
                                        />
                                        <br/>
                                        
                                        <label htmlFor="updatedBookAuthor">Nuevo autor:</label>
                                        <input
                                        type="text"
                                        id="updatedBookAuthor"
                                        value={updatedBookAuthor}
                                        onChange={(e) => setUpdatedBookAuthor(e.target.value)}
                                        required
                                        />
                                        <br/>

                                        <label htmlFor="updatedBookAvailable">¿Disponible?:</label>
                                        <input
                                        type="checkbox"
                                        id="updatedBookAvailable"
                                        checked={updatedBookAvailable}
                                        onChange={(e) => setUpdatedBookAvailable(e.target.checked)}
                                        />
                                        <br />

                                        <br />
                                        <label htmlFor="updatedBookGenres">Géneros del libro:</label>
                                        <div style={{ display: 'flex', flexDirection: 'column', gap: '5px', marginTop: '5px' }}>
                                            {genresBook.map((genre) => (
                                                <div key={genre.id} style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                                                    <input
                                                        type="checkbox"
                                                        id={`update-genre-${genre.id}`}
                                                        value={genre.genreName}
                                                        checked={updatedBookGenres.includes(genre.genreName)}
                                                        onChange={() => handleUpdateGenreChange(genre.genreName)}
                                                    />
                                                    <label htmlFor={`update-genre-${genre.id}`} style={{ margin: 0 }}>
                                                        {genre.genreName}
                                                    </label>
                                                </div>
                                            ))}
                                        </div>
                                        <br />

                                        <label htmlFor="updatedBookImage">Nueva portada del libro: (ignore esta sección si no desea cambiar la portada)</label>
                                        <input
                                            type="file"
                                            id="updatedBookImage"
                                            accept="image/png, image/jpeg, image/jpg"
                                            onChange={(e) => setUpdatedBookImage(e.target.files[0])} 
                                        />
                                        <br/>

                                        <br/>
                                        <Button type="submit" style={{ marginTop: '10px' }}>Guardar cambios</Button>
                                    </form>
                                </div>
                            )}
                            </li>
                        ))}
                        </ul>
                    ) : (
                        <p>No hay libros cargados.</p>
                    )}
                    </div>
                )}

                {/* Vista de BORRAR */}
                {activeAction === 'delete' && (
                    <div>
                    <h3>Borrar libro</h3>
                    {books.length > 0 ? (
                        <ul>
                        {books.map((book, index) => (
                            <li key={index}
                            onClick={() => handleDeleteBook(book)}
                            style={{ cursor: 'pointer'}}
                            >
                            {book.title}</li>
                        ))}
                        </ul>
                    ) : (
                        <p>No hay libros cargados.</p>
                    )}
                    </div>
                )}
            </div>
        </>
    );
}
export default BooksSection;
