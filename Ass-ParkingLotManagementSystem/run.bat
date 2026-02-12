@echo off
cd /d "%~dp0"

echo ========================================
echo    PARKING LOT MANAGEMENT SYSTEM
echo ========================================

echo.
echo 1. Compiling project...
javac -d bin @sources.txt
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)
echo Compilation successful!

echo.
echo 2. Running Parking Lot Management System...
java -cp "bin;." main.java.view.MainGUI

echo.
echo ========================================
echo    PROGRAM CLOSED
echo ========================================
pause
