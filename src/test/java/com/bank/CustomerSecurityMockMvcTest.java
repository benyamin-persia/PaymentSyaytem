package com.bank;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerSecurityMockMvcTest {

    @Autowired
    private MockMvc mockMvc;                                                                                  // Concept: Field | Why: stores persistent state required by this type.

    @Test
    void customersEndpoint_allowsUserRole() throws Exception {
        mockMvc.perform(get("/customers").with(user("user").roles("USER")))
                .andExpect(status().isOk());
    }

    @Test
    void addCustomerForm_deniesUserRole() throws Exception {
        mockMvc.perform(get("/customers/add").with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void addCustomerForm_allowsAdminRole() throws Exception {
        mockMvc.perform(get("/customers/add").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void addCustomerSubmit_deniesUserRole() throws Exception {
        mockMvc.perform(post("/customers/add")
                        .with(user("user").roles("USER"))
                        .with(csrf())
                        .param("firstName", "Test")
                        .param("lastName", "User")
                        .param("email", "test.user@example.com"))
                .andExpect(status().isForbidden());
    }

    @Test
    void customersEndpoint_redirectsUnauthenticatedUser() throws Exception {
        mockMvc.perform(get("/customers"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
