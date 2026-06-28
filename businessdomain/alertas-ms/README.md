# Microservicio: alertas-ms

**Municipalidad Valle del Sol — Sistema de Gestión de Incendios**

## Descripción

Microservicio REST independiente que gestiona las **alertas comunitarias** emitidas por la municipalidad ante riesgos de incendio. Las alertas tienen niveles de gravedad: `ALTO`, `MEDIO` o `BAJO`.

## Patrones de Diseño Aplicados

- **Repository Pattern**: `AlertaRepository` extiende `JpaRepository` para abstraer el acceso a datos.
- **Factory Method Pattern**: `AlertaFactory` define el método abstracto `crearAlerta()`, con implementaciones concretas para cada nivel de gravedad. El controlador delega la creación a la fábrica correspondiente según el nivel recibido.

## Tecnologías

- Java 17
- Spring Boot 4.x
- Spring Data JPA
- H2 (base de datos en memoria)
- Lombok
- Eureka Client (registro en service discovery)
- Maven

## Estructura

```
alertas-ms/
├── src/main/java/com/valledelsol/alertas/
│   ├── AlertasApplication.java
│   ├── controller/AlertaController.java
│   ├── entities/Alerta.java
│   ├── exception/GlobalExceptionHandler.java
│   ├── factory/AlertaFactory.java
│   └── repository/AlertaRepository.java
├── src/test/java/com/valledelsol/alertas/
│   ├── AlertaControllerTest.java
│   ├── AlertaIntegrationTest.java
│   └── exception/GlobalExceptionHandlerTest.java
├── src/main/resources/
│   ├── application.properties
│   └── application-local.properties
└── pom.xml
```

## Configuración

| Propiedad | Valor |
|---|---|
| Puerto | `8082` |
| Base de datos | H2 en memoria (local) / MySQL `alertasdb` (Docker) |
| Consola H2 | `http://localhost:8082/h2-console` (solo perfil local) |

## Endpoints REST

| Método | URL | Descripción |
|---|---|---|
| GET | `/api/alertas` | Lista todas las alertas |
| POST | `/api/alertas` | Crea una alerta (usa Factory Method) |
| GET | `/api/alertas/{id}` | Obtiene una alerta por ID |
| DELETE | `/api/alertas/{id}` | Elimina una alerta |

### Ejemplo de Request (POST)

```json
{
  "titulo": "Riesgo de Incendio en Sector Norte",
  "mensaje": "Se detectó humo en cercanías del parque.",
  "nivelGravedad": "ALTO"
}
```

> Si `nivelGravedad` no se envía, el Factory Method asigna `MEDIO` por defecto.

## Instalación y Ejecución

### Prerrequisitos

- Java 17+
- Maven 3.8+

### Sin Eureka (perfil local — recomendado)

```bash
cd businessdomain/alertas-ms
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

El servicio estará en `http://localhost:8082`.

### Con Eureka

```bash
cd businessdomain/alertas-ms
mvn spring-boot:run
```

### Ejecutar Pruebas

```bash
mvn test
```

Cobertura: **95.6%** (líneas). Incluye 5 pruebas del Factory Method.

## Docker

```bash
docker compose up -d alertas-ms
```
> En Docker usa MySQL 8.0 automáticamente (perfil `docker`).
