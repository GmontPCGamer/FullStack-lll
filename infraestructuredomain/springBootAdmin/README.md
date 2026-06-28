# Spring Boot Admin

**Monitor de Microservicios**

## Descripción

Servidor de monitoreo Spring Boot Admin que proporciona una interfaz web para supervisar el estado y métricas de todos los microservicios registrados en Eureka.

## Tecnologías

- Java 17 · Spring Boot Admin Server (de.codecentric)
- Eureka Client · Actuator

## Configuración

| Propiedad | Valor |
|---|---|
| Puerto | `8062` |
| Dashboard | `http://localhost:8062` |

## Ejecución

```bash
cd infraestructuredomain/springBootAdmin
mvn spring-boot:run
```

Requiere `eureka-server` corriendo.
