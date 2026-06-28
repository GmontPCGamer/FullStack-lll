# Microservicio: customer-ms

**Municipalidad Valle del Sol — Sistema de Gestión de Incendios**

## Descripción

Microservicio REST que gestiona los **clientes** del sistema. Proporciona CRUD completo y se integra con `product-ms` mediante WebClient con balanceo de carga (Eureka) para enriquecer las respuestas con datos de productos asociados.

## Patrones de Diseño Aplicados

- **Repository Pattern**: `CustomerRepository` extiende `JpaRepository`.
- **Circuit Breaker**: Resilience4j en `GET /customer/{id}` para tolerancia a fallos de `product-ms`.

## Tecnologías

- Java 17 · Spring Boot 4.x · Spring Data JPA · H2 · WebClient (reactivo)
- Eureka Client · SpringDoc OpenAPI · Resilience4j · Lombok

## Estructura

```
customer/
├── src/main/java/com/paymentchain/customer/
│   ├── CustomerApplication.java
│   ├── controller/CustomerRestController.java
│   ├── entities/
│   │   ├── Customer.java
│   │   └── CustomerProduct.java
│   └── repository/CustomerRepository.java
├── src/test/java/com/paymentchain/customer/
│   ├── CustomerIntegrationTest.java
│   └── CustomerRestControllerTest.java
└── pom.xml
```

## Configuración

| Propiedad | Valor |
|---|---|
| Puerto | `8085` |
| Base de datos | H2 en memoria (`customerdb`) |
| Consola H2 | `http://localhost:8085/h2-console` |

## Endpoints REST

| Método | URL | Descripción |
|---|---|---|
| GET | `/customer` | Lista todos los clientes |
| GET | `/customer/{id}` | Obtiene un cliente (con Circuit Breaker) |
| POST | `/customer` | Crea un cliente |
| PUT | `/customer/{id}` | Actualiza nombre/teléfono |
| DELETE | `/customer/{id}` | Elimina un cliente |

## Ejecución

```bash
cd businessdomain/customer
mvn spring-boot:run
```

Servicio en `http://localhost:8085`.
