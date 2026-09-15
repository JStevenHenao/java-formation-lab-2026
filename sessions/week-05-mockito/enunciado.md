# Enunciado — Week 05: Mockito (Buenas Prácticas)

## Contexto del reto

Completar los casos de prueba utilizando Mockito para la clase InventoryService.


## Lo que debes implementar

1. Escribe tests unitarios para `InventoryService` usando `@ExtendWith(MockitoExtension.class)`.
2. Mockea `ICatalogRepository`, `InventoryService`.
3. Cubre los escenarios:
   - Consulta de productos exitoso. 
   - Creacion de productos exitoso. 
   - Actualización de productos exitoso. 
4. Utiliza los recursos de la libreria Mockito. 

## Restricciones técnicas (para todos)

- Usar `@Mock` y `@InjectMocks`, no `Mockito.mock()` manualmente.
- No usar `@SpringBootTest` — estos deben ser tests unitarios puros.
- No verificar con `verify()` interacciones que ya están implícitas en el resultado del test.
- **Criterio no funcional (calidad)**: cada test debe poder leerse como una especificación — el nombre del método describe el escenario y el resultado esperado.

## Criterio de aceptación del PR

- [ ] 3 escenarios cubiertos con tests independientes
- [ ] `ArgumentCaptor` usado correctamente en el escenario de éxito
- [ ] Sin `@SpringBootTest` ni contexto completo
- [ ] Sin `verify()` redundantes (que no agregan valor al test)
- [ ] `mvn verify` en verde

## Bonus (opcional)

- Agregar un test con `@Spy` para verificar que `PaymentService` llama a un método auxiliar propio.
- Usar `BDDMockito` (`given/when/then`) en lugar de `when/thenReturn` para mayor legibilidad.
