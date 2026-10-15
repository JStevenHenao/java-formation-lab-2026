package com.indra.catalog.products;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
        "catalog.security.editor.password=editor-smoke-password",
        "catalog.security.admin.password=admin-smoke-password"
})
@AutoConfigureMockMvc
class ProductCatalogSmokeTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void applicationWiresWebServiceAndRepository() throws Exception {
        mockMvc.perform(get("/api/products/PRD-MISSING"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PRODUCT_NOT_FOUND"));
    }
}