# Análisis de Patrones de Diseño y Arquetipos
**Municipalidad Valle del Sol - Plataforma de Gestión de Emergencias**

---

## 1. Patrones Arquitectónicos

### Backend For Frontend (BFF)
**Componente:** `bff-web` (puerto 8080)

El BFF actúa como la única puerta de entrada para el cliente web React, orquestando y agregando las llamadas a los microservicios subyacentes.

**Justificación:**
- Evita que el frontend conozca la topología interna de los microservicios.
- Resuelve el problema de CORS de manera centralizada.
- Permite adaptar las respuestas a las necesidades específicas del cliente web (reducción de over-fetching/under-fetching).

### Arquitectura de Microservicios Independientes
**Componentes:** `reportes-ms` (puerto 8081) y `alertas-ms` (puerto 8082)

Dos microservicios completamente desacoplados, cada uno con su propia base de datos H2 en memoria y ciclo de vida independiente.

**Justificación:**
- Escalabilidad independiente: si hay un pico de reportes de incendios, solo se escala `reportes-ms` sin afectar `alertas-ms`.
- Aislamiento de fallos: si `alertas-ms` cae, `reportes-ms` sigue operativo.
- Despliegues independientes sin riesgo de regresión en el otro servicio.

---

## 2. Patrones de Diseño (Backend)

### 2.1 Repository Pattern (Patrón Repositorio)
**Implementado en:** `ReporteRepository.java`, `AlertaRepository.java`

```java
// alertas-ms
public interface AlertaRepository extends JpaRepository<Alerta, Long> {}

// reportes-ms
public interface ReporteRepository extends JpaRepository<Reporte, Long> {}
```

**Justificación:**
Abstrae completamente la lógica de acceso a datos. Los controladores interactúan con las entidades de dominio sin conocer consultas SQL específicas. Esto facilita el mantenimiento (si se cambia H2 por MySQL, solo se modifica la capa de configuración) y las pruebas unitarias (se puede mockear el repositorio con Mockito sin levantar una BD real).

---

### 2.2 Factory Method Pattern (Patrón Fábrica)
**Implementado en:** `AlertaFactory.java` + `AlertaController.java`

```java
// Clase abstracta con Factory Method
public abstract class AlertaFactory {
    public abstract Alerta crearAlerta(String titulo, String mensaje);

    // Fábricas concretas
    public static AlertaFactory deNivelAlto()  { ... }
    public static AlertaFactory deNivelMedio() { ... }
    public static AlertaFactory deNivelBajo()  { ... }
}

// Uso en el controlador
AlertaFactory factory = switch (alerta.getNivelGravedad().toUpperCase()) {
    case "ALTO"  -> AlertaFactory.deNivelAlto();
    case "BAJO"  -> AlertaFactory.deNivelBajo();
    default      -> AlertaFactory.deNivelMedio();
};
alerta = factory.fabricar(titulo, mensaje);
```

**Justificación:**
Resuelve el problema de instanciación controlada de alertas según su nivel de riesgo. Sin este patrón, la lógica de creación estaría duplicada o mezclada con la lógica del controlador. El Factory Method permite extender fácilmente el sistema (añadir nivel "CRITICO") sin modificar el código cliente.

---

### 2.3 Circuit Breaker Pattern (Patrón Disyuntor)
**Implementado en:** `BffController.java` con Resilience4j

```java
@GetMapping("/reportes")
@CircuitBreaker(name = "reportes-service", fallbackMethod = "fallbackGetReportes")
public Mono<ResponseEntity<String>> getReportes() { ... }

// Método de fallback
public Mono<ResponseEntity<String>> fallbackGetReportes(Throwable t) {
    return Mono.just(ResponseEntity.status(503).body("{\"error\":\"Servicio no disponible\"}"));
}
```

**Configuración (application.properties):**
- Ventana deslizante: 5 llamadas
- Umbral de fallo: 50%
- Tiempo en estado OPEN: 10 segundos

**Justificación:**
Resuelve el problema de **cascada de fallos** en sistemas distribuidos. Si `reportes-ms` falla (por sobrecarga o caída), sin Circuit Breaker el BFF también fallaría propagando el error al usuario. Con el Circuit Breaker activado, tras 3 fallos consecutivos el circuito se "abre" y las siguientes solicitudes reciben inmediatamente una respuesta de error controlada (503), protegiendo los recursos del sistema y mejorando la experiencia de usuario.

---

## 3. Patrones de Diseño (Frontend)

### 3.1 Container / Presenter Pattern
**Implementado en:** `App.jsx`

El componente `App.jsx` actúa como **contenedor inteligente** que gestiona el estado global (`useState`, `useEffect`) y las llamadas al BFF vía Axios. Los elementos visuales (tabla, lista de alertas, formularios) son **presentadores** que reciben datos como props o leen el estado del contenedor.

**Justificación:**
Separa la lógica de negocio (fetching de datos, manejo de errores) de la presentación visual. Hace los componentes más predecibles, testeables y reutilizables.

### 3.2 Observer Pattern (via React Hooks)
**Implementado en:** `useState` / `useEffect` en `App.jsx`

El uso de `useState` implementa el patrón Observer: cuando el estado cambia (nuevo reporte enviado, nueva alerta cargada), React notifica automáticamente a los componentes suscritos que deben re-renderizarse.

**Justificación:**
Permite actualizar la UI de forma reactiva sin manipulación manual del DOM, garantizando consistencia de datos en toda la interfaz.

---

## 4. Arquetipos Maven

Los tres componentes backend (`bff-web`, `reportes-ms`, `alertas-ms`) fueron construidos usando la estructura de **arquetipo Maven estándar de Spring Boot** (`spring-boot-starter-parent`), con un POM jerárquico:

```
paymentchainparent (POM raíz)
└── businessdomain (POM padre de módulos)
    ├── bff-web
    ├── reportes-ms
    └── alertas-ms
```

Esta jerarquía permite:
- Centralizar versiones de dependencias en el POM raíz (Spring Boot 4.0.3, Spring Cloud 2025.1.1)
- Compartir configuración del compilador (Java 17) en todos los módulos
- Cada microservicio hereda solo lo que necesita y añade sus propias dependencias

---

## 5. Conclusión

| Patrón | Dónde | Problema que resuelve |
|---|---|---|
| Repository | `reportes-ms`, `alertas-ms` | Abstracción del acceso a datos |
| Factory Method | `alertas-ms` | Creación controlada de alertas por nivel |
| Circuit Breaker | `bff-web` | Resiliencia ante fallos de microservicios |
| BFF | `bff-web` | Punto único de entrada para el frontend |
| Container/Presenter | Frontend | Separación estado/presentación |
| Observer | Frontend | UI reactiva ante cambios de estado |

La combinación de estos patrones garantiza que la solución para la Municipalidad Valle del Sol sea **escalable, resiliente, eficiente y altamente mantenible**.
