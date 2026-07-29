import { useState, useEffect } from 'react';
import axios from '../api/axios';
import Button from '../components/Button';
import './UsersDashboard.css';

const UsersSection = ({ activeAction, handleSectionClick, token }) => {
    const [users, setUsers] = useState([]);
    const [roles, setRoles] = useState([]);
    const [selectedUser, setSelectedUser] = useState(null);
    const [displayedUser, setDisplayedUsers] = useState([]);
    

    const [userName, setUserName] = useState("");
    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [role, setRole] = useState("");

    const [updatedUserName, setUpdatedUserName] = useState("");
    const [updatedName, setUpdatedName] = useState("");
    const [updatedEmail, setUpdatedEmail] = useState("");
    const [updatedPassword, setUpdatedPassword] = useState("");
    const [updatedRole, setUpdatedRole] = useState(null)

    useEffect(() => {
        let isMounted = true;
        const controller = new AbortController();
    
        const getUsers = async () => {
            try {
                const response = await axios.get('users', {
                signal: controller.signal,
                headers: { 'Authorization': token ? `Bearer ${token}` : '' }
            });
            if (isMounted) {
                setUsers(response.data);
                setDisplayedUsers(response.data);
            }
            }
            catch (err){
                console.error('Error al obtener usuarios:', err);
            }
            
        }

        const getRoles = async () => {
            try {
                const response = await axios.get('roles', {
                signal: controller.signal,
                headers: { 'Authorization': token ? `Bearer ${token}` : '' }
            });
            if (isMounted) setRoles(response.data);
            }
            catch (err){
                console.error('Error al obtener roles:', err);
            }
            
        }
    
        getUsers();
        getRoles();
    
        return() => {
            isMounted = false;
        }}, [token, users, roles])

        const handleSubmitCreateUser = async (e) => {
            e.preventDefault();
            await axios.post('users',
            {
                userName: userName,
                name: name,
                email: email,
                password: password,
                role: role
            },
            {
                headers: { 'Authorization': token ? `Bearer ${token}` : '' }
            }
            );
            setUserName('');
            setName("");
            setEmail("");
            setPassword("");
            setRole("");
            handleSectionClick('users');
            alert("¡Registro del usuario completo!")
        }
        
        const handleUserClickForUpdate = (user) => {
            setSelectedUser(user);
            setUpdatedUserName(user.userName)
            setUpdatedName(user.name);
            setUpdatedEmail(user.email);
            setUpdatedPassword("");
            setUpdatedRole(user.role);
        }
        
        const handleUpdateUser = async (e) => {
            e.preventDefault();
            if (!selectedUser) return;

            const dataToUpdate = {
                userName: updatedUserName,
                name: updatedName,
                email: updatedEmail,
                role: updatedRole
            };

            if (updatedPassword.trim() !== "") {
                dataToUpdate.password = updatedPassword;
            }

            try {
            await axios.put(`users/${selectedUser.id}`, dataToUpdate,
                {
                    headers: { 'Authorization': token ? `Bearer ${token}` : '' }
                }
            );
            setSelectedUser(null);
            setUpdatedUserName("")
            setUpdatedName("");
            setUpdatedEmail("");
            setUpdatedPassword("");
            setUpdatedRole("");
            alert("¡Usuario actualizado!");
            } catch (err) {
            console.error(err);
            alert("Error al actualizar el usuario");
            }
        }
        
        const handleDeleteUser = async (user) => {
            if (window.confirm(`¿Estás seguro de que quieres borrar el usuario "${user.userName}"?`)) {
            try {
                await axios.delete(`users/${user.id}`, {
                headers: { 'Authorization': token ? `Bearer ${token}` : '' }
                });
                alert("¡Usuario eliminado!");
            } catch (err) {
                    console.error(err);
                    alert("Error al eliminar el rol");
                }
            }
        }

        const handleResetList = () => {
            setDisplayedUsers(users);
        };

        const handleSearchUser = async (searchType, promptText) => {
            const searchTerm = window.prompt(promptText);
            
            if (!searchTerm || searchTerm.trim() === "") return;

            try {
                const response = await axios.get(`users/${searchType}/${searchTerm.trim()}`, {
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': token ? `Bearer ${token}` : ''
                    },
                    withCredentials: true
                });
                
                const data = response.data;
                if (data && (!Array.isArray(data) || data.length > 0)) {
                    const booksArray = Array.isArray(data) ? data : [data];
                    setDisplayedUsers(booksArray);
                } else {
                    alert("Usuario no encontrado");
                    setDisplayedUsers([]);
                }
            } catch (error) {
                console.error("Error en la búsqueda:", error);
                alert("Ocurrió un error o no se encontró al usuario.");
                setDisplayedUsers([]);
            }
        };

    return (
        <>
        <div className="action-content users-dashboard">
                {/* Vista de CREAR */}
                {activeAction === 'create' && (
                    <div>
                    <h3>Crear nuevo usuario</h3>
                        <form onSubmit={handleSubmitCreateUser}>
                        <label htmlFor= "userName">
                            Apodo del usuario:
                        </label>
                        <input
                            type="text"
                            id="userName"
                            onChange={(e) => setUserName(e.target.value)}
                            value={userName}
                            autoComplete="off"
                            required
                        />

                        <br/>
                        <label htmlFor= "name">
                            Nombre del usuario:
                        </label>
                        <input
                            type="text"
                            id="name"
                            onChange={(e) => setName(e.target.value)}
                            value={name}
                            autoComplete="off"
                            required
                        />

                        <br/>
                        <label htmlFor= "email">
                            Email del usuario:
                        </label>
                        <input
                            type="text"
                            id="email"
                            onChange={(e) => setEmail(e.target.value)}
                            value={email}
                            autoComplete="off"
                            required
                        />

                        <br/>
                        <label htmlFor= "password">
                            Password del usuario:
                        </label>
                        <input
                            type="text"
                            id="password"
                            onChange={(e) => setPassword(e.target.value)}
                            value={password}
                            autoComplete="off"
                            required
                        />

                        <br/>
                        <label htmlFor= "role">Rol del usuario:</label>
                        <select
                            id="role"
                            onChange={(e) => setRole(e.target.value)}
                            value={role}
                            required
                        >
                            <option value="" disabled>Seleccione un rol...</option>
                            {roles.map((role) => (
                                <option key={role.id} value={role.role}>
                                    {role.role}
                                </option>
                            ))}
                        </select>

                        <br/>
                        <Button type="submit" disabled={!userName}>
                            Crear usuario
                        </Button>
                        </form>
                    </div>
                )}

                {/* Vista de LEER */}
                {activeAction === 'read' && (
                    <div>
                    <h3>Lista de usuarios</h3>
                        <div>
                            <Button onClick={handleResetList}>
                                Mostrar Todos
                            </Button>

                            <Button onClick={() => handleSearchUser(1, "Introduce el nick del usuario a buscar:")}>
                                Por nick
                            </Button>

                            <Button onClick={() => handleSearchUser(2, "Introduce el nombre del usuario a buscar:")}>
                                Por nombre
                            </Button>

                            <Button onClick={() => handleSearchUser(3, "Introduce el email del usuario a buscar:")}>
                                Por email
                            </Button>

                            <Button onClick={() => handleSearchUser(4, "Introduce el rol del usuario a buscar:")}>
                                Por rol
                            </Button>
                        </div>

                    {displayedUser.length === 1 ? (
                        <ul>
                        {displayedUser.map((user, index) => (
                            <>
                                <p key={index}>Nick: {user.userName}</p>
                                <p key={index}>Nombre: {user.name}</p>
                                <p key={index}>Email: {user.email}</p>
                                <p key={index}>Rol: {user.role}</p>
                            </>
                        ))}
                        </ul>
                    ) : displayedUser.length > 1 ? (
                        <ul>
                        {displayedUser.map((user, index) => (
                            <li key={index}>{user.userName}</li>
                        ))}
                        </ul>
                    ) : (
                        <p>No hay usuarios cargados.</p>
                    )}
                    </div>
                )}

                {/* Vista de ACTUALIZAR */}
                {activeAction === 'update' && (
                    <div>
                    <h3>Actualizar usuario</h3>
                        {users.length > 0 ? (
                        <ul>
                        {users.map((user, index) => (
                            <li key={index}
                            onClick={() => handleUserClickForUpdate(user)}
                            style={{ cursor: 'pointer'}}
                            >
                            {user.userName}
                            
                            {selectedUser && selectedUser.id === user.id && (
                                <div 
                                    onClick={(e) => e.stopPropagation()} 
                                    style={{ marginTop: '10px', padding: '15px', border: '1px solid rgba(255, 255, 255, 0.2)', borderRadius: '8px', cursor: 'default' }}
                                >
                                    <form onSubmit={handleUpdateUser}>
                                        <label htmlFor="updatedUserName">Nuevo apodo:</label>
                                        <input
                                        type="text"
                                        id="updatedUserName"
                                        value={updatedUserName}
                                        onChange={(e) => setUpdatedUserName(e.target.value)}
                                        required
                                        />

                                        <br/>
                                        <label htmlFor="updatedName">Nuevo nombre:</label>
                                        <input
                                        type="text"
                                        id="updatedName"
                                        value={updatedName}
                                        onChange={(e) => setUpdatedName(e.target.value)}
                                        required
                                        />

                                        <br/>
                                        <label htmlFor="updatedEmail">Nuevo email:</label>
                                        <input
                                        type="text"
                                        id="updatedEmail"
                                        value={updatedEmail}
                                        onChange={(e) => setUpdatedEmail(e.target.value)}
                                        required
                                        />

                                        <br/>
                                        <label htmlFor="updatedPassword">Nueva contraseña:</label>
                                        <input
                                        type="text"
                                        id="updatedPassword"
                                        value={updatedPassword}
                                        onChange={(e) => setUpdatedPassword(e.target.value)}
                                        />

                                        <br/>
                                        <label htmlFor="updatedRole">Cambio de rol:</label>
                                        <select
                                        id="updatedRole"
                                        onChange={(e) => setUpdatedRole(e.target.value)}
                                        value={updatedRole}
                                        required
                                        >
                                        <option value="" disabled>Seleccione un rol...</option>
                                        {roles.map((role) => (
                                            <option key={role.id} value={role.role}>
                                                {role.role}
                                            </option>
                                        ))}
                                    </select>

                                        <br/>
                                        <Button type="submit" style={{ marginTop: '10px' }}>Guardar cambios</Button>
                                    </form>
                                </div>
                            )}
                            </li>
                        ))}
                        </ul>
                    ) : (
                        <p>No hay usuarios cargados.</p>
                    )}
                    </div>
                )}

                {/* Vista de BORRAR */}
                {activeAction === 'delete' && (
                    <div>
                    <h3>Borrar usuario</h3>
                    {users.length > 0 ? (
                        <ul>
                        {users.map((user, index) => (
                            <li key={index}
                            onClick={() => handleDeleteUser(user)}
                            style={{ cursor: 'pointer'}}
                            >
                            {user.userName}</li>
                        ))}
                        </ul>
                    ) : (
                        <p>No hay usuarios cargados.</p>
                    )}
                    </div>
                )}
            </div>
        </>
    );
}
export default UsersSection;
