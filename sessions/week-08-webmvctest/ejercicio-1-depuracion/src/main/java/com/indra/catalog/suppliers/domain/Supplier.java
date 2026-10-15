package com.indra.catalog.suppliers.domain;

import java.util.Locale;
import java.util.Objects;

public record Supplier(String id, String name, String taxId, String email) {

    public Supplier {
        name = Objects.requireNonNull(name, "name").strip().toUpperCase(Locale.ROOT);
        taxId = Objects.requireNonNull(taxId, "taxId").strip().replace(".", "");
        email = Objects.requireNonNull(email, "email").strip();
        if (name.isBlank()) {
            throw new IllegalArgumentException("Supplier name is required");
        }
        if (!taxId.matches("[0-9]{9,11}-[0-9]")) {
            throw new IllegalArgumentException("Supplier tax id is invalid");
        }
        if (!email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            throw new IllegalArgumentException("Supplier email is invalid");
        }
        if (id != null && id.isBlank()) {
            throw new IllegalArgumentException("Supplier id cannot be blank");
        }
    }

    public static Supplier create(String name, String taxId, String email) {
        return new Supplier(null, name, taxId, email);
    }

    public Supplier withId(String id) {
        return new Supplier(id, name, taxId, email);
    }
}
