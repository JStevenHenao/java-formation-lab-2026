package com.indra.catalog.products.infrastructure;

import com.indra.catalog.products.application.ProductService;
import com.indra.catalog.products.application.ProductServiceImpl;
import com.indra.catalog.products.domain.port.ProductRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductModuleConfiguration {

    @Bean
    ProductService productService(ProductRepository repository) {
        return new ProductServiceImpl(repository);
    }
}