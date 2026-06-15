import { useLocation, Navigate, Outlet } from "react-router-dom";
import useAuth from "../hooks/useAuth";

const RequireAuth = ({ allowedRoles }) => {
    const { auth } = useAuth();
    const location = useLocation();

    const userRoles = Array.isArray(auth?.roles)
        ? auth.roles
        : auth?.roles
            ? [auth.roles]
            : [];

    const hasAllowedRole = userRoles.some(role => allowedRoles?.includes(role));

    return (
        hasAllowedRole
            ? <Outlet />
            : auth?.userName
                ? <Navigate to="/libros" state={{ from: location }} replace />
                : <Navigate to="/" state={{ from: location, openLogin: true }} replace />
    )
}

export default RequireAuth;