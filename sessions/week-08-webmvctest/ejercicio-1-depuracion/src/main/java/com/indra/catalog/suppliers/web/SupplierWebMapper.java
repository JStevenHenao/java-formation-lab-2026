package com.indra.catalog.suppliers.web;

import com.indra.catalog.suppliers.domain.Supplier;
import com.indra.catalog.suppliers.web.dto.CreateSupplierRequest;
import com.indra.catalog.suppliers.web.dto.SupplierResponse;
import org.springframework.stereotype.Component;

@Component
public class SupplierWebMapper {

    public Supplier toDomain(CreateSupplierRequest request) {
        return Supplier.create(request.name(), request.taxId(), request.email());
    }

    public SupplierResponse toResponse(Supplier supplier) {
        return new SupplierResponse(supplier.id(), supplier.name(), supplier.taxId(), supplier.email());
    }
}
