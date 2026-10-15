package com.indra.catalog.products.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void creationTrimsNameAndPreservesExactPrice() {
        Product product = Product.create(" Laptop ", new BigDecimal("3500000.00"), 10);

        assertEquals("Laptop", product.name());
        assertEquals(new BigDecimal("3500000.00"), product.price());
    }

    @Test
    void creationRejectsNonPositivePrice() {
        assertThrows(IllegalArgumentException.class,
                () -> Product.create("Laptop", BigDecimal.ZERO, 10));
    }

    @Test
    void creationRejectsBlankName() {
        assertThrows(IllegalArgumentException.class,
                () -> Product.create(" ", BigDecimal.ONE, 10));
    }

    @Test
    void creationRejectsNegativeStock() {
        assertThrows(IllegalArgumentException.class,
                () -> Product.create("Laptop", BigDecimal.ONE, -1));
    }
}