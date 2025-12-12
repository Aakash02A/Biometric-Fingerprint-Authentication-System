# Fingerprint Scanner Setup Guide

This guide will help you connect your laptop's biometric fingerprint scanner for real-time scanning with the Biometric Fingerprint Authentication System.

## System Requirements

- Windows 7 or later (Windows 10/11 recommended)
- Java 11 or later
- Compatible fingerprint scanner (FocalTech, SecuGen, Crossmatch, NITGEN, etc.)
- Windows Biometric Framework (included with Windows 7+)

## Current System Status

Based on our diagnostics, your system has:
- ✅ Windows 11 operating system
- ✅ Windows Biometric Framework available
- ✅ FocalTech Fingerprint Reader detected and in good status
- ✅ JNA libraries installed

## Connecting Your Fingerprint Scanner

### Step 1: Verify Windows Biometric Service

1. Press `Win + R`, type `services.msc`, and press Enter
2. Find "Windows Biometric Service"
3. Ensure it's running (if not, right-click and select "Start")
4. Set Startup type to "Automatic"

### Step 2: Check Device Manager

1. Press `Win + X` and select "Device Manager"
2. Expand "Biometric devices"
3. Verify your FocalTech Fingerprint Reader is listed
4. Right-click and select "Properties" to check device status

### Step 3: Run with Administrator Privileges

For best results, run the application with administrator privileges:

1. Right-click on Command Prompt or PowerShell
2. Select "Run as administrator"
3. Navigate to the project directory
4. Run the fingerprint test:
   ```
   java -cp "backend/lib/*;backend/src/main/java" com.bioauth.SimpleFingerprintTest
   ```

## Using the Full Application

### Option 1: Web Interface (Recommended)

1. Start the backend server (instructions in README.md)
2. Open `frontend/index.html` in your browser
3. Use the web interface to test fingerprint scanning

### Option 2: Command Line

1. Compile the application:
   ```
   javac -cp "backend/lib/*;backend/src/main/java" backend/src/main/java/com/bioauth/*.java
   ```

2. Run the demo:
   ```
   java -cp "backend/lib/*;backend/src/main/java" com.bioauth.SystemDemo
   ```

## Troubleshooting

### Common Issues

1. **"Access Denied" or "General Failure" Errors**
   - Solution: Run as Administrator
   - Check Windows Biometric Service is running

2. **"No Devices Found"**
   - Solution: Check Device Manager
   - Reinstall scanner drivers

3. **"Library Not Loaded"**
   - Solution: Ensure JNA libraries are in the lib folder
   - Check Java installation

### Advanced Diagnostics

Run our diagnostic tool to get detailed system information:
```
java -cp "backend/lib/*;backend/src/main/java" com.bioauth.BiometricDiagnostic
```

## Real-Time Scanning Features

When your fingerprint scanner is properly connected, the system provides:

- Real-time fingerprint capture
- Quality scoring (0-100 scale)
- Automatic retry on low-quality scans
- Multi-scan fusion for better accuracy
- Template encryption and secure storage

## Expected Results

Once connected, you should see:
- ✅ Sensor initialization success
- ✅ Real-time scanning when you place your finger
- ✅ Quality scores for each scan
- ✅ Successful authentication with good quality fingerprints

## Support

If you continue to experience issues:

1. Check that your fingerprint scanner is supported (FocalTech is supported)
2. Ensure all drivers are up to date
3. Verify Windows Biometric Service is running
4. Try running the application as Administrator

For additional help, refer to the project documentation or contact support.