# 🎬 Script de Video — Valle del Sol
## Sistema de Gestión de Incendios · Municipalidad Valle del Sol
### Distribución en 3 partes (estimado: 20–30 min total)

---

> **Nota de coordinación:** Cada persona graba su parte por separado y luego unen los tres videos.
> Se recomienda mostrar el código en un editor (VS Code) y el sistema corriendo en paralelo.
> El orden de edición final debe ser: **Persona 1 → Persona 2 → Persona 3**.

---

## 👤 PERSONA 1 — Arquitectura, Infraestructura y Seguridad
**Tiempo estimado: 8–10 minutos**
**Ítems de la rúbrica cubiertos: 1–11, 31–38**

### Qué mostrar en pantalla:
- Diagrama de arquitectura del README (el bloque ASCII o uno visual preparado)
- El panel de Eureka en `http://localhost:8761`
- El archivo `docker-compose.yml` abierto en VS Code
- El código de `apigateway/setups/AuthenticationFilterinig.java`
- El código de `keycloakadapter/service/JwtService.java` y `KeycloakRestService.java`
- El archivo `infraestructuredomain/apigateway/src/main/resources/application.yml`
- La colección Postman corriendo el endpoint `/login` y `/valid`

---

### 🎙️ DIÁLOGO — PERSONA 1

---

**[INTRODUCCIÓN — ~1 min]**

"Hola, mi nombre es [Nombre] y les voy a presentar la solución que desarrollamos para la **Municipalidad Valle del Sol**, un sistema de gestión de incendios que permite a la comunidad reportar focos de incendio y recibir alertas en tiempo real.

La problemática que resolvimos es la siguiente: la municipalidad necesitaba una plataforma que permitiera a los ciudadanos reportar emergencias de incendio y que el personal municipal pudiera emitir alertas de riesgo de forma eficiente, todo esto de manera escalable y con alta disponibilidad. La solución tradicional con una arquitectura monolítica generaba un acoplamiento fuerte, cuellos de botella en los picos de emergencia y dificultades para mantener el sistema sin interrumpir el servicio.

Por eso elegimos una **arquitectura de microservicios basada en DDD** — Domain Driven Design — descomponiendo el sistema en subdominios de negocio independientes: reportes, alertas, clientes y productos, cada uno con su propio ciclo de vida y base de datos."

---

**[ARQUITECTURA GENERAL — ~2 min]**

*[Mostrar el diagrama del README]*

"Acá pueden ver la arquitectura completa. Tenemos dos capas principales:

La primera es la **capa de infraestructura**, donde viven los servicios transversales: el **Eureka Server** para el descubrimiento de servicios, el **API Gateway** como punto de entrada único, el **Spring Boot Admin** para el monitoreo, y el **Keycloak Adapter** para la seguridad.

La segunda es la **capa de dominio de negocio**, donde están nuestros microservicios: `reportes-ms`, `alertas-ms`, `bff-web`, `customer-ms` y `product-ms`.

El flujo de una petición desde el usuario es: el frontend en React llama al BFF, el BFF orquesta los microservicios de negocio, y todos se registran automáticamente en Eureka para que puedan encontrarse entre sí sin URLs hardcodeadas.

Cada componente fue elegido con justificación: usamos **Spring Cloud** porque nos da un ecosistema completo para microservicios en Java, **Docker Compose** para que el ambiente sea reproducible en cualquier máquina, y **MySQL en producción** con H2 en desarrollo para facilitar el onboarding."

---

**[SERVICE DISCOVERY — ~2 min]**

*[Abrir `http://localhost:8761` en el navegador]*

"Pasemos al Service Discovery. Aquí estoy en el panel de **Netflix Eureka**, que es nuestra base de datos dinámica de ubicaciones de red. Pueden ver que están registrados todos los microservicios: `reportes-ms`, `alertas-ms`, `bff-web`, `customer-ms`, `product-ms`, el gateway y el admin.

¿Por qué Eureka? Porque en un entorno de microservicios los servicios pueden estar en cualquier IP y puerto, especialmente con Docker. Eureka implementa el patrón de **Self-Registration**: cada microservicio, al levantarse, se registra automáticamente en Eureka enviando un heartbeat cada 30 segundos. Si un servicio se cae, Eureka lo elimina del registro y los demás dejan de enrutarle tráfico.

Lo bueno es que es transparente para el código: usamos `@LoadBalanced` y `lb://nombre-servicio` en las URLs, y Eureka resuelve la IP real en tiempo de ejecución. Lo malo es que agrega latencia inicial y complejidad de configuración.

*[Abrir `eurekaServer/src/main/resources/application.properties`]*

La configuración es simple: definimos el nombre de la aplicación, el puerto 8761, y desactivamos el auto-registro del propio servidor para que no aparezca como cliente de sí mismo.

Para descargarlo, está en `infraestructuredomain/eurekaServer/`. Para levantarlo:

```bash
cd infraestructuredomain/eurekaServer
mvn spring-boot:run
```

Y verificamos que responde en `http://localhost:8761`."

---

**[SEGURIDAD — ~2 min]**

*[Abrir `keycloakadapter/service/JwtService.java` y `KeycloakRestService.java`]*

"Ahora la seguridad. Justificamos aplicar seguridad porque la plataforma maneja datos sensibles de emergencias y no podemos permitir accesos no autorizados a crear alertas falsas o eliminar reportes reales.

Usamos **Keycloak** como servidor de identidad y **JWT** como mecanismo de autenticación stateless. El flujo es:

Primero, el usuario llama a `/login` en el `keycloak-adapter`. Este servicio **genera el JWT** llamando al endpoint de token de Keycloak, que retorna un access token firmado con RS256.

*[Mostrar `JwtService.java`]*

El JWT se **configura** con claims estándar: `sub` para el usuario, `exp` para la expiración, y `realm_access.roles` para los roles. La validación ocurre en el API Gateway:

*[Abrir `apigateway/setups/AuthenticationFilterinig.java`]*

Acá está el filtro de autenticación del gateway. Cada petición que llega es interceptada, se extrae el header `Authorization: Bearer <token>`, se llama al endpoint `/valid` del `keycloak-adapter` para **validar el JWT**, y solo si es válido se enruta la petición al microservicio destino. Si el token es inválido o expirado, retorna un 401 inmediatamente."

---

**[API GATEWAY — ~1.5 min]**

*[Abrir `apigateway/src/main/resources/application.yml`]*

"El **API Gateway** es el único punto de entrada público al sistema. Funciona como un proxy inteligente con tres tipos de filtros:

*[Mostrar `GlobalPreFiltering.java`]*

El **Pre-filter** se ejecuta antes de enrutar la petición: acá es donde validamos el JWT, logueamos la entrada, y podemos agregar headers adicionales.

*[Mostrar `GlobalPostFiltering.java`]*

El **Post-filter** se ejecuta después de recibir la respuesta del microservicio: acá logueamos la respuesta y podemos transformarla.

*[Mostrar `application.yml`]*

Y acá está la configuración de rutas: cada path como `/customer/**` se mapea al microservicio correspondiente usando la URL de Eureka con el prefijo `lb://`. El gateway distribuye automáticamente la carga entre instancias.

*[Mostrar en Postman un request con y sin token]*

Pueden ver que sin token obtenemos 401, y con token válido la petición pasa correctamente al microservicio."

---

**[CIERRE PERSONA 1 — ~30 seg]**

"En resumen, la infraestructura que levantamos da al sistema **descubrimiento de servicios automático, un único punto de entrada seguro, y autenticación con JWT**. Esto asegura que el sistema pueda escalar horizontalmente y mantener la seguridad sin cambios en el código de los microservicios de negocio. Le paso ahora a [Nombre Persona 2] para que les muestre los microservicios en detalle."

---
---

## 👤 PERSONA 2 — Microservicios de Negocio, Patrones y Pruebas
**Tiempo estimado: 10–12 minutos**
**Ítems de la rúbrica cubiertos: 12–30**

### Qué mostrar en pantalla:
- Estructura de carpetas de `reportes-ms` y `alertas-ms` en VS Code
- Código de `AlertaFactory.java`
- Código de `BffController.java` (Circuit Breaker)
- `application.properties` de cada microservicio
- Los archivos `pom.xml` con dependencias
- Resultados de tests corriendo con `mvn test` (o el reporte de JaCoCo)
- Spring Boot Admin en `http://localhost:8062`
- Postman haciendo peticiones a los endpoints

---

### 🎙️ DIÁLOGO — PERSONA 2

---

**[INTRODUCCIÓN — ~30 seg]**

"Gracias [Nombre 1]. Yo soy [Nombre] y les voy a mostrar en profundidad cómo funcionan los microservicios de negocio: `reportes-ms`, `alertas-ms` y el `bff-web`, incluyendo los patrones de diseño aplicados, la seguridad interna, el manejo de excepciones y las pruebas."

---

**[MICROSERVICIO: REPORTES-MS — ~2.5 min]**

*[Abrir la carpeta `businessdomain/reportes-ms/` en VS Code]*

"Empecemos con `reportes-ms`. El **dominio** de este microservicio es gestionar los reportes de focos de incendio que envían los ciudadanos.

*[Mostrar `entities/Reporte.java`]*

La entidad `Reporte` tiene los campos clave: ubicación por latitud y longitud, descripción del incidente, timestamp de creación y estado. Estas son las **reglas de negocio**: no se puede crear un reporte sin ubicación, y la descripción es obligatoria — validaciones que están en el controlador.

**La estructura de carpetas** sigue la arquitectura en capas estándar de Spring:
- `controller/` — recibe las peticiones HTTP y delega al repositorio
- `entities/` — el modelo de dominio mapeado a la base de datos con JPA
- `repository/` — la abstracción de persistencia via Spring Data
- `exception/` — el manejo centralizado de errores
- `resources/` — los archivos de configuración

*[Mostrar `ReporteController.java`]*

El **controlador** expone cuatro endpoints REST: GET para listar, POST para crear, GET por ID y DELETE. Usamos `ResponseEntity` para retornar el **HttpStatus correcto**: 201 Created al crear, 200 OK al listar, 404 cuando no se encuentra un reporte, y 500 en errores internos — esto es una buena práctica que vimos en clases.

*[Mostrar `application.properties`]*

La **configuración** incluye el nombre de la aplicación para Eureka, el puerto, el datasource, y la configuración de Actuator para exponer los endpoints de health y métricas. Estas **métricas internas** permiten a Spring Boot Admin monitorear el microservicio en tiempo real."

---

**[MICROSERVICIO: ALERTAS-MS Y FACTORY METHOD — ~3 min]**

*[Abrir `businessdomain/alertas-ms/`]*

"Ahora `alertas-ms`. El dominio es gestionar las alertas que emite la municipalidad hacia la comunidad. Aquí tenemos el patrón de diseño más interesante del proyecto: el **Factory Method**.

*[Mostrar `factory/AlertaFactory.java`]*

El problema que resuelve es el siguiente: una alerta puede tener tres niveles de gravedad — ALTO, MEDIO y BAJO — y cada nivel podría tener lógica de creación diferente. Sin el patrón, tendríamos un `if-else` gigante en el controlador mezclando lógica de negocio con lógica de creación.

Con Factory Method, `AlertaFactory` es una clase abstracta con el método `crearAlerta()` que cada subclase implementa a su manera. Tenemos tres fábricas concretas: `deNivelAlto()`, `deNivelMedio()` y `deNivelBajo()`.

*[Mostrar `AlertaController.java` — la parte del switch]*

Acá en el controlador, cuando llega un POST para crear una alerta, hacemos un switch sobre el `nivelGravedad` del request, obtenemos la fábrica correspondiente, y llamamos a `fabricar()`. Esto nos permite agregar fácilmente un nivel \"CRITICO\" en el futuro sin tocar el código cliente.

*[Mostrar `GlobalExceptionHandler.java`]*

El **manejo de excepciones** está centralizado en `GlobalExceptionHandler` anotado con `@RestControllerAdvice`. Captura cualquier excepción no controlada y retorna siempre una respuesta JSON estructurada con el código de error y el mensaje, nunca stacktraces al cliente — otra buena práctica."

---

**[BFF-WEB Y CIRCUIT BREAKER — ~2.5 min]**

*[Abrir `businessdomain/bff-web/`]*

"El `bff-web` es el **Backend For Frontend**: el único servicio con el que habla el React. Su dominio es orquestar y agregar las respuestas de `reportes-ms` y `alertas-ms`.

*[Mostrar `BffController.java`]*

Acá está el patrón más importante para la resiliencia: el **Circuit Breaker con Resilience4j**. La anotación `@CircuitBreaker(name = \"reportes-service\", fallbackMethod = \"fallbackGetReportes\")` le dice a Resilience4j que monitoree las llamadas a `reportes-ms`.

Si el servicio falla más del 50% de las veces en una ventana de 5 llamadas, el circuito se **abre**: durante los próximos 10 segundos, las llamadas ni siquiera llegan al microservicio, sino que van directo al `fallbackGetReportes` que retorna un 503 con mensaje descriptivo. Esto evita la **cascada de fallos**: si `reportes-ms` está saturado, no queremos que el BFF también colapse intentando conectarse.

*[Mostrar `application.properties` del BFF]*

La configuración del Circuit Breaker está en el properties: sliding window de 5, threshold del 50%, wait duration de 10 segundos.

*[Mostrar en el navegador: apagar reportes-ms y hacer una petición al BFF]*

Miren lo que pasa si apago `reportes-ms` — el BFF responde inmediatamente con el mensaje de error controlado en lugar de quedarse esperando hasta timeout."

---

**[DEPENDENCIAS Y CONFIGURACIÓN — ~1 min]**

*[Abrir `pom.xml` del BFF]*

"En cuanto a las **dependencias** — el pom.xml de `bff-web` incluye `spring-boot-starter-webflux` porque usamos programación reactiva con `Mono` para las llamadas non-blocking, `spring-cloud-starter-netflix-eureka-client` para el Service Discovery, `resilience4j-reactor` para el Circuit Breaker reactivo, y `spring-boot-starter-actuator` para las métricas.

El **log interno** está configurado en el `application.properties` con `logging.level.com.valledelsol=DEBUG`, lo que nos permite ver en los logs cada petición que entra y sale del BFF con su trazabilidad completa."

---

**[PRUEBAS Y COBERTURA — ~2 min]**

*[Correr `mvn test` en un terminal o mostrar el reporte de JaCoCo en el navegador]*

"Las **pruebas** son uno de los puntos fuertes de este proyecto. Tenemos 58 tests en total.

Para `reportes-ms` tenemos:
- **Tests unitarios** en `ReporteControllerTest.java`: prueban el controlador de forma aislada, mockeando el repositorio con Mockito. Verifican cada endpoint, los códigos HTTP, y los mensajes de error.
- **Tests de integración** en `ReporteIntegrationTest.java`: levantan el contexto de Spring con H2 en memoria y verifican el flujo completo desde la petición HTTP hasta la persistencia en base de datos.
- **Tests del handler de excepciones** en `GlobalExceptionHandlerTest.java`: verifican que los errores retornan el formato JSON correcto.

Para `alertas-ms` tenemos la misma estructura más los **tests del Factory Method**, verificando que cada nivel de gravedad crea la alerta con los atributos correctos.

Para `bff-web` tenemos tests E2E en `BffE2ETest.java` que simulan el flujo completo orquestando ambos microservicios.

*[Mostrar el reporte JaCoCo o las cifras del README]*

La cobertura es: `reportes-ms` 96.4%, `alertas-ms` 95.6%, `bff-web` 97.2%, con un promedio de **~96% de cobertura de líneas** en el backend."

---

**[MONITOREO — ~1 min]**

*[Abrir `http://localhost:8062` — Spring Boot Admin]*

"Para el **monitoreo**, tenemos Spring Boot Admin corriendo en el puerto 8062. Acá podemos ver en tiempo real el estado de cada microservicio: si está UP o DOWN, el uso de memoria y CPU, el número de peticiones activas, y los logs en vivo.

¿Qué hacemos si un microservicio falla? Spring Boot Admin nos avisa visualmente, y en el futuro se puede configurar para enviar notificaciones por email o Slack. Adicionalmente, el Circuit Breaker del BFF actúa como primera línea de defensa, y los endpoints de Actuator en `/actuator/health` permiten configurar health checks en Docker Compose para reiniciar automáticamente los servicios caídos.

El seguimiento para mejoras está en los logs: todos los microservicios tienen logging estructurado que permite rastrear el ID de una petición de extremo a extremo y detectar cuellos de botella."

---

**[CIERRE PERSONA 2 — ~30 seg]**

"En resumen, los microservicios de negocio implementan correctamente los patrones Repository, Factory Method y Circuit Breaker, tienen un manejo de excepciones centralizado, métricas internas, y una cobertura de pruebas del 96%. Le paso ahora a [Nombre Persona 3] para que les muestre el frontend y la demostración funcional del sistema completo."

---
---

## 👤 PERSONA 3 — Frontend, Demostración Funcional y Conclusión
**Tiempo estimado: 7–9 minutos**
**Ítems de la rúbrica cubiertos: 43–52 (Video Arquitectura) + todos del Video de Uso (1–9)**

### Qué mostrar en pantalla:
- El frontend corriendo en `http://localhost:5173`
- Código de `valledelsol-frontend/src/` (App.jsx o componentes principales)
- `package.json` del frontend
- Postman o el propio frontend haciendo operaciones CRUD
- Los tests del frontend corriendo con `npm test`
- Docker Compose levantando todo el sistema (`docker compose up -d`)

---

### 🎙️ DIÁLOGO — PERSONA 3

---

**[INTRODUCCIÓN — ~30 seg]**

"Gracias [Nombre 2]. Soy [Nombre] y voy a mostrarles el frontend de la plataforma, los patrones que aplicamos, y al final una demostración completa del sistema funcionando de punta a punta."

---

**[FRONTEND: TECNOLOGÍA Y JUSTIFICACIÓN — ~1.5 min]**

*[Mostrar `package.json` del frontend]*

"El frontend fue desarrollado con **React 19** y **Vite 8**. Elegimos React porque es el framework más extendido para interfaces reactivas, tiene un ecosistema maduro, y nos permite separar claramente la lógica de presentación del estado de la aplicación. Vite lo elegimos como bundler porque su tiempo de hot-reload es prácticamente instantáneo durante el desarrollo, a diferencia de Webpack.

El proyecto usa **TypeScript** — pueden ver en el `package.json` las dependencias `typescript` y `@types/react`. Esto nos da tipado estático, lo que reduce significativamente los bugs en tiempo de ejecución y mejora el autocompletado en el editor.

Para las peticiones HTTP usamos **Axios**, que nos da interceptors para manejar errores globalmente y es más ergonómico que el fetch nativo."

---

**[FRONTEND: CÓDIGO Y PATRONES — ~2 min]**

*[Abrir `src/App.jsx` o el componente principal]*

"Miremos el código. El frontend implementa el patrón **Container/Presenter**: el componente `App.jsx` actúa como contenedor inteligente — gestiona todo el estado con `useState`, hace las llamadas al BFF con `useEffect`, y maneja los errores. Los componentes visuales como la tabla de reportes y la lista de alertas son presentadores puros que solo reciben datos por props.

*[Mostrar el useEffect o la llamada a Axios]*

Las **reglas de negocio del frontend** son: mostrar los reportes ordenados por fecha de creación más reciente, y las alertas deben destacarse visualmente según su nivel de gravedad: rojo para ALTO, naranja para MEDIO y amarillo para BAJO.

*[Mostrar el manejo de errores con Axios]*

La **seguridad aplicada** en el frontend es la siguiente: el JWT recibido en el login se almacena de forma segura y se adjunta automáticamente en cada petición al BFF mediante un interceptor de Axios. Si el servidor responde con 401, el interceptor redirige automáticamente al login.

*[Mostrar el manejo de timeout o el Circuit Breaker activado desde el frontend]*

Si una petición no recibe respuesta — por ejemplo, porque el Circuit Breaker del BFF está abierto — el frontend muestra un mensaje de error amigable al usuario en lugar de quedarse colgado. El timeout de Axios está configurado en 5 segundos. Los mensajes de error son concordantes con el negocio: 'El servicio de reportes no está disponible temporalmente, intente más tarde.' — no stacktraces técnicos al usuario.

**El log interno** del frontend está implementado con `console.error` estructurado en los catch de Axios, permitiendo rastrear errores desde las DevTools del navegador."

---

**[DEMOSTRACIÓN FUNCIONAL — ~3 min]**

*[Levantar el sistema completo con Docker Compose]*

"Ahora la **demostración funcional completa**. Voy a mostrarles cómo instalar, levantar y usar el sistema.

**Requisitos del sistema:** Java 17+, Maven 3.8+, Node.js 18+, Docker y Docker Compose.

**Instalación y configuración:**

```bash
# Clonar el repositorio
git clone [repositorio]
cd FullStack-lll

# Levantar todo con Docker
docker compose build --no-cache
docker compose up -d
```

*[Mostrar los contenedores levantándose con `docker compose ps`]*

En aproximadamente 30–60 segundos todos los servicios están UP. Pueden ver en el panel de Eureka que todos se han registrado correctamente.

**Cómo acceder al sistema:** abrimos `http://localhost:5173`.

*[Abrir el frontend en el navegador]*

**Descripción de la interfaz:** tenemos dos secciones principales: la sección de **Reportes**, donde los ciudadanos pueden ver y crear reportes de focos de incendio, y la sección de **Alertas**, donde se muestran las alertas emitidas por la municipalidad con su nivel de gravedad.

**Funcionalidades principales:**

*[Crear un reporte desde el formulario]*

Primero, creamos un reporte: ingresamos la latitud `-33.4569`, longitud `-70.6483`, y la descripción `Foco de incendio en sector norte`. Enviamos y aparece inmediatamente en la lista.

*[Crear una alerta desde el formulario]*

Ahora creamos una alerta de nivel ALTO: título `Riesgo extremo`, mensaje `Humo detectado en sector norte`. Vean que aparece destacada en rojo en la lista de alertas.

*[Mostrar el GET de reportes y alertas desde la lista]*

Podemos ver todos los reportes y alertas en tiempo real. El sistema también permite eliminar registros.

*[Abrir Postman y demostrar el flujo directo por API]*

Para los técnicos, también tenemos la colección Postman en `/postman/ValleDelSol.postman_collection.json` que permite probar todos los endpoints directamente."

---

**[CONCLUSIÓN Y ESCALABILIDAD — ~1 min]**

"Para cerrar el video de uso: el sistema **Valle del Sol** permite a la Municipalidad gestionar emergencias de incendio de forma eficiente, con una interfaz simple para ciudadanos y un backend robusto basado en microservicios.

**¿Cómo escala el sistema?** Si en un evento de emergencia masiva hay un pico de miles de reportes simultáneos, solo se escala el `reportes-ms` — podemos levantar 3 réplicas y el API Gateway distribuye la carga automáticamente sin tocar el resto de los servicios. El Circuit Breaker protege el sistema ante sobrecargas, y la arquitectura de microservicios permite actualizar cada componente de forma independiente sin downtime total.

A futuro, el sistema podría extenderse con notificaciones push a los ciudadanos, un mapa en tiempo real de los focos de incendio, integración con los bomberos y carabineros, y un panel de análisis estadístico de emergencias por zona.

Gracias por ver nuestra presentación. Esperamos haber cubierto todos los puntos de la rúbrica. Cualquier duda pueden revisar el README completo del proyecto."

---
---

## 📋 RESUMEN DE COBERTURA DE RÚBRICA

| Ítem rúbrica | Cubierto por | ✅ |
|---|---|---|
| 1. Explica la problemática | Persona 1 (intro) | ✅ |
| 2. Descomposición DDD / microservicios | Persona 1 (arquitectura) | ✅ |
| 3. Describe componentes y justificación | Persona 1 (arquitectura) | ✅ |
| 4. Explica componentes del diagrama | Persona 1 (arquitectura) | ✅ |
| 5–11. Service Discovery (Eureka) | Persona 1 (Eureka) | ✅ |
| 12. Dominio de microservicios | Persona 2 (reportes/alertas) | ✅ |
| 13. Reglas de negocio | Persona 2 | ✅ |
| 14. Procesamiento de datos | Persona 2 | ✅ |
| 15. Validaciones | Persona 2 | ✅ |
| 16. Casos de uso específicos | Persona 2 + Persona 3 demo | ✅ |
| 17. Estructura de carpetas | Persona 2 | ✅ |
| 18. Dependencias (pom.xml) | Persona 2 | ✅ |
| 19. Controladores | Persona 2 | ✅ |
| 20. Métodos de seguridad en MS | Persona 1 (JWT/Keycloak) | ✅ |
| 21. Patrones en el código | Persona 2 (Factory, CB, Repo) | ✅ |
| 22. Archivos de configuración | Persona 2 (properties) | ✅ |
| 23. Cómo levantar y verificar | Persona 3 (Docker) | ✅ |
| 24. Manejo de excepciones | Persona 2 (GlobalExceptionHandler) | ✅ |
| 25. Buenas prácticas (HttpStatus) | Persona 2 | ✅ |
| 26. Circuit Breaker | Persona 2 (BFF) | ✅ |
| 27. HttpStatus 201, 500, etc. | Persona 2 | ✅ |
| 28. Log interno | Persona 2 + Persona 3 | ✅ |
| 29. Métricas internas | Persona 2 (Actuator/Admin) | ✅ |
| 30. Pruebas (unit, integración, frontend) | Persona 2 | ✅ |
| 31–35. Seguridad JWT | Persona 1 (Keycloak) | ✅ |
| 36–38. API Gateway | Persona 1 | ✅ |
| 39–42. Monitoreo (Spring Boot Admin) | Persona 2 | ✅ |
| 43–52. Frontend | Persona 3 | ✅ |
| Video de Uso 1–9 | Persona 3 (demo completa) | ✅ |

---

## ⏱️ TIEMPOS ESTIMADOS

| Persona | Secciones | Tiempo estimado |
|---|---|---|
| Persona 1 | Arquitectura, Eureka, Seguridad, API Gateway | 8–10 min |
| Persona 2 | Microservicios, Patrones, Pruebas, Monitoreo | 10–12 min |
| Persona 3 | Frontend, Demo funcional, Conclusión | 7–9 min |
| **Total** | | **25–31 min** |
