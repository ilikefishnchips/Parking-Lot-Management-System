package main.java.model;

public class HandicappedVehicle extends Vehicle {
    public HandicappedVehicle(String plate) { super(plate); }

    @Override
    public boolean canParkIn(ParkingSpotType spotType) {
        return true; 
    }

    @Override
    public double getEffectiveHourlyRate(ParkingSpot spot) {
        if (spot.getType() == ParkingSpotType.HANDICAPPED) {
            return 0.0; // free parking in handicapped spot
        }
        return super.getEffectiveHourlyRate(spot);
    }
}
