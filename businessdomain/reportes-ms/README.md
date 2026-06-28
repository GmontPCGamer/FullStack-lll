# Microservicio: reportes-ms

**Municipalidad Valle del Sol — Sistema de Gestión de Incendios**

## Descripción

Microservicio REST independiente que gestiona los **reportes ciudadanos de focos de incendio**. Almacena ubicación geográfica, descripción y estado del incidente.

## Patrones de Diseño Aplicados

- **Repository Pattern**: `ReporteRepository` extiende `JpaRepository` para abstraer el acceso a datos.
- **Singleton**: Spring IoC gestiona instancias únicas de controladores y repositorios.

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
reportes-ms/
├── src/main/java/com/valledelsol/reportes/
│   ├── ReportesApplication.java
│   ├── controller/ReporteController.java
│   ├── entities/Reporte.java
│   ├── exception/GlobalExceptionHandler.java
│   └── repository/ReporteRepository.java
├── src/test/java/com/valledelsol/reportes/
│   ├── ReporteControllerTest.java
│   ├── ReporteIntegrationTest.java
│   └── exception/GlobalExceptionHandlerTest.java
├── src/main/resources/
│   ├── application.properties
│   └── application-local.properties
└── pom.xml
```

## Configuración

| Propiedad | Valor |
|---|---|
| Puerto | `8081` |
| Base de datos | H2 en memoria (`reportesdb`) |
| Consola H2 | `http://localhost:8081/h2-console` |

## Endpoints REST

| Método | URL | Descripción |
|---|---|---|
| GET | `/api/reportes` | Lista todos los reportes |
| POST | `/api/reportes` | Crea un nuevo reporte |
| GET | `/api/reportes/{id}` | Obtiene un reporte por ID |
| DELETE | `/api/reportes/{id}` | Elimina un reporte por ID |

### Ejemplo de Request (POST)

```json
{
  "ubicacionLatitud": "-33.4569",
  "ubicacionLongitud": "-70.6483",
  "descripcion": "Foco de incendio detectado en sector norte"
}
```

## Instalación y Ejecución

### Prerrequisitos

- Java 17+
- Maven 3.8+

### Sin Eureka (perfil local — recomendado para desarrollo rápido)

```bash
cd businessdomain/reportes-ms
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

Inicio ~5.5s. El servicio estará en `http://localhost:8081`.

### Con Eureka (requiere eureka-server corriendo)

```bash
cd businessdomain/reportes-ms
mvn spring-boot:run
```

### Ejecutar Pruebas

```bash
mvn test
```

Cobertura: **96.4%** (líneas). Incluye pruebas unitarias, de integración y del GlobalExceptionHandler.

## Docker

```bash
docker compose up -d reportes-ms
```
