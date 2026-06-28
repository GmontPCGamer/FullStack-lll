# Eureka Server

**Service Registry — Netflix Eureka**

## Descripción

Servidor de registro y descubrimiento de servicios (Service Registry). Todos los microservicios se registran aquí y se descubren entre sí mediante su nombre de aplicación.

## Tecnologías

- Java 17 · Spring Cloud Netflix Eureka Server

## Configuración

| Propiedad | Valor |
|---|---|
| Puerto | `8761` |
| Dashboard | `http://localhost:8761` |

## Ejecución

```bash
cd infraestructuredomain/eurekaServer
mvn spring-boot:run
```

## Docker

```bash
docker compose up -d eureka-server
```

> **Nota**: Para desarrollo local sin Eureka, usar el perfil `local` en cada microservicio.
