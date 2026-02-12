package main.java.model;

public class ParkingSpot {
    private String spotId;
    private ParkingSpotType type;
    private boolean isOccupied;
    private Vehicle currentVehicle;
    private int floorNumber;      // NEW
    private String row;           // NEW
    private double hourlyRate;    // NEW
    private int spotNumber;       // NEW: For unique spot number

    public ParkingSpot(int floorNumber, String row, int spotNumber, ParkingSpotType type) {
        this.floorNumber = floorNumber;
        this.row = row;
        this.spotNumber = spotNumber;
        this.type = type;
        this.isOccupied = false;
        
        // Generate spot ID in format "F{floor}-R{row}-S{spotNumber}"
        this.spotId = String.format("F%d-R%s-S%d", floorNumber, row, spotNumber);
        
        // Set hourly rate based on spot type
        switch (type) {
            case COMPACT:
                this.hourlyRate = 2.0;      // RM 2/hour
                break;
            case REGULAR:
                this.hourlyRate = 5.0;      // RM 5/hour
                break;
            case HANDICAPPED:
                this.hourlyRate = 2.0;      // RM 2/hour
                break;
            case RESERVED:
                this.hourlyRate = 10.0;     // RM 10/hour
                break;
            default:
                this.hourlyRate = 5.0;      // Default rate
        }
    }

    // Getters
    public String getSpotId() { return spotId; }
    public ParkingSpotType getType() { return type; }
    public boolean isOccupied() { return isOccupied; }
    public int getFloorNumber() { return floorNumber; }      // NEW
    public String getRow() { return row; }                   // NEW
    public double getHourlyRate() { return hourlyRate; }     // NEW
    public int getSpotNumber() { return spotNumber; }        // NEW
    
    // For displaying in dropdown
    @Override
    public String toString() {
        String status = isOccupied ? "[OCCUPIED]" : "[FREE]";
        return String.format("%s (%s) %s - RM%.1f/hour", 
            spotId, type, status, hourlyRate);
    }

    public void assignVehicle(Vehicle v) {
        this.currentVehicle = v;
        this.isOccupied = true;
    }

    public void removeVehicle() {
        this.currentVehicle = null;
        this.isOccupied = false;
    }

        // Add this getter method
    public Vehicle getCurrentVehicle() {
        return currentVehicle;
    }
}