package Member2_assignment;

public class EntryService {

    public Ticket processEntry(String plateNumber, String vehicleType, ParkingSpot selectedSpot) throws Exception {
        
        // 1. Validation
        if (selectedSpot.isOccupied()) {
            throw new Exception("Error: Spot " + selectedSpot.getSpotId() + " is occupied.");
        }

        // 2. Create Vehicle
        Vehicle vehicle = VehicleFactory.createVehicle(vehicleType, plateNumber);

        // 3. Validate Rules (Member 2's core logic)
        if (!vehicle.canParkIn(selectedSpot.getType())) {
            throw new Exception("Error: " + vehicleType + " cannot park in " + selectedSpot.getType());
        }

        // 4. Park
        selectedSpot.assignVehicle(vehicle);

        // 5. Ticket
        return new Ticket(vehicle, selectedSpot);
    }
}