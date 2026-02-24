package main.java.model;

/**
 * FUTURE-PROOFING: Represents a Bus vehicle.
 * 
 * This class demonstrates the Open/Closed Principle - we can add new vehicle types
 * without modifying existing code. Buses can only park in REGULAR spots.
 * 
 * To activate this feature:
 * 1. Uncomment this class
 * 2. Add "bus" case to VehicleFactory.createVehicle()
 * 3. Add "Bus" to EntryPanel's vehicle type combo box
 * 4. Add bus handling in ParkingLot.findAvailableSpots()
 * 
 * @author Future-Proof Implementation
 */
public class Bus extends Vehicle {

    public Bus(String plate) {
        super(plate);
    }

    /**
     * Buses are large vehicles; they can only park in REGULAR spots.
     * If we later add a LARGE spot type, we can update this method.
     * 
     * @param spotType The type of parking spot to check
     * @return true if buses can park in REGULAR spots
     */
    @Override
    public boolean canParkIn(ParkingSpotType spotType) {
        // Buses are large; they can only park in REGULAR spots.
        // If we later add a LARGE spot type, we can update this method.
        return spotType == ParkingSpotType.REGULAR;
    }

    // Optionally override getEffectiveHourlyRate if buses get a discount or surcharge.
    // Example: Buses might pay a premium rate
    // @Override
    // public double getEffectiveHourlyRate(ParkingSpot spot) {
    //     // Example: Buses pay double? But we'll keep default.
    //     return spot.getHourlyRate();
    // }
}
