package Member2_assignment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ticket {
    private String ticketId;
    private String spotLocation;
    private String licensePlate;
    private LocalDateTime issueTime;

    public Ticket(Vehicle vehicle, ParkingSpot spot) {
        this.spotLocation = spot.getSpotId();
        this.licensePlate = vehicle.getLicensePlate();
        this.issueTime = vehicle.getEntryTime();
        this.ticketId = generateTicketId();
    }

    private String generateTicketId() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        return "T-" + licensePlate + "-" + issueTime.format(formatter);
    }

    @Override
    public String toString() {
        return "Ticket ID: " + ticketId + " | Spot: " + spotLocation;
    }
}