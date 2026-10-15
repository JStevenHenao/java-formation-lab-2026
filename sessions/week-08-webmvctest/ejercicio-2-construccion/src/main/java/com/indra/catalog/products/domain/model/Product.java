package com.indra.catalog.products.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public record Product(String id, String name, BigDecimal price, int stock) {

    public Product {
        name = Objects.requireNonNull(name, "name").strip();
        price = Objects.requireNonNull(price, "price");
        if (name.isBlank() || name.length() > 120) {
            throw new IllegalArgumentException("Product name must contain 1 to 120 characters");
        }
        if (price.signum() <= 0) {
            throw new IllegalArgumentException("Product price must be greater than zero");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Product stock cannot be negative");
        }
        if (id != null && id.isBlank()) {
            throw new IllegalArgumentException("Product id cannot be blank");
        }
    }

    public static Product create(String name, BigDecimal price, int stock) {
        return new Product(null, name, price, stock);
    }

    public Product withId(String id) {
        return new Product(id, name, price, stock);
    }
}