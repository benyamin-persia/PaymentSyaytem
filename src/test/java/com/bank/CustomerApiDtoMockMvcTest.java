package com.bank;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerApiDtoMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAll_returnsCustomerDtoShapeWithoutNestedAccounts() throws Exception {
        mockMvc.perform(get("/api/customers").with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].firstName").exists())
                .andExpect(jsonPath("$[0].lastName").exists())
                .andExpect(jsonPath("$[0].email").exists())
                .andExpect(jsonPath("$[0].accounts").doesNotExist()) // Proves we are not serialising the JPA relation graph.
                .andExpect(jsonPath("$[0].createdBy").doesNotExist()); // Internal audit field stays off the wire.
    }

    @Test
    void getById_returnsDto() throws Exception {
        mockMvc.perform(get("/api/customers/1").with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.accounts").doesNotExist());
    }
}
