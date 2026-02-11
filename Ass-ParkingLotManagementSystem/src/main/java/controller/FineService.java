package main.java.controller;

import main.java.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FineService {

    private static FineService instance;

    private FineScheme currentScheme;
    private List<Fine> allFines;

    private FineService() {
        this.currentScheme = new FixedFineScheme();
        this.allFines = new ArrayList<>();
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

        long totalHours = Duration.between(
                vehicle.getEntryTime(),
                LocalDateTime.now()
        ).toHours();

        if (totalHours > 24) {

            long overstayHours = totalHours - 24;
            double amount = currentScheme.calculateFine(overstayHours);

            Fine fine = new Fine(
                    "F-" + vehicle.getLicensePlate() + "-" + System.currentTimeMillis(),
                    vehicle.getLicensePlate(),
                    amount,
                    "Overstay",
                    LocalDateTime.now()
            );

            allFines.add(fine);
            issuedFines.add(fine);
        }

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
            if (fine.getLicensePlate().equals(licensePlate)) {
                fine.markAsPaid();
            }
        }
    }

    public List<Fine> getAllFines() {
        return allFines;
    }
}
