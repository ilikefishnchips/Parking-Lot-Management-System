package Member2_assignment;

public class ParkingSpot {
    private String spotId;
    private ParkingSpotType type;
    private boolean isOccupied;
    private Vehicle currentVehicle;

    public ParkingSpot(String spotId, ParkingSpotType type) {
        this.spotId = spotId;
        this.type = type;
        this.isOccupied = false;
    }

    public boolean isOccupied() { return isOccupied; }
    public ParkingSpotType getType() { return type; }
    public String getSpotId() { return spotId; }

    public void assignVehicle(Vehicle v) {
        this.currentVehicle = v;
        this.isOccupied = true;
    }

    public void removeVehicle() {
        this.currentVehicle = null;
        this.isOccupied = false;
    }
}