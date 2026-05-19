# Microservicio: alertas-ms
**Municipalidad Valle del Sol — Sistema de Gestión de Incendios**

## Descripción
Microservicio REST independiente que gestiona las **alertas comunitarias** emitidas por la municipalidad ante riesgos de incendio. Las alertas tienen niveles de gravedad: `ALTO`, `MEDIO` o `BAJO`.

## Patrones de Diseño Aplicados
- **Repository Pattern**: `AlertaRepository` extiende `JpaRepository` para abstraer el acceso a datos.
- **Factory Method Pattern**: `AlertaFactory` define el método abstracto `crearAlerta()`, con implementaciones concretas para cada nivel de gravedad. El controlador delega la creación de alertas a la fábrica correspondiente según el nivel recibido.

## Tecnologías
- Java 17
- Spring Boot 4.x
- Spring Data JPA
- H2 (base de datos en memoria)
- Lombok
- Maven (arquetipo base)

## Estructura del Proyecto
```
alertas-ms/
├── src/main/java/com/valledelsol/alertas/
│   ├── AlertasApplication.java              ← Punto de entrada
│   ├── controller/AlertaController.java
│   ├── entities/Alerta.java
│   ├── factory/AlertaFactory.java           ← Factory Method Pattern
│   └── repository/AlertaRepository.java
├── src/test/java/com/valledelsol/alertas/
│   └── AlertaControllerTest.java            ← Pruebas unitarias (8 tests)
├── src/main/resources/application.properties
└── pom.xml
```

## Configuración
| Propiedad | Valor |
|---|---|
| Puerto | `8082` |
| Base de datos | H2 en memoria (`alertasdb`) |
| Consola H2 | `http://localhost:8082/h2-console` |

## Endpoints REST
| Método | URL | Descripción |
|---|---|---|
| GET | `/api/alertas` | Lista todas las alertas |
| POST | `/api/alertas` | Crea una nueva alerta (usa Factory Method) |
| GET | `/api/alertas/{id}` | Obtiene una alerta por ID |
| DELETE | `/api/alertas/{id}` | Elimina una alerta |

### Ejemplo de Request (POST)
```json
{
  "titulo": "Riesgo de Incendio en Sector Norte",
  "mensaje": "Se detectó humo en cercanías del parque. Evite el sector.",
  "nivelGravedad": "ALTO"
}
```
> Si `nivelGravedad` no se envía, el Factory Method asignará `MEDIO` por defecto.

## Instalación y Ejecución

### Prerrequisitos
- Java 17+
- Maven 3.8+

### Pasos
```bash
# Desde la raíz del proyecto (Proyecto_valle_dol_sol/)
cd businessdomain/alertas-ms

# Compilar
mvn clean install -DskipTests

# Ejecutar
mvn spring-boot:run
```
El servicio estará disponible en: `http://localhost:8082`

## Ejecutar Pruebas Unitarias
```bash
mvn test
```
Incluye 8 pruebas unitarias que cubren:
- Listado de alertas
- Creación con nivel por defecto (Factory Method)
- Creación con niveles explícitos
- Búsqueda por ID (encontrado / no encontrado)
- Los tres tipos de fábricas (ALTO, MEDIO, BAJO)
