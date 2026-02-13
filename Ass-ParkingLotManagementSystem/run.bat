@echo off

@REM changes the current working directory to the exact folder where run.bat is stored.
cd /d "%~dp0"

@REM Assign bin file, lib, mysql-connector-j-9.2.0.jar to the variable CP.
set CP=bin;.;lib\mysql-connector-j-9.2.0.jar

@REM Print
echo ========================================
echo    PARKING LOT MANAGEMENT SYSTEM
echo ========================================

echo.
echo 1. Compiling project...

@REM Write .class files to bin, set the class path, read the text file

@REM -cp "%CP%"  code imports and refers to classes from that JAR.
@REM The compiler must validate those references to ensure your program is well‑formed.
@REM Without the JAR on the classpath, the compiler cannot resolve com.mysql.cj.jdbc.Driver – compilation fails.
javac -d bin -cp "%CP%" @sources.txt
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)
echo Compilation successful!

echo.
echo 2. Running Parking Lot Management System...

@REM Use the classpath stored in CP to locate the MainGUI class, then launch it.
java -cp "%CP%" main.java.view.MainGUI

echo.
pause