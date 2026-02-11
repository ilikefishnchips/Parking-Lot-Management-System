package main.java.model;

public class CardPayment implements PaymentStrategy {

    @Override
    public void pay(double amount) {
        System.out.println("Processing card payment...");
        System.out.println("Amount charged: RM " + amount);
    }

    @Override
    public String getPaymentType() {
        return "Card";
    }
}
