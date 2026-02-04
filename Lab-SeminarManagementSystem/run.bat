@echo off
cd /d "D:\GitHub\Parking-Lot-Management-System\Lab-SeminarManagementSystem"

echo Cleaning bin directory...
rmdir /s /q bin 2>nul
mkdir bin 2>nul

echo Compiling with new features...
echo.

echo Step 1: Compile common model...
javac -d bin src\common\model\*.java
if %errorlevel% neq 0 goto error

echo Step 2: Compile Student model...
javac -d bin -cp bin src\Student\model\*.java
if %errorlevel% neq 0 goto error

echo Step 3: Compile Student controller...
javac -d bin -cp bin src\Student\controller\*.java
if %errorlevel% neq 0 goto error

echo Step 4: Compile Student UI...
javac -d bin -cp bin src\Student\UI\*.java
if %errorlevel% neq 0 goto error

echo Step 5: Compile Evaluator model...
javac -d bin -cp bin src\Evaluator\model\*.java
if %errorlevel% neq 0 goto error

echo Step 6: Compile Evaluator controller...
javac -d bin -cp bin src\Evaluator\controller\*.java
if %errorlevel% neq 0 goto error

echo Step 7: Compile Evaluator UI...
javac -d bin -cp bin src\Evaluator\ui\*.java
if %errorlevel% neq 0 goto error

echo Step 8: Compile Coordinator model...
javac -d bin -cp bin src\Coordinator\model\*.java
if %errorlevel% neq 0 goto error

echo Step 9: Compile Coordinator controllers...
javac -d bin -cp bin src\Coordinator\controller\*.java
if %errorlevel% neq 0 goto error

echo Step 10: Compile Coordinator UI...
javac -d bin -cp bin src\Coordinator\ui\*.java
if %errorlevel% neq 0 goto error

echo Step 11: Compile common UI...
javac -d bin -cp bin src\common\ui\*.java
if %errorlevel% neq 0 goto error

echo Step 12: Compile MainApplication...
javac -d bin -cp bin MainApplication.java
if %errorlevel% neq 0 goto error

echo.
echo ====================================
echo COMPILATION SUCCESSFUL WITH NEW FEATURES!
echo ====================================
echo.
echo Starting Enhanced Seminar Management System...
echo.
java -cp bin MainApplication
goto end

:error
echo.
echo ====================================
echo COMPILATION FAILED!
echo ====================================
echo Check the error above and fix it.
pause
exit /b 1

:end
pause