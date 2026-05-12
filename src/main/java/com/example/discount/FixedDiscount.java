package com.example.discount;

public class FixedDiscount extends Discount {
    private final double amount;                                                                              // Concept: Field | Why: keeps financial value needed for business calculations.

    public FixedDiscount(double amount) {
        this.amount = amount;                                                                                 // Concept: State Change | Why: updates runtime or domain state after checks pass.
    }

    @Override
    public double apply(double price) {                                                                       // Concept: Method | Why: encapsulates apply behavior for this component boundary.
        return Math.max(0, price - amount);
    }
}
