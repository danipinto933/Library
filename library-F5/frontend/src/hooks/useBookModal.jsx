import { useState } from 'react';

export const useBookModal = () => {
    const [isOpen, setIsOpen] = useState(false);
    const [selectedBook, setSelectedBook] = useState(null);

    const openModal = (book) => {
        setSelectedBook(book);
        setIsOpen(true);
    };

    const closeModal = () => {
        setIsOpen(false);
        setSelectedBook(null);
    };

    return {
        isOpen,
        selectedBook,
        openModal,
        closeModal
    };
};