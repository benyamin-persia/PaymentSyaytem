package com.bank.service;

import com.bank.dto.RegistrationRequest;
import com.bank.model.AppUser;
import com.bank.model.VerificationToken;
import com.bank.repository.AppUserRepository;
import com.bank.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private static final int MAX_FAILED_ATTEMPTS = 5;                                                         // Concept: Field | Why: stores persistent state required by this type.
    private static final long LOCK_MINUTES = 30;                                                              // Concept: Field | Why: stores persistent state required by this type.

    private final AppUserRepository appUserRepository;                                                        // Concept: Field | Why: stores persistent state required by this type.
    private final VerificationTokenRepository verificationTokenRepository;                                    // Concept: Field | Why: stores persistent state required by this type.
    private final PasswordEncoder passwordEncoder;                                                            // Concept: Field | Why: stores credential data for authentication decisions.
    private final EmailService emailService;                                                                  // Concept: Field | Why: keeps contact address for notifications and verification.

    @Transactional
    public void register(RegistrationRequest request) {                                                       // Concept: Method | Why: encapsulates register behavior for this component boundary.
        if (appUserRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");                                    // Concept: Exception Handling | Why: fails fast when a contract or rule is violated.
        }
        if (appUserRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");                                       // Concept: Exception Handling | Why: fails fast when a contract or rule is violated.
        }

        AppUser appUser = new AppUser();
        appUser.setUsername(request.getUsername());                                                           // Concept: Note | Why: keep login identity separate from email address.
        appUser.setEmail(request.getEmail());                                                                 // Concept: Note | Why: save canonical email for verification + notifications.
        appUser.setPassword(passwordEncoder.encode(request.getPassword()));                                   // Concept: Note | Why: never store plain-text passwords.
        appUser.setRole("USER");                                                                              // Concept: Note | Why: default role for self-registration.
        appUser.setEnabled(false);                                                                            // Concept: Note | Why: account remains disabled until email verification completes.
        appUser.setAccountNonLocked(true);                                                                    // Concept: Note | Why: new users start unlocked.
        appUser.setFailedAttempt(0);                                                                          // Concept: Note | Why: initialize failed-attempt counter.
        appUser.setLockTime(null);                                                                            // Concept: Note | Why: no lock timestamp for a fresh account.
        AppUser savedUser = appUserRepository.save(appUser);

        VerificationToken token = new VerificationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(savedUser);
        token.setExpiryDate(LocalDateTime.now().plusHours(24));
        verificationTokenRepository.save(token);

        try {
            emailService.sendVerificationEmail(savedUser.getUsername(), savedUser.getEmail(), token.getToken());
        } catch (MailAuthenticationException ex) {
            throw new IllegalStateException("Email service authentication failed. Check Mailtrap credentials."); // Concept: Exception Handling | Why: fails fast when a contract or rule is violated.
        }
    }

    @Transactional
    public boolean verifyAccount(String tokenValue) {                                                         // Concept: Method | Why: encapsulates verifyAccount behavior for this component boundary.
        Optional<VerificationToken> tokenOptional = verificationTokenRepository.findByToken(tokenValue);
        if (tokenOptional.isEmpty()) {                                                                        // Concept: Validation | Why: blocks invalid input before business logic executes.
            return false;
        }

        VerificationToken token = tokenOptional.get();
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            verificationTokenRepository.delete(token);
            return false;
        }

        AppUser user = token.getUser();
        user.setEnabled(true);
        appUserRepository.save(user);
        verificationTokenRepository.delete(token);
        return true;
    }

    @Transactional
    public void onAuthenticationFailure(String username) {                                                    // Concept: Method | Why: encapsulates onAuthenticationFailure behavior for this component boundary.
        appUserRepository.findByUsername(username).ifPresent(user -> {
            int newFailedAttempt = user.getFailedAttempt() + 1;
            user.setFailedAttempt(newFailedAttempt);
            if (newFailedAttempt >= MAX_FAILED_ATTEMPTS) {                                                    // Concept: Validation | Why: blocks invalid input before business logic executes.
                user.setAccountNonLocked(false);
                user.setLockTime(LocalDateTime.now());
            }
            appUserRepository.save(user);
        });
    }

    @Transactional
    public void onAuthenticationSuccess(String username) {                                                    // Concept: Method | Why: encapsulates onAuthenticationSuccess behavior for this component boundary.
        appUserRepository.findByUsername(username).ifPresent(user -> {
            unlockIfLockTimeExpired(user);
            user.setFailedAttempt(0);
            appUserRepository.save(user);
        });
    }

    @Transactional
    public void unlockIfLockTimeExpired(AppUser user) {                                                       // Concept: Method | Why: encapsulates unlockIfLockTimeExpired behavior for this component boundary.
        if (!user.isAccountNonLocked() && user.getLockTime() != null) {                                       // Concept: Validation | Why: blocks invalid input before business logic executes.
            LocalDateTime unlockTime = user.getLockTime().plusMinutes(LOCK_MINUTES);
            if (LocalDateTime.now().isAfter(unlockTime)) {
                user.setAccountNonLocked(true);
                user.setFailedAttempt(0);
                user.setLockTime(null);
                appUserRepository.save(user);
            }
        }
    }

    @Transactional
    public void unlockExpiredLockedAccounts() {                                                               // Concept: Method | Why: encapsulates unlockExpiredLockedAccounts behavior for this component boundary.
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(LOCK_MINUTES);
        appUserRepository.findByAccountNonLockedFalseAndLockTimeBefore(threshold)
                .forEach(user -> {
                    user.setAccountNonLocked(true);
                    user.setFailedAttempt(0);
                    user.setLockTime(null);
                    appUserRepository.save(user);
                });
    }
}
