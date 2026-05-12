package com.bank.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                                                                                          // Concept: Field | Why: preserves entity identity across service operations.

    private String accountNumber;                                                                             // Concept: Field | Why: stores persistent state required by this type.
    private String owner;                                                                                     // Concept: Field | Why: stores persistent state required by this type.
    private double balance;                                                                                   // Concept: Field | Why: keeps financial value needed for business calculations.

    @Enumerated(EnumType.STRING)
    private AccountType type = AccountType.CHECKING;                                                          // Concept: Field | Why: stores persistent state required by this type.

    @Enumerated(EnumType.STRING)
    private AccountStatus status = AccountStatus.ACTIVE;                                                      // Concept: Field | Why: tracks lifecycle state for rule enforcement.

    private LocalDate openedDate;                                                                             // Concept: Field | Why: stores persistent state required by this type.
    private double interestRate;                                                                              // Concept: Field | Why: stores persistent state required by this type.
    private double minimumBalance;                                                                            // Concept: Field | Why: keeps financial value needed for business calculations.
    @CreatedDate
    private LocalDateTime createdAt;                                                                          // Concept: Field | Why: stores persistent state required by this type.
    @CreatedBy
    private String createdBy;                                                                                 // Concept: Field | Why: stores persistent state required by this type.
    private LocalDateTime updatedAt;                                                                          // Concept: Field | Why: stores persistent state required by this type.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;                                                                                // Concept: Field | Why: stores persistent state required by this type.

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        updatedAt = now;                                                                                      // Concept: State Change | Why: updates runtime or domain state after checks pass.
        if (openedDate == null) {                                                                             // Concept: Validation | Why: blocks invalid input before business logic executes.
            openedDate = LocalDate.now();                                                                     // Concept: State Change | Why: updates runtime or domain state after checks pass.
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();                                                                      // Concept: State Change | Why: updates runtime or domain state after checks pass.
    }
}
