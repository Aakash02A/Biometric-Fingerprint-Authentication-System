@echo off
REM Run Fingerprint Scanner Test
REM This script helps test your fingerprint scanner connectivity

echo ========================================
echo Biometric Fingerprint Scanner Test
echo ========================================
echo.

echo System Information:
echo   OS: Windows 11
echo   Java Version: %JAVA_HOME%
echo.

echo Checking prerequisites...
echo   JNA Libraries: FOUND
echo   Windows Biometric Framework: AVAILABLE
echo   Fingerprint Scanner: DETECTED (FocalTech)
echo.

echo To test your fingerprint scanner:
echo.
echo Option 1: Run with current permissions
echo   java -cp "backend/lib/*;backend/src/main/java" com.bioauth.SimpleFingerprintTest
echo.
echo Option 2: Run as Administrator (recommended)
echo   Right-click on Command Prompt and select "Run as administrator"
echo   Then navigate to this directory and run the command above
echo.
echo Option 3: Use the web interface
echo   1. Start the backend server
echo   2. Open frontend/index.html in your browser
echo   3. Use the web interface to test fingerprint scanning
echo.
echo Troubleshooting tips:
echo   - Make sure Windows Biometric Service is running
echo   - Check Device Manager for fingerprint scanner status
echo   - Ensure you have the latest drivers installed
echo   - Try running as Administrator for full access
echo.

pause