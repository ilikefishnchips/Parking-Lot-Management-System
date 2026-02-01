package Member2_assignment;

import java.time.LocalDateTime;

public abstract class Vehicle {
    private String licensePlate;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;

    public Vehicle(String licensePlate) {
        this.licensePlate = licensePlate;
        this.entryTime = LocalDateTime.now();
    }

    // Abstract method: Every child class must implement this
    public abstract boolean canParkIn(ParkingSpotType spotType);

    public String getLicensePlate() { return licensePlate; }
    public LocalDateTime getEntryTime() { return entryTime; }
    
    public LocalDateTime getExitTime() { return exitTime; }
    public void setExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }
}