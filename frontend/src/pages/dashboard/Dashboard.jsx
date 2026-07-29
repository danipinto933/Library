import './Dashboard.css'
import { useState, useEffect } from 'react'
import axios from '../../api/axios'
import { useDashboardData } from '../../hooks/useDashboardData'
import Button from '../../components/Button'
import GenresSection from '../../components/GenreDashboard'
import RolesSection from '../../components/RolesDashboard'
import BooksSection from '../../components/BooksDashboard'
import UsersSection from '../../components/UsersDashboard'
import ReservesSection from '../../components/ReservesDashboard'

function Dashboard() {
  
  const [activeSection, setActiveSection] = useState(null);
  const [activeAction, setActiveAction] = useState(null);
  const sessionString = localStorage.getItem('user_session');
  const token = sessionString ? JSON.parse(sessionString).accessToken : null;


  const handleSectionClick = (section) => {
    setActiveSection(section);
    setActiveAction('read');
  }

return (
    <div className="dashboard-container">
      <h1>Panel de Administración</h1>

      <div className="main-buttons">
        <Button className={activeSection === 'genres' ? 'active' : ''} onClick={() => handleSectionClick('genres')}>Géneros</Button>
        <Button className={activeSection === 'books' ? 'active' : ''} onClick={() => handleSectionClick('books')}>Libros</Button>
        <Button className={activeSection === 'roles' ? 'active' : ''} onClick={() => handleSectionClick('roles')}>Roles</Button>
        <Button className={activeSection === 'users' ? 'active' : ''} onClick={() => handleSectionClick('users')}>Usuarios</Button>
        <Button className={activeSection === 'reserves' ? 'active' : ''} onClick={() => handleSectionClick('reserves')}>Reservas</Button>
      </div>

      <hr />

      {activeSection && (
        <div className="crud-buttons">
          <h2>Gestión de {activeSection}</h2>
          <Button className={activeAction === 'create' ? 'active' : ''} onClick={() => setActiveAction('create')}>Crear</Button>
          <Button className={activeAction === 'read' ? 'active' : ''} onClick={() => setActiveAction('read')}>Leer</Button>
          <Button className={activeAction === 'update' ? 'active' : ''} onClick={() => setActiveAction('update')}>Actualizar</Button>
          <Button className={activeAction === 'delete' ? 'active' : ''} onClick={() => setActiveAction('delete')}>Borrar</Button>
        </div>
      )}

      <div>
        {activeSection === 'genres' && (
          <GenresSection activeAction={activeAction} handleSectionClick={handleSectionClick} token={token} />
        )}

        {activeSection === 'roles' && (
          <RolesSection activeAction={activeAction} handleSectionClick={handleSectionClick} token={token}/>
        )}

        {activeSection === 'books' && (
          <BooksSection activeAction={activeAction} handleSectionClick={handleSectionClick} token={token}/>
        )}

        {activeSection === 'users' && (
          <UsersSection activeAction={activeAction} handleSectionClick={handleSectionClick} token={token}/>
        )}

        {activeSection === 'reserves' && (
          <ReservesSection activeAction={activeAction} handleSectionClick={handleSectionClick} token={token}/>
        )}
      </div>
    </div>
  );
}

export default Dashboard;
