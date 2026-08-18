import { useState, useEffect } from 'react';
import useAuth from '../../hooks/useAuth';
import axios from '../../api/axios';
import './Profile.css';

const Profile = () => {
    const { auth } = useAuth();
    const [myReserves, setMyReserves] = useState([]);
    const [loading, setLoading] = useState(true);
    const [user, setUser] = useState(null);

    const sessionString = localStorage.getItem('user_session');
    const token = sessionString ? JSON.parse(sessionString).accessToken : null;


    useEffect(() => {
        let isMounted = true;
        const controller = new AbortController();

        const fetchProfileData = async () => {
            try {
                const currentToken = auth?.accessToken || auth?.token;
                if (!currentToken || !auth?.email) {
                    setLoading(false);
                    return;
                }

                const config = {
                    signal: controller.signal,
                    headers: { 'Authorization': `Bearer ${currentToken}` }
                };

                const userResponse = await axios.get(`users/3/${auth.email}`, config);
                const userProfile = userResponse.data;
                
                if (isMounted) {
                    setUser(userProfile);
                }

                const reservesResponse = await axios.get(`reserves/3/${userProfile.id}`, config);
                
                if (isMounted) {
                    setMyReserves(
                        Array.isArray(reservesResponse.data) 
                            ? reservesResponse.data 
                            : [reservesResponse.data]
                    );
                }

            } catch (err) {
                if (err.name !== 'CanceledError') {
                    console.error('Error al cargar el perfil:', err);
                }
            } finally {
                if (isMounted) {
                    setLoading(false);
                }
            }
        };

        fetchProfileData();

        return () => {
            isMounted = false;
        };
    }, [auth]);

    const handleReturn = async (id) => {
        if (!window.confirm("¿Estás seguro de que quieres devolver estos libros?")) return;

        try {
            const token = auth?.accessToken || auth?.token;
            await axios.delete(`reserves/${id}`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            setMyReserves(myReserves.filter(res => res.id !== id));
            alert("Libros devueltos con éxito.");
        } catch (err) {
            alert("No se pudo procesar la devolución: "+err);
        }
    };

    const handleExtend = async (reserve) => {
        if (reserve.ampliated) {
            alert("Esta reserva ya ha sido ampliada anteriormente.");
            return;
        }

        try {
            const token = auth?.accessToken || auth?.token;
            const dataToUpdate = {
                ...reserve,
                ampliated: true
            };

            await axios.put(`reserves/${reserve.id}`, dataToUpdate, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            setMyReserves(prevReserves => 
                            prevReserves.map(r => 
                                r.id === reserve.id ? { ...r, ampliated: true } : r
                            )
                        );
            alert("Reserva ampliada por un mes más.");
        } catch (err) {
            alert("No se pudo ampliar la reserva: "+err);
        }
    };

    if (loading) return <div className="loading">Cargando tu perfil...</div>;

    return (
        <div className="profile-container">
            <div className="profile-header">
                <h2>Mi Perfil</h2>
                <div className="user-info">
                    <p><strong>Usuario:</strong> {auth.userName}</p>
                    <p><strong>Email:</strong> {auth.email}</p>
                </div>
            </div>

            <section className="reserves-section">
                <h3>Mis Reservas Activas</h3>
                {myReserves.length === 0 ? (
                    <p className="no-reserves">No tienes reservas pendientes actualmente.</p>
                ) : (
                    <div className="reserves-grid">
                        {myReserves.map((reserve) => (
                            <div key={reserve.id} className="reserve-card">
                                <div className="reserve-details">
                                    <p><strong>Pedido el:</strong> {reserve.reserveDate}</p>
                                    <p><strong>Devolución:</strong> <span className="date-highlight">{reserve.returnDate}</span></p>
                                    <p><strong>Libros:</strong></p>
                                    <ul>
                                        {reserve.books?.map(b => <li key={b.id}>{b.title}</li>)}
                                    </ul>
                                </div>
                                <div className="reserve-actions">
                                    <button 
                                        className="btn-extend" 
                                        onClick={() => handleExtend(reserve)}
                                        disabled={reserve.ampliated}
                                    >
                                        {reserve.ampliated ? "Ya ampliada" : "Alargar Reserva"}
                                    </button>
                                    <button 
                                        className="btn-return" 
                                        onClick={() => handleReturn(reserve.id)}
                                    >
                                        Devolver Libros
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </section>
        </div>
    );
};

export default Profile;