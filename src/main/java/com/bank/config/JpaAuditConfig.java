package com.bank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "securityAuditorAware")
public class JpaAuditConfig {

    @Bean
    public AuditorAware<String> securityAuditorAware() {                                                      // Concept: Method | Why: encapsulates securityAuditorAware behavior for this component boundary.
        return new SecurityAuditorAware();
    }
}
