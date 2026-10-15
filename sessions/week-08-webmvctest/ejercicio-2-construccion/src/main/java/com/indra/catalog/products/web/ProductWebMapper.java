package com.indra.catalog.products.web;

import com.indra.catalog.products.domain.model.Product;
import com.indra.catalog.products.web.dto.CreateProductRequest;
import com.indra.catalog.products.web.dto.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductWebMapper {

    public Product toDomain(CreateProductRequest request) {
        return Product.create(request.name(), request.price(), request.stock());
    }

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(product.id(), product.name(), product.price(), product.stock());
    }
}