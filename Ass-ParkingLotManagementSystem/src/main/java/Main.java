package main.java;

import main.java.model.*;
import main.java.controller.ExitService;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        ParkingLot parkingLot = ParkingLot.getInstance();

        ParkingSpot spot1 = new ParkingSpot("A1", 5.0);

        Vehicle vehicle1 = new Vehicle("ABC1234");
        vehicle1.assignSpot(spot1);

        parkingLot.parkVehicle(vehicle1);

        // Simulate parking time
        Thread.sleep(3000);

        ExitService exitService = new ExitService();

        PaymentStrategy payment = new CardPayment();

        exitService.processExit("ABC1234", payment);
    }
}
