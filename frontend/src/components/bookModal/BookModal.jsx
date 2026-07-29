import { useContext } from 'react';
import useAuth from '../../hooks/useAuth'; 
import CartContext from '../../context/CartContext'; 
import './BookModal.css'; 

const BookModal = ({ isOpen, onClose, book }) => { 
    const { auth } = useAuth(); 
    const { addToCart } = useContext(CartContext); 

    if (!isOpen || !book) return null;

    const isLogged = auth && Object.keys(auth).length > 0; 

    const handleReserveClick = () => {
        addToCart(book); 
        onClose();       
    };

    const nombreImagen = book.image ? book.image.name : null;
    const coversBaseUrl = import.meta.env.VITE_COVERS_URL || "http://localhost:8080/uploads/covers/";
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
                            disabled={!book.available} 
                        >
                            {book.available ? 'Añadir a Reservas' : 'No disponible'}
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