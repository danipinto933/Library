import { useState, useEffect } from 'react';
import axios from '../api/axios';
import Button from '../components/Button';
import './ReservesDashboard.css';

const ReservesSection = ({ activeAction, handleSectionClick, token }) => {
    const [reserves, setReserves] = useState([]);
    const [ampliatedReserve, setAmpliatedReserve] = useState(false);
    const [displayedReserves, setDisplayedReserves] = useState([]);
    const [displayedUser, setDisplayedUser] = useState(null);
    const [users, setUsers] = useState([]);
    const [availableBooks, setAvailableBooks] = useState([]);
    const [notAvailableBooks, setNotAvailableBooks] = useState([]);


    const [reserveUserId, setReserveUserId] = useState("");
    const [reserveBookIds, setReserveBookIds] = useState([]);

    const [selectedReserve, setSelectedReserve] = useState(null);
    const [updatedUserId, setUpdatedUserId] = useState("");
    const [updatedBookIds, setUpdatedBookIds] = useState([]);

    useEffect(() => {
        let isMounted = true;
        const controller = new AbortController();
    
        const fetchData = async () => {
            try {
                const config = {
                    signal: controller.signal,
                    headers: { 'Authorization': token ? `Bearer ${token}` : '' }
                };

                const [reservesData, usersData, availableBooksData, notAvailableBooksData] = await Promise.all([
                    axios.get('reserves', config),
                    axios.get('users', config),
                    axios.get('books/5', config),
                    axios.get('books/6', config)
                ]);

                if (isMounted) {
                    setReserves(reservesData.data);
                    setDisplayedReserves(reservesData.data);
                    setUsers(usersData.data);
                    setAvailableBooks(availableBooksData.data);
                    setNotAvailableBooks(notAvailableBooksData.data);
                }
            } catch (err) {
                console.error('Error al obtener datos:', err);
            }
        };

        fetchData();
    
        return () => {
            isMounted = false;
        };
    }, [token]);

        const handleSubmitCreateReserve = async (e) => {
        e.preventDefault();

        const newReserve = {
            user: { id: reserveUserId },
            books: reserveBookIds.map(id => ({ id: id }))
        };

        try {
            await axios.post('reserves', newReserve, {
                headers: { 
                    'Content-Type': 'application/json',
                    'Authorization': token ? `Bearer ${token}` : ''
                },
                withCredentials: true
            });
            
            setReserveUserId("");
            setReserveBookIds([]);
            handleSectionClick('reserves');
            alert("¡Registro de la reserva completo!");
        } catch (error) {
            console.error(error);
            alert("Error al crear la reserva");
        }
    };

        const handleBookChange = (bookId) => {
            if (reserveBookIds.includes(bookId)) {
                setReserveBookIds(reserveBookIds.filter(id => id !== bookId));
            } else {
                setReserveBookIds([...reserveBookIds, bookId]);
            }
        };

        const handleUpdateBookChange = (bookId) => {
            if (updatedBookIds.includes(bookId)) {
                setUpdatedBookIds(updatedBookIds.filter(id => id !== bookId));
            } else {
                setUpdatedBookIds([...updatedBookIds, bookId]);
            }
        };
        
        const handleReserveClickForUpdate = (reserve) => {
            setSelectedReserve(reserve);
            setUpdatedUserId(reserve.user?.id || "");
            setUpdatedBookIds(reserve.books ? reserve.books.map(b => b.id) : []);
            const isAmpliated = Boolean(
                reserve.ampliated === true || reserve.ampliated === "true" || reserve.ampliated === 1
            );
            
            setAmpliatedReserve(isAmpliated);
        };
        
        const handleUpdateReserve = async (e) => {
            e.preventDefault();
            if (!selectedReserve) return;

            const dataToUpdate = {
                id: selectedReserve.id,
                user: { id: updatedUserId },
                books: updatedBookIds.map(id => ({ id: id })),
                ampliated: ampliatedReserve
            };

            try {
                await axios.put(`reserves/${selectedReserve.id}`, dataToUpdate, { 
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': token ? `Bearer ${token}` : ''
                    },
                    withCredentials: true
                });
                
                setSelectedReserve(null);
                setUpdatedUserId("");
                setUpdatedBookIds([]);
                alert("¡Reserva actualizada!");
            } catch (err) {
                    if (err.response) {
                        if (!selectedReserve.isAmpliated) {
                            alert("No se puede actualizar: la reserva ya fue ampliada o ya no está disponible.");
                        } else {
                            alert(`Error del servidor: ${err.response.status}. No se pudo actualizar.`);
                        }
                    } else {
                        alert("Error de conexión al intentar actualizar la reserva.");
                    }
            }
        };
        
        const handleDeleteReserve = async (reserve) => {
            if (window.confirm(`¿Estás seguro de que quieres borrar la reserva #${reserve.id}?`)) {
                try {
                    await axios.delete(`reserves/${reserve.id}`, { 
                        headers: { 
                            'Content-Type': 'application/json',
                            'Authorization': token ? `Bearer ${token}` : ''
                        },
                        withCredentials: true 
                    });
                    alert("¡Reserva eliminada!");
                    setDisplayedReserves(displayedReserves.filter(r => r.id !== reserve.id));
                } catch (err) {
                    console.error(err);
                    alert("Error al eliminar la reserva");
                }
                }
        };

        const handleResetList = () => {
            setDisplayedReserves(reserves);
        };

        const handleSearchReserve = async (searchType, promptText) => {
            const searchTerm = window.prompt(promptText);
            if (!searchTerm || searchTerm.trim() === "") return;

            try {
                const response = await axios.get(`reserves/${searchType}/${searchTerm.trim()}`, {
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': token ? `Bearer ${token}` : ''
                    },
                    withCredentials: true
                });
                    
                const data = response.data;
                if (data && (!Array.isArray(data) || data.length > 0)) {
                    const reservesArray = Array.isArray(data) ? data : [data];
                    setDisplayedReserves(reservesArray);
                    console.log(displayedReserves)
                } else {
                    setDisplayedReserves([]);
                    alert("Reserva no encontrada");
                }
                } catch (error) {
                    setDisplayedReserves([]);
                    console.error("Error en la búsqueda:", error);
                    alert("Ocurrió un error o no se encontró la reserva.");
                }
        };

    return (
        <>
        <div className="action-content reserves-dashboard">
                
                {/* Vista de CREAR */}
                {activeAction === 'create' && (
                    <div>
                    <h3>Crear nueva reserva</h3>
                        <form onSubmit={handleSubmitCreateReserve}>
                            <label htmlFor="reserveUser">Usuario que hace la reserva:</label>
                            <select
                                id="reserveUser"
                                onChange={(e) => setReserveUserId(e.target.value)}
                                value={reserveUserId}
                                required
                            >
                                <option value="" disabled>Seleccione un usuario...</option>
                                {users.map((user) => (
                                    <option key={user.id} value={user.id}>
                                        {user.userName} ({user.email})
                                    </option>
                                ))}
                            </select>

                            <br /><br />
                            <label>Libros disponibles a reservar:</label>
                            <div style={{ display: 'flex', flexDirection: 'column', gap: '5px', marginTop: '5px' }}>
                                {availableBooks.map((book) => (
                                    <div key={book.id} style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                                        <input
                                            type="checkbox"
                                            id={`book-${book.id}`}
                                            value={book.id}
                                            checked={reserveBookIds.includes(book.id)} 
                                            onChange={() => handleBookChange(book.id)}
                                        />
                                        <label htmlFor={`book-${book.id}`} style={{ margin: 0 }}>
                                            {book.title} - {book.author}
                                        </label>
                                    </div>
                                ))}
                            </div>

                            <br />
                            <Button type="submit" disabled={!reserveUserId || reserveBookIds.length === 0}>
                                Crear reserva
                            </Button>
                        </form>
                    </div>
                )}

                {/* Vista de LEER */}
                {activeAction === 'read' && (
                    <div>
                    <h3>Lista de reservas</h3>
                        <div>
                            <Button onClick={handleResetList}>
                                Mostrar Todos
                            </Button>

                            <Button onClick={() => handleSearchReserve(1, "Introduce la fecha de la reserva:")}>
                                Por fecha reserva
                            </Button>

                            <Button onClick={() => handleSearchReserve(2, "Introduce la fecha de devolución de la reserva:")}>
                                Por fecha devolución
                            </Button>

                            <Button onClick={() => handleSearchReserve(3, "Introduce el ID del usuario de la reserva:")}>
                                Por usuario
                            </Button>

                            <Button onClick={() => handleSearchReserve(4, "Introduce el ID de la reserva a buscar:")}>
                                Por ID
                            </Button>
                        </div>

                    {displayedReserves.length === 1 ? (
                        <ul>
                        {displayedReserves.map((reserve, index) => (
                            <li key={index}>
                                <p>Reserva #{reserve.id}</p>
                                <p>Fecha reserva: {reserve.reserveDate}</p>
                                <p>Fecha devolución: {reserve.returnDate}</p>
                                <p>Usuario: {reserve.user?.userName || 'Desconocido'}</p>
                                <p>Libros: {
                                    Array.isArray(reserve.books) && reserve.books.length > 0 
                                        ? reserve.books.map(b => b.title).join(', ') 
                                        : 'Sin libros'
                                }</p>
                            </li>
                        ))}
                        </ul>
                    ) : displayedReserves.length > 1 ? (
                        <ul>
                        {displayedReserves.map((reserve, index) => (
                            <li key={index}>
                                Reserva #{reserve.id} |
                                Usuario: {reserve.user?.userName} |
                                Libros: {
                                    Array.isArray(reserve.books) && reserve.books.length > 0 
                                        ? reserve.books.map(b => b.title).join(', ') 
                                        : 'Sin libros'
                                }
                            </li>
                        ))}
                        </ul>
                    ) : (
                        <p>No hay reservas cargadas.</p>
                    )}
                    </div>
                )}

                {/* Vista de ACTUALIZAR */}
                {activeAction === 'update' && (
                    <div>
                    <h3>Actualizar reserva</h3>
                    
                        {reserves.length > 0 ? (
                        <ul>
                        {reserves.map((reserve, index) => {
                            return (
                                <li key={index}
                                onClick={() => handleReserveClickForUpdate(reserve)}
                                style={{ cursor: 'pointer' }}
                                >
                                Reserva #{reserve.id} - {reserve.user?.userName}

                                {selectedReserve && selectedReserve.id === reserve.id && (
                                    <div onClick={(e) => e.stopPropagation()} style={{ marginTop: '10px', padding: '15px', border: '1px solid rgba(255, 255, 255, 0.2)', borderRadius: '8px', cursor: 'default' }}>
                                        <h4>Editando Reserva #{selectedReserve.id}</h4>
                                        <form onSubmit={handleUpdateReserve}>
                                            <label htmlFor="updatedUser">Usuario de la reserva:</label>
                                            <select
                                                id="updatedUser"
                                                onChange={(e) => setUpdatedUserId(e.target.value)}
                                                value={updatedUserId}
                                                required
                                            >
                                                <option value="" disabled>Seleccione un usuario...</option>
                                                {users.map((user) => (
                                                    <option key={user.id} value={user.id}>
                                                        {user.userName}
                                                    </option>
                                                ))}
                                            </select>
                                            <br /><br />

                                            <label htmlFor="ampliatedReserve">¿Reserva ampliada?:</label>
                                            <input
                                                type="checkbox"
                                                id="ampliatedReserve"
                                                checked={ampliatedReserve} 
                                                disabled={selectedReserve?.ampliated === true}
                                                onChange={(e) => setAmpliatedReserve(e.target.checked)}
                                            />
                                            <br />

                                            <br />
                                            <label>Gestión de Libros:</label>
                                            <div style={{ display: 'flex', gap: '40px', marginTop: '10px' }}>
                                                <div style={{ flex: 1 }}>
                                                    <h4 style={{ fontSize: '14px', marginBottom: '10px' }}>Libros Disponibles</h4>
                                                    <div style={{ display: 'flex', flexDirection: 'column', gap: '5px' }}>
                                                        {availableBooks.map((book) => (
                                                            <div key={book.id} style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                                                                <input
                                                                    type="checkbox"
                                                                    id={`update-book-avail-${book.id}`}
                                                                    value={book.id}
                                                                    checked={updatedBookIds.includes(book.id)}
                                                                    onChange={() => handleUpdateBookChange(book.id)}
                                                                />
                                                                <label htmlFor={`update-book-avail-${book.id}`} style={{ margin: 0 }}>
                                                                    {book.title}
                                                                </label>
                                                            </div>
                                                        ))}
                                                    </div>
                                                </div>

                                                <div style={{ flex: 1 }}>
                                                    <h4 style={{ fontSize: '14px', marginBottom: '10px' }}>Libros en Reserva</h4>
                                                    <div style={{ display: 'flex', flexDirection: 'column', gap: '5px' }}>
                                                        {selectedReserve.books.map((book) => (
                                                            <div key={book.id} style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                                                                <input
                                                                    type="checkbox"
                                                                    id={`update-book-notavail-${book.id}`}
                                                                    value={book.id}
                                                                    checked={updatedBookIds.includes(book.id)}
                                                                    onChange={() => handleUpdateBookChange(book.id)}
                                                                />
                                                                <label htmlFor={`update-book-notavail-${book.id}`} style={{ margin: 0 }}>
                                                                    {book.title}
                                                                </label>
                                                            </div>
                                                        ))}
                                                    </div>
                                                </div>

                                            </div>

                                            <br/>
                                            <Button type="submit" style={{ marginTop: '10px' }} disabled={!updatedUserId || updatedBookIds.length === 0}>
                                                Guardar cambios
                                            </Button>
                                        </form>
                                    </div>
                                )}
                                </li>
                            );
                        })}
                        </ul>
                    ) : (
                        <p>No hay reservas cargadas.</p>
                    )}
                    </div>
                )}

                {/* Vista de BORRAR */}
                {activeAction === 'delete' && (
                    <div>
                    <h3>Borrar reserva</h3>
                    {reserves.length > 0 ? (
                        <ul>
                        {reserves.map((reserve, index) => (
                            <li key={index}
                            onClick={() => handleDeleteReserve(reserve)}
                            style={{ cursor: 'pointer', marginBottom: '5px'}}
                            >
                            Reserva #{reserve.id} |
                            Usuario: {reserve.user?.userName} |
                            Libros: {
                                Array.isArray(reserve.books) && reserve.books.length > 0 
                                    ? reserve.books.map(b => b.title).join(', ') 
                                    : 'Sin libros'
                            }
                            </li>
                        ))}
                        </ul>
                    ) : (
                        <p>No hay reservas cargadas.</p>
                    )}
                    </div>
                )}
            </div>
        </>
    );
}
export default ReservesSection;
