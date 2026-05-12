package com.bank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountUnlockScheduler {

    private final RegistrationService registrationService;                                                    // Concept: Field | Why: stores persistent state required by this type.

    @Scheduled(fixedDelay = 60000)
    public void unlockExpiredAccounts() {                                                                     // Concept: Method | Why: encapsulates unlockExpiredAccounts behavior for this component boundary.
        registrationService.unlockExpiredLockedAccounts();
    }
}
