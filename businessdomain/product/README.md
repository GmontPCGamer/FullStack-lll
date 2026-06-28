# Microservicio: product-ms

**Municipalidad Valle del Sol — Sistema de Gestión de Incendios**

## Descripción

Microservicio REST que gestiona el **catálogo de productos**. Proporciona CRUD completo y se registra en Eureka para que `customer-ms` pueda descubrirlo y consumir sus datos.

## Tecnologías

- Java 17 · Spring Boot 4.x · Spring Data JPA · H2
- Eureka Client · SpringDoc OpenAPI · Lombok

## Estructura

```
product/
├── src/main/java/com/paymentchain/product/
│   ├── ProductApplication.java
│   ├── controller/ProductRestController.java
│   ├── entities/Product.java
│   └── repository/ProductRepository.java
├── src/test/java/com/paymentchain/product/
│   ├── ProductIntegrationTest.java
│   └── ProductRestControllerTest.java
└── pom.xml
```

## Configuración

| Propiedad | Valor |
|---|---|
| Puerto | `8083` |
| Base de datos | H2 en memoria (`productdb`) |
| Consola H2 | `http://localhost:8083/h2-console` |

## Endpoints REST

| Método | URL | Descripción |
|---|---|---|
| GET | `/product` | Lista todos los productos |
| GET | `/product/{id}` | Obtiene un producto |
| POST | `/product` | Crea un producto |
| PUT | `/product/{id}` | Actualiza código/nombre |
| DELETE | `/product/{id}` | Elimina un producto |

## Ejecución

```bash
cd businessdomain/product
mvn spring-boot:run
```

Servicio en `http://localhost:8083`.
