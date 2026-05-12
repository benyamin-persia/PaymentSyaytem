package com.bank.service;

import com.bank.model.Account;
import com.bank.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository repository;                                                               // Concept: Field | Why: stores persistent state required by this type.

    public List<Account> getAllAccounts() {                                                                   // Concept: Method | Why: encapsulates getAllAccounts behavior for this component boundary.
        return repository.findAll();
    }

    public List<Account> getAccountsForOwner(String ownerUsername) {                                          // Concept: Method | Why: encapsulates getAccountsForOwner behavior for this component boundary.
        return repository.findByOwnerIgnoreCase(ownerUsername);
    }

    public Optional<Account> getAccountById(Long id) {                                                        // Concept: Method | Why: encapsulates getAccountById behavior for this component boundary.
        return repository.findById(id);
    }

    public Optional<Account> getAccountByIdForOwner(Long id, String ownerUsername) {                          // Concept: Method | Why: encapsulates getAccountByIdForOwner behavior for this component boundary.
        return repository.findByIdAndOwnerIgnoreCase(id, ownerUsername);
    }

    public Account save(Account account) {                                                                    // Concept: Method | Why: encapsulates save behavior for this component boundary.
        return repository.save(account);
    }

    public void delete(Long id) {                                                                             // Concept: Method | Why: encapsulates delete behavior for this component boundary.
        repository.deleteById(id);
    }
}
