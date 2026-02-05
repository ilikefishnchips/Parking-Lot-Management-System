@echo off
cd /d "D:\GitHub\Parking-Lot-Management-System\Lab-SeminarManagementSystem"

echo Cleaning bin directory...
rmdir /s /q bin 2>nul
mkdir bin 2>nul

echo Compiling with new features...
echo.

echo Step 1: Compile common model (without dependencies)...
javac -d bin src\common\model\*.java
if %errorlevel% neq 0 goto error

echo Step 2: Compile Student model (depends on common)...
javac -d bin -cp bin src\Student\model\*.java
if %errorlevel% neq 0 goto error

echo Step 3: Compile Student controller (depends on Student model and common)...
javac -d bin -cp bin src\Student\controller\*.java
if %errorlevel% neq 0 goto error

echo Step 4: Compile Evaluator model (depends on common)...
javac -d bin -cp bin src\Evaluator\model\*.java
if %errorlevel% neq 0 goto error

echo Step 5: Compile Evaluator controller (depends on Evaluator model and common)...
javac -d bin -cp bin src\Evaluator\controller\*.java
if %errorlevel% neq 0 goto error

echo Step 6: Compile Coordinator model (depends on common)...
javac -d bin -cp bin src\Coordinator\model\*.java
if %errorlevel% neq 0 goto error

echo Step 7: Compile Coordinator controllers (depends on Coordinator model and common)...
javac -d bin -cp bin src\Coordinator\controller\*.java
if %errorlevel% neq 0 goto error

echo Step 8: Compile Student UI (depends on Student model/controller and common)...
javac -d bin -cp bin src\Student\ui\*.java
if %errorlevel% neq 0 goto error

echo Step 9: Compile Evaluator UI (depends on Evaluator model/controller and common)...
javac -d bin -cp bin src\Evaluator\ui\*.java
if %errorlevel% neq 0 goto error

echo Step 10: Compile Coordinator UI (depends on Coordinator model/controller and common)...
javac -d bin -cp bin src\Coordinator\ui\*.java
if %errorlevel% neq 0 goto error

echo Step 11: Compile common UI (depends on ALL packages)...
javac -d bin -cp "bin;." src\common\ui\*.java
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