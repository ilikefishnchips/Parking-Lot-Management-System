package main.java.model;

public class CashPayment implements PaymentStrategy {

    @Override
    public void pay(double amount) {
        System.out.println("Processing cash payment...");
        System.out.println("Amount paid: RM " + amount);
    }

    @Override
    public String getPaymentType() {
        return "Cash";
    }
}

