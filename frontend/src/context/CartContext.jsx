import { createContext, useState, useEffect } from "react";
import useAuth from "../hooks/useAuth"; 

const CartContext = createContext({});

export const CartProvider = ({ children }) => {
    const { auth } = useAuth(); 
    const [cart, setCart] = useState([]);
    const userKey = auth?.userName; 

    useEffect(() => {
        if (userKey) {
            const savedCart = localStorage.getItem(`cart_user_${userKey}`);
            setCart(savedCart ? JSON.parse(savedCart) : []);
        } else {
            setCart([]);
        }
    }, [userKey]);

    useEffect(() => {
        if (userKey) {
            localStorage.setItem(`cart_user_${userKey}`, JSON.stringify(cart));
        }
    }, [cart, userKey]);

    const addToCart = (book) => {
        if (!userKey) {
            alert("Debes estar logueado para añadir libros.");
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
        <CartContext.Provider value={{ cart, setCart, addToCart, removeFromCart }}>
            {children}
        </CartContext.Provider>
    );
};

export default CartContext;