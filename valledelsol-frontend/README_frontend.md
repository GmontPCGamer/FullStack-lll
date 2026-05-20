# Frontend: valledelsol-frontend
**Municipalidad Valle del Sol — Sistema de Gestión de Incendios**

## Descripción
Aplicación web React + Vite que permite a la comunidad:
- **Reportar** focos de incendio con ubicación geográfica.
- **Visualizar alertas** emitidas por la municipalidad según nivel de gravedad (ALTO, MEDIO, BAJO).

Se comunica exclusivamente con el **BFF** (`bff-web` en puerto 8080), nunca directamente con los microservicios.

## Patrones de Diseño Aplicados
- **Container / Presenter Pattern**: `App.jsx` centraliza el estado y las llamadas HTTP (contenedor); los elementos visuales son presentadores sin lógica.
- **Observer Pattern** (React Hooks): `useState` y `useEffect` implementan reactividad automática ante cambios de estado.

## Tecnologías
- React 19 + Vite 8
- Axios (HTTP client)
- React Router DOM 7
- ESLint

## Estructura del Proyecto
```
valledelsol-frontend/
├── public/
│   ├── favicon.svg
│   └── icons.svg
├── src/
│   ├── App.jsx          ← Contenedor principal (estado + llamadas BFF)
│   ├── App.css
│   ├── main.jsx         ← Punto de entrada React
│   ├── index.css
│   └── assets/
│       └── hero.png
├── index.html
├── package.json
├── vite.config.js
└── eslint.config.js
```

## Instalación y Ejecución

### Prerrequisitos
- Node.js 18+
- npm 9+
- BFF (`bff-web`) corriendo en `http://localhost:8080`

### Pasos
```bash
# Desde la raíz del proyecto
cd valledelsol-frontend

# Instalar dependencias
npm install

# Ejecutar en modo desarrollo (con hot-reload)
npm run dev
```

La aplicación estará disponible en: **http://localhost:5173**

### Build para producción
```bash
npm run build
# Los archivos compilados quedan en ./dist/
```

### Preview del build de producción
```bash
npm run preview
```

## Dependencias Principales (package.json)

| Paquete | Versión | Uso |
|---|---|---|
| react | ^19.2.6 | Framework UI |
| react-dom | ^19.2.6 | Renderizado DOM |
| react-router-dom | ^7.15.1 | Enrutamiento SPA |
| axios | ^1.16.1 | Cliente HTTP para BFF |

## Conexión con el Backend
El frontend se comunica **exclusivamente** con el BFF:

| Acción | Método | URL |
|---|---|---|
| Ver reportes | GET | `http://localhost:8080/api/bff/reportes` |
| Crear reporte | POST | `http://localhost:8080/api/bff/reportes` |
| Ver alertas | GET | `http://localhost:8080/api/bff/alertas` |
| Crear alerta | POST | `http://localhost:8080/api/bff/alertas` |

## Notas
- CORS está configurado en el BFF (`CorsConfig.java`), no en el frontend.
- Si el BFF no está disponible, el Circuit Breaker retornará un error 503 que el frontend muestra como mensaje de "Servicio no disponible".
