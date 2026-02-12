package main.java.controller;

import java.time.Duration;
import java.time.LocalDateTime;   // <-- NEW IMPORT
import java.util.ArrayList;
import java.util.List;
import main.java.data.DatabaseManager;
import main.java.model.*;

public class FineService {

    private static FineService instance;

    private FineScheme currentScheme;
    private List<Fine> allFines;

    private FineService() {
        this.currentScheme = new FixedFineScheme();
        // --- LOAD EXISTING FINES FROM DATABASE ---
        this.allFines = DatabaseManager.getInstance().loadAllFines();
    }

    public static FineService getInstance() {
        if (instance == null) {
            instance = new FineService();
        }
        return instance;
    }

    public void setFineScheme(FineScheme scheme) {
        this.currentScheme = scheme;
    }

    public List<Fine> checkForNewFines(Vehicle vehicle, ParkingSpot spot) {

        List<Fine> issuedFines = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime limit = vehicle.getEntryTime().plusHours(24);
        
        // --- OVERSTAY FINE ---
        if (now.isAfter(limit)) {
            long overstayMinutes = Duration.between(limit, now).toMinutes();
            long overstayHours = (overstayMinutes + 59) / 60;  // ceiling rounding
            double amount = currentScheme.calculateFine(overstayHours);

            Fine fine = new Fine(
                    "F-" + vehicle.getLicensePlate() + "-" + System.currentTimeMillis(),
                    vehicle.getLicensePlate(),
                    amount,
                    "Overstay (" + overstayHours + "h)",
                    now
            );

            allFines.add(fine);
            issuedFines.add(fine);
            // --- SAVE TO DATABASE ---
            DatabaseManager.getInstance().saveFine(fine);
        }

        // --- RESERVED SPOT MISUSE FINE ---
        if (spot.getType() == ParkingSpotType.RESERVED &&
                !(vehicle instanceof HandicappedVehicle)) {

            Fine fine = new Fine(
                    "F-" + vehicle.getLicensePlate() + "-" + System.currentTimeMillis(),
                    vehicle.getLicensePlate(),
                    100.0,
                    "Reserved Spot Misuse",
                    LocalDateTime.now()
            );

            allFines.add(fine);
            issuedFines.add(fine);
            // --- SAVE TO DATABASE ---
            DatabaseManager.getInstance().saveFine(fine);
        }

        return issuedFines;
    }

    public double getTotalUnpaid(String licensePlate) {
        double total = 0;

        for (Fine fine : allFines) {
            if (fine.getLicensePlate().equals(licensePlate) && !fine.isPaid()) {
                total += fine.getAmount();
            }
        }
        return total;
    }

    public void markFinesAsPaid(String licensePlate) {
        for (Fine fine : allFines) {
            if (fine.getLicensePlate().equals(licensePlate) && !fine.isPaid()) {
                fine.markAsPaid();
                // --- UPDATE DATABASE ---
                DatabaseManager.getInstance().updateFinePaid(fine.getFineId());
            }
        }
    }

    public List<Fine> getAllFines() {
        return allFines;
    }
}