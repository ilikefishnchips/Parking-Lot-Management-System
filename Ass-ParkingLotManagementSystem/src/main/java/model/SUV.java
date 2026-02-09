package main.java.model;

public class SUV extends Vehicle {
    public SUV(String plate) { super(plate); }

    @Override
    public boolean canParkIn(ParkingSpotType spotType) {
        return spotType == ParkingSpotType.REGULAR;
    }
}
