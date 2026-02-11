package test.controller;

import main.java.controller.ExitService;
import main.java.model.*;

public class ParkingLotTest {

    public static void main(String[] args) {
        try {
            ParkingLot parkingLot = ParkingLot.getInstance();

            // --------------------------
            // 1. simulate vehicle entry
            // --------------------------
            Vehicle car1 = new Car("ABC123"); // assume Car is a subclass of Vehicle
            ParkingSpot spotForCar1 = parkingLot.findAvailableSpots("car").get(0);

            parkingLot.addParkedVehicle(car1.getLicensePlate(), car1, spotForCar1);
            car1.setParkingSpotId(spotForCar1.getSpotId());

            System.out.println("Car " + car1.getLicensePlate() + " parked at " + spotForCar1.getSpotId());

            // --------------------------
            // 2. simulate some parking duration
            // --------------------------
            // Thread.sleep(3600 * 1000); // simulate 1 hour parking

            // --------------------------
            // 3. simulate vehicle exit
            // --------------------------
            ExitService exitService = new ExitService();
            double fee = exitService.processExit(car1.getLicensePlate());

            System.out.println("Car " + car1.getLicensePlate() + " exited.");
            System.out.printf("Parking fee: RM %.2f\n", fee);

            // --------------------------
            // 4. print parking lot status
            // --------------------------
            parkingLot.printParkingLotStatus();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

