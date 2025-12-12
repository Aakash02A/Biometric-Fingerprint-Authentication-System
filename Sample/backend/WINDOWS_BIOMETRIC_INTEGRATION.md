# Windows Biometric Framework Integration

This document describes the Windows Biometric Framework (WBF) integration for real fingerprint sensor support.

## Overview

The system now supports real fingerprint capture from Windows-compatible fingerprint scanners using the Windows Biometric Framework API. The implementation uses JNA (Java Native Access) to interface with the native Windows APIs.

## Architecture

### Components

1. **WinBioAPI.java**
   - JNA bindings to Windows Biometric Framework API
   - Defines WinBio interface mapping to `winbio.dll`
   - Contains native method signatures for fingerprint operations
   - Handles error messages and constants

2. **WindowsBiometricSensor.java**
   - Implements the `FingerprintSensor` interface
   - Real fingerprint capture from hardware sensors
   - Device detection and enumeration
   - Session management with WBF
   - Quality score calculation from captured data

3. **SensorFactory.java**
   - Factory pattern for sensor creation
   - Auto-detects Windows OS and biometric framework availability
   - Falls back to simulated sensor if WBF not available
   - Configurable via `SystemConfig.SENSOR_TYPE`

4. **SystemConfig.java** (Updated)
   - `SENSOR_TYPE` configuration: "auto", "windows", or "simulated"
   - Default: "auto" (auto-detect)

## Configuration

### Setting Sensor Type

Edit `application.yml` or `application-production.yml`:

```yaml
# Use auto-detection (default)
biometric:
  sensor_type: auto

# Force Windows Biometric Framework
biometric:
  sensor_type: windows

# Force simulated sensor (for testing)
biometric:
  sensor_type: simulated
```

Or directly modify `SystemConfig.java`:

```java
public static final String SENSOR_TYPE = "auto";  // "auto", "windows", or "simulated"
```

## Requirements

### Hardware
- **Windows 7 or later**
- **Compatible fingerprint scanner** (USB or integrated)
- Common supported vendors:
  - Authentec
  - Validity
  - Biometric Security
  - Synaptics
  - SecuGen

### Software
- **Java 11+**
- **JNA 5.13.0+** (included in pom.xml)
- **Windows Biometric Framework** (part of Windows OS)

### Optional: Install Fingerprint Scanner Drivers
Some laptops require manufacturer-specific drivers:

**For Dell Laptops:**
```powershell
# Install from Dell Support website or Windows Update
# Usually available under Biometric Devices or Sensors
```

**For HP/Lenovo/ASUS:**
Check manufacturer's support website for fingerprint reader drivers

## Java Code Integration

### Basic Usage

```java
// The system auto-selects the sensor based on configuration
BiometricAuthenticationSystem system = new BiometricAuthenticationSystem();

// Register user with fingerprint
byte[] fingerprintData = system.captureFingerprintFromSensor();
User user = system.registerUser(userId, username, email, department, fingerprintData);

// Authenticate user
AuthenticationResult result = system.authenticate(userId, fingerprintData);
```

### Using Specific Sensor

```java
// Force Windows Biometric Framework
FingerprintSensor sensor = new WindowsBiometricSensor();
sensor.initialize();
byte[] fingerprintImage = sensor.captureFingerprintImage();
int quality = sensor.getLastCaptureQuality();
sensor.shutdown();

// Or use factory
FingerprintSensor sensor = SensorFactory.createSensor();  // Auto-detect
FingerprintSensor windowsSensor = SensorFactory.createWindowsBiometricSensor();  // Explicit
```

## Windows Biometric Framework API Details

### Key API Functions Used

**WinBioOpenSession**
- Opens a connection to the biometric framework
- Returns session handle for subsequent operations
- Pool type: WINBIO_POOL_SYSTEM (system pool for general use)

**WinBioCaptureSample**
- Captures fingerprint sample from the sensor
- Purpose: 0x01 (enrollment)
- Returns raw fingerprint data (typically 256+ bytes)

**WinBioEnumBiometricUnits**
- Enumerates available biometric devices
- Returns device information (manufacturer, model, serial)
- Used to verify sensor availability during initialization

**WinBioGetSensorStatus**
- Checks if sensor is ready for capture
- Returns status: READY, BUSY, NOT_CALIBRATED, etc.

**WinBioCloseSession**
- Closes biometric session and releases resources

## Quality Scoring

The fingerprint quality score (0-100) is calculated based on:

1. **Data Entropy** (0-30 points)
   - Measures variation in captured data
   - Higher entropy indicates better ridge distinction

2. **Transition Patterns** (0-20 points)
   - Counts byte value transitions
   - Good transitions indicate fingerprint ridge patterns

3. **Base Score** (50 points)
   - Minimum quality baseline

Quality Thresholds (from SystemConfig):
- **POOR_QUALITY: < 50** - Rejection likely, request retry
- **GOOD_QUALITY: 70** - Acceptable for authentication
- **EXCELLENT_QUALITY: 85** - Optimal for enrollment

## Error Handling

### Common Issues and Solutions

| Issue | Cause | Solution |
|-------|-------|----------|
| "No fingerprint sensors detected" | No compatible scanner installed | Install fingerprint scanner driver from manufacturer |
| "Windows Biometric Framework not available" | WBF not installed or Windows < 7 | Upgrade to Windows 7+ or update system files |
| "Failed to open biometric session" | Permission denied or device in use | Run as administrator; close other biometric apps |
| "Fingerprint quality too low" | Poor scanner contact or dirty surface | Clean scanner surface and retry capture |
| "Sensor timeout" | Capture took too long | Check sensor connection; try again |

### Error Messages

All errors are thrown as `SensorException` with detailed messages:

```java
try {
    sensor.initialize();
} catch (SensorException e) {
    System.err.println("Sensor error: " + e.getMessage());
    // Fall back to simulated sensor or manual capture
}
```

## Testing

### Check for Available Sensors (PowerShell)

```powershell
# List all biometric devices
Get-PnpDevice | Where-Object {$_.Class -eq "Biometric"} | Select-Object Name, Manufacturer

# Check Windows Biometric Framework
Test-Path C:\Windows\System32\winbio.dll

# Get sensor details
Get-WmiObject -Class Win32_PnPDevice | Where-Object {$_.Description -like "*biometric*"}
```

### Test Java Integration

```bash
# Build project
mvn clean package

# Run with Windows Biometric Framework (auto-detect)
java -jar target/biometric-fingerprint-auth-1.0.0-jar-with-dependencies.jar

# Output should show:
# "Windows Biometric Framework Sensor (Fingerprint) - Devices: 1"
```

### Fallback Testing

If no real sensor is available, the system automatically falls back to the simulated sensor:

```
Fingerprint capture failed: Windows Biometric Framework not available...
Using simulated sensor
```

## Development Notes

### JNA Configuration

JNA automatically detects and loads `winbio.dll` from `C:\Windows\System32\`.

To load from a custom path:
```java
WinBio lib = Native.load("custom/path/winbio.dll", WinBio.class);
```

### Extending the Implementation

To add support for other sensor types:

1. Create a new class implementing `FingerprintSensor` interface
2. Register in `SensorFactory.createSensor()`
3. Add configuration option to `SystemConfig`

### Building without Windows

On non-Windows systems, the JNA bindings gracefully fail and the system falls back to simulated sensors.

## Performance

- **Initialization**: ~200-500ms
- **Fingerprint Capture**: ~1000-5000ms (depends on sensor and user)
- **Feature Extraction**: ~100-200ms
- **Total Registration**: ~1.5-6 seconds per fingerprint

## Security Considerations

1. **Biometric Data Protection**
   - Raw fingerprint data is encrypted immediately after capture
   - Templates are stored encrypted in the database
   - Never log raw fingerprint data

2. **Session Management**
   - Sessions are closed immediately after use
   - No persistent sensor connections
   - Resources are properly released

3. **User Privacy**
   - Biometric data is never transmitted unencrypted
   - Audit logs record authentication attempts, not actual fingerprints
   - Compliant with biometric data protection regulations

## Future Enhancements

- [ ] Support for ISO/IEC standards compliance
- [ ] Multi-sensor support (multiple devices simultaneously)
- [ ] Liveness detection to prevent spoofing
- [ ] Cross-platform support (macOS, Linux with fingerprint readers)
- [ ] Integration with Windows Hello for Business
- [ ] Fingerprint quality pre-check before full capture

## References

- [Windows Biometric Framework Documentation](https://docs.microsoft.com/en-us/windows/win32/secbiomet/biometric-framework-api)
- [WinBio API Reference](https://docs.microsoft.com/en-us/windows/win32/api/winbio)
- [JNA Project](https://github.com/java-native-access/jna)
- [Windows 10/11 Biometric Services](https://docs.microsoft.com/en-us/windows/win32/secbiomet/overview)
