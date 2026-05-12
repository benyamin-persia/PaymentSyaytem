package com.bank.controller;

import com.bank.dto.CustomerCreateRequest;
import com.bank.dto.CustomerDto;
import com.bank.model.Customer;
import com.bank.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @GetMapping
    public List<CustomerDto> getAll() {
        return service.getAllCustomers().stream().map(CustomerController::toDto).toList(); // Maps each entity to a safe DTO so Jackson never serialises accounts/createdBy.
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> getById(@PathVariable Long id) {
        return service.getCustomerById(id).map(CustomerController::toDto).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build()); // 404 when missing; body is DTO not entity.
    }

    @PostMapping
    public CustomerDto create(@Valid @RequestBody CustomerCreateRequest request) {
        Customer customer = new Customer(); // Fresh entity: only fields from request are set—clients cannot inject audit or relation fields via JSON.
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        Customer saved = service.save(customer);
        return toDto(saved); // Response mirrors GET: same DTO contract, no accidental extra columns.
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (service.getCustomerById(id).isPresent()) {
            service.delete(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private static CustomerDto toDto(Customer c) {
        CustomerDto dto = new CustomerDto(); // Explicit mapping keeps API stable if entity gains new fields later.
        dto.setId(c.getId());
        dto.setFirstName(c.getFirstName());
        dto.setLastName(c.getLastName());
        dto.setEmail(c.getEmail());
        dto.setCreatedAt(c.getCreatedAt()); // Optional in API; omit setter line if you want createdAt hidden too.
        return dto;
    }
}
