package main.java.model;

/**
 * FUTURE-PROOFING: Car vehicle class.
 * 
 * To add ELECTRIC spot support:
 * 1. Uncomment ELECTRIC in ParkingSpotType enum
 * 2. Uncomment the ELECTRIC case below
 * 3. Optionally create an ElectricCar subclass for special pricing
 */
public class Car extends Vehicle {
    public Car(String plate) { super(plate); }

    @Override
    public boolean canParkIn(ParkingSpotType spotType) {
        // FUTURE-PROOFING: Allow cars in ELECTRIC spots
        // To activate: Uncomment the ELECTRIC line below
        return spotType == ParkingSpotType.COMPACT 
            || spotType == ParkingSpotType.REGULAR;
            // || spotType == ParkingSpotType.ELECTRIC;
    }
}
