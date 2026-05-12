package com.bank.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_tokens")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                                                                                          // Concept: Field | Why: preserves entity identity across service operations.

    @Column(nullable = false, unique = true, length = 120)
    private String token;                                                                                     // Concept: Field | Why: stores persistent state required by this type.

    @Column(nullable = false)
    private LocalDateTime expiryDate;                                                                         // Concept: Field | Why: stores persistent state required by this type.

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private AppUser user;                                                                                     // Concept: Field | Why: stores persistent state required by this type.

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;                                                                          // Concept: Field | Why: stores persistent state required by this type.

    @CreatedBy
    private String createdBy;                                                                                 // Concept: Field | Why: stores persistent state required by this type.
}
