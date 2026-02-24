package main.java.model;

import java.time.LocalDateTime;

/**
 * FUTURE-PROOFING: Represents a parking reservation.
 * 
 * This class allows customers to reserve a spot for a future time. The system
 * can then check reservations at entry to ensure only reserved vehicles can
 * use reserved spots.
 * 
 * To activate this feature:
 * 1. Uncomment this class
 * 2. Add reservations list to ParkingLot
 * 3. Modify EntryService.processEntry() to check reservations
 * 4. Optionally create a ReservationPanel in GUI
 * 
 * Database integration would require a new 'reservation' table:
 *   CREATE TABLE reservation (
 *     reservation_id VARCHAR(50) PRIMARY KEY,
 *     license_plate VARCHAR(20) NOT NULL,
 *     spot_id VARCHAR(20),
 *     spot_type VARCHAR(20),
 *     start_time DATETIME NOT NULL,
 *     end_time DATETIME NOT NULL,
 *     is_active BOOLEAN DEFAULT TRUE
 *   );
 * 
 * @author Future-Proof Implementation
 */
public class Reservation {
    private String reservationId;
    private String licensePlate;
    private String spotId;          // Can be null if only spot type is reserved
    private ParkingSpotType spotType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isActive;       // Whether the reservation is still valid

    /**
     * Creates a new reservation.
     * 
     * @param reservationId Unique identifier for this reservation
     * @param licensePlate License plate of the vehicle
     * @param spotId Specific spot ID (or null for type-based reservation)
     * @param spotType Type of spot requested
     * @param startTime Reservation start time
     * @param endTime Reservation end time
     */
    public Reservation(String reservationId, String licensePlate, String spotId, 
                       ParkingSpotType spotType, LocalDateTime startTime, LocalDateTime endTime) {
        this.reservationId = reservationId;
        this.licensePlate = licensePlate;
        this.spotId = spotId;
        this.spotType = spotType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isActive = true;
    }

    // Getters and Setters
    public String getReservationId() { return reservationId; }
    public void setReservationId(String reservationId) { this.reservationId = reservationId; }
    
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    
    public String getSpotId() { return spotId; }
    public void setSpotId(String spotId) { this.spotId = spotId; }
    
    public ParkingSpotType getSpotType() { return spotType; }
    public void setSpotType(ParkingSpotType spotType) { this.spotType = spotType; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    /**
     * Checks if this reservation is valid at a given time.
     * 
     * @param time The time to check
     * @return true if the reservation is active and covers the given time
     */
    public boolean isValidAt(LocalDateTime time) {
        return isActive && 
               !time.isBefore(startTime) && 
               !time.isAfter(endTime);
    }

    @Override
    public String toString() {
        return String.format("Reservation[%s]: %s for %s, %s - %s, %s",
            reservationId, licensePlate, spotType, 
            startTime, endTime, 
            isActive ? "ACTIVE" : "INACTIVE");
    }
}
