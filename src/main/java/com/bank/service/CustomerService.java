package com.bank.service;

import com.bank.model.Customer;
import com.bank.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;                                                              // Concept: Field | Why: stores persistent state required by this type.

    public List<Customer> getAllCustomers() {                                                                 // Concept: Method | Why: encapsulates getAllCustomers behavior for this component boundary.
        return repository.findAll();
    }

    public Optional<Customer> getCustomerById(Long id) {                                                      // Concept: Method | Why: encapsulates getCustomerById behavior for this component boundary.
        return repository.findById(id);
    }

    public Customer save(Customer customer) {                                                                 // Concept: Method | Why: encapsulates save behavior for this component boundary.
        return repository.save(customer);
    }

    public void delete(Long id) {                                                                             // Concept: Method | Why: encapsulates delete behavior for this component boundary.
        repository.deleteById(id);
    }
}
