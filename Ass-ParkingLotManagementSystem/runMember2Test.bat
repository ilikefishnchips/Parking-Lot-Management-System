@echo off
echo Running Member 2 Test...
cd /d "D:\GitHub\Parking-Lot-Management-System\Ass-ParkingLotManagementSystem"
javac -cp ".;src" src/test/controller/Member2Test.java -d bin
java -cp "bin" test.controller.Member2Test
pause
