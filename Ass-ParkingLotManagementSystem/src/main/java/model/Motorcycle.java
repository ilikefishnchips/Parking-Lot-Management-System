package main.java.model;

public class Motorcycle extends Vehicle {
    public Motorcycle(String plate) { super(plate); }

    @Override
    public boolean canParkIn(ParkingSpotType spotType) {
        return spotType == ParkingSpotType.COMPACT;
    }
}
