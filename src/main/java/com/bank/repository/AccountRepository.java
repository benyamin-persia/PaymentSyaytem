package com.bank.repository;

import com.bank.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByOwnerIgnoreCase(String owner);
    Optional<Account> findByIdAndOwnerIgnoreCase(Long id, String owner);
    Optional<Account> findByAccountNumber(String accountNumber);
}
