package main.java.controller;

import main.java.data.DatabaseManager;
// FUTURE-PROOFING: Import Reservation for reservation system
// import main.java.model.Reservation;
import main.java.model.ParkingLot;
import main.java.model.ParkingSpot;
import main.java.model.Ticket;
import main.java.model.Vehicle;
// import java.time.LocalDateTime;

public class EntryService {

    public Ticket processEntry(String plateNumber, String vehicleType, ParkingSpot selectedSpot) throws Exception {
    
        // 1. Validation - Check for duplicate entry
        ParkingLot parkingLot = ParkingLot.getInstance();
            if (parkingLot.getParkedVehicle(plateNumber) != null) {
                throw new Exception("Error: Vehicle with license plate " + plateNumber + " is already parked.");
            }
            
        // 2. Validate spot
        if (selectedSpot.isOccupied()) {
            throw new Exception("Error: Spot " + selectedSpot.getSpotId() + " is occupied.");
        }

        // 3. Create Vehicle
        Vehicle vehicle = VehicleFactory.createVehicle(vehicleType, plateNumber);
        
        // Store parking spot ID in vehicle
        vehicle.setParkingSpotId(selectedSpot.getSpotId());

        // 4. Validate Rules
        if (!vehicle.canParkIn(selectedSpot.getType())) {
            throw new Exception("Error: " + vehicleType + " cannot park in " + selectedSpot.getType());
        }
        
        // FUTURE-PROOFING: Reservation System Check
        // To activate: Uncomment the code below
        // This checks if the spot is reserved at entry time and validates
        // that the entering vehicle has a valid reservation
        /*
        LocalDateTime now = LocalDateTime.now();
        if (parkingLot.isSpotReserved(selectedSpot, now)) {
            // Spot is reserved, check if this vehicle has the reservation
            Reservation res = parkingLot.findReservation(plateNumber, now);
            if (res == null) {
                throw new Exception("Error: Spot " + selectedSpot.getSpotId() + 
                    " is reserved for another vehicle. Please use a different spot.");
            }
            // Optionally mark reservation as used or update status
        }
        */

        // 5. Park - Update ParkingLot
        parkingLot.addParkedVehicle(plateNumber, vehicle, selectedSpot);

        // 6. Save to vehicle_entry table
        DatabaseManager.getInstance().saveVehicleEntry(
            plateNumber,
            vehicleType,
            vehicle.getEntryTime(),
            selectedSpot.getSpotId()
        );

        // 7. Ticket
        return new Ticket(vehicle, selectedSpot);
    }
}
