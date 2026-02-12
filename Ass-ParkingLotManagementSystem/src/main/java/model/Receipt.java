package main.java.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Receipt {

    private String licensePlate;
    private String spotId;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private long hours;
    private double hourlyRate;
    private double parkingFee;
    private double fines;
    private double totalAmount;
    private String paymentType;
    private LocalDateTime paymentTime;

    public Receipt(String licensePlate,
                   String spotId,
                   LocalDateTime entryTime,
                   LocalDateTime exitTime,
                   long hours,
                   double hourlyRate,
                   double parkingFee,
                   double fines,
                   double totalAmount,
                   String paymentType) {
        this.licensePlate = licensePlate;
        this.spotId = spotId;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.hours = hours;
        this.hourlyRate = hourlyRate;
        this.parkingFee = parkingFee;
        this.fines = fines;
        this.totalAmount = totalAmount;
        this.paymentType = paymentType;
        this.paymentTime = LocalDateTime.now();
    }

    public void printReceipt() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println("\n============= PARKING RECEIPT =============");
        System.out.println("License Plate : " + licensePlate);
        System.out.println("Parking Spot   : " + spotId);
        System.out.println("Entry Time     : " + entryTime.format(dtf));
        System.out.println("Exit Time      : " + exitTime.format(dtf));
        System.out.println("Duration       : " + hours + " hour(s)");
        System.out.println("Hourly Rate    : RM " + String.format("%.2f", hourlyRate));
        System.out.println("Parking Fee    : RM " + String.format("%.2f", parkingFee));
        System.out.println("Fines          : RM " + String.format("%.2f", fines));
        System.out.println("-------------------------------------------");
        System.out.println("TOTAL PAID     : RM " + String.format("%.2f", totalAmount));
        System.out.println("Payment Method : " + paymentType);
        System.out.println("Payment Time   : " + paymentTime.format(dtf));
        System.out.println("===========================================\n");
    }
}