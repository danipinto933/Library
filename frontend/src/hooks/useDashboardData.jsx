import { useState, useEffect } from 'react';
import axios from '../api/axios';

export const useDashboardData = () => {
  const [genres, setGenres] = useState([]);
  const [books, setBooks] = useState([]);
  const [roles, setRoles] = useState([]);
  const [users, setUsers] = useState([]);
  const [reserves, setReserves] = useState([]);
  const token = localStorage.getItem('token');

  useEffect(() => {
    let isMounted = true;
    const controller = new AbortController();

    const fetchAll = async () => {
      if (!token) return;

      try {
        const config = {
          headers: {
            Authorization: `Bearer ${token}`
          },
          withCredentials: true, // 2. Añadido para consistencia con Genres.jsx
          signal: controller.signal
        };
        const [resGenres, resBooks, resRoles, resUsers, resReserves] = await Promise.all([
          axios.get('genres', config),
          axios.get('books', config),
          axios.get('roles', config),
          axios.get('users', config),
          axios.get('reserves', config)
        ]);

        if (isMounted) {
          setGenres(resGenres.data);
          setBooks(resBooks.data);
          setRoles(resRoles.data);
          setUsers(resUsers.data);
          setReserves(resReserves.data);
        }
      } catch (err) {
        if (err.name !== 'CanceledError' && err.message !== 'canceled') {
          console.error("Error:", err);
        }
        else
        {
          console.error(err)
        }
      }
    };

    fetchAll();

    return () => {
      isMounted = false;
      controller.abort();
    };
  }, []);

  return { genres, books, roles, users, reserves };
};