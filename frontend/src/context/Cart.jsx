import { createContext, useState, useContext } from 'react';

const CartContext = createContext();

export function CartProvider({ children }) {
    const [cart, setCart] = useState([]);

    const addToCart = (book) => {
    setCart((prevCart) => {
        const exists = prevCart.find((item) => item.id === book.id);
        if (exists) return prevCart;
        return [...prevCart, book];
    });
    };

    const removeFromCart = (bookId) => {
        setCart((prevCart) => prevCart.filter((item) => item.id !== bookId));
    };

    const clearCart = () => {
        setCart([]);
    };

    return (
        <CartContext.Provider value={{ cart, addToCart, removeFromCart, clearCart }}>
        {children}
        </CartContext.Provider>
    );
    }

export const useCart = () => useContext(CartContext);