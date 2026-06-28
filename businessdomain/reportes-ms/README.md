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
- Maven (arquetipo base)

## Estructura del Proyecto
```
reportes-ms/
├── src/main/java/com/valledelsol/reportes/
│   ├── ReportesApplication.java       ← Punto de entrada
│   ├── controller/ReporteController.java
│   ├── entities/Reporte.java
│   └── repository/ReporteRepository.java
├── src/test/java/com/valledelsol/reportes/
│   └── ReporteControllerTest.java     ← Pruebas unitarias
├── src/main/resources/application.properties
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
  "descripcion": "Foco de incendio detectado en sector norte, aproximadamente 2 hectáreas"
}
```

## Instalación y Ejecución

### Prerrequisitos
- Java 17+
- Maven 3.8+

### Pasos
```bash
# Desde la raíz del proyecto (Proyecto_valle_dol_sol/)
cd businessdomain/reportes-ms

# Compilar
mvn clean install -DskipTests

# Ejecutar
mvn spring-boot:run
```
El servicio estará disponible en: `http://localhost:8081`

## Ejecutar Pruebas Unitarias
```bash
mvn test
```
