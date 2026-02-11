package main.java.model;

public class EWalletPayment implements PaymentStrategy {

    @Override
    public void pay(double amount) {
        System.out.println("Processing E-Wallet payment...");
        System.out.println("Amount deducted: RM " + amount);
    }

    @Override
    public String getPaymentType() {
        return "E-Wallet";
    }
}
