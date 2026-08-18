import { useState, useEffect, useContext } from 'react'
import { Link, useNavigate, useLocation } from 'react-router-dom'
import './Header.css'
import logo from '../../assets/logo_biblioteca.png'
import Login from '../../login/Login.jsx'
import useAuth from '../../hooks/useAuth.jsx'
import CartContext from '../../context/CartContext.jsx'
import axios from '../../api/axios'

function Header() {
  const [showLogin, setShowLogin] = useState(false);
  const [isCartOpen, setIsCartOpen] = useState(false); 
  const [user, setUser] = useState(null); 
  
  const { auth, setAuth } = useAuth();
  const { cart, setCart, removeFromCart, activeReservesCount } = useContext(CartContext); 
  
  const navigate = useNavigate();
  const location = useLocation();

  const sessionString = localStorage.getItem('user_session');
  const token = sessionString ? JSON.parse(sessionString).accessToken : null;

  useEffect(() => {
    let isMounted = true;
    const controller = new AbortController();

    if (location.state?.openLogin) {
      setShowLogin(true);
    }

    const getDataUser = async () => {
      try {
        if (auth?.email) {
          const response = await axios.get(`users/3/${auth.email}`, {
            signal: controller.signal,
            headers: { 'Authorization': token ? `Bearer ${token}` : '' }
          });
          if (isMounted) setUser(response.data);
        }
      } catch (err) {
        if (err.name !== 'CanceledError') {
          console.error('Error al obtener el usuario:', err);
        }
      }
    };
        
    getDataUser();

    return () => {
      isMounted = false;
      controller.abort();
    }; 
  }, [auth?.email, token, location.state?.openLogin]);

  useEffect(() => {
    const loggedInUser = localStorage.getItem("user_session");
    if (loggedInUser && !auth?.accessToken) {
      const foundUser = JSON.parse(loggedInUser);
      setAuth(foundUser);
    }
  }, [auth?.accessToken, setAuth]);

  const logout = () => {
    setAuth({});
    localStorage.removeItem('user_session');
    setIsCartOpen(false);
    setUser(null);
    navigate('/');
  };

  const confirmarReserva = async () => {
    if (cart.length === 0) return;

    if (!user || !user.id) {
      alert("Cargando datos del usuario... por favor intenta de nuevo en unos segundos.");
      return;
    }

    if (cart.length + activeReservesCount > 3) {
      alert(`No se puede confirmar la reserva. El límite total es de 3 libros por usuario (Tienes ${activeReservesCount} activos y ${cart.length} en el carrito).`);
      return;
    }
  
    try {
      const newReserve = {
        user: { id: user.id }, 
        books: cart.map(book => ({ id: book.id }))
      };

      const response = await axios.post('reserves', newReserve, {
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        withCredentials: true
      });

      if (response.status === 200 || response.status === 201) {
        alert("¡Registro de la reserva completo!");
        window.dispatchEvent(new CustomEvent('reservaConfirmada', { detail: cart }));
        setCart([]); 
        setIsCartOpen(false);
      }

    } catch (error) {
      console.error("Error al crear la reserva:", error);
      
      if (error.response?.data?.message) {
          alert(`Error: ${error.response.data.message}`);
      } else if (error.response?.status === 401) {
          alert("Error 401: Sesión no válida. Verifica que el usuario tenga ID asignado.");
      } else {
          alert("Error al crear la reserva");
      }
    }
  };

  return (
    <>
      <header>
        <nav className="header_nav">
          <Link to="/"><img src={logo} alt="Logo"/></Link>
          
          {(auth?.roles === "USER" || auth?.roles === "ADMIN") && (
            <>
              <Link to="/generos">Géneros</Link>
              <Link to="/autores">Autores</Link>
            </>
          )}

          {auth?.roles === "ADMIN" && (
            <Link to="/dashboard">Dashboard</Link>
          )}

          <div className="header_auth">
            {auth?.accessToken ? (
              <>
                <span className="welcome-text">
                  <Link to="/profile">¡Hola, {auth.userName}!</Link>
                  
                </span>

                <div className="cart-wrapper">
                  <div 
                    className="cart-icon-container" 
                    onClick={() => setIsCartOpen(!isCartOpen)} 
                  >
                    <span className="cart-icon" title="Mis Reservas">🛒</span>
                    {cart.length > 0 && (
                      <span className="cart-badge">{cart.length}</span>
                    )}
                  </div>

                  {isCartOpen && (
                    <div className="cart-dropdown">
                      <h4>Tus Reservas</h4>
                      
                      {cart.length === 0 ? (
                        <p className="empty-cart">No hay libros reservados.</p>
                      ) : (
                        <>
                          <ul className="cart-list">
                            {cart.map((book) => (
                              <li key={book.id} className="cart-item">
                                <span className="cart-item-title">{book.title}</span>
                                <button 
                                  className="remove-item-btn"
                                  onClick={() => removeFromCart(book.id)}
                                  title="Quitar"
                                >
                                  &times;
                                </button>
                              </li>
                            ))}
                          </ul>
                          <button 
                            className="confirm-reserve-btn"
                            onClick={confirmarReserva}
                          >
                            Confirmar Reserva ({cart.length})
                          </button>
                        </>
                      )}
                    </div>
                  )}
                </div>

                <button onClick={logout} className="login-btn">Logout</button>
              </>
            ) : (
              <button onClick={() => setShowLogin(true)} className="login-btn">
                Iniciar Sesión
              </button>
            )}
          </div>
        </nav>
      </header>

      {showLogin && (
        <div className="modal-overlay">
          <div className="modal-content">
            <button className="close-button" onClick={() => setShowLogin(false)}>
              &times;
            </button>
            <Login onLoginSuccess={() => setShowLogin(false)} />
          </div>
        </div>
      )}
    </>
  )
}

export default Header;