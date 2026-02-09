package main.java.controller;

import main.java.model.ParkingLot;
import main.java.model.ParkingSpot;
import main.java.model.Ticket;
import main.java.model.Vehicle;

public class EntryService {

    public Ticket processEntry(String plateNumber, String vehicleType, ParkingSpot selectedSpot) throws Exception {
    
    // 1. Validation
    if (selectedSpot.isOccupied()) {
        throw new Exception("Error: Spot " + selectedSpot.getSpotId() + " is occupied.");
    }

    // 2. Create Vehicle
    Vehicle vehicle = VehicleFactory.createVehicle(vehicleType, plateNumber);
    
    // Store parking spot ID in vehicle
    vehicle.setParkingSpotId(selectedSpot.getSpotId());

    // 3. Validate Rules
    if (!vehicle.canParkIn(selectedSpot.getType())) {
        throw new Exception("Error: " + vehicleType + " cannot park in " + selectedSpot.getType());
    }

    // 4. Park - Update ParkingLot
    ParkingLot parkingLot = ParkingLot.getInstance();
    parkingLot.addParkedVehicle(plateNumber, vehicle, selectedSpot);

    // 5. Ticket
    return new Ticket(vehicle, selectedSpot);
}
}