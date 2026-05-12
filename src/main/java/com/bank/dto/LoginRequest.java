package com.bank.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank
    private String username; // Maps to Spring Security username (same field as form login).

    @NotBlank
    private String password; // Checked against BCrypt hash via AuthenticationManager.
}
