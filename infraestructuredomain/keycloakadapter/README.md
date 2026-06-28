# Keycloak Adapter

**Servicio de Autenticación y Autorización**

## Descripción

Adaptador de integración con Keycloak para autenticación y autorización. Proporciona endpoints para login, logout, refresh de tokens, validación JWT y extracción de roles. Utiliza JWKS para verificar firmas de tokens de forma segura.

## Tecnologías

- Java 17 · Spring Boot Web · Eureka Client
- `com.auth0:java-jwt` · `com.auth0:jwks-rsa` · RestTemplate · Lombok

## Configuración

| Propiedad | Valor |
|---|---|
| Puerto | `8088` |

### Endpoints

| Método | URL | Descripción |
|---|---|---|
| GET | `/roles` | Valida JWT y retorna roles del token |
| GET | `/valid` | Verifica si el token sigue vigente |
| POST | `/login` | Autentica usuario/contraseña |
| POST | `/logout` | Invalida refresh token |
| POST | `/refresh` | Refresca token expirado |

## Ejecución

```bash
cd infraestructuredomain/keycloakadapter
mvn spring-boot:run
```

Requiere Keycloak Server corriendo en `http://localhost:8091`.
