package com.bank.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                                                                                          // Concept: Field | Why: preserves entity identity across service operations.

    @NotBlank(message = "First name required")
    @Size(min = 1, max = 100)
    private String firstName;                                                                                 // Concept: Field | Why: stores persistent state required by this type.

    @NotBlank(message = "Last name required")
    @Size(min = 1, max = 100)
    private String lastName;                                                                                  // Concept: Field | Why: stores persistent state required by this type.

    @NotNull(message = "Email required")
    @Email(message = "Must be a valid email")
    @Column(nullable = false, unique = true)
    private String email;                                                                                     // Concept: Field | Why: keeps contact address for notifications and verification.

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;                                                                          // Concept: Field | Why: stores persistent state required by this type.

    @CreatedBy
    @Column(name = "created_by")
    private String createdBy;                                                                                 // Concept: Field | Why: stores persistent state required by this type.

    @OneToMany(mappedBy = "customer",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Account> accounts = new ArrayList<>();
}
