@echo off
cd /d "D:\GitHub\Parking-Lot-Management-System\Lab-SeminarManagementSystem"

echo Cleaning bin directory...
rmdir /s /q bin 2>nul
mkdir bin 2>nul

echo Compiling Java files...

javac -d bin src/common/model/*.java
javac -d bin -cp bin src/common/ui/*.java

javac -d bin -cp bin src/Student/model/*.java
javac -d bin -cp bin src/Student/controller/*.java
javac -d bin -cp bin src/Student/UI/*.java

javac -d bin -cp bin src/Evaluator/model/*.java
javac -d bin -cp bin src/Evaluator/controller/*.java
javac -d bin -cp bin src/Evaluator/ui/*.java

javac -d bin -cp bin src/Coordinator/model/*.java
javac -d bin -cp bin src/Coordinator/controller/*.java
javac -d bin -cp bin src/Coordinator/ui/*.java

javac -d bin -cp bin MainApplication.java

echo.
echo ====================================
echo COMPILATION COMPLETE!
echo ====================================
echo.
echo Run with: java -cp "bin;src" MainApplication
echo.
pause