package com.bank.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {                                                             // Concept: Method | Why: encapsulates getCurrentAuditor behavior for this component boundary.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) { // Concept: Validation | Why: blocks invalid input before business logic executes.
            return Optional.of("system");
        }
        return Optional.of(authentication.getName());
    }
}
