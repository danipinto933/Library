import { createContext, useState, useEffect, useCallback } from "react";
import useAuth from "../hooks/useAuth"; 
import axios from "../api/axios";

const CartContext = createContext({});

export const CartProvider = ({ children }) => {
    const { auth } = useAuth(); 
    const [cart, setCart] = useState([]);
    const [activeReservesCount, setActiveReservesCount] = useState(0);
    const userKey = auth?.userName; 

    const sessionString = localStorage.getItem('user_session');
    const token = sessionString ? JSON.parse(sessionString).accessToken : null;

    useEffect(() => {
        if (userKey) {
            const savedCart = localStorage.getItem(`cart_user_${userKey}`);
            setCart(savedCart ? JSON.parse(savedCart) : []);
        } else {
            setCart([]);
            setActiveReservesCount(0);
        }
    }, [userKey]);

    useEffect(() => {
        if (userKey) {
            localStorage.setItem(`cart_user_${userKey}`, JSON.stringify(cart));
        }
    }, [cart, userKey]);

    const fetchActiveReservesCount = useCallback(async () => {
        if (!auth?.email) {
            setActiveReservesCount(0);
            return;
        }

        try {
            const config = {
                headers: { 'Authorization': token ? `Bearer ${token}` : '' }
            };
            const userRes = await axios.get(`users/3/${auth.email}`, config);
            const user = userRes.data;
            if (user?.id) {
                const reservesRes = await axios.get(`reserves/3/${user.id}`, config);
                const reserves = Array.isArray(reservesRes.data) ? reservesRes.data : (reservesRes.data ? [reservesRes.data] : []);
                const totalActiveBooks = reserves.reduce((total, res) => {
                    return total + (Array.isArray(res.books) ? res.books.length : 0);
                }, 0);
                setActiveReservesCount(totalActiveBooks);
            }
        } catch (err) {
            console.error("Error al obtener recuento de reservas activas:", err);
        }
    }, [auth?.email, token]);

    useEffect(() => {
        fetchActiveReservesCount();
    }, [fetchActiveReservesCount]);

    useEffect(() => {
        const handleReservaConfirmada = () => {
            fetchActiveReservesCount();
        };
        window.addEventListener('reservaConfirmada', handleReservaConfirmada);
        return () => {
            window.removeEventListener('reservaConfirmada', handleReservaConfirmada);
        };
    }, [fetchActiveReservesCount]);

    const maxLimit = 3;
    const isLimitReached = (cart.length + activeReservesCount) >= maxLimit;

    const addToCart = (book) => {
        if (!userKey) {
            alert("Debes estar logueado para añadir libros.");
            return;
        }

        if (cart.length + activeReservesCount >= maxLimit) {
            alert(`No puedes añadir más libros. Has alcanzado el límite máximo de 3 libros (Libros activos: ${activeReservesCount}, En carrito: ${cart.length}).`);
            return;
        }
        
        const isAlreadyInCart = cart.find((item) => item.id === book.id);
        if (!isAlreadyInCart) {
            setCart([...cart, book]);
        }
    };

    const removeFromCart = (bookId) => {
        setCart(cart.filter(book => book.id !== bookId));
    };

    return (
        <CartContext.Provider value={{ 
            cart, 
            setCart, 
            addToCart, 
            removeFromCart, 
            activeReservesCount, 
            isLimitReached, 
            maxLimit,
            fetchActiveReservesCount 
        }}>
            {children}
        </CartContext.Provider>
    );
};

export default CartContext;