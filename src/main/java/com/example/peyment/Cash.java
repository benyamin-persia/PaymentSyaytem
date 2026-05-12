package com.example.peyment;

/**
 * Concrete payment type implementing Payment.
 * Inheritance: Cash implements the Payment interface.
 * Polymorphism: Cash can be used anywhere a Payment is expected.
 */
public class Cash implements Payment {

    @Override
    public void pay(double amount) {                                                                          // Concept: Method | Why: encapsulates pay behavior for this component boundary.
        System.out.println("Paid by cash: " + amount);
    }
}
