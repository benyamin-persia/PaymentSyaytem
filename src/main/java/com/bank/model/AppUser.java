package com.bank.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "app_users")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                                                                                          // Concept: Field | Why: preserves entity identity across service operations.

    @Column(nullable = false, unique = true)
    private String username;                                                                                  // Concept: Field | Why: stores persistent state required by this type.

    @Column(nullable = false, unique = true)
    private String email;                                                                                     // Concept: Note | Why: stores unique registration email for verification/notifications.

    @Column(nullable = false)
    private String password;                                                                                  // Concept: Field | Why: stores credential data for authentication decisions.

    @Column(nullable = false)
    private String role;                                                                                      // Concept: Field | Why: stores persistent state required by this type.

    @Column(nullable = false)
    private boolean enabled = true;                                                                           // Concept: Field | Why: stores persistent state required by this type.

    @Column(nullable = false)
    private boolean accountNonLocked = true;                                                                  // Concept: Field | Why: stores persistent state required by this type.

    @Column(nullable = false)
    private int failedAttempt = 0;                                                                            // Concept: Field | Why: stores persistent state required by this type.

    private LocalDateTime lockTime;                                                                           // Concept: Field | Why: stores persistent state required by this type.

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;                                                                          // Concept: Field | Why: stores persistent state required by this type.

    @CreatedBy
    private String createdBy;                                                                                 // Concept: Field | Why: stores persistent state required by this type.
}
