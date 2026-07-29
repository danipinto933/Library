import './App.css'
import Header from './components/header/Header.jsx'
import Genres from './pages/genres/Genres.jsx'
import Books from './pages/books/Books.jsx'
import Dashboard from './pages/dashboard/Dashboard.jsx'
import Author from './pages/authors/Author.jsx'
import Register from './register/Register.jsx'
import Profile from './pages/profile/Profile.jsx'
import { Routes, Route } from 'react-router-dom'
import Layout from './components/Layout.jsx'
import { CartProvider } from './context/Cart.jsx'
import RequireAuth from './components/RequireAuth.jsx'

function App() {

  return (
    <>
    <CartProvider>
      <Header/>
        <Routes>
          <Route path="/" element={<Layout />}>
            {/* Rutas Públicas */}
            <Route path="/" element={<Books />} />
            <Route path="/libros" element={<Books />} />
            <Route path="/register" element={<Register />} />

            {/* Rutas Protegidas para USER y ADMIN */}
            <Route element={<RequireAuth allowedRoles={['USER', 'ADMIN']} />}>
              <Route path="/generos" element={<Genres />} />
              <Route path="/autores" element={<Author />} />
              <Route path="/profile" element={<Profile />} />
            </Route>

            {/* Rutas Protegidas solo para ADMIN */}
            <Route element={<RequireAuth allowedRoles={['ADMIN']} />}>
              <Route path="/dashboard" element={<Dashboard />} />
            </Route>
          </Route>
        </Routes>
    </CartProvider>

    </>

    
  )
}

export default App;