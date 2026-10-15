# Estrategia de testing — Catálogo de productos

## 1. Pirámide propuesta

| Nivel | Herramienta | Qué valida | Qué NO valida | Nº de tests | Tiempo aprox. |
|-------|-------------|------------|---------------|-------------|---------------|
| Unitario (dominio) | JUnit 5, sin Spring | Invariantes, normalización del nombre y precisión monetaria | HTTP, serialización, configuración | 4 | < 0.1 s |
| Unitario (aplicación) | JUnit 5 + Mockito, sin Spring | Generación de ID, persistencia y excepciones de búsqueda/borrado | Adaptador real, wiring, HTTP | 3 | < 0.2 s |
| Slice web | `@WebMvcTest` + `MockMvc` | Contrato HTTP, validación, `Location`, formato de errores y reglas Basic Auth | Repositorio y wiring de producción | 7 | Ver medición Surefire abajo |
| Integración | Un `@SpringBootTest` + `MockMvc` | Composición real controller → service → repositorio y handler | Persistencia externa, despliegue, carga | 1 | Ver `mvn verify` |

La suite concentra casos deterministas en los niveles rápidos; el único contexto completo es un smoke test de cableado. No se usa base de datos porque el adaptador requerido es en memoria.

## 2. Matriz de decisión

| Regla / comportamiento | Dominio | Aplicación | Slice web | Integración | Justificación |
|------------------------|:-------:|:----------:|:---------:|:-----------:|---------------|
| El precio debe ser > 0 | X | | | | Invariante independiente de cualquier transporte; se prueba en `ProductTest`. |
| El nombre vacío devuelve 400 | X | | X | | El dominio impide crear el estado inválido y el slice verifica su traducción a `VALIDATION_ERROR`. |
| Producto inexistente devuelve 404 | | X | X | X | La aplicación define el caso no encontrado; el slice y el smoke validan el mapeo y cableado HTTP. |
| Se genera un id al crear | | X | X | | La política vive en el caso de uso; el slice valida que la respuesta exponga el resultado. |
| `Location` apunta al recurso creado | | | X | | Es un contrato HTTP, no una regla del dominio. |
| `DELETE` exige rol `ADMIN` | | | X | | La autorización está en el filtro de seguridad; `httpBasic` verifica acceso y denegación por rol. |
| Las capas quedan cableadas | | | | X | El smoke usa adaptadores reales y prueba el composition root completo sin duplicar todos los bordes. |

## 3. Decisiones y trade-offs

- En `@WebMvcTest`, `@MockBean ProductService` reemplaza solo el puerto de entrada: el slice carga MVC, validación y la configuración de seguridad importada, sin instanciar el servicio ni el repositorio. En `ProductServiceImplTest`, Mockito inyecta un `@Mock ProductRepository` directamente; no arranca Spring y permite verificar política de aplicación aislada.
- Un `@SpringBootTest` por caso reconstruye y mantiene un contexto mayor: alarga la suite, requiere más infraestructura y hace más costoso repetir pruebas de controller. La comparación real para esta implementación está en las mediciones; el smoke es la única excepción deliberada.
- Spring reutiliza contextos cuando clases comparten configuración, propiedades, imports y mocks. Cambiar `@MockBean`, perfiles, propiedades, `@Import` o `@DirtiesContext` puede producir otra clave y crear un contexto nuevo. Por eso los siete casos web comparten una sola clase y configuración.
- Para cumplir el umbral local, Surefire usa `forkCount=0`: el slice reportó 1.881 s sin el costo de arrancar otro JVM. Surefire advierte que un fork mejora el aislamiento; este módulo no comparte estado global entre pruebas y no ejecuta tests en paralelo. Si aparecen tests que mutan propiedades globales, agentes o paralelismo, hay que revaluar el fork y medir en el agente CI antes de conservar este ajuste.
- Riesgos pendientes: el adaptador en memoria no prueba durabilidad ni concurrencia entre instancias; no hay contrato con clientes ni pruebas de carga; HTTP Basic exige TLS y las contraseñas deben gestionarse en el secret store del entorno. Se cubrirían con una prueba de integración del adaptador de producción, contract tests y un test de carga acordado con SLOs.

## 4. Mediciones

Las mediciones corresponden a Java 21 en este equipo. Para el slice se usa `Time elapsed` de Surefire; para `verify`, el tiempo de módulo que reporta Maven Reactor, que incluye compilación, tests y empaquetado. No son métricas intercambiables.

| Comando | Tests | Tiempo observado |
|---------|-------|-------------------------------|
| `mvn -pl ejercicio-2-construccion test -Dtest='ProductControllerTest*'` | 7 | 2.127 s (Surefire; cumple el umbral < 3 s con `forkCount=0`) |
| `mvn -pl ejercicio-2-construccion verify` | 15 | 13.813 s (módulo en Reactor; incluye smoke test y empaquetado) |
