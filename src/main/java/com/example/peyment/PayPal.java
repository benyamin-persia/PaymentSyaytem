package com.example.peyment;

public class PayPal implements Payment {
    private String email;                                                                                     // Concept: Field | Why: keeps contact address for notifications and verification.

    public PayPal(String email) {                                                                             // Concept: Constructor | Why: initializes required dependencies and initial state.
        this.email = email;                                                                                   // Concept: State Change | Why: updates runtime or domain state after checks pass.
    }

    @Override
    public void pay(double amount) {                                                                          // Concept: Method | Why: encapsulates pay behavior for this component boundary.
        System.out.println("Paid by PayPal account " + email + ": " + amount);
    }
}
