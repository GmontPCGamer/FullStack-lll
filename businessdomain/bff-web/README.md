# Backend For Frontend (BFF): bff-web

**Municipalidad Valle del Sol — Sistema de Gestión de Incendios**

## Descripción

Componente BFF (Backend For Frontend) que actúa como único punto de entrada para el cliente web React. Agrega, adapta y orquesta las llamadas hacia los microservicios `reportes-ms` y `alertas-ms`, aplicando el **patrón Circuit Breaker** para garantizar resiliencia ante fallos.

## Patrones de Diseño Aplicados

- **BFF (Backend For Frontend) Pattern**: centraliza las llamadas del frontend, evitando que el cliente React conozca los microservicios subyacentes.
- **Circuit Breaker Pattern** (Resilience4j): si `reportes-ms` o `alertas-ms` falla repetidamente, el circuito se abre y retorna `503 SERVICE_UNAVAILABLE`.

## Tecnologías

- Java 17
- Spring Boot 4.x (WebFlux — reactivo)
- Resilience4j 2.2.0 (Circuit Breaker)
- WebClient (reactive HTTP client)
- Eureka Client
- Maven

## Arquitectura

```
React Frontend (:5173)
      │
      ▼  (/api/bff/*)
   [BFF - bff-web] (:8084)
      │              │
      ▼              ▼
[reportes-ms]   [alertas-ms]
   :8081            :8082
```

## Estructura

```
bff-web/
├── src/main/java/com/valledelsol/bff/
│   ├── BffWebApplication.java
│   ├── config/CorsConfig.java
│   └── controller/BffController.java
├── src/test/java/com/valledelsol/bff/
│   ├── BffControllerTest.java
│   ├── BffE2ETest.java
│   └── BffWebIntegrationTest.java
├── src/main/resources/
│   ├── application.properties
│   └── application-local.properties
└── pom.xml
```

## Configuración

| Propiedad | Valor |
|---|---|
| Puerto | `8084` |
| reportes.service.url | `http://localhost:8081/api/reportes` |
| alertas.service.url | `http://localhost:8082/api/alertas` |

### Circuit Breaker

| Parámetro | Valor |
|---|---|
| Ventana deslizante | 5 llamadas |
| Umbral de fallos | 50% |
| Tiempo en OPEN | 10 segundos |

## Endpoints (expuestos al frontend)

| Método | URL | Descripción |
|---|---|---|
| GET | `/api/bff/reportes` | Obtiene reportes de `reportes-ms` |
| POST | `/api/bff/reportes` | Crea reporte en `reportes-ms` |
| GET | `/api/bff/alertas` | Obtiene alertas de `alertas-ms` |
| POST | `/api/bff/alertas` | Crea alerta en `alertas-ms` |

## Instalación y Ejecución

### Prerrequisitos

- Java 17+, Maven 3.8+
- `reportes-ms` corriendo en puerto 8081
- `alertas-ms` corriendo en puerto 8082

### Orden de inicio obligatorio

```bash
# 1. Iniciar reportes-ms (:8081)
cd businessdomain/reportes-ms
mvn spring-boot:run -Dspring-boot.run.profiles=local

# 2. Iniciar alertas-ms (:8082)
cd businessdomain/alertas-ms
mvn spring-boot:run -Dspring-boot.run.profiles=local

# 3. Iniciar bff-web (:8084)
cd businessdomain/bff-web
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### Ejecutar Pruebas

```bash
mvn test
```

### Monitoreo

```
http://localhost:8084/actuator/circuitbreakers
http://localhost:8084/actuator/health
```

## Docker

```bash
docker compose up -d bff-web
```
