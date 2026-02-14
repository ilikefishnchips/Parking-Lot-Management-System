package main.java.controller;

import java.time.LocalDateTime;
import main.java.data.DatabaseManager;
import main.java.model.*;

public class ExitService {

    public double processExit(String licensePlate, PaymentStrategy paymentStrategy, boolean payFines) {

        ParkingLot parkingLot = ParkingLot.getInstance();
        FineService fineService = FineService.getInstance();

        Vehicle vehicle = parkingLot.getParkedVehicle(licensePlate);
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle not found: " + licensePlate);
        }

        // ✅ 1. Set exit time immediately
        LocalDateTime exitTime = LocalDateTime.now();
        vehicle.setExitTime(exitTime);

        // 2. Get parking spot
        String spotId = vehicle.getParkingSpotId();
        ParkingSpot spot = parkingLot.getParkingSpotById(spotId);
        if (spot == null) {
            throw new IllegalStateException("Parking spot not found for vehicle: " + licensePlate);
        }

        // 3. Calculate parking fee (now uses the stored exit time)
        long hours = vehicle.calculateParkingHours();
        double hourlyRate = vehicle.getEffectiveHourlyRate(spot);

        double parkingFee = hours * hourlyRate;

        // 4. Check and collect fines
        fineService.checkForNewFines(vehicle, spot);
        double totalFines = fineService.getTotalUnpaid(vehicle.getLicensePlate());

        // 5. Total amount due (only include fines if payFines is true)
        double totalAmount = parkingFee + (payFines ? totalFines : 0.0);

        // 6. Process payment
        paymentStrategy.pay(totalAmount);
        parkingLot.addRevenue(totalAmount);

        // 7. Mark fines as paid only if user chose to pay them
        if (payFines && totalFines > 0) {
            fineService.markFinesAsPaid(vehicle.getLicensePlate());
        }

        // 8. Generate and print receipt (enhanced version)
        Receipt receipt = new Receipt(
                vehicle.getLicensePlate(),
                spot.getSpotId(),
                vehicle.getEntryTime(),
                vehicle.getExitTime(),      // now set
                hours,
                hourlyRate,
                parkingFee,
                payFines ? totalFines : 0.0,
                totalAmount,
                paymentStrategy.getPaymentType()
        );
        receipt.printReceipt();

        // 9. Release parking spot
        parkingLot.removeParkedVehicle(licensePlate);
        
        // 10. Update database
        DatabaseManager.getInstance().updateVehicleExit(licensePlate, exitTime);

        return totalAmount;
    }
}