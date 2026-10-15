package com.indra.catalog.products.application;

import com.indra.catalog.products.domain.model.Product;

public interface ProductService {

    Product create(Product product);

    Product findById(String id);

    void delete(String id);
}