package com.example.peyment;

/**
 * Payment is an abstraction: it defines a contract for payment behavior.
 * Every concrete payment class implements this interface.
 * This enables polymorphism: code can use Payment without knowing the actual type.
 */
public interface Payment {
    void pay(double amount);
}
