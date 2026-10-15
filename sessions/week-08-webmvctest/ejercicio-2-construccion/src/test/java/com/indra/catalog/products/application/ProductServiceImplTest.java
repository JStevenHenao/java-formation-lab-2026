package com.indra.catalog.products.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.indra.catalog.products.domain.exception.ProductNotFoundException;
import com.indra.catalog.products.domain.model.Product;
import com.indra.catalog.products.domain.port.ProductRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductServiceImpl service;

    @Test
    void createAssignsGeneratedIdAndPersistsProduct() {
        when(repository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product created = service.create(Product.create("Laptop", new BigDecimal("3500000.00"), 10));

        assertEquals("PRD-0001", created.id());
        verify(repository).save(created);
    }

    @Test
    void findByIdThrowsWhenProductDoesNotExist() {
        when(repository.findById("PRD-9999")).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> service.findById("PRD-9999"));
    }

    @Test
    void deleteThrowsWhenProductDoesNotExist() {
        when(repository.deleteById("PRD-9999")).thenReturn(false);

        assertThrows(ProductNotFoundException.class, () -> service.delete("PRD-9999"));
    }
}