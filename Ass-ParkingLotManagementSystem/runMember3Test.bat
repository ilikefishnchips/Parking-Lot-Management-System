@echo off
echo Running Member 3 Test (Parking Lot Test)...
cd /d "%~dp0"
javac -cp ".;src;lib/mysql-connector-j-9.2.0.jar" src/test/controller/ParkingLotTest.java -d bin
java -cp "bin;lib/mysql-connector-j-9.2.0.jar;." test.controller.ParkingLotTest
pause
