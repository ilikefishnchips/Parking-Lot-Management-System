@echo off
cd /d "%~dp0"

set CP=bin;.;lib\mysql-connector-j-9.2.0.jar

echo ========================================
echo    PARKING LOT MANAGEMENT SYSTEM
echo ========================================

echo.
echo 1. Compiling project...
javac -d bin -cp "%CP%" @sources.txt
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)
echo Compilation successful!

echo.
echo 2. Running Parking Lot Management System...
java -cp "%CP%" main.java.view.MainGUI

echo.
pause