# Valle del Sol — Sistema de Gestión de Incendios

Arquitectura de microservicios para la **Municipalidad Valle del Sol**. Permite a la comunidad reportar focos de incendio y recibir alertas de la municipalidad.

---

## Arquitectura

```
[Frontend React] (:5173)
       │
       ▼  /api/bff/*
   [BFF - bff-web] (:8084) ← Circuit Breaker (Resilience4j)
       │              │
       ▼              ▼
[reportes-ms]    [alertas-ms]
   :8081            :8082       ← Factory Method Pattern
───────────────────────────────────────
[customer-ms]   [product-ms]
   :8085            :8083
       │              │
       └── WebClient ──┘       ← Service Discovery (Eureka)
───────────────────────────────────────
[eureka-server]  [api-gateway]  [spring-boot-admin]  [keycloak-adapter]
   :8761            :8080            :8062                 :8088
```

---

## Tecnologías

| Capa | Tecnología |
|---|---|
| Backend | Java 17, Spring Boot 4.x (Framework 7.x) |
| Frontend | React 19, Vite 8, Axios |
| Database | H2 en memoria (local) / MySQL 8.0 (Docker) |
| Service Discovery | Netflix Eureka |
| API Gateway | Spring Cloud Gateway |
| Circuit Breaker | Resilience4j 2.2.0 |
| Auth | Keycloak + JWT |
| Contenerización | Docker, Docker Compose |
| Build | Maven 3.9.9 |

---

## Requisitos

- **Java 17+** (Eclipse Temurin recomendado)
- **Maven 3.8+**
- **Node.js 18+** y **npm 9+**
- **Docker** y **Docker Compose** (para depliegue contenerizado)

---

## Guía de Inicio Rápido

### Opción A: Desarrollo local (sin Docker, sin Eureka)

Iniciar cada microservicio en terminales separadas en este orden:

```bash
# Terminal 1 — reportes-ms
cd businessdomain/reportes-ms
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Terminal 2 — alertas-ms
cd businessdomain/alertas-ms
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Terminal 3 — bff-web
cd businessdomain/bff-web
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Terminal 4 — Frontend
cd valledelsol-frontend
npm install
npm run dev
```

| Servicio | URL |
|---|---|
| Frontend | `http://localhost:5173` |
| BFF | `http://localhost:8084` |
| reportes-ms | `http://localhost:8081` |
| alertas-ms | `http://localhost:8082` |

### Opción B: Docker (con Eureka, producción)

```bash
# Construir y levantar todos los servicios
docker compose build --no-cache
docker compose up -d

# Verificar estado
docker compose ps
```

| Servicio | URL |
|---|---|
| Frontend | `http://localhost:5173` |
| BFF | `http://localhost:8084` |
| reportes-ms | `http://localhost:8081` |
| alertas-ms | `http://localhost:8082` |
| Eureka Dashboard | `http://localhost:8761` |
| Spring Boot Admin | `http://localhost:8062` |
| API Gateway | `http://localhost:8080` |
| keycloak-adapter | `http://localhost:8088` |
| MySQL | `localhost:3306` (root/root) |

> Los microservicios usan el perfil `docker` activado automáticamente via `SPRING_PROFILES_ACTIVE=docker`.

**Detener (los datos MySQL persisten):**

```bash
docker compose down
# Para eliminar también los datos de BD:
docker compose down -v
```

---

## Verificar el Funcionamiento

```bash
# reportes-ms responde
curl http://localhost:8081/api/reportes

# alertas-ms responde
curl http://localhost:8082/api/alertas

# BFF orquesta ambos servicios
curl http://localhost:8084/api/bff/reportes
curl http://localhost:8084/api/bff/alertas

# Eureka muestra servicios registrados
curl http://localhost:8761/eureka/apps

# Frontend sirve la SPA
curl -s -o /dev/null -w "%{http_code}" http://localhost:5173
```

### Crear datos de prueba

```bash
# Crear reporte en reportes-ms (o vía BFF)
curl -X POST http://localhost:8081/api/reportes \
  -H "Content-Type: application/json" \
  -d '{"ubicacionLatitud":"-33.4569","ubicacionLongitud":"-70.6483","descripcion":"Foco de incendio en sector norte"}'

# Crear alerta en alertas-ms (o vía BFF)
curl -X POST http://localhost:8082/api/alertas \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Riesgo Alto","mensaje":"Humo detectado","nivelGravedad":"ALTO"}'
```

---

## Pruebas y Cobertura

### Backend

```bash
# Ejecutar todos los tests del backend
cd businessdomain
mvn test

# O un microservicio específico
cd businessdomain/reportes-ms
mvn test

cd businessdomain/alertas-ms
mvn test

cd businessdomain/bff-web
mvn test
```

### Frontend

```bash
cd valledelsol-frontend
npm test
```

### Cobertura

| Microservicio | Cobertura (líneas) | Tests |
|---|---|---|
| reportes-ms | **96.4%** | 16 (unit + integración + exception handler) |
| alertas-ms | **95.6%** | 16 (unit + integración + exception handler + factory) |
| bff-web | **97.2%** | 19 (controller + integración + e2e) |
| **Total backend** | **~96%** | **51 tests** |
| Frontend | - | **7 tests** |
| **Total** | - | **58 tests** |

---

## Endpoints por Microservicio

### reportes-ms (:8081)

| Método | URL | Descripción |
|---|---|---|
| GET | `/api/reportes` | Listar reportes |
| POST | `/api/reportes` | Crear reporte |
| GET | `/api/reportes/{id}` | Obtener por ID |
| DELETE | `/api/reportes/{id}` | Eliminar reporte |

### alertas-ms (:8082)

| Método | URL | Descripción |
|---|---|---|
| GET | `/api/alertas` | Listar alertas |
| POST | `/api/alertas` | Crear alerta (Factory Method) |
| GET | `/api/alertas/{id}` | Obtener por ID |
| DELETE | `/api/alertas/{id}` | Eliminar alerta |

### bff-web (:8084)

| Método | URL | Descripción |
|---|---|---|
| GET | `/api/bff/reportes` | Obtener reportes (delega a reportes-ms) |
| POST | `/api/bff/reportes` | Crear reporte |
| GET | `/api/bff/alertas` | Obtener alertas (delega a alertas-ms) |
| POST | `/api/bff/alertas` | Crear alerta |

### customer-ms (:8085)

| Método | URL | Descripción |
|---|---|---|
| GET | `/customer` | Listar clientes |
| GET | `/customer/{id}` | Obtener cliente (con Circuit Breaker) |
| POST | `/customer` | Crear cliente |
| PUT | `/customer/{id}` | Actualizar cliente |
| DELETE | `/customer/{id}` | Eliminar cliente |

### product-ms (:8083)

| Método | URL | Descripción |
|---|---|---|
| GET | `/product` | Listar productos |
| GET | `/product/{id}` | Obtener producto |
| POST | `/product` | Crear producto |
| PUT | `/product/{id}` | Actualizar producto |
| DELETE | `/product/{id}` | Eliminar producto |

### keycloak-adapter (:8088)

| Método | URL | Descripción |
|---|---|---|
| GET | `/roles` | Validar JWT y obtener roles |
| GET | `/valid` | Verificar validez del token |
| POST | `/login` | Autenticar usuario |
| POST | `/logout` | Cerrar sesión |
| POST | `/refresh` | Refrescar token |

---

## Patrones de Diseño Implementados

| Patrón | Ubicación | Descripción |
|---|---|---|
| **BFF (Backend For Frontend)** | `bff-web/` | Un único punto de entrada para el frontend React |
| **Circuit Breaker** | `bff-web/BffController.java` | Resilience4j evita cascadas de fallos |
| **Factory Method** | `alertas-ms/factory/` | `AlertaFactory` con implementaciones ALTO/MEDIO/BAJO |
| **Repository** | Todos los microservicios | Abstracción de persistencia vía Spring Data JPA |
| **Singleton** | Todos los microservicios | Beans gestionados por Spring IoC |
| **API Gateway** | `apigateway/` | Punto único de entrada con filtros de autenticación |
| **Service Registry** | `eurekaServer/` | Netflix Eureka para discovery |

---

## Docker — Detalles de Construcción

Cada microservicio tiene su propio `Dockerfile` multi-etapa en `/docker/`:

```
docker/
├── Dockerfile.eureka
├── Dockerfile.admin
├── Dockerfile.gateway
├── Dockerfile.keycloak
├── Dockerfile.customer
├── Dockerfile.product
├── Dockerfile.reportes
├── Dockerfile.alertas
├── Dockerfile.bff
├── Dockerfile.frontend
└── nginx.conf
```

Build optimizado con `mvn dependency:go-offline` para cachear dependencias.

---

## Notas Importantes

### Base de datos

Cada microservicio usa **dos perfiles de base de datos** intercambiables:

| Perfil | Base de datos | Cuándo |
|---|---|---|
| `local` (default sin perfil) | **H2 en memoria** | Desarrollo local (`mvn spring-boot:run`) |
| `docker` | **MySQL 8.0** + volumen persistente | Docker Compose |

**H2 (local):** Sin instalación. Consolas en `/h2-console` (JDBC: `jdbc:h2:mem:<nombre-db>`, user: `sa`, pass: vacío).

**MySQL (Docker):** Se levanta automáticamente con `docker compose up`. Datos persistentes entre reinicios vía volumen `mysql_data`.

| Microservicio | Base MySQL | Puerto |
|---|---|---|
| reportes-ms | `reportesdb` | 8081 |
| alertas-ms | `alertasdb` | 8082 |
| customer-ms | `customerdb` | 8085 |
| product-ms | `productdb` | 8083 |

### Perfil local vs Eureka

- **Perfil `local`**: Desactiva Eureka (`eureka.client.enabled=false`). Startup ~5.5s. Ideal para desarrollo.
- **Por defecto**: Activa Eureka. Startup ~15-20s (espera healthcheck). Requiere `eureka-server` corriendo.

### Circuit Breaker

Si `reportes-ms` o `alertas-ms` no responden, el BFF retorna `503 SERVICE_UNAVAILABLE` con mensaje descriptivo. El circuito se abre tras 3 fallos consecutivos y se recupera tras 10 segundos.

### CORS

Configurado globalmente en `bff-web/config/CorsConfig.java`. El frontend no maneja CORS.
