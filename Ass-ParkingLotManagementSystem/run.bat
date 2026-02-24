@REM For member Chong Yu Tze compilation purpose only, as he have accidentally deleted my compiler to free up space on my device and I am unable to compile the project.
@echo off
cd /d "%~dp0"

set CP=bin;.;lib\mysql-connector-j-9.2.0.jar
set JAVA_HOME=C:\Program Files\Android\Android Studio1\jbr


echo ========================================
echo    PARKING LOT MANAGEMENT SYSTEM
echo ========================================

echo.
echo Running Parking Lot Management System...
"%JAVA_HOME%\bin\java.exe" -cp "%CP%" main.java.view.MainGUI

echo.
pause

@REM Original code for compilation and running, commented out for member Chong Yu Tze's use only
@REM cd /d "%~dp0"

@REM set CP=bin;.;lib\mysql-connector-j-9.2.0.jar



@REM echo ========================================
@REM echo    PARKING LOT MANAGEMENT SYSTEM
@REM echo ========================================

@REM echo.
@REM echo 1. Compiling project...
@REM javac -d bin -cp "%CP%" @sources.txt
@REM if %errorlevel% neq 0 (
@REM     echo Compilation failed!
@REM     pause
@REM     exit /b 1
@REM )
@REM echo Compilation successful!

@REM echo.
@REM echo 2. Running Parking Lot Management System...
@REM java -cp "%CP%" main.java.view.MainGUI

@REM echo.
@REM pause