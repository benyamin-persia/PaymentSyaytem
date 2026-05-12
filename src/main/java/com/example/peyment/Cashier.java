package com.example.peyment;

public class Cashier {

    public void checkout(Payment payment, double amount) {                                                    // Concept: Method | Why: encapsulates checkout behavior for this component boundary.
        System.out.println("Processing payment...");
        payment.pay(amount);
        System.out.println("Payment done.\n");
    }
}
