@echo off
cd /d "D:\GitHub\Parking-Lot-Management-System\Lab-SeminarManagementSystem"

echo Cleaning bin directory...
rmdir /s /q bin 2>nul
mkdir bin 2>nul

echo Compiling all Java files...
javac -d bin -cp "src" MainApplication.java src/**/*.java

echo.
echo ====================================
echo COMPILATION COMPLETE!
echo ====================================
echo.
echo Run with: java -cp bin MainApplication
echo.
pause