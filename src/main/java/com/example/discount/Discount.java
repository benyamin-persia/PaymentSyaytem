package com.example.discount;

/**
 * Discount is an abstraction: it defines the discount contract.
 * Subclasses provide concrete discount behavior.
 * This allows polymorphism when code uses Discount references.
 */
public abstract class Discount {
    /**
     * Apply the discount to the given price.
     * This method is polymorphic: each subclass implements its own behavior.
     */
    public abstract double apply(double price);
}
