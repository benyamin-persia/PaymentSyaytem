package com.bank.controller;

import com.bank.model.Customer;
import com.bank.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class CustomerViewController {

    private final CustomerService customerService;                                                            // Concept: Field | Why: stores persistent state required by this type.

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/customers")
    public String customers(Model model) {                                                                    // Concept: Method | Why: encapsulates customers behavior for this component boundary.
        model.addAttribute("customers", customerService.getAllCustomers());
        return "customers";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/customers/add")
    public String showAddCustomerForm(Model model) {                                                          // Concept: Method | Why: encapsulates showAddCustomerForm behavior for this component boundary.
        model.addAttribute("customer", new Customer());
        return "add-customer";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/customers/add")
    public String addCustomer(@Valid @ModelAttribute("customer") Customer customer, BindingResult bindingResult) { // Concept: Method | Why: encapsulates addCustomer behavior for this component boundary.
        if (bindingResult.hasErrors()) {
            return "add-customer";
        }
        customerService.save(customer);
        return "redirect:/customers";
    }
}
