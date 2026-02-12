@echo off
echo Running Member 2 Test (Entry System)...
cd /d "%~dp0"
javac -cp ".;src;lib/mysql-connector-j-9.2.0.jar" src/test/controller/Member2Test.java -d bin
java -cp "bin;lib/mysql-connector-j-9.2.0.jar;." test.controller.Member2Test
pause
