package main.java.controller;

import main.java.model.*;

public class ExitService {

    // Process vehicle exit
    public double processExit(String licensePlate, PaymentStrategy paymentStrategy) {

        ParkingLot parkingLot = ParkingLot.getInstance();
        FineService fineService = FineService.getInstance(); 

        Vehicle vehicle = parkingLot.getParkedVehicle(licensePlate);

        if (vehicle == null) {
            System.out.println("Vehicle not found.");
            return 0.0;
        }

        // get parking spot
        String spotId = vehicle.getParkingSpotId();
        ParkingSpot spot = parkingLot.getParkingSpotById(spotId);

        if (spot == null) {
            System.out.println("Parking spot not found.");
            return 0.0;
        }

        // --------------------------
        // calculate parking fee
        // --------------------------
        double hours = vehicle.calculateParkingHours();
        double hourlyRate = spot.getHourlyRate();
        double parkingFee = hours * hourlyRate;

        // Check new fines (overstay / reserved misuse)
        fineService.checkForNewFines(vehicle, spot);

        // Get total unpaid fines (including previous + new)
        double totalFines = fineService.getTotalUnpaid(vehicle.getLicensePlate());
        
        double totalAmount = parkingFee + totalFines;

        // --------------------------
        // process payment
        // --------------------------
        paymentStrategy.pay(totalAmount);

        // record revenue (include fines too)
        parkingLot.addRevenue(totalAmount);

        // mark fines as paid after successful payment
        fineService.markFinesAsPaid(vehicle.getLicensePlate());

        // --------------------------
        // print receipt
        // --------------------------
        Receipt receipt = new Receipt(
                vehicle.getLicensePlate(),
                spot.getSpotId(),
                hours,
                hourlyRate,
                totalAmount,
                paymentStrategy.getPaymentType()
        );

        receipt.printReceipt();

        // --------------------------
        // release parking spot
        // --------------------------
        parkingLot.removeParkedVehicle(licensePlate);

        return totalAmount;
    }
}
