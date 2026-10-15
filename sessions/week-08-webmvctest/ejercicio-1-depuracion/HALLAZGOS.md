# Hallazgos — Ejercicio 1: proveedores

## Fallo 1 — El slice no carga el mapper
- **Síntoma**: los cinco tests terminaban en error al cargar `ApplicationContext`; no se ejecutaba ninguna petición. Spring reportaba que faltaba `SupplierWebMapper`.
- **Causa raíz**: `@WebMvcTest(SupplierController.class)` crea un slice MVC y excluye componentes de aplicación arbitrarios. `@Component` por sí solo no hace que el mapper forme parte de ese slice.
- **Corrección**: importar explícitamente `SupplierWebMapper` en el test. El mapper sigue disponible como bean en producción; el slice no depende de un escaneo general de componentes.
- **Prevención**: importar dependencias web deliberadas o probarlas sin Spring cuando sean transformaciones puras; no ensanchar el test con `@SpringBootTest`.

## Fallo 2 — `Location` concatenaba el id sin separador
- **Síntoma**: el test de creación esperaba `/api/suppliers/SUP-001`, pero recibía `/api/suppliersSUP-001`.
- **Causa raíz**: `ServletUriComponentsBuilder.path("{id}")` no inserta automáticamente una barra entre segmentos.
- **Corrección**: construir el URI con `path("/{id}")` y conservar la aserción exacta del header.
- **Prevención**: validar `Location` en el slice como URI completa, no solo comprobar que el header exista.

## Fallo 3 — La validación declarada nunca se ejecutaba
- **Síntoma**: enviar un nombre vacío no producía `400 VALIDATION_ERROR`.
- **Causa raíz**: las restricciones de `CreateSupplierRequest` solo se evalúan cuando el argumento del controller está anotado con `@Valid`.
- **Corrección**: añadir `@Valid` al request y mantener las restricciones junto al DTO de entrada. El slice verifica el error y que el servicio no se invoque.
- **Prevención**: cubrir entradas inválidas en el contrato MVC y afirmar que no alcanzan el puerto de aplicación.

## Fallo 4 — DELETE violaba el contrato HTTP
- **Síntoma**: DELETE devolvía `200 OK` en lugar de `204 No Content`.
- **Causa raíz**: el controller utilizaba `ResponseEntity.ok()` después de borrar.
- **Corrección**: responder `ResponseEntity.noContent()`; la prueba conserva el status esperado y verifica delegación al servicio.
- **Prevención**: fijar status y headers del contrato HTTP en tests de slice con `andExpect`.

## Refactors aplicados

| Principio / práctica | Antes | Después | Test que lo respalda |
|----------------------|-------|---------|----------------------|
| DIP | Controller ligado a `SupplierServiceImpl`; servicio construía repositorio | Controller usa `SupplierService`; servicio recibe `SupplierRepository` | `SupplierControllerTest`, `SupplierServiceImplTest` |
| SRP | Controller normalizaba nombre/NIT y agregaba una nota temporal | Dominio inmutable normaliza y protege invariantes | `SupplierTest` |
| Seguridad de errores | Respuesta 500 exponía `exception.toString()` | Detalle solo en log; cliente recibe mensaje genérico | `unexpectedFailuresDoNotExposeInternalDetails` |
| Slice MVC | Mapper dependía de escaneo de componentes | Mapper puro importado deliberadamente en el slice | `SupplierControllerTest` |
| Robustez de tests | Nombres genéricos y comparación de JSON como `String` | Nombres descriptivos, `jsonPath`, Content-Type y verificación de interacción | `SupplierControllerTest` |

La actividad pide que los cuatro fallos se separen en commits `fix:`. No se crearon commits durante esta implementación; los cuatro hallazgos están documentados para poder separarlos en el historial del PR sin mezclarlo con los refactors posteriores.