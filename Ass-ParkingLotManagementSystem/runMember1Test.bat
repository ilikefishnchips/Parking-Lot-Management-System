@echo off
echo Running Member 1 Test (Parking Lot Structure)...
cd /d "%~dp0"
javac -cp ".;src;lib/mysql-connector-j-9.2.0.jar" src/test/controller/Member1Test.java -d bin
java -cp "bin;lib/mysql-connector-j-9.2.0.jar;." test.controller.Member1Test
pause
