# Windows Biometric Framework Integration - Quick Start

## What Was Changed

Your biometric authentication system now supports real fingerprint capture from Windows-compatible fingerprint scanners!

### New Files Created

1. **WinBioAPI.java** - JNA bindings to Windows Biometric Framework
2. **WindowsBiometricSensor.java** - Real fingerprint sensor implementation  
3. **SensorFactory.java** - Factory pattern for sensor selection
4. **WINDOWS_BIOMETRIC_INTEGRATION.md** - Complete documentation

### Files Modified

1. **SystemConfig.java** - Added `SENSOR_TYPE` configuration option
2. **BiometricAuthenticationSystem.java** - Updated to use `SensorFactory`
3. **pom.xml** - Added JNA dependencies for Windows API access

## How It Works

### Auto-Detection (Default)
```
System starts → Detects OS
└─ If Windows + WBF available → Uses WindowsBiometricSensor (REAL)
└─ Otherwise → Falls back to SimulatedFingerprintSensor (TEST)
```

### Configuration Options

In `SystemConfig.java`:
```java
public static final String SENSOR_TYPE = "auto";  // "auto", "windows", or "simulated"
```

## Using Real Fingerprint Sensors

### Prerequisites
- ✅ Windows 7 or later
- ✅ Compatible fingerprint scanner (USB or built-in)
- ✅ Scanner drivers installed
- ✅ Java 11+

### Checking for Available Sensors

Open PowerShell as Administrator:
```powershell
# Check for biometric devices
Get-PnpDevice | Where-Object {$_.Class -eq "Biometric"} | Select-Object Name

# Check for fingerprint reader drivers
Get-WmiObject -Class Win32_PnPDevice | Where-Object {$_.Description -like "*fingerprint*"} | Select-Object Description, Manufacturer
```

### Running the System

```bash
# Build
mvn clean package -DskipTests

# Run (will auto-detect and use real sensor if available)
java -jar target/biometric-fingerprint-auth-1.0.0-jar-with-dependencies.jar
```

**Output with real sensor:**
```
Biometric Authentication System initialized successfully
Sensor: Windows Biometric Framework Sensor (Fingerprint) - Devices: 1
Available fingerprint devices: 1
```

**Output without real sensor (fallback):**
```
Biometric Authentication System initialized successfully
Sensor: Simulated Fingerprint Sensor v1.0 (Development Mode)
```

## API Usage

### Register User with Real Fingerprint
```java
BiometricAuthenticationSystem system = new BiometricAuthenticationSystem();

// Capture real fingerprint from sensor
byte[] fingerprintData = system.captureFingerprintFromSensor();

// Register user
User user = system.registerUser(
    "user123",           // userId
    "John Doe",          // username
    "john@example.com",  // email
    "Engineering",       // department
    fingerprintData      // REAL fingerprint from sensor
);
```

### Authenticate User
```java
// Capture fingerprint
byte[] fingerprintData = system.captureFingerprintFromSensor();

// Authenticate
AuthenticationResult result = system.authenticate("user123", fingerprintData);
if (result.isSuccess()) {
    System.out.println("Authentication successful!");
}
```

## Quality Scoring

Captured fingerprints are automatically scored 0-100:
- **50-70**: Lower quality, may need retries
- **70-85**: Good quality, suitable for authentication
- **85-100**: Excellent quality, ideal for enrollment

## Troubleshooting

### "No fingerprint sensors detected"
- Install fingerprint scanner driver from laptop manufacturer
- Plug in USB scanner and install drivers
- Restart the application

### "Windows Biometric Framework not available"
- Upgrade to Windows 7 or later
- Update Windows to latest version
- Check System32/winbio.dll exists

### "Failed to open biometric session"
- Run Java application as Administrator
- Close any other biometric applications
- Check scanner connection (USB scanners)

### "Fingerprint quality too low"
- Clean the scanner surface
- Ensure full finger contact
- Retry capture

## Next Steps

1. **Install fingerprint scanner drivers** (if needed)
2. **Verify sensor detection** with PowerShell commands
3. **Build and test** the system with real hardware
4. **Monitor quality scores** during registration and authentication

## Detailed Documentation

See **WINDOWS_BIOMETRIC_INTEGRATION.md** for:
- Complete architecture details
- Windows API reference
- Security considerations
- Performance metrics
- Development guidelines
- Advanced configuration

## Key Features

✅ **Auto-detection** - Automatically uses real sensor when available  
✅ **Fallback support** - Falls back to simulated sensor if not available  
✅ **Graceful degradation** - Works on any Windows system  
✅ **Multi-device support** - Detects and enumerates all available sensors  
✅ **Quality validation** - Automatic quality scoring (0-100)  
✅ **Error handling** - Detailed error messages for troubleshooting  
✅ **JNA integration** - Direct Windows API access via Java  

## Security Notes

- Raw fingerprint data is encrypted immediately after capture
- All biometric templates are stored encrypted
- Sessions are properly closed and resources released
- No fingerprint data is logged or transmitted unencrypted
