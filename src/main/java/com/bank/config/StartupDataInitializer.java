package com.bank.config;

import com.bank.model.Account;
import com.bank.model.AccountStatus;
import com.bank.model.AccountType;
import com.bank.model.AppUser;
import com.bank.model.Customer;
import com.bank.repository.AccountRepository;
import com.bank.repository.AppUserRepository;
import com.bank.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class StartupDataInitializer implements CommandLineRunner {

    private final AppUserRepository appUserRepository;                                                        // Concept: Field | Why: stores persistent state required by this type.
    private final CustomerRepository customerRepository;                                                      // Concept: Field | Why: stores persistent state required by this type.
    private final AccountRepository accountRepository;                                                        // Concept: Field | Why: stores persistent state required by this type.
    private final PasswordEncoder passwordEncoder;                                                            // Concept: Field | Why: stores credential data for authentication decisions.

    @Override
    @Transactional
    public void run(String... args) {                                                                         // Concept: Method | Why: encapsulates run behavior for this component boundary.
        upsertUser("admin", "admin@bank.local", "admin123", "ADMIN", true);
        upsertUser("user", "user@bank.local", "user123", "USER", true);
        upsertUser("benyamin", "benyamin@bank.local", "benyamin123", "USER", true);

        Customer customerAlice = upsertCustomer("Alice", "Smith", "alice@example.com");
        Customer customerSara = upsertCustomer("Sara", "Ahmed", "sara@bank.com");
        upsertCustomer("Khalid", "Mahmoud", "khalid@bank.com");

        upsertAccount("ACC-1001", "user", 1200.50, AccountType.SAVINGS, AccountStatus.ACTIVE, 1.50, 100.00, customerAlice);
        upsertAccount("ACC-1002", "benyamin", 530.75, AccountType.CHECKING, AccountStatus.ACTIVE, 0.00, 0.00, customerAlice);
        upsertAccount("ACC-2001", "admin", 10000.00, AccountType.BUSINESS, AccountStatus.ACTIVE, 0.00, 500.00, customerSara);
    }

    private void upsertUser(String username, String email, String rawPassword, String role, boolean enabled) { // Concept: Method | Why: encapsulates upsertUser behavior for this component boundary.
        AppUser user = appUserRepository.findByUsername(username).orElseGet(AppUser::new);
        user.setUsername(username);                                                                           // Concept: Note | Why: stabilizes seed identity if DB row was manually altered.
        user.setEmail(email);                                                                                 // Concept: Note | Why: ensures non-null/unique email requirement is always satisfied.
        user.setPassword(passwordEncoder.encode(rawPassword));                                                // Concept: Note | Why: re-hashes canonical password so known credentials always work.
        user.setRole(role);                                                                                   // Concept: Note | Why: guarantees role mapping used by hasRole(...) stays correct.
        user.setEnabled(enabled);                                                                             // Concept: Note | Why: keeps seeded users login-enabled.
        user.setAccountNonLocked(true);                                                                       // Concept: Note | Why: resets lock state to avoid permanent lockouts after failed attempts.
        user.setFailedAttempt(0);                                                                             // Concept: Note | Why: clears stale failed login counters.
        user.setLockTime(null);                                                                               // Concept: Note | Why: removes stale lock timestamp.
        appUserRepository.save(user);
    }

    private Customer upsertCustomer(String firstName, String lastName, String email) {                        // Concept: Method | Why: encapsulates upsertCustomer behavior for this component boundary.
        Customer customer = customerRepository.findByEmailIgnoreCase(email).orElseGet(Customer::new);
        customer.setFirstName(firstName);                                                                     // Concept: Note | Why: keeps display data consistent with sample dataset.
        customer.setLastName(lastName);                                                                       // Concept: Note | Why: keeps display data consistent with sample dataset.
        customer.setEmail(email);                                                                             // Concept: Note | Why: uses email as stable lookup key for idempotent customer seed updates.
        return customerRepository.save(customer);
    }

    private void upsertAccount(String accountNumber, String owner, double balance, AccountType type, AccountStatus status,
                               double interestRate, double minimumBalance, Customer customer) {
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseGet(Account::new);
        account.setAccountNumber(accountNumber);                                                              // Concept: Note | Why: uses account number as stable key for idempotent account seed updates.
        account.setOwner(owner);                                                                              // Concept: Note | Why: aligns account ownership with currently seeded usernames.
        account.setBalance(balance);                                                                          // Concept: Note | Why: ensures deterministic starter balances in demos/tests.
        account.setType(type);                                                                                // Concept: Note | Why: ensures enum values remain valid after model changes.
        account.setStatus(status);                                                                            // Concept: Note | Why: ensures enum values remain valid after model changes.
        account.setInterestRate(interestRate);                                                                // Concept: Note | Why: satisfies required non-null numeric column introduced in schema.
        account.setMinimumBalance(minimumBalance);                                                            // Concept: Note | Why: satisfies required non-null numeric column introduced in schema.
        account.setCustomer(customer);                                                                        // Concept: Note | Why: keeps FK relation valid and resilient to regenerated IDs.
        accountRepository.save(account);
    }
}
