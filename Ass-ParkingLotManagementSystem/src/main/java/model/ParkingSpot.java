package main.java.model;

public class ParkingSpot {
    private String spotId;
    private ParkingSpotType type;
    private boolean isOccupied;
    private Vehicle currentVehicle;
    private int floorNumber;
    private String row;
    private double hourlyRate;
    private int spotNumber;

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
                this.hourlyRate = 2.0;
                break;
            case REGULAR:
                this.hourlyRate = 5.0;
                break;
            case HANDICAPPED:
                this.hourlyRate = 2.0;
                break;
            case RESERVED:
                this.hourlyRate = 10.0;
                break;
            default:
                this.hourlyRate = 5.0;
        }
    }

    // Getters
    public String getSpotId() { return spotId; }
    public ParkingSpotType getType() { return type; }
    public boolean isOccupied() { return isOccupied; }
    public int getFloorNumber() { return floorNumber; }
    public String getRow() { return row; }
    public double getHourlyRate() { return hourlyRate; }
    public int getSpotNumber() { return spotNumber; }
    public Vehicle getCurrentVehicle() { return currentVehicle; }
    
    // Setters
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }
    public void setOccupied(boolean occupied) { this.isOccupied = occupied; }
    public void setCurrentVehicle(Vehicle vehicle) { this.currentVehicle = vehicle; }
    
    public void assignVehicle(Vehicle v) {
        this.currentVehicle = v;
        this.isOccupied = true;
    }
    
    public void removeVehicle() {
        this.currentVehicle = null;
        this.isOccupied = false;
    }

    // For displaying in dropdown
    @Override
    public String toString() {
        String status = isOccupied ? "[OCCUPIED]" : "[FREE]";
        return String.format("%s (%s) %s - RM%.1f/hour", 
            spotId, type, status, hourlyRate);
    }
}
