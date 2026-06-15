import { useState, useEffect } from 'react';
import axios from '../api/axios';
import Button from '../components/Button';
import './RolesDashboard.css';

const RolesSection = ({ activeAction, handleSectionClick, token }) => {
    const [roleName, setRoleName] = useState('');
    const [roles, setRoles] = useState([]);
    const [selectedRole, setSelectedRole] = useState(null);
    const [updatedRoleName, setUpdatedRoleName] = useState('');

    useEffect(() => {
        let isMounted = true;
        const controller = new AbortController();
    
        const getRoles = async () => {
            try {
                const response = await axios.get('roles', {
                signal: controller.signal,
                headers: { 'Authorization': token ? `Bearer ${token}` : '' }
            });
            if (isMounted) setRoles(response.data);
            }
            catch (err){
            if (err.name === 'CanceledError') {
                    console.log('Petición de Axios cancelada exitosamente.', err);
                } else {
                    console.error('Error al obtener roles:', err);
                }
            }
        }
    
        getRoles();
    
        return() => {
            isMounted = false;
        }}, [token, roles])

        const handleSubmitCreateRole = async (e) => {
            e.preventDefault();
            await axios.post('roles',
            {
                role: roleName,
            },
            {
                headers: { 'Authorization': token ? `Bearer ${token}` : '' }
            }
            );
            setRoleName('');
            handleSectionClick('roles');
            alert("¡Registro del rol completo!")
        }
        
        const handleRoleClickForUpdate = (role) => {
            setSelectedRole(role);
            setUpdatedRoleName(role.role);
        }
        
        const handleUpdateRole = async (e) => {
            e.preventDefault();
            if (!selectedRole) return;
            try {
            await axios.put(`roles/${selectedRole.id}`,
                { role: updatedRoleName },
                {
                    headers: { 'Authorization': token ? `Bearer ${token}` : '' }
                }
            );
            setSelectedRole(null);
            setUpdatedRoleName('');
            alert("¡Rol actualizado!");
            } catch (err) {
            console.error(err);
            alert("Error al actualizar el género");
            }
        }
        
        const handleDeleteRole = async (role) => {
            if (window.confirm(`¿Estás seguro de que quieres borrar el género "${role.role}"?`)) {
            try {
                await axios.delete(`roles/${role.id}`, {
                headers: { 'Authorization': token ? `Bearer ${token}` : '' }
                });
                alert("¡Rol eliminado!");
            } catch (err) {
                    console.error(err);
                    alert("Error al eliminar el rol");
                }
            }
        }
    return (
        <>
        <div className="action-content roles-dashboard">
                {/* Vista de CREAR */}
                {activeAction === 'create' && (
                    <div>
                    <h3>Crear nuevo rol</h3>
                        <form onSubmit={handleSubmitCreateRole}>
                        <label htmlFor= "roleName">
                            Nombre del rol:
                        </label>
                        <input
                            type="text"
                            id="roleName"
                            onChange={(e) => setRoleName(e.target.value)}
                            value={roleName}
                            autoComplete="off"
                            required
                        />

                        <Button type="submit" disabled={!roleName}>
                            Crear rol
                        </Button>
                        </form>
                    </div>
                )}

                {/* Vista de LEER */}
                {activeAction === 'read' && (
                    <div>
                    <h3>Lista de roles</h3>
                    {roles.length > 0 ? (
                        <ul>
                        {roles.map((role, index) => (
                            <li key={index}>{role.role}</li>
                        ))}
                        </ul>
                    ) : (
                        <p>No hay roles cargados.</p>
                    )}
                    </div>
                )}

                {/* Vista de ACTUALIZAR */}
                {activeAction === 'update' && (
                    <div>
                    <h3>Actualizar rol</h3>
                        {roles.length > 0 ? (
                        <ul>
                        {roles.map((role, index) => (
                            <li key={index}
                            onClick={() => handleRoleClickForUpdate(role)}
                            style={{ cursor: 'pointer'}}
                            >
                        {role.role}
                        
                        {selectedRole && selectedRole.id === role.id && (
                            <div 
                                onClick={(e) => e.stopPropagation()} 
                                style={{ marginTop: '10px', padding: '15px', border: '1px solid rgba(255, 255, 255, 0.2)', borderRadius: '8px', cursor: 'default' }}
                            >
                                <form onSubmit={handleUpdateRole}>
                                    <label htmlFor="updatedRoleName">Nuevo nombre:</label>
                                    <input
                                    type="text"
                                    id="updatedRoleName"
                                    value={updatedRoleName}
                                    onChange={(e) => setUpdatedRoleName(e.target.value)}
                                    required
                                    />
                                    <Button type="submit" style={{ marginTop: '10px' }}>Guardar cambios</Button>
                                </form>
                            </div>
                        )}
                        </li>
                        ))}
                        </ul>
                    ) : (
                        <p>No hay roles cargados.</p>
                    )}
                    </div>
                )}

                {/* Vista de BORRAR */}
                {activeAction === 'delete' && (
                    <div>
                    <h3>Borrar rol</h3>
                    {roles.length > 0 ? (
                        <ul>
                        {roles.map((role, index) => (
                            <li key={index}
                            onClick={() => handleDeleteRole(role)}
                            style={{ cursor: 'pointer'}}
                            >
                            {role.role}</li>
                        ))}
                        </ul>
                    ) : (
                        <p>No hay roles cargados.</p>
                    )}
                    </div>
                )}
            </div>
        </>
    );
}
export default RolesSection;
