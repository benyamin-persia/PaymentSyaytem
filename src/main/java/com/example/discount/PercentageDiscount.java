package com.example.discount;

public class PercentageDiscount extends Discount {
    private final double percentage;                                                                          // Concept: Field | Why: stores persistent state required by this type.

    public PercentageDiscount(double percentage) {                                                            // Concept: Constructor | Why: initializes required dependencies and initial state.
        this.percentage = percentage;                                                                         // Concept: State Change | Why: updates runtime or domain state after checks pass.
    }

    @Override
    public double apply(double price) {                                                                       // Concept: Method | Why: encapsulates apply behavior for this component boundary.
        double discountAmount = price * (percentage / 100.0);
        return price - discountAmount;
    }
}
