package com.bank.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Body for POST /api/customers: only fields a client may set (no id, no audit fields).
 */
@Data
public class CustomerCreateRequest {

    @NotBlank
    @Size(max = 100)
    private String firstName; // Required name part for new customer.

    @NotBlank
    @Size(max = 100)
    private String lastName; // Required name part for new customer.

    @NotBlank
    @Email
    @Size(max = 255)
    private String email; // Unique email used for customer record.
}
