import { useRef, useState, useEffect, useContext } from "react"
import AuthContext from '../context/AuthProvider';
import axios from '../api/axios'
import { useNavigate, useLocation } from 'react-router-dom'
import './Login.css'

const LOGIN_URL = 'login';

const Login = ({ onLoginSuccess }) => {
    const { setAuth } = useContext(AuthContext);

    const navigate = useNavigate();
    const location = useLocation();
    const from = location.state?.from?.pathname || "/";

    const userRef = useRef();
    const errRef = useRef();

    const [userName, setUserName] = useState('');
    const [pwd, setPwd] = useState('');
    const [errMsg, setErrMsg] = useState('');

    useEffect(() => {
        userRef.current.focus();
    }, [])

    useEffect(() => {
        setErrMsg('');
    }, [userName, pwd])

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            const response = await axios.post(LOGIN_URL,
                JSON.stringify({ userName, password: pwd }),
                {
                    headers: { 'Content-Type': 'application/json' },
                    withCredentials: true
                }
            );

            let accessToken = response?.data?.accessToken || response?.data?.token;

            if (!accessToken) {
                const authHeader = response?.headers['authorization'];
                if (authHeader) {
                    accessToken = authHeader.startsWith('Bearer ') ? authHeader.slice(7) : authHeader;
                }
            }

            const userResponse = await axios.get(`users/1/${userName}`, {
                headers: { 'Authorization': `Bearer ${accessToken}` }
            });

            const safeUserData = {
                userName: userResponse.data.userName,
                email: userResponse.data.email,
                nombre: userResponse.data.name,
                roles: userResponse.data.role,
                accessToken: accessToken
            };

            setAuth(safeUserData);
            console.log(safeUserData);

            localStorage.setItem('user_session', JSON.stringify(safeUserData));

            setUserName('');
            setPwd('');
            
            navigate(from, { replace: true });
            
            if (onLoginSuccess) {
                onLoginSuccess();
            }

        } catch (err) {
            if (!err?.response) {
                setErrMsg(err.message || 'No hay respuesta del servidor');
            } else if (err.response?.status === 400) {
                setErrMsg('Falta usuario o contraseña');
            } else if (err.response?.status === 401) {
                setErrMsg('Usuario o contraseña incorrectos');
            } else {
                setErrMsg('Fallo en el inicio de sesión');
            }
            errRef.current.focus();
        }
    }

    return (
        <section>
            <p ref={errRef} className={errMsg ? "errmsg" : "offscreen"} aria-live="assertive">
                {errMsg}
            </p>
            <h1>Iniciar Sesión</h1>
            <form onSubmit={handleSubmit}>
                <label htmlFor="username">Usuario:</label>
                <input
                    type="text"
                    id="username"
                    ref={userRef}
                    autoComplete="off"
                    onChange={(e) => setUserName(e.target.value)}
                    value={userName}
                    required
                />

                <label htmlFor="password">Contraseña:</label>
                <input
                    type="password"
                    id="password"
                    onChange={(e) => setPwd(e.target.value)}
                    value={pwd}
                    required
                />
                <button>Ingresar</button>
            </form>
            <p>
                <span className="line">
                    <a href="/register">¿Necesitas una cuenta?</a>
                </span>
            </p>
        </section>
    )
}

export default Login