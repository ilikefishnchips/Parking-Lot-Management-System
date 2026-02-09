package main.java.model;

import java.util.ArrayList;
import java.util.List;

public class Floor {
    private int floorNumber;
    private List<ParkingSpot> spots;
    private int totalSpots;
    private int occupiedSpots;

    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.spots = new ArrayList<>();
        this.totalSpots = 0;
        this.occupiedSpots = 0;
        initializeSpots();
    }

    private void initializeSpots() {
        // Create spots for this floor
        // Example: 20 spots per floor with different types
        
        // Row A: 5 Compact spots
        for (int i = 1; i <= 5; i++) {
            spots.add(new ParkingSpot(floorNumber, "A", i, ParkingSpotType.COMPACT));
        }
        
        // Row B: 10 Regular spots
        for (int i = 1; i <= 10; i++) {
            spots.add(new ParkingSpot(floorNumber, "B", i, ParkingSpotType.REGULAR));
        }
        
        // Row C: 3 Handicapped spots
        for (int i = 1; i <= 3; i++) {
            spots.add(new ParkingSpot(floorNumber, "C", i, ParkingSpotType.HANDICAPPED));
        }
        
        // Row D: 2 Reserved spots
        for (int i = 1; i <= 2; i++) {
            spots.add(new ParkingSpot(floorNumber, "D", i, ParkingSpotType.RESERVED));
        }
        
        totalSpots = spots.size();
    }

    // Get all available spots of a specific type
    public List<ParkingSpot> getAvailableSpots(ParkingSpotType type) {
        List<ParkingSpot> available = new ArrayList<>();
        for (ParkingSpot spot : spots) {
            if (spot.getType() == type && !spot.isOccupied()) {
                available.add(spot);
            }
        }
        return available;
    }

    // Get all available spots (any type)
    public List<ParkingSpot> getAllAvailableSpots() {
        List<ParkingSpot> available = new ArrayList<>();
        for (ParkingSpot spot : spots) {
            if (!spot.isOccupied()) {
                available.add(spot);
            }
        }
        return available;
    }

    // Get occupancy rate (0.0 to 1.0)
    public double getOccupancyRate() {
        if (totalSpots == 0) return 0.0;
        return (double) occupiedSpots / totalSpots;
    }

    // Get number of occupied spots
    public int getOccupiedSpots() {
        int count = 0;
        for (ParkingSpot spot : spots) {
            if (spot.isOccupied()) {
                count++;
            }
        }
        occupiedSpots = count;
        return count;
    }

    // Get total spots
    public int getTotalSpots() {
        return totalSpots;
    }

    // Get spots by type
    public List<ParkingSpot> getSpotsByType(ParkingSpotType type) {
        List<ParkingSpot> result = new ArrayList<>();
        for (ParkingSpot spot : spots) {
            if (spot.getType() == type) {
                result.add(spot);
            }
        }
        return result;
    }

    // Find spot by spotId
    public ParkingSpot getSpotById(String spotId) {
        for (ParkingSpot spot : spots) {
            if (spot.getSpotId().equals(spotId)) {
                return spot;
            }
        }
        return null;
    }

    // Getters
    public int getFloorNumber() { return floorNumber; }
    public List<ParkingSpot> getSpots() { return spots; }
}