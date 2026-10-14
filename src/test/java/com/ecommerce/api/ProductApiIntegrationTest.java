package com.ecommerce.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void productsEndpointReturnsOk() throws Exception {
        mockMvc.perform(get("/api/products")).andExpect(status().isOk());
    }

    @Test
    void categoriesEndpointReturnsOk() throws Exception {
        mockMvc.perform(get("/api/categories")).andExpect(status().isOk());
    }
}
