package main.java.model;

import java.time.LocalDateTime;

public abstract class Vehicle {
    private String licensePlate;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private String parkingSpotId;  // To track which spot the vehicle is in

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
    public String getParkingSpotId() { return parkingSpotId; }
    public void setParkingSpotId(String parkingSpotId) { this.parkingSpotId = parkingSpotId; }

     // --------------------------
    // New method: calculate parking hours （member3）
    // --------------------------
    public int calculateParkingHours() {
        LocalDateTime exit = (exitTime != null) ? exitTime : LocalDateTime.now();
        long minutesParked = Duration.between(entryTime, exit).toMinutes();
        int hours = (int) Math.ceil(minutesParked / 60.0); // Round up to nearest hour
        return Math.max(hours, 1); // Minimum 1 hour
    }
}