package main.java.model;

import java.time.LocalDateTime;

public class Receipt {

    private String licensePlate;
    private String spotId;
    private double hours;
    private double hourlyRate;
    private double totalAmount;
    private String paymentType;
    private LocalDateTime paymentTime;

    public Receipt(String licensePlate,
                   String spotId,
                   double hours,
                   double hourlyRate,
                   double totalAmount,
                   String paymentType) {

        this.licensePlate = licensePlate;
        this.spotId = spotId;
        this.hours = hours;
        this.hourlyRate = hourlyRate;
        this.totalAmount = totalAmount;
        this.paymentType = paymentType;
        this.paymentTime = LocalDateTime.now();
    }

    public void printReceipt() {
        System.out.println("\n====== PARKING RECEIPT ======");
        System.out.println("License Plate: " + licensePlate);
        System.out.println("Parking Spot: " + spotId);
        System.out.println("Hours Parked: " + hours);
        System.out.println("Hourly Rate: RM " + hourlyRate);
        System.out.println("Total Amount: RM " + totalAmount);
        System.out.println("Payment Method: " + paymentType);
        System.out.println("Payment Time: " + paymentTime);
        System.out.println("=============================\n");
    }
}
