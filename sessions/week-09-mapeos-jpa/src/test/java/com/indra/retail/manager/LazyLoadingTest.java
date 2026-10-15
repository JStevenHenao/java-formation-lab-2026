package com.indra.retail.manager;

import com.indra.retail.entities.Customer;
import com.indra.retail.entities.Order;
import com.indra.retail.entities.OrderItem;
import com.indra.retail.entities.Product;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class LazyLoadingTest {

    @Autowired
    private EntityManagerRecordManager manager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private Long orderId;

    @BeforeEach
    void createOrderWithItems() {
        // Todo en una transacción: las entidades quedan managed y el test no
        // depende de qué cascade tenga el mapeo.
        orderId = transactionTemplate.execute(status -> {
            Customer customer = manager.create(new Customer("Carla", UUID.randomUUID() + "@indra.com"));
            Product product = manager.create(new Product("Laptop", new BigDecimal("1500.00"), 4));
            Order order = new Order(customer);
            order.addItem(new OrderItem(product, 1, product.getPrice()));
            return manager.create(order).getId();
        });
    }

    @Test
    @DisplayName("Acceder a items fuera de la transacción lanza LazyInitializationException")
    void shouldThrowLazyInitializationExceptionOutsideTransaction() {
        // findById abre y cierra su transacción: la Order sale DETACHED
        Order order = manager.findById(Order.class, orderId).orElseThrow();

        assertThrows(LazyInitializationException.class, () -> order.getItems().size());
    }

    @Test
    @DisplayName("Acceder a items dentro de una transacción activa funciona")
    void shouldLoadItemsInsideTransaction() {
        int itemCount = transactionTemplate.execute(status -> {
            Order order = manager.findById(Order.class, orderId).orElseThrow();
            return order.getItems().size();
        });

        assertEquals(1, itemCount);
    }

    // ───────────────────────── Retos para participantes ─────────────────────────

    @Test
    @Disabled("TODO(participante): habilitar tras cambiar las relaciones a LAZY")
    @DisplayName("Customer de un Order no se carga hasta que se usa (LAZY)")
    void shouldLoadCustomerLazily() {
        // TODO: con Order.customer en LAZY, acceder a order.getCustomer().getName()
        // fuera de la transacción debe lanzar LazyInitializationException,
        // y dentro de una transacción debe funcionar.
    }

    @Test
    @Disabled("TODO(participante): habilitar tras configurar orphanRemoval = true")
    @DisplayName("Quitar un ítem de la lista elimina la fila (orphanRemoval)")
    void shouldRemoveOrphanItem() {
        // TODO: dentro de una transacción, quitar un ítem de order.getItems()
        // y verificar que ya no existe en la base de datos.
    }

    @Test
    @Disabled("TODO(participante): habilitar tras hacer order_id NOT NULL")
    @DisplayName("No se puede guardar un OrderItem sin Order")
    void shouldRejectOrphanOrderItem() {
        // TODO: persistir un OrderItem sin Order debe fallar
        // (p. ej. DataIntegrityViolationException / ConstraintViolationException).
    }

    @Test
    @Disabled("TODO(participante): habilitar tras quitar cascade desde OrderItem hacia Product")
    @DisplayName("Borrar un OrderItem no borra el Product")
    void shouldNotDeleteProductWhenDeletingItem() {
        // TODO: eliminar un OrderItem y verificar que su Product sigue existiendo.
    }
}
