package Member2_assignment;

public class Member2Test {
    public static void main(String[] args) {
        System.out.println("--- Testing Member 2 Logic ---");

        EntryService entryService = new EntryService();

        // Scenario 1: Valid Parking
        try {
            ParkingSpot spot1 = new ParkingSpot("L1-C1", ParkingSpotType.COMPACT);
            Ticket t1 = entryService.processEntry("MOTO-1", "Motorcycle", spot1);
            System.out.println("1. Success: " + t1.toString());
        } catch (Exception e) {
            System.out.println("1. Failed: " + e.getMessage());
        }

        // Scenario 2: Invalid Type (SUV attempting to park in a NEW empty Compact spot)
        try {
            ParkingSpot spot2 = new ParkingSpot("L1-C2", ParkingSpotType.COMPACT); // Empty spot
            System.out.println("\nAttempting to park SUV in Compact spot...");
            entryService.processEntry("SUV-99", "SUV", spot2);
        } catch (Exception e) {
            // WE WANT TO SEE THIS ERROR
            System.out.println("2. Success (Caught Expected Error): " + e.getMessage());
        }
    }
}