@echo off
cd /d "%~dp0"

echo ========================================
echo    RUNNING ALL TESTS - PARKING LOT SYSTEM
echo ========================================

echo.
echo 1. Compiling entire project...
javac -d bin @sources.txt
if %errorlevel% neq 0 (
    echo ❌ Compilation failed!
    pause
    exit /b 1
)
echo ✅ Compilation successful!

echo.
echo 2. Running Member 1 Test (Parking Lot Structure)...
java -cp "bin;." test.controller.Member1Test
if %errorlevel% neq 0 (
    echo ❌ Member 1 Test failed!
    pause
    exit /b 1
)

echo.
echo 3. Running Member 2 Test (Entry System)...
java -cp "bin;." test.controller.Member2Test
if %errorlevel% neq 0 (
    echo ❌ Member 2 Test failed!
    pause
    exit /b 1
)

echo.
echo ========================================
echo    ✅ ALL TESTS PASSED!
echo ========================================
echo.
echo Next steps:
echo 1. Merge branch 'member1-parkinglot-structure' to main
echo 2. Members 3 & 4 can now start their work
echo 3. Run the GUI: java -cp "bin;." main.java.view.MainGUI
pause