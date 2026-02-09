package test.model;

import main.java.model.*;

public class Member1Test {
    public static void main(String[] args) {
        System.out.println("=== MEMBER 1 TEST: Parking Lot Structure ===\n");
        
        // Test 1: Parking Lot Singleton
        System.out.println("1. Testing Singleton Pattern:");
        ParkingLot lot1 = ParkingLot.getInstance();
        ParkingLot lot2 = ParkingLot.getInstance();
        System.out.println("   Are both instances the same? " + (lot1 == lot2));
        
        // Test 2: Spot ID Format
        System.out.println("\n2. Testing Spot ID Format:");
        ParkingSpot spot = new ParkingSpot(1, "A", 5, ParkingSpotType.COMPACT);
        System.out.println("   Spot ID: " + spot.getSpotId());
        System.out.println("   Expected: F1-R1-S5, Got: " + spot.getSpotId());
        
        // Test 3: Hourly Rates
        System.out.println("\n3. Testing Hourly Rates:");
        ParkingSpot compactSpot = new ParkingSpot(1, "A", 1, ParkingSpotType.COMPACT);
        ParkingSpot regularSpot = new ParkingSpot(1, "B", 1, ParkingSpotType.REGULAR);
        ParkingSpot handicappedSpot = new ParkingSpot(1, "C", 1, ParkingSpotType.HANDICAPPED);
        ParkingSpot reservedSpot = new ParkingSpot(1, "D", 1, ParkingSpotType.RESERVED);
        
        System.out.println("   Compact rate: RM" + compactSpot.getHourlyRate() + " (Expected: 2.0)");
        System.out.println("   Regular rate: RM" + regularSpot.getHourlyRate() + " (Expected: 5.0)");
        System.out.println("   Handicapped rate: RM" + handicappedSpot.getHourlyRate() + " (Expected: 2.0)");
        System.out.println("   Reserved rate: RM" + reservedSpot.getHourlyRate() + " (Expected: 10.0)");
        
        // Test 4: Occupancy Calculation
        System.out.println("\n4. Testing Occupancy Calculation:");
        System.out.println("   Initial occupancy: " + (lot1.getOverallOccupancy() * 100) + "%");
        System.out.println("   Total spots: " + lot1.getTotalSpotsCount());
        System.out.println("   Occupied spots: " + lot1.getOccupiedSpotsCount());
        
        // Test 5: Find Available Spots
        System.out.println("\n5. Testing Available Spots:");
        System.out.println("   Available for Motorcycle: " + lot1.findAvailableSpots("Motorcycle").size());
        System.out.println("   Available for Car: " + lot1.findAvailableSpots("Car").size());
        System.out.println("   Available for SUV: " + lot1.findAvailableSpots("SUV").size());
        System.out.println("   Available for Handicapped: " + lot1.findAvailableSpots("Handicapped").size());
        
        // Test 6: Spot Assignment/Release
        System.out.println("\n6. Testing Spot Assignment:");
        // Create a test vehicle
        Vehicle testCar = new Car("TEST-123");
        testCar.setParkingSpotId(compactSpot.getSpotId());
        
        System.out.println("   Before assignment - is occupied? " + compactSpot.isOccupied());
        compactSpot.assignVehicle(testCar);
        System.out.println("   After assignment - is occupied? " + compactSpot.isOccupied());
        
        System.out.println("   Before release - is occupied? " + compactSpot.isOccupied());
        compactSpot.removeVehicle();
        System.out.println("   After release - is occupied? " + compactSpot.isOccupied());
        
        // Test 7: Get Spot by ID
        System.out.println("\n7. Testing Get Spot by ID:");
        ParkingSpot foundSpot = lot1.getParkingSpotById("F1-A-1");
        if (foundSpot != null) {
            System.out.println("   Found spot: " + foundSpot.getSpotId() + " Type: " + foundSpot.getType());
        } else {
            System.out.println("   Spot not found!");
        }
        
        // Print final status
        System.out.println("\n=== FINAL PARKING LOT STATUS ===");
        lot1.printParkingLotStatus();
        
        System.out.println("\n✅ All tests completed!");
    }
}