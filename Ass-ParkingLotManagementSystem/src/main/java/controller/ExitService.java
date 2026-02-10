package main.java.controller;

import main.java.model.ParkingLot;
import main.java.model.ParkingSpot;
import main.java.model.Vehicle;

public class ExitService {

    public double processExit(String plateNumber) throws Exception {

        // 1. Get ParkingLot instance
        ParkingLot parkingLot = ParkingLot.getInstance();

        // 2. Get parked vehicle
        Vehicle vehicle = parkingLot.getParkedVehicle(plateNumber);
        if (vehicle == null) {
            throw new Exception("Error: Vehicle with plate number " + plateNumber + " not found.");
        }

        // 3. Get parking spot of the vehicle
        ParkingSpot spot = parkingLot.getAllSpots().get(vehicle.getParkingSpotId());
        if (spot == null) {
            throw new Exception("Error: Parking spot not found for vehicle " + plateNumber);
        }

        // 4. Calculate parking hours
        int hours = vehicle.calculateParkingHours();

        // 5. Calculate parking fee
        double fee = spot.getHourlyRate() * hours;

        // 6. Remove vehicle and free parking spot
        spot.removeVehicle();
        parkingLot.removeParkedVehicle(plateNumber);

        // 7. Add to total revenue
        parkingLot.addRevenue(fee);

        // 8. Return total fee
        return fee;
    }
}
