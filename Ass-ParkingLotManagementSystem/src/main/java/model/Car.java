package main.java.model;

public class Car extends Vehicle {
    public Car(String plate) { super(plate); }

    @Override
    public boolean canParkIn(ParkingSpotType spotType) {
        return spotType == ParkingSpotType.COMPACT || spotType == ParkingSpotType.REGULAR;
    }
}
