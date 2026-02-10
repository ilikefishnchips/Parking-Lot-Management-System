package test.controller;

import main.java.controller.ExitService;
import main.java.model.*;

public class ParkingLotTest {

    public static void main(String[] args) {
        try {
            ParkingLot parkingLot = ParkingLot.getInstance();

            // --------------------------
            // 1. 模拟车辆入场
            // --------------------------
            Vehicle car1 = new Car("ABC123"); // 假设 Car 是 Vehicle 的子类
            ParkingSpot spotForCar1 = parkingLot.findAvailableSpots("car").get(0);

            parkingLot.addParkedVehicle(car1.getLicensePlate(), car1, spotForCar1);
            car1.setParkingSpotId(spotForCar1.getSpotId());

            System.out.println("Car " + car1.getLicensePlate() + " parked at " + spotForCar1.getSpotId());

            // --------------------------
            // 2. 模拟等待一段时间 (可以用 sleep 模拟)
            // --------------------------
            // Thread.sleep(3600 * 1000); // 模拟 1 小时停车（可选）

            // --------------------------
            // 3. 车辆出场
            // --------------------------
            ExitService exitService = new ExitService();
            double fee = exitService.processExit(car1.getLicensePlate());

            System.out.println("Car " + car1.getLicensePlate() + " exited.");
            System.out.printf("Parking fee: RM %.2f\n", fee);

            // --------------------------
            // 4. 打印停车场状态
            // --------------------------
            parkingLot.printParkingLotStatus();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

