package com.bank.controller;

import com.bank.model.Account;
import com.bank.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService service;                                                                     // Concept: Field | Why: stores persistent state required by this type.

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public List<Account> getAll(@AuthenticationPrincipal UserDetails principal) {                             // Concept: Method | Why: encapsulates getAll behavior for this component boundary.
        if (isAdmin(principal)) {
            return service.getAllAccounts();
        }
        return service.getAccountsForOwner(principal.getUsername());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<Account> getById(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal) { // Concept: Method | Why: encapsulates getById behavior for this component boundary.
        if (isAdmin(principal)) {
            return service.getAccountById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
        return service.getAccountByIdForOwner(id, principal.getUsername())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Account create(@RequestBody Account account) {                                                     // Concept: Method | Why: encapsulates create behavior for this component boundary.
        return service.save(account);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal) { // Concept: Method | Why: encapsulates delete behavior for this component boundary.
        if (isAdmin(principal) && service.getAccountById(id).isPresent()) {
            service.delete(id);
            return ResponseEntity.noContent().build();
        }
        if (!isAdmin(principal) && service.getAccountByIdForOwner(id, principal.getUsername()).isPresent()) { // Concept: Validation | Why: blocks invalid input before business logic executes.
            service.delete(id);
            return ResponseEntity.noContent().build();
        }
        if (!isAdmin(principal)) {                                                                            // Concept: Validation | Why: blocks invalid input before business logic executes.
            throw new AccessDeniedException("You cannot delete accounts you do not own.");                    // Concept: Exception Handling | Why: fails fast when a contract or rule is violated.
        }
        return ResponseEntity.notFound().build();
    }

    private boolean isAdmin(UserDetails principal) {                                                          // Concept: Method | Why: encapsulates isAdmin behavior for this component boundary.
        return principal.getAuthorities()
                .stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }
}
