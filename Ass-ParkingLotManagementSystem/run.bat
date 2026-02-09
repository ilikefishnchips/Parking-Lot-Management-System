@echo off
cd /d "%~dp0"

rem Generate sources list if not exists
dir /s /b src\main\java\*.java src\test\controller\*.java > sources.txt 2>nul

echo Compiling...
javac -d bin @sources.txt
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)
echo Running tests...
java -cp bin test.controller.Member2Test
pause
