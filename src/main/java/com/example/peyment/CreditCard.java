package com.example.peyment;

public class CreditCard implements Payment {
    private String cardNumber;                                                                                // Concept: Field | Why: stores persistent state required by this type.

    public CreditCard(String cardNumber) {                                                                    // Concept: Constructor | Why: initializes required dependencies and initial state.
        this.cardNumber = cardNumber;                                                                         // Concept: State Change | Why: updates runtime or domain state after checks pass.
    }

    @Override
    public void pay(double amount) {                                                                          // Concept: Method | Why: encapsulates pay behavior for this component boundary.
        System.out.println("Paid by credit card " + cardNumber + ": " + amount);
    }
}
