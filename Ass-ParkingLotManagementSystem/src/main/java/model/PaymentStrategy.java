package main.java.model;

public interface PaymentStrategy {
    void pay(double amount);
    String getPaymentType();
}
