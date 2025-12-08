@echo off
REM Installation and Startup Script for Biometric Authentication System

setlocal enabledelayedexpansion

echo ==========================================
echo Biometric Fingerprint Authentication System
echo Automated Setup Script
echo ==========================================
echo.

REM Check Java
echo Checking Java installation...
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java not found. Please install Java 11 or higher.
    exit /b 1
)
for /f "tokens=*" %%i in ('java -version 2^>^&1 ^| findstr /R "version"') do set JAVA_VERSION=%%i
echo   Found: %JAVA_VERSION%

REM Check Maven
echo Checking Maven installation...
mvn -v >nul 2>&1
if errorlevel 1 (
    echo ERROR: Maven not found. Please install Maven.
    exit /b 1
)
echo   Found Maven

REM Build Backend
echo.
echo Building Spring Boot API...
cd backend
call mvn clean package -q
if errorlevel 1 (
    echo ERROR: Maven build failed
    exit /b 1
)
echo OK - Backend build complete
cd ..

REM Create data directory
echo Creating data directory...
if not exist "data" mkdir data
echo OK - Data directory created

REM Display information
echo.
echo ==========================================
echo Setup Complete!
echo ==========================================
echo.
echo To start the application:
echo.
echo Option 1 - Direct JAR:
echo   java -jar backend\target\biometric-auth-api.jar
echo.
echo Option 2 - Docker Compose:
echo   docker-compose up -d
echo.
echo API will be available at:
echo   http://localhost:8080/v1
echo.
echo Frontend:
echo   Open frontend/index.html in your browser
echo.
echo Documentation:
echo   - QUICKSTART.md - Get running in 5 minutes
echo   - DEPLOYMENT_GUIDE.md - Production deployment
echo.
pause
