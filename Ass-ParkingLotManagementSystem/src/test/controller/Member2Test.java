package test.controller;

import main.java.controller.EntryService;
import main.java.model.ParkingSpot;
import main.java.model.ParkingSpotType;
import main.java.model.Ticket;

public class Member2Test {
    public static void main(String[] args) {
        System.out.println("--- Testing Member 2 Logic ---");

        EntryService entryService = new EntryService();

        // Scenario 1: Valid Parking
        try {
            // Use correct constructor: (floorNumber, row, spotNumber, type)
            ParkingSpot spot1 = new ParkingSpot(1, "A", 1, ParkingSpotType.COMPACT);
            Ticket t1 = entryService.processEntry("MOTO-1", "Motorcycle", spot1);
            System.out.println("1. Success: " + t1.toString());
        } catch (Exception e) {
            System.out.println("1. Failed: " + e.getMessage());
        }

        // Scenario 2: Invalid Type (SUV attempting to park in a Compact spot)
        try {
            // Create a new compact spot for this test
            ParkingSpot spot2 = new ParkingSpot(1, "A", 2, ParkingSpotType.COMPACT); // Compact spot
            System.out.println("\nAttempting to park SUV in Compact spot...");
            entryService.processEntry("SUV-99", "SUV", spot2);
        } catch (Exception e) {
            // WE WANT TO SEE THIS ERROR
            System.out.println("2. Success (Caught Expected Error): " + e.getMessage());
        }
    }
}
