# Clean bin directory
Remove-Item -Path "bin" -Recurse -Force -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Path "bin" -Force | Out-Null

Write-Host "Compiling all Java files..." -ForegroundColor Green

# 1. Compile common model (no dependencies)
javac -d bin src/common/model/*.java
if ($LASTEXITCODE -ne 0) {
    Write-Host "Error compiling common model" -ForegroundColor Red
    exit
}

# 2. Compile common ui (depends on common model)
javac -d bin -cp bin src/common/ui/*.java
if ($LASTEXITCODE -ne 0) {
    Write-Host "Error compiling common ui" -ForegroundColor Red
    exit
}

# 3. Compile Student model (depends on common model)
javac -d bin -cp bin src/Student/model/*.java
if ($LASTEXITCODE -ne 0) {
    Write-Host "Error compiling Student model" -ForegroundColor Red
    exit
}

# 4. Compile Student controller (depends on Student model and common model)
javac -d bin -cp bin src/Student/controller/*.java
if ($LASTEXITCODE -ne 0) {
    Write-Host "Error compiling Student controller" -ForegroundColor Red
    exit
}

# 5. Compile Student ui (depends on Student model, controller, and common ui)
javac -d bin -cp bin src/Student/ui/*.java
if ($LASTEXITCODE -ne 0) {
    Write-Host "Error compiling Student ui" -ForegroundColor Red
    exit
}

# 6. Compile Evaluator model (depends on common model)
javac -d bin -cp bin src/Evaluator/model/*.java
if ($LASTEXITCODE -ne 0) {
    Write-Host "Error compiling Evaluator model" -ForegroundColor Red
    exit
}

# 7. Compile Evaluator controller (depends on Evaluator model and common model)
javac -d bin -cp bin src/Evaluator/controller/*.java
if ($LASTEXITCODE -ne 0) {
    Write-Host "Error compiling Evaluator controller" -ForegroundColor Red
    exit
}

# 8. Compile Evaluator ui (depends on Evaluator model, controller, and common ui)
javac -d bin -cp bin src/Evaluator/ui/*.java
if ($LASTEXITCODE -ne 0) {
    Write-Host "Error compiling Evaluator ui" -ForegroundColor Red
    exit
}

# 9. Compile Coordinator model (depends on common model)
javac -d bin -cp bin src/Coordinator/model/*.java
if ($LASTEXITCODE -ne 0) {
    Write-Host "Error compiling Coordinator model" -ForegroundColor Red
    exit
}

# 10. Compile Coordinator controller (depends on Coordinator model and common model)
javac -d bin -cp bin src/Coordinator/controller/*.java
if ($LASTEXITCODE -ne 0) {
    Write-Host "Error compiling Coordinator controller" -ForegroundColor Red
    exit
}

# 11. Compile Coordinator ui (depends on Coordinator model, controller, and common ui)
javac -d bin -cp bin src/Coordinator/ui/*.java
if ($LASTEXITCODE -ne 0) {
    Write-Host "Error compiling Coordinator ui" -ForegroundColor Red
    exit
}

# 12. Finally compile MainApplication (depends on everything)
javac -d bin -cp bin MainApplication.java
if ($LASTEXITCODE -ne 0) {
    Write-Host "Error compiling MainApplication" -ForegroundColor Red
    exit
}

Write-Host "Compilation successful!" -ForegroundColor Green
Write-Host "To run: java -cp 'bin;src' MainApplication" -ForegroundColor Yellow