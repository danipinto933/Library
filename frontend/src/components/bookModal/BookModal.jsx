import { useContext } from 'react';
import useAuth from '../../hooks/useAuth'; 
import CartContext from '../../context/CartContext'; 
import './BookModal.css'; 

const BookModal = ({ isOpen, onClose, book }) => { 
    const { auth } = useAuth(); 
    const { addToCart, isLimitReached, cart } = useContext(CartContext); 

    if (!isOpen || !book) return null;

    const isLogged = auth && Object.keys(auth).length > 0; 
    const isInCart = cart?.some(item => item.id === book.id);
    const cannotReserve = !book.available || isLimitReached || isInCart;

    const getButtonText = () => {
        if (!book.available) return 'No disponible';
        if (isInCart) return 'Ya en tu carrito';
        if (isLimitReached) return 'Límite alcanzado (máx. 3 libros)';
        return 'Añadir a Reservas';
    };

    const handleReserveClick = () => {
        addToCart(book); 
        onClose();       
    };

    const nombreImagen = book.image ? book.image.name : null;
    const rawBaseUrl = import.meta.env.VITE_COVERS_URL || "http://localhost:8085/uploads/covers/";
    const coversBaseUrl = rawBaseUrl.endsWith('/') ? rawBaseUrl : `${rawBaseUrl}/`;
    const imagenUrl = nombreImagen 
        ? `${coversBaseUrl}${nombreImagen}`
        : 'https://via.placeholder.com/300x450?text=Sin+Portada';

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                <button className="close-btn" onClick={onClose}>&times;</button>
                
                <div className="modal-body">
                    <div className="modal-image-container">
                        <img src={imagenUrl} alt={book.title} />
                    </div>

                    <div className="modal-info">
                        <h2>{book.title}</h2>
                        <p><strong>Autor:</strong> {book.author}</p>
                        <p><strong>Géneros:</strong> {book.genres?.join(', ')}</p>
                        <p><strong>ISBN:</strong> {book.isbn}</p>
                        <p><strong>Disponible:</strong> {book.available ? 'Sí' : 'No'}</p> 
                    </div>
                </div>
                
                <div className="modal-actions">
                    {isLogged ? (
                        <button 
                            className="reserve-btn" 
                            onClick={handleReserveClick}
                            disabled={cannotReserve} 
                        >
                            {getButtonText()}
                        </button>
                    ) : (
                        <p className="auth-warning">Inicia sesión para reservar este libro.</p>
                    )}
                </div>
            </div>
        </div>
    );
};

export default BookModal;