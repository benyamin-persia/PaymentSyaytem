# PaymentSystem Project Guide

## 1. What data do you have?

### Input data
- `amount` (double): the payment amount entered by the user.
- `choice` (int): user selection for payment method (Credit Card, PayPal, Cash).
- `cardNumber` (String): card details entered when using Credit Card.
- `email` (String): PayPal account email entered when using PayPal.

### Discount data
- `percentage` (double): discount percentage for `PercentageDiscount`.
- `amount` (double): fixed discount amount for `FixedDiscount`.

### Classes and objects you already have
- `Payment` interface
- `CreditCard`, `PayPal`, `Cash` classes implementing `Payment`
- `Cashier` class that performs checkout by calling `pay(...)`
- `Discount` abstract class
- `PercentageDiscount` and `FixedDiscount` classes extending `Discount`
- `Main` class as program entry point

## 2. What operations do you perform on this data?

### Payment operations
1. User enters the payment amount.
2. User chooses a payment method.
3. `createPayment(...)` builds the right `Payment` object.
4. `Cashier.checkout(payment, amount)` calls `payment.pay(amount)`.

### Discount operations
1. Create a `PercentageDiscount` or `FixedDiscount` object.
2. Call `discount.apply(amount)` to compute the discounted price.
3. Use the returned value for display or for charging later.

## 3. What is the expected output?

For a sample flow with amount `200`: 
- Original price: `200.0`
- After `PercentageDiscount(10)`: `180.0`
- After `FixedDiscount(30)`: `170.0`

Then the payment output depends on the payment type:
- Cash: `Paid by cash: <amount>`
- Credit Card: `Paid by credit card <cardNumber>: <amount>`
- PayPal: `Paid by PayPal account <email>: <amount>`

## 4. How to start coding, step by step

### Step 1: define the core contract
- Start with the `Payment` interface in `src/main/java/com/example/peyment/Payment.java`.
- This is the abstract contract: every payment type must implement `pay(double amount)`.
- This sets the foundation for **polymorphism**.
- Example snippet from `Payment.java`:
  ```java
  public interface Payment {
      void pay(double amount);
  }
  ```

### Step 2: implement concrete payment behaviors
- Create `CreditCard`, `PayPal`, and `Cash` classes in `src/main/java/com/example/peyment/`.
- Each class implements `Payment.pay(...)` in its own way.
- This shows **inheritance** and **polymorphism**.
- Store payment-specific data privately to show **encapsulation**.
- Example snippet from `CreditCard.java`:
  ```java
  public class CreditCard implements Payment {
      private String cardNumber;
      public void pay(double amount) {
          System.out.println("Paid by credit card " + cardNumber + ": " + amount);
      }
  }
  ```

### Step 3: add a checkout coordinator
- Create `Cashier` in `src/main/java/com/example/peyment/Cashier.java` with a `checkout(Payment payment, double amount)` method.
- This class depends only on the `Payment` interface, not on specific payment types.
- This demonstrates **dependency inversion** and **loose coupling**.
- Example snippet from `Cashier.java`:
  ```java
  public void checkout(Payment payment, double amount) {
      payment.pay(amount);
  }
  ```

### Step 4: create the abstract discount type
- Add `Discount` as an abstract class in `src/main/java/com/example/peyment/Discount.java` with `apply(double price)`.
- This is **abstraction**, because it defines what a discount does without how it does it.
- Example snippet from `Discount.java`:
  ```java
  public abstract class Discount {
      public abstract double apply(double price);
  }
  ```

### Step 5: add discount implementations
- Add `PercentageDiscount` and `FixedDiscount` in `src/main/java/com/example/peyment/`.
- Each subclass overrides `apply(...)` with its own logic.
- This is **polymorphism** again: code can treat both as `Discount`.
- Use private fields to store internal discount values, showing **encapsulation**.
- Example snippet from `PercentageDiscount.java`:
  ```java
  public class PercentageDiscount extends Discount {
      private final double percentage;
      public double apply(double price) {
          return price - (price * percentage / 100.0);
      }
  }
  ```

### Step 6: connect everything in `Main`
- Read user input in `src/main/java/com/example/Main.java`.
- Demonstrate discounts first, then choose a payment method.
- Create the appropriate objects and call their methods.
- The program flow starts in `Main.main(...)` and ends when `cashier.checkout(...)` returns.
- Example snippet from `Main.java`:
  ```java
  Discount percentageDiscount = new PercentageDiscount(10);
  double afterPercentage = percentageDiscount.apply(amount);
  Payment payment = createPayment(choice, scanner);
  cashier.checkout(payment, amount);
  ```

## 5. Where does each real part of the project map to programming concepts?

### `Payment` interface (`src/main/java/com/example/peyment/Payment.java`)
- Concept: abstraction and contract definition
- Real role: defines the common behavior for all payment methods.
- Code example:
  ```java
  public interface Payment {
      void pay(double amount);
  }
  ```

### `CreditCard`, `PayPal`, `Cash` (`src/main/java/com/example/peyment/CreditCard.java`, `PayPal.java`, `Cash.java`)
- Concepts: inheritance, polymorphism, encapsulation
- Real role: each class implements a different payment behavior and stores its own data.
- Code example from `PayPal.java`:
  ```java
  public class PayPal implements Payment {
      private String email;
      public void pay(double amount) {
          System.out.println("Paid by PayPal account " + email + ": " + amount);
      }
  }
  ```

### `Cashier` class (`src/main/java/com/example/peyment/Cashier.java`)
- Concepts: dependency inversion, loose coupling
- Real role: uses the `Payment` abstraction so it works with any payment type.
- Code example:
  ```java
  public void checkout(Payment payment, double amount) {
      payment.pay(amount);
  }
  ```

### `Discount` abstract class (`src/main/java/com/example/peyment/Discount.java`)
- Concept: abstraction
- Real role: common parent for all discount logic.
- Code example:
  ```java
  public abstract class Discount {
      public abstract double apply(double price);
  }
  ```

### `PercentageDiscount`, `FixedDiscount` (`src/main/java/com/example/peyment/PercentageDiscount.java`, `FixedDiscount.java`)
- Concepts: polymorphism, encapsulation
- Real role: calculate different discounts while hiding internal calculation details.
- Code example from `FixedDiscount.java`:
  ```java
  public class FixedDiscount extends Discount {
      private final double amount;
      public double apply(double price) {
          return Math.max(0, price - amount);
      }
  }
  ```

### `Main` class (`src/main/java/com/example/Main.java`)
- Concepts: program entry point, workflow orchestration
- Real role: collects input, connects discount calculation and payment execution.
- Code example:
  ```java
  Discount percentageDiscount = new PercentageDiscount(10);
  double afterPercentage = percentageDiscount.apply(amount);
  Payment payment = createPayment(choice, scanner);
  cashier.checkout(payment, amount);
  ```

## 6. Suggested order to read and modify code

1. `Payment.java` — understand the base contract.
2. `Cash.java`, `CreditCard.java`, `PayPal.java` — see concrete payment behavior.
3. `Cashier.java` — see how the payment object is used.
4. `Discount.java` — understand general discount structure.
5. `PercentageDiscount.java`, `FixedDiscount.java` — see discount implementations.
6. `Main.java` — follow the application flow from input to output.

## 7. Example learning path

1. Start with `Payment` and `Discount` definitions.
2. Read the concrete classes to see how each type works.
3. Look at `Main` to see when each object is created.
4. Run the program and notice output changes when you choose different methods.
5. Change `Main` to apply discount before checkout, and observe how the final charged amount changes.

## 8. Practical tip
- If you want to charge the discounted amount, compute it first and pass the discounted value into `cashier.checkout(...)`.
- That change is the next logical step once you understand the existing flow.

---

This file describes the data, operations, expected outputs, and the exact project points that implement each object-oriented concept.
