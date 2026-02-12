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
    private Map<String, ParkingSpot> allSpots;
    private Map<String, Vehicle> parkedVehicles;
    
    // Private constructor for Singleton
    private ParkingLot() {
        this.floors = new ArrayList<>();
        this.allSpots = new HashMap<>();
        this.parkedVehicles = new HashMap<>();
        initializeParkingLot();
    }
    
    // Singleton getInstance method
    public static ParkingLot getInstance() {
        if (instance == null) {
            instance = new ParkingLot();
        }
        return instance;
    }
    
    // Initialize parking lot from database
    private void initializeParkingLot() {
        // Load floors from DB
        List<Integer> floorNumbers = DatabaseManager.getInstance().getAllFloors();
        if (floorNumbers.isEmpty()) {
            // First run: create default 5 floors
            for (int i = 1; i <= 5; i++) {
                DatabaseManager.getInstance().saveFloor(i);
                floorNumbers.add(i);
            }
        }
        
        // Create Floor objects and load spots
        for (int fn : floorNumbers) {
            Floor floor = new Floor(fn);
            floors.add(floor);
        }
        
        // Load spots from DB and assign to floors
        List<ParkingSpot> spots = DatabaseManager.getInstance().loadAllSpots();
        
        // Count spots by type
        int compactCount = 0;
        int regularCount = 0;
        int handicappedCount = 0;
        
        for (ParkingSpot spot : spots) {
            switch (spot.getType()) {
                case COMPACT: compactCount++; break;
                case REGULAR: regularCount++; break;
                case HANDICAPPED: handicappedCount++; break;
                default: break;
            }
        }
        
        // Add default spots if needed (at least 3 of each type per floor)
        if (compactCount < 3 || regularCount < 3 || handicappedCount < 3) {
            System.out.println("Adding default parking spots...");
            addDefaultSpotsIfNeeded(floorNumbers, spots);
            spots = DatabaseManager.getInstance().loadAllSpots();
        }
        
        for (ParkingSpot spot : spots) {
            allSpots.put(spot.getSpotId(), spot);
            for (Floor floor : floors) {
                if (floor.getFloorNumber() == spot.getFloorNumber()) {
                    floor.addSpot(spot);
                    break;
                }
            }
        }
        
        // Load active vehicles from database and restore state
        loadActiveVehicles();
        
        System.out.println("Parking Lot Initialized from Database");
        System.out.println("- Total Floors: " + floors.size());
        System.out.println("- Total Spots: " + allSpots.size());
        System.out.println("- Active Vehicles: " + parkedVehicles.size());
    }
    
    // Create default parking spots if needed
    private void addDefaultSpotsIfNeeded(List<Integer> floorNumbers, List<ParkingSpot> existingSpots) {
        // Track which spot IDs already exist to avoid duplicates
        java.util.Set<String> existingIds = new java.util.HashSet<>();
        for (ParkingSpot spot : existingSpots) {
            existingIds.add(spot.getSpotId());
        }
        
        // Count spots per floor per type
        java.util.Map<Integer, java.util.Map<ParkingSpotType, Integer>> spotsPerFloor = new java.util.HashMap<>();
        for (ParkingSpot spot : existingSpots) {
            spotsPerFloor.computeIfAbsent(spot.getFloorNumber(), k -> new java.util.HashMap<>());
            java.util.Map<ParkingSpotType, Integer> floorSpots = spotsPerFloor.get(spot.getFloorNumber());
            floorSpots.merge(spot.getType(), 1, Integer::sum);
        }
        
        int spotsAdded = 0;
        
        for (int floorNum : floorNumbers) {
            java.util.Map<ParkingSpotType, Integer> floorSpots = spotsPerFloor.getOrDefault(floorNum, new java.util.HashMap<>());
            
            int compactNeeded = Math.max(0, 3 - floorSpots.getOrDefault(ParkingSpotType.COMPACT, 0));
            int regularNeeded = Math.max(0, 3 - floorSpots.getOrDefault(ParkingSpotType.REGULAR, 0));
            int handicappedNeeded = Math.max(0, 1 - floorSpots.getOrDefault(ParkingSpotType.HANDICAPPED, 0));
            int reservedNeeded = Math.max(0, 1 - floorSpots.getOrDefault(ParkingSpotType.RESERVED, 0));
            
            String[] rows = {"A", "B", "C"};
            int spotNum = 1;
            
            // Add COMPACT spots (for motorcycles)
            for (int i = 0; i < compactNeeded; i++) {
                String row = rows[i % rows.length];
                while (existingIds.contains("F" + floorNum + "-" + row + "-" + spotNum)) {
                    spotNum++;
                }
                ParkingSpot spot = new ParkingSpot(floorNum, row, spotNum++, ParkingSpotType.COMPACT);
                spot.setHourlyRate(2.0);
                DatabaseManager.getInstance().saveParkingSpot(spot);
                existingIds.add(spot.getSpotId());
                spotsAdded++;
            }
            
            // Add REGULAR spots (for cars/SUVs)
            for (int i = 0; i < regularNeeded; i++) {
                String row = rows[i % rows.length];
                while (existingIds.contains("F" + floorNum + "-" + row + "-" + spotNum)) {
                    spotNum++;
                }
                ParkingSpot spot = new ParkingSpot(floorNum, row, spotNum++, ParkingSpotType.REGULAR);
                spot.setHourlyRate(5.0);  // RM 5.00 for REGULAR
                DatabaseManager.getInstance().saveParkingSpot(spot);
                existingIds.add(spot.getSpotId());
                spotsAdded++;
            }
            
            // Add HANDICAPPED spot
            if (handicappedNeeded > 0) {
                while (existingIds.contains("F" + floorNum + "-H-" + spotNum)) {
                    spotNum++;
                }
                ParkingSpot spot = new ParkingSpot(floorNum, "H", spotNum++, ParkingSpotType.HANDICAPPED);
                spot.setHourlyRate(2.0);  // RM 2.00 for HANDICAPPED (discount)
                DatabaseManager.getInstance().saveParkingSpot(spot);
                spotsAdded++;
            }
            
            // Add RESERVED spot
            if (reservedNeeded > 0) {
                while (existingIds.contains("F" + floorNum + "-V-" + spotNum)) {
                    spotNum++;
                }
                ParkingSpot spot = new ParkingSpot(floorNum, "V", spotNum++, ParkingSpotType.RESERVED);
                spot.setHourlyRate(10.0);  // RM 10.00 for RESERVED
                DatabaseManager.getInstance().saveParkingSpot(spot);
                spotsAdded++;
            }
        }
        
        if (spotsAdded > 0) {
            System.out.println("Added " + spotsAdded + " default parking spots");
        }
    }
    
    // Load active vehicles from database
    private void loadActiveVehicles() {
        List<DatabaseManager.VehicleEntryRecord> activeEntries = DatabaseManager.getInstance().loadActiveVehicleEntries();
        for (DatabaseManager.VehicleEntryRecord entry : activeEntries) {
            // Create vehicle based on type
            Vehicle vehicle = null;
            try {
                switch (entry.vehicleType.toLowerCase()) {
                    case "motorcycle":
                        vehicle = new Motorcycle(entry.licensePlate);
                        break;
                    case "car":
                        vehicle = new Car(entry.licensePlate);
                        break;
                    case "suv":
                        vehicle = new SUV(entry.licensePlate);
                        break;
                    case "handicapped":
                        vehicle = new HandicappedVehicle(entry.licensePlate);
                        break;
                    default:
                        vehicle = new Car(entry.licensePlate);
                }
            } catch (Exception e) {
                // If subclass not available, use base Vehicle
                vehicle = new Car(entry.licensePlate);
            }
            
            // Set the entry time from database
            vehicle.setEntryTime(entry.entryTime);
            vehicle.setParkingSpotId(entry.spotId);
            
            // Restore vehicle to spot
            ParkingSpot spot = allSpots.get(entry.spotId);
            if (spot != null && !spot.isOccupied()) {
                spot.assignVehicle(vehicle);
                parkedVehicles.put(entry.licensePlate, vehicle);
                System.out.println("Restored vehicle: " + entry.licensePlate + " at spot " + entry.spotId);
            }
        }
    }
    
    // Find available spots for a vehicle type
    public List<ParkingSpot> findAvailableSpots(String vehicleType) {
        List<ParkingSpot> availableSpots = new ArrayList<>();
        
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
                allowedTypes.add(ParkingSpotType.COMPACT);
                allowedTypes.add(ParkingSpotType.REGULAR);
                allowedTypes.add(ParkingSpotType.HANDICAPPED);
                allowedTypes.add(ParkingSpotType.RESERVED);
                break;
        }
        
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
    
    // Get overall occupancy rate
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
        
        for (ParkingSpotType type : ParkingSpotType.values()) {
            totalByType.put(type, 0);
            occupiedByType.put(type, 0);
        }
        
        for (ParkingSpot spot : allSpots.values()) {
            ParkingSpotType type = spot.getType();
            totalByType.put(type, totalByType.get(type) + 1);
            if (spot.isOccupied()) {
                occupiedByType.put(type, occupiedByType.get(type) + 1);
            }
        }
        
        Map<ParkingSpotType, Double> rates = new HashMap<>();
        for (ParkingSpotType type : ParkingSpotType.values()) {
            int total = totalByType.get(type);
            int occupied = occupiedByType.get(type);
            rates.put(type, total > 0 ? (double) occupied / total : 0.0);
        }
        return rates;
    }
    
    // Vehicle management methods
    public void addParkedVehicle(String licensePlate, Vehicle vehicle, ParkingSpot spot) {
        parkedVehicles.put(licensePlate, vehicle);
        spot.assignVehicle(vehicle);
        DatabaseManager.getInstance().updateSpotOccupancy(spot.getSpotId(), true, licensePlate);
    }
    
    public void removeParkedVehicle(String licensePlate) {
        Vehicle vehicle = parkedVehicles.remove(licensePlate);
        if (vehicle != null) {
            String spotId = vehicle.getParkingSpotId();
            if (spotId != null) {
                ParkingSpot spot = allSpots.get(spotId);
                if (spot != null) {
                    spot.removeVehicle();
                    DatabaseManager.getInstance().updateSpotOccupancy(spotId, false, null);
                }
            }
        }
    }
    
    public Vehicle getParkedVehicle(String licensePlate) {
        return parkedVehicles.get(licensePlate);
    }
    
    // Get all parked vehicles
    public List<Vehicle> getAllParkedVehicles() {
        return new ArrayList<>(parkedVehicles.values());
    }
    
    // Revenue tracking
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
    
    // Print parking lot status
    public void printParkingLotStatus() {
        System.out.println("=== PARKING LOT STATUS ===");
        System.out.println("Total Floors: " + floors.size());
        System.out.println("Total Spots: " + allSpots.size());
        System.out.println("Occupied Spots: " + getOccupiedSpotsCount());
        System.out.println("Overall Occupancy: " + String.format("%.2f", getOverallOccupancy() * 100) + "%");
        System.out.println("Total Revenue: RM " + String.format("%.2f", getTotalRevenue()));
    }
}
