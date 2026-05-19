# Análisis de Patrones de Diseño y Arquetipos
**Municipalidad Valle del Sol - Plataforma de Gestión de Emergencias**

## 1. Patrones Arquitectónicos
### Backend For Frontend (BFF)
Se implementó el componente `bff-web` utilizando el patrón arquitectónico BFF. Este componente actúa como la única puerta de entrada para el cliente web (React), orquestando y agregando las llamadas a los microservicios subyacentes (`reportes-ms` y `alertas-ms`).
- **Justificación:** El BFF permite adaptar las respuestas a las necesidades específicas del frontend, reduciendo el "Over-fetching" o "Under-fetching" de datos. Además, maneja problemas como CORS de manera centralizada.

### Arquitectura de Microservicios Independientes
Se implementaron `reportes-ms` y `alertas-ms` como microservicios completamente independientes.
- **Justificación:** Facilita la escalabilidad independiente. Si hay un aumento en los reportes de incendios, se puede escalar el servicio de reportes sin afectar al de alertas. Permite despliegues independientes y aislamiento de fallos (aislamiento de bases de datos H2 por microservicio).

## 2. Patrones de Diseño (Backend)
### Repository Pattern (Patrón Repositorio)
Aplicado en `ReporteRepository.java` y `AlertaRepository.java` mediante Spring Data JPA.
- **Justificación:** Abstrae la lógica de acceso a datos de la base de datos (H2/MySQL). Permite al controlador/servicio interactuar con las entidades de dominio sin preocuparse por consultas SQL específicas, facilitando el mantenimiento y pruebas.

### Singleton Pattern (Patrón Singleton)
Utilizado inherentemente por el contenedor de Inyección de Dependencias de Spring Boot (IoC Container) para los Controladores y Repositorios.
- **Justificación:** Garantiza que solo exista una instancia de los controladores y repositorios en toda la aplicación, reduciendo el consumo de memoria y optimizando el rendimiento.

### Factory / Builder Pattern
(A través de la anotación `@Builder` de Lombok, si bien aquí usamos `@Data` que genera getters/setters, el concepto de instanciación controlada de entidades se gestiona en la persistencia).

## 3. Patrones de Diseño (Frontend)
### Container / Presenter Pattern
La aplicación React centraliza el estado en el componente principal (`App.jsx` actúa como contenedor inteligente que llama al BFF) e inyecta propiedades a los componentes visuales.
- **Justificación:** Separa la lógica de negocio (obtención de datos vía Axios) de la presentación visual, haciendo que los componentes sean más predecibles, testeables y reutilizables.

### Provider Pattern (Context/State Management)
El uso de `useState` y `useEffect` (Hooks de React) proporciona el patrón de manejo de estado reactivo, donde los cambios en el estado actualizan la UI automáticamente.

## Conclusión
La combinación de estos patrones asegura que la solución para la Municipalidad Valle del Sol sea **escalable, eficiente y altamente mantenible**. El sistema puede crecer fácilmente añadiendo nuevos microservicios y extendiendo el BFF.
