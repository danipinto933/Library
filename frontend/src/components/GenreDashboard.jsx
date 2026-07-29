import { useState, useEffect } from 'react';
import axios from '../api/axios';
import Button from '../components/Button';
import './GenreDashboard.css';

const GenresSection = ({ activeAction, handleSectionClick, token }) => {
    const [genreName, setGenreName] = useState('');
    const [genreBooks, setGenreBooks] = useState([]);
    const [selectedGenre, setSelectedGenre] = useState(null);
    const [updatedGenreName, setUpdatedGenreName] = useState('');
    const [displayedGenres, setDisplayedGenres] = useState([]);

    useEffect(() => {
        let isMounted = true;
        const controller = new AbortController();
    
        const getGenres = async () => {
            try {
                const response = await axios.get('genres', {
                signal: controller.signal,
                headers: { 'Authorization': token ? `Bearer ${token}` : '' }
            });
            if (isMounted){
                setGenreBooks(response.data);
                setDisplayedGenres(response.data);
            }
            }
            catch (err){
            console.error(err);
            }
        }
    
        getGenres();
    
        return() => {
            isMounted = false;
        }}, [genreBooks, token])

        const handleSubmitCreateGenre = async (e) => {
            e.preventDefault();
            await axios.post('genres',
            {
                genreName: genreName,
            },
            {
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': token ? `Bearer ${token}` : '' },
                withCredentials: true
            }
            );
            setGenreName('');
            handleSectionClick('genres');
            alert("¡Registro del género completo!")

        }
        
        const handleGenreClickForUpdate = (genre) => {
            setSelectedGenre(genre);
            setUpdatedGenreName(genre.genreName);
        }
        
        const handleUpdateGenre = async (e) => {
            e.preventDefault();
            if (!selectedGenre) return;
            try {
            await axios.put(`genres/${selectedGenre.id}`,
                { genreName: updatedGenreName },
                { headers:
                { 'Content-Type': 'application/json',
                    'Authorization': token ? `Bearer ${token}` : ''
                },
                withCredentials: true 
                }
            );
            alert("¡Género actualizado!");
            setSelectedGenre(null);
            setUpdatedGenreName('');
            } catch (err) {
            console.error(err);
            alert("Error al actualizar el género");
            }
        }
        
        const handleDeleteGenre = async (genre) => {
            if (window.confirm(`¿Estás seguro de que quieres borrar el género "${genre.genreName}"?`)) {
            try {
                await axios.delete(`genres/${genre.id}`,
                { headers:
                { 'Content-Type': 'application/json',
                    'Authorization': token ? `Bearer ${token}` : ''
                },
                withCredentials: true 
                });
                alert("¡Género eliminado!");
            } catch (err) {
                console.error(err);
                alert("Error al eliminar el género");
            }
            }
        }

        const handleSearchGenre = async (e) => {
        const searchTerm = window.prompt("Introduce el nombre del género a buscar:");
            try {
                const responseGenres = await axios.get(`genres/1/${searchTerm}`,
                { headers:
                    { 'Content-Type': 'application/json',
                        'Authorization': token ? `Bearer ${token}` : ''
                    },
                withCredentials: true
                });
                const data = responseGenres.data;
                if (data) {
                    const GenresArray = Array.isArray(data) ? data : [data];
                    setDisplayedGenres(GenresArray);
                } else {
                    alert("Género no encontrado");
                    setDisplayedGenres([]);
                }
            } catch (error) {
                console.log(error)
            }
        };

        const handleResetList = () => {
            setDisplayedGenres(genreBooks);
        };
    return (
        <>
        <div className="action-content genre-dashboard">
                {/* Vista de CREAR */}
                {activeAction === 'create' && (
                    <div>
                    <h3>Crear nuevo género</h3>
                        <form onSubmit={handleSubmitCreateGenre}>
                        <label htmlFor= "genreName">
                            Nombre del género:
                        </label>
                        <input
                            type="text"
                            id="genreName"
                            onChange={(e) => setGenreName(e.target.value)}
                            value={genreName}
                            autoComplete="off"
                            required
                        />

                        <Button type="submit" disabled={!genreName}>
                            Crear género
                        </Button>
                        </form>
                    </div>
                )}

                {/* Vista de LEER */}
                {activeAction === 'read' && (
                    <div>
                    <h3>Lista de Géneros</h3>

                        <div style={{ marginBottom: '15px', display: 'flex', gap: '10px' }}>
                            <Button onClick={handleResetList}>
                                Mostrar Todos
                            </Button>
                            <Button onClick={handleSearchGenre}>
                                Buscar Género
                            </Button>
                        </div>

                    {displayedGenres.length > 0 ? (
                        <ul>
                        {displayedGenres.map((genre, index) => (
                            <li key={index}>{genre.genreName}</li>
                        ))}
                        </ul>
                    ) : (
                        <p>No hay géneros cargados.</p>
                    )}
                    </div>
                )}

                {/* Vista de ACTUALIZAR */}
                {activeAction === 'update' && (
                    <div>
                    <h3>Actualizar género</h3>
                        {genreBooks.length > 0 ? (
                        <ul>
                        {genreBooks.map((genre, index) => (
                            <li key={index}
                            onClick={() => handleGenreClickForUpdate(genre)}
                            style={{ cursor: 'pointer'}}
                            >
                        {genre.genreName}
                        
                        {selectedGenre && selectedGenre.id === genre.id && (
                            <div 
                                onClick={(e) => e.stopPropagation()} 
                                style={{ marginTop: '10px', padding: '15px', border: '1px solid rgba(255, 255, 255, 0.2)', borderRadius: '8px', cursor: 'default' }}
                            >
                                <form onSubmit={handleUpdateGenre}>
                                    <label htmlFor="updatedGenreName">Nuevo nombre:</label>
                                    <input
                                    type="text"
                                    id="updatedGenreName"
                                    value={updatedGenreName}
                                    onChange={(e) => setUpdatedGenreName(e.target.value)}
                                    required
                                    />
                                    <Button type="submit" style={{ marginTop: '10px' }}>Guardar cambios</Button>
                                </form>
                            </div>
                        )}
                        </li>
                        ))}
                        </ul>
                    ) : (
                        <p>No hay géneros cargados.</p>
                    )}
                    </div>
                )}

                {/* Vista de BORRAR */}
                {activeAction === 'delete' && (
                    <div>
                    <h3>Borrar género</h3>
                    {genreBooks.length > 0 ? (
                        <ul>
                        {genreBooks.map((genre, index) => (
                            <li key={index}
                            onClick={() => handleDeleteGenre(genre)}
                            style={{ cursor: 'pointer'}}
                            >
                            {genre.genreName}</li>
                        ))}
                        </ul>
                    ) : (
                        <p>No hay géneros cargados.</p>
                    )}
                    </div>
                )}
            </div>
        </>
    );
}
export default GenresSection;
