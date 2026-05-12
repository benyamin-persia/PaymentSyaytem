package com.bank;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthJwtMockMvcTest {

    @Autowired
    private MockMvc mockMvc; // Drives HTTP requests against the full security filter chain including JwtAuthenticationFilter.

    private final ObjectMapper objectMapper = new ObjectMapper(); // Standalone JSON parser so the test does not depend on an ObjectMapper Spring bean.

    @Test
    void login_withBadPassword_returns401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"wrong-password\"}"))
                .andExpect(status().isUnauthorized()); // AuthController maps BadCredentialsException to 401 without leaking details.
    }

    @Test
    void login_thenBearerJwt_reachesProtectedApi() throws Exception {
        MvcResult login = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andReturn(); // Captures response body so we can forward the token to /api/customers.

        JsonNode body = objectMapper.readTree(login.getResponse().getContentAsString()); // Parses JSON without hardcoding string offsets.
        String token = body.get("accessToken").asText(); // Compact JWS string used in Authorization header.

        assertThat(token).isNotBlank(); // Sanity check before issuing authenticated GET.

        mockMvc.perform(get("/api/customers")
                        .header("Authorization", "Bearer " + token)) // JwtAuthenticationFilter should populate SecurityContext for ROLE_ADMIN.
                .andExpect(status().isOk()); // Seeded admin is allowed to list customers under existing /api/** rules.
    }
}
