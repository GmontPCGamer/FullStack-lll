# Backend For Frontend (BFF): bff-web
**Municipalidad Valle del Sol — Sistema de Gestión de Incendios**

## Descripción
Componente BFF (Backend For Frontend) que actúa como único punto de entrada para el cliente web React. Agrega, adapta y orquesta las llamadas hacia los microservicios `reportes-ms` y `alertas-ms`, aplicando el **patrón Circuit Breaker** para garantizar resiliencia ante fallos.

## Patrones de Diseño Aplicados
- **BFF (Backend For Frontend) Pattern**: centraliza las llamadas del frontend, evitando que el cliente React conozca los microservicios subyacentes.
- **Circuit Breaker Pattern** (Resilience4j): si `reportes-ms` o `alertas-ms` falla repetidamente, el circuito se "abre" y se retorna una respuesta de fallback `503 SERVICE_UNAVAILABLE` en lugar de propagar el error.

## Tecnologías
- Java 17
- Spring Boot 4.x (WebFlux — reactivo)
- Resilience4j 2.2.0 (Circuit Breaker)
- WebClient (reactive HTTP client)
- Maven (arquetipo base)

## Arquitectura
```
React Frontend
      │
      ▼  (puerto 8080)
   [BFF - bff-web]
      │              │
      ▼              ▼
[reportes-ms]   [alertas-ms]
  :8081            :8082
```

## Estructura del Proyecto
```
bff-web/
├── src/main/java/com/valledelsol/bff/
│   ├── BffWebApplication.java             ← Punto de entrada + WebClient Bean
│   ├── config/CorsConfig.java             ← Configuración CORS global (WebFlux)
│   └── controller/BffController.java      ← Endpoints + Circuit Breaker
├── src/main/resources/application.properties
└── pom.xml
```

## Configuración
| Propiedad | Valor |
|---|---|
| Puerto | `8084` |
| reportes-ms URL | `http://localhost:8081/api/reportes` |
| alertas-ms URL | `http://localhost:8082/api/alertas` |

### Circuit Breaker (Resilience4j)
| Parámetro | Valor |
|---|---|
| Ventana deslizante | 5 llamadas |
| Umbral de fallos | 50% |
| Tiempo en estado OPEN | 10 segundos |

## Endpoints REST (expuestos al frontend)
| Método | URL | Descripción |
|---|---|---|
| GET | `/api/bff/reportes` | Obtiene reportes de `reportes-ms` |
| POST | `/api/bff/reportes` | Crea reporte en `reportes-ms` |
| GET | `/api/bff/alertas` | Obtiene alertas de `alertas-ms` |
| POST | `/api/bff/alertas` | Crea alerta en `alertas-ms` |

## Instalación y Ejecución

### Prerrequisitos
- Java 17+
- Maven 3.8+
- `reportes-ms` corriendo en puerto 8081
- `alertas-ms` corriendo en puerto 8082

### Pasos
```bash
# Orden de inicio obligatorio:
# 1. Iniciar reportes-ms  (puerto 8081)
# 2. Iniciar alertas-ms   (puerto 8082)
# 3. Iniciar bff-web      (puerto 8084)

cd businessdomain/bff-web
mvn clean install -DskipTests
mvn spring-boot:run
```
BFF disponible en: `http://localhost:8084`

## Monitorear Circuit Breaker
```
http://localhost:8080/actuator/circuitbreakers
http://localhost:8080/actuator/health
```
