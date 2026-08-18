import './Card.css'

function Card({ book, onClick }) {

    const nombreImagen = book.image ? book.image.name : null;
    const rawBaseUrl = import.meta.env.VITE_COVERS_URL || "http://localhost:8085/uploads/covers/";
    const coversBaseUrl = rawBaseUrl.endsWith('/') ? rawBaseUrl : `${rawBaseUrl}/`;

    const imagenUrl = nombreImagen 
        ? `${coversBaseUrl}${nombreImagen}`
        : 'https://via.placeholder.com/300x450?text=Sin+Portada';
    
    return (
        <article 
            className={`book-card ${!book.available ? 'is-rented' : ''}`} 
            onClick={onClick}
            style={{ cursor: 'pointer' }}
        >
            {!book.available && (
                <div className="rented-banner">Alquilado</div>
            )}
            <div className="card-image-container">
                <img src={imagenUrl} alt={`Portada de ${book.title}`}/>
                <span className="card-genre">{book.genres?.[0] || 'Sin género'}</span>
            </div>
            <div className="card-content">
                <h3 className="card-title">{book.title}</h3>
                <p className="card-author">Escrito por: {book.author}</p>
            </div>
        </article>
    )
}

export default Card;