package main.java;

import main.java.controller.ExitService;
import main.java.model.*;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        ParkingLot parkingLot = ParkingLot.getInstance();

        // Create a parking spot (floor 1, row A, spot 1, REGULAR type)
        ParkingSpot spot1 = new ParkingSpot(1, "A", 1, ParkingSpotType.REGULAR);

        // Create a concrete vehicle (Car)
        Vehicle vehicle1 = new Car("ABC1234");
        vehicle1.setParkingSpotId(spot1.getSpotId());

        parkingLot.addParkedVehicle("ABC1234", vehicle1, spot1);

        // Simulate parking time
        Thread.sleep(3000);

        ExitService exitService = new ExitService();

        PaymentStrategy payment = new CardPayment();

        exitService.processExit("ABC1234", payment);
    }
}
