@echo off
echo Running Member 3 Test...
cd /d "D:\GitHub\Parking-Lot-Management-System\Ass-ParkingLotManagementSystem"
javac -cp ".;src" src/test/controller/ParkingLotTest.java -d bin
java -cp "bin" test.controller.ParkingLotTest
pause