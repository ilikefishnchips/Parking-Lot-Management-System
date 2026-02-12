package main.java.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import main.java.data.DatabaseManager;

public class ParkingLot {
    // Singleton instance
    private static ParkingLot instance;
    
    // Parking lot properties
    private List<Floor> floors;
    private Map<String, ParkingSpot> allSpots;  // spotId -> ParkingSpot
    private Map<String, Vehicle> parkedVehicles; // licensePlate -> Vehicle
    private double totalRevenue;  // Will be updated by Member 3
    
    // Private constructor for Singleton
    private ParkingLot() {
        this.floors = new ArrayList<>();
        this.allSpots = new HashMap<>();
        this.parkedVehicles = new HashMap<>();
        this.totalRevenue = 0.0;
        initializeParkingLot();
    }
    
    // Singleton getInstance method
    public static ParkingLot getInstance() {
        if (instance == null) {
            instance = new ParkingLot();
        }
        return instance;
    }
    
    // Initialize parking lot with 5 floors
    private void initializeParkingLot() {
        // Create 5 floors
        for (int i = 1; i <= 5; i++) {
            Floor floor = new Floor(i);
            floors.add(floor);
            
            // Add all spots from this floor to the map
            for (ParkingSpot spot : floor.getSpots()) {
                allSpots.put(spot.getSpotId(), spot);
            }
        }
        
        System.out.println("Parking Lot Initialized:");
        System.out.println("- Total Floors: " + floors.size());
        System.out.println("- Total Spots: " + allSpots.size());
    }
    
    // Find available spots for a vehicle type
    public List<ParkingSpot> findAvailableSpots(String vehicleType) {
        List<ParkingSpot> availableSpots = new ArrayList<>();
        
        // Determine which spot types this vehicle can park in
        List<ParkingSpotType> allowedTypes = new ArrayList<>();
        
        switch (vehicleType.toLowerCase()) {
            case "motorcycle":
                allowedTypes.add(ParkingSpotType.COMPACT);
                break;
            case "car":
                allowedTypes.add(ParkingSpotType.COMPACT);
                allowedTypes.add(ParkingSpotType.REGULAR);
                break;
            case "suv":
            case "truck":
                allowedTypes.add(ParkingSpotType.REGULAR);
                break;
            case "handicapped":
                // Can park in any spot
                allowedTypes.add(ParkingSpotType.COMPACT);
                allowedTypes.add(ParkingSpotType.REGULAR);
                allowedTypes.add(ParkingSpotType.HANDICAPPED);
                allowedTypes.add(ParkingSpotType.RESERVED);
                break;
        }
        
        // Find available spots of allowed types
        for (ParkingSpot spot : allSpots.values()) {
            if (!spot.isOccupied() && allowedTypes.contains(spot.getType())) {
                availableSpots.add(spot);
            }
        }
        
        return availableSpots;
    }
    
    // Find available spots by specific type
    public List<ParkingSpot> findAvailableSpotsByType(ParkingSpotType type) {
        List<ParkingSpot> available = new ArrayList<>();
        for (ParkingSpot spot : allSpots.values()) {
            if (spot.getType() == type && !spot.isOccupied()) {
                available.add(spot);
            }
        }
        return available;
    }
    
    // Get spot by ID
    public ParkingSpot getParkingSpotById(String spotId) {
        return allSpots.get(spotId);
    }
    
    // Get overall occupancy rate (0.0 to 1.0)
    public double getOverallOccupancy() {
        int totalSpots = allSpots.size();
        if (totalSpots == 0) return 0.0;
        
        int occupiedCount = 0;
        for (ParkingSpot spot : allSpots.values()) {
            if (spot.isOccupied()) {
                occupiedCount++;
            }
        }
        
        return (double) occupiedCount / totalSpots;
    }
    
    // Get occupancy by floor
    public Map<Integer, Double> getOccupancyByFloor() {
        Map<Integer, Double> occupancyMap = new HashMap<>();
        for (Floor floor : floors) {
            occupancyMap.put(floor.getFloorNumber(), floor.getOccupancyRate());
        }
        return occupancyMap;
    }
    
    // Get occupancy by spot type
    public Map<ParkingSpotType, Double> getOccupancyBySpotType() {
        Map<ParkingSpotType, Integer> totalByType = new HashMap<>();
        Map<ParkingSpotType, Integer> occupiedByType = new HashMap<>();
        
        // Initialize counts
        for (ParkingSpotType type : ParkingSpotType.values()) {
            totalByType.put(type, 0);
            occupiedByType.put(type, 0);
        }
        
        // Count spots
        for (ParkingSpot spot : allSpots.values()) {
            ParkingSpotType type = spot.getType();
            totalByType.put(type, totalByType.get(type) + 1);
            if (spot.isOccupied()) {
                occupiedByType.put(type, occupiedByType.get(type) + 1);
            }
        }
        
        // Calculate rates
        Map<ParkingSpotType, Double> occupancyRates = new HashMap<>();
        for (ParkingSpotType type : ParkingSpotType.values()) {
            int total = totalByType.get(type);
            if (total > 0) {
                double rate = (double) occupiedByType.get(type) / total;
                occupancyRates.put(type, rate);
            } else {
                occupancyRates.put(type, 0.0);
            }
        }
        
        return occupancyRates;
    }
    
    // Vehicle management methods (for Member 3 integration)
    public void addParkedVehicle(String licensePlate, Vehicle vehicle, ParkingSpot spot) {
        parkedVehicles.put(licensePlate, vehicle);
        spot.assignVehicle(vehicle);
    }
    
    public Vehicle getParkedVehicle(String licensePlate) {
        return parkedVehicles.get(licensePlate);
    }
    
    public void removeParkedVehicle(String licensePlate) {
        Vehicle vehicle = parkedVehicles.remove(licensePlate);
        if (vehicle != null) {
            // Find and clear the spot
            for (ParkingSpot spot : allSpots.values()) {
                if (spot.isOccupied() && spot.getSpotId().equals(
                    vehicle.getParkingSpotId())) {
                    spot.removeVehicle();
                    break;
                }
            }
        }
    }
    
    // Revenue management (will be called by Member 3)
    public void addRevenue(double amount) {
        DatabaseManager.getInstance().addRevenue(amount, "Parking fee + fines");
    }
    
    public double getTotalRevenue() {
        return DatabaseManager.getInstance().getTotalRevenue();
    }
    
    // Getters
    public List<Floor> getFloors() { return floors; }
    public Map<String, ParkingSpot> getAllSpots() { return allSpots; }
    public int getTotalSpotsCount() { return allSpots.size(); }
    public int getOccupiedSpotsCount() { 
        return (int) (getOverallOccupancy() * allSpots.size()); 
    }
    
    // For debugging/display
    public void printParkingLotStatus() {
        System.out.println("\n=== PARKING LOT STATUS ===");
        System.out.printf("Overall Occupancy: %.1f%%\n", getOverallOccupancy() * 100);
        System.out.printf("Total Revenue: RM%.2f\n", totalRevenue);
        
        for (Floor floor : floors) {
            System.out.printf("\nFloor %d: %.1f%% occupied (%d/%d spots)\n",
                floor.getFloorNumber(),
                floor.getOccupancyRate() * 100,
                floor.getOccupiedSpots(),
                floor.getTotalSpots());
        }
    }
}