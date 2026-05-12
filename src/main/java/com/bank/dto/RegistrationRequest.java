package com.bank.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;                                                                                  // Concept: Field | Why: stores persistent state required by this type.

    @NotBlank
    @Email
    @Size(max = 100)
    private String email;                                                                                     // Concept: Field | Why: keeps contact address for notifications and verification.

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;                                                                                  // Concept: Field | Why: stores credential data for authentication decisions.
}
