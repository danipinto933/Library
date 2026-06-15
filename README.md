# 📚 Sistema de Gestión de Biblioteca (Library)

¡Bienvenido al proyecto **Library-F5**! Este es un sistema web full-stack diseñado para la gestión de una biblioteca, permitiendo la administración de libros, autores, géneros, usuarios y reservas de libros. La aplicación cuenta con un sistema robusto de autenticación y autorización basado en roles.

---

## 📋 Características Principales

*   **Catálogo de Libros:** Consulta de libros disponibles con filtros por autor, género y estado.
*   **Gestión de Reservas:** Permite a los usuarios reservar libros disponibles por un período de tiempo y gestionar extensiones de reservas.
*   **Control de Acceso Basado en Roles (RBAC):**
    *   **Invitados (No autenticados):** Solo pueden ver la página principal de inicio y acceder al login o registro.
    *   **Usuarios Registrados:** Pueden explorar el catálogo completo de libros, ver autores/géneros y gestionar sus propias reservas.
    *   **Administradores:** Tienen acceso total al panel de administración (Dashboard) para gestionar libros, géneros, reservas de todos los usuarios, usuarios y roles.
*   **Gestión de Portadas:** Soporte para subir imágenes de portadas de libros que se almacenan en el backend y se sirven en el frontend.
*   **Seguridad:** Manejo seguro de contraseñas y autenticación basada en tokens JWT.

---

## 🛠️ Tecnologías Utilizadas

El proyecto está dividido en dos partes principales: el **Backend** en Java y el **Frontend** en React.

### ☕ Backend
*   **Lenguaje:** Java 17
*   **Framework:** Spring Boot 3.5.7
    *   **Spring Web:** Creación de APIs RESTful.
    *   **Spring Security:** Control de accesos y seguridad de endpoints.
    *   **Spring Data JPA:** Abstracción para el acceso a la base de datos.
    *   **Spring Validation:** Validación de datos de entrada en controladores.
*   **Seguridad y Tokens:** JWT (java-jwt v4.5.0) para autenticación sin estado.
*   **Gestión de Entorno:** Dotenv (spring-dotenv / dotenv-java) para la configuración de credenciales y variables de entorno.
*   **Mapeadores y Utilidades:**
    *   **Lombok:** Reducción de código repetitivo (boilers).
    *   **MapStruct:** Mapeo rápido y seguro entre entidades de base de datos y DTOs (Data Transfer Objects).
*   **Gestión de Dependencias:** Maven

### ⚛️ Frontend
*   **Librería Principal:** React 19
*   **Herramienta de Construcción:** Vite
*   **Enrutado:** React Router DOM v6
*   **Cliente HTTP:** Axios para comunicación fluida con la API del Backend.
*   **Diseño y Estilos:** CSS Nativo (Vanilla CSS) para un control total y modular de la interfaz.
*   **Iconografía:** FontAwesome Icons para React.

### 🗄️ Base de Datos y DevOps
*   **Base de Datos:** PostgreSQL 15 (Alpine) para persistencia de datos relacionales.
*   **Contenedores:** Docker y Docker Compose para orquestar la base de datos, el backend y el frontend de manera integrada.

---

## 🐳 Cómo probar el proyecto con Docker

El proyecto está completamente preparado para ejecutarse mediante Docker, lo que evita la necesidad de configurar bases de datos, instalar Java o Node en tu sistema local.

> [!NOTE]
> Los archivos de configuración de Docker (`docker-compose.yml`, `Dockerfile.backend` y `Dockerfile.frontend`) se encuentran en la **carpeta raíz del repositorio de nivel superior** para facilitar la orquestación global del proyecto.

### Pasos para el despliegue rápido:

1.  **Asegúrate de tener Docker y Docker Compose instalados:**
    *   [Docker Desktop](https://www.docker.com/products/docker-desktop) en ejecución.

2.  **Ubicación de los comandos:**
    Abre tu terminal favorita en la carpeta que contiene el archivo `docker-compose.yml` (la carpeta contenedora de esta carpeta de proyecto `library-F5`).

3.  **Construir y levantar los servicios:**
    Ejecuta el siguiente comando para compilar e iniciar los servicios en segundo plano:
    ```bash
    docker-compose up -d --build
    ```
    *Este comando descargará la imagen de PostgreSQL 15, compilará la aplicación Spring Boot, preparará el servidor de desarrollo de Vite/React y establecerá las comunicaciones de red y volúmenes requeridos.*

4.  **Acceso a la aplicación:**
    Una vez que finalice el proceso de construcción y los contenedores estén en estado *healthy*, puedes acceder a:
    *   **Interfaz de Usuario (Frontend):** [http://localhost:5173](http://localhost:5173)
    *   **Servicio de API (Backend):** [http://localhost:8080](http://localhost:8080)
    *   **Base de Datos PostgreSQL:** Puerto local `5432`

5.  **Detener la ejecución:**
    Cuando desees detener el entorno de ejecución, ejecuta:
    ```bash
    docker-compose down
    ```
    *Si deseas borrar también el volumen persistente de la base de datos para realizar un reinicio limpio desde cero, añade el flag `-v`:*
    ```bash
    docker-compose down -v
    ```

---

## ⚙️ Variables de Entorno Configuradas

La aplicación utiliza variables de entorno para una configuración limpia. Dentro del entorno Docker, estas ya vienen preconfiguradas en el archivo `docker-compose.yml`, pero en un entorno local debes configurar:

### Backend (`backend/.env`)
*   `DB_URL`: Ruta de conexión a la base de datos (Ej: `jdbc:postgresql://localhost:5432/library`).
*   `DB_USERNAME`: Usuario de la base de datos (Ej: `postgres`).
*   `DB_PASSWORD`: Contraseña del usuario.
*   `FRONTEND_URL`: URL del frontend permitido para CORS (Ej: `http://localhost:5173`).
*   `JWT_SECRET`: Llave secreta utilizada para la firma digital de los tokens JWT.

### Frontend (`frontend/.env`)
*   `VITE_API_URL`: Dirección base de la API del Backend (Ej: `http://localhost:8080/api/v1/`).
*   `VITE_COVERS_URL`: Endpoint de acceso para las portadas subidas (Ej: `http://localhost:8080/uploads/covers/`).
