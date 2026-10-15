package com.indra.catalog.suppliers.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.indra.catalog.suppliers.domain.Supplier;
import com.indra.catalog.suppliers.domain.SupplierRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SupplierServiceImplTest {

    @Mock
    private SupplierRepository repository;

    @InjectMocks
    private SupplierServiceImpl service;

    @Test
    void createAssignsAnIdAndPersistsSupplier() {
        when(repository.save(any(Supplier.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Supplier created = service.create(Supplier.create("ACME", "900123456-7", "compras@acme.co"));

        assertEquals("SUP-001", created.id());
        verify(repository).save(created);
    }
}