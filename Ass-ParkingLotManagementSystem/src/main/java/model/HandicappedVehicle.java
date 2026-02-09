package main.java.model;

public class HandicappedVehicle extends Vehicle {
    public HandicappedVehicle(String plate) { super(plate); }

    @Override
    public boolean canParkIn(ParkingSpotType spotType) {
        return true; 
    }
}
