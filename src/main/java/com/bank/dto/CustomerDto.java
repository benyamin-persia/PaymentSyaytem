package com.bank.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Public API shape for customers: only fields we intentionally expose over JSON.
 * The JPA {@link com.bank.model.Customer} also has accounts, createdBy, etc.—those stay server-side.
 */
@Data
public class CustomerDto {

    private Long id; // Stable identifier for clients.
    private String firstName; // Safe display field.
    private String lastName; // Safe display field.
    private String email; // Safe contact field.
    private LocalDateTime createdAt; // Read-only metadata; omit if you prefer not to expose timing.
}
