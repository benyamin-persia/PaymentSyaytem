package com.example;

import com.example.peyment.Cash;
import com.example.peyment.Cashier;
import com.example.peyment.CreditCard;
import com.example.discount.Discount;
import com.example.discount.FixedDiscount;
import com.example.peyment.Payment;
import com.example.discount.PercentageDiscount;
import com.example.peyment.PayPal;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {                                                                  // Concept: Method | Why: encapsulates main behavior for this component boundary.
        Scanner scanner = new Scanner(System.in);
        Cashier cashier = new Cashier();

        System.out.println("=== Checkout ===");
        System.out.print("Enter amount: ");
        double amount = scanner.nextDouble();

        System.out.println("\nOriginal price: " + amount);

        Discount percentageDiscount = new PercentageDiscount(10);
        double afterPercentage = percentageDiscount.apply(amount);
        System.out.println("After PercentageDiscount (10%): " + afterPercentage);

        Discount fixedDiscount = new FixedDiscount(30);
        double afterFixed = fixedDiscount.apply(amount);
        System.out.println("After FixedDiscount (30 SAR): " + afterFixed);

        System.out.println("\nChoose payment method:");
        System.out.println("1. Credit Card");
        System.out.println("2. PayPal");
        System.out.println("3. Cash");
        System.out.print("Your choice: ");
        int choice = scanner.nextInt();

        Payment payment = createPayment(choice, scanner);

        if (payment != null) {                                                                                // Concept: Validation | Why: blocks invalid input before business logic executes.
            cashier.checkout(payment, amount);
        } else {
            System.out.println("Invalid choice!");
        }

        scanner.close();
    }

    static Payment createPayment(int choice, Scanner scanner) {
        if (choice == 1) {                                                                                    // Concept: Validation | Why: blocks invalid input before business logic executes.
            System.out.print("Enter card number: ");
            String card = scanner.next();
            return new CreditCard(card);
        } else if (choice == 2) {
            System.out.print("Enter PayPal email: ");
            String email = scanner.next();
            return new PayPal(email);
        } else if (choice == 3) {
            return new Cash();
        } else {
            return null;
        }
    }
}
