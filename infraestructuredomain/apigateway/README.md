# API Gateway

**Spring Cloud Gateway**

## Descripción

API Gateway que enruta todo el tráfico entrante al microservicio `customer-ms` mediante balanceo de carga (Eureka). Aplica un filtro de autenticación personalizado que valida JWTs contra `keycloak-adapter` y verifica el rol `Partners`.

## Patrones de Diseño Aplicados

- **API Gateway Pattern**: punto único de entrada, enrutamiento y filtrado.
- **Authentication Filter**: filtro personalizado que extrae el token Bearer, lo valida contra Keycloak y verifica roles.

## Tecnologías

- Java 17 · Spring Cloud Gateway (reactivo)
- Eureka Client · WebFlux · Lombok

## Configuración

| Propiedad | Valor |
|---|---|
| Puerto | `8080` |

## Ejecución

```bash
cd infraestructuredomain/apigateway
mvn spring-boot:run
```

Requiere `eureka-server` y `keycloak-adapter` corriendo.
