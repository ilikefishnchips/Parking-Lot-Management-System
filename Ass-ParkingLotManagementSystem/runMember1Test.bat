@echo off
echo Running Member 1 Test...
cd /d "D:\GitHub\Parking-Lot-Management-System\Ass-ParkingLotManagementSystem"
javac -cp ".;src" src/test/model/Member1Test.java -d bin
java -cp "bin" test.model.Member1Test
pause