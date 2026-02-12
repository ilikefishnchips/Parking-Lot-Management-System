package main.java.model;

import java.util.ArrayList;
import java.util.List;

public class Floor {
    private int floorNumber;
    private List<ParkingSpot> spots;

    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.spots = new ArrayList<>();
    }

    public void addSpot(ParkingSpot spot) {
        spots.add(spot);
    }

    public void removeSpot(String spotId) {
        spots.removeIf(spot -> spot.getSpotId().equals(spotId));
    }

    public List<ParkingSpot> getSpots() { return spots; }
    public int getFloorNumber() { return floorNumber; }
    public int getTotalSpots() { return spots.size(); }

    public int getOccupiedSpots() {
        int count = 0;
        for (ParkingSpot spot : spots) {
            if (spot.isOccupied()) count++;
        }
        return count;
    }

    public double getOccupancyRate() {
        if (spots.isEmpty()) return 0.0;
        return (double) getOccupiedSpots() / spots.size();
    }
}
