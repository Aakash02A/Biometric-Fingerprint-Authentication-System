# Developer Quick Reference - Windows Biometric Integration

## Project Structure

```
biometric-fingerprint-auth/
├── backend/
│   ├── pom.xml                                    [JNA dependencies added]
│   └── src/main/java/com/bioauth/
│       ├── BiometricAuthenticationSystem.java     [Uses SensorFactory now]
│       ├── config/
│       │   └── SystemConfig.java                  [SENSOR_TYPE added]
│       └── biometric/
│           ├── FingerprintSensor.java             [Interface - unchanged]
│           ├── SimulatedFingerprintSensor.java    [Test implementation]
│           ├── WindowsBiometricSensor.java        [NEW - Real sensor]
│           ├── SensorFactory.java                 [NEW - Factory]
│           └── WinBioAPI.java                     [NEW - JNA bindings]
└── docs/
    ├── WINDOWS_BIOMETRIC_INTEGRATION.md
    ├── WINDOWS_BIOMETRIC_QUICK_START.md
    ├── WINDOWS_FINGERPRINT_SETUP.md
    ├── ARCHITECTURE_DIAGRAMS.md
    ├── IMPLEMENTATION_SUMMARY.md
    └── CHANGELOG.md
```

---

## Key Classes

### SensorFactory
```java
// Get sensor (auto-detect)
FingerprintSensor sensor = SensorFactory.createSensor();

// Get Windows sensor (real)
FingerprintSensor realSensor = SensorFactory.createWindowsBiometricSensor();

// Get simulated sensor (test)
FingerprintSensor testSensor = SensorFactory.createSimulatedSensor();
```

### WindowsBiometricSensor
```java
FingerprintSensor sensor = new WindowsBiometricSensor();
sensor.initialize();
byte[] data = sensor.captureFingerprintImage();
int quality = sensor.getLastCaptureQuality();
sensor.shutdown();
```

### WinBioAPI
```java
// Check availability
if (WinBioAPI.isAvailable()) {
    // Use Windows API
}

// Get error message
String error = WinBioAPI.getErrorMessage(errorCode);
```

---

## Configuration

### Quick Config Change
**File:** `backend/src/main/java/com/bioauth/config/SystemConfig.java`

```java
// Auto-detect (default)
public static final String SENSOR_TYPE = "auto";

// Force real
public static final String SENSOR_TYPE = "windows";

// Force simulated (testing)
public static final String SENSOR_TYPE = "simulated";
```

---

## Build & Run

### Build
```bash
cd backend
mvn clean install -DskipTests
```

### Run
```bash
java -jar target/biometric-fingerprint-auth-1.0.0-jar-with-dependencies.jar
```

### Run with Maven
```bash
mvn exec:java -Dexec.mainClass="com.bioauth.SystemDemo"
```

---

## Common Tasks

### Check for Biometric Devices
```powershell
Get-PnpDevice | Where-Object {$_.Class -eq "Biometric"} | Select-Object Name, Manufacturer, Status
```

### Install Drivers
Visit manufacturer support page and download "Fingerprint" or "Biometric" driver.

### Test Biometric Framework
```powershell
Test-Path C:\Windows\System32\winbio.dll  # Should return True
```

### Debug Sensor Detection
```java
FingerprintSensor sensor = SensorFactory.createSensor();
System.out.println(sensor.getSensorInfo());
```

---

## API Reference

### FingerprintSensor Interface
```java
public interface FingerprintSensor {
    byte[] captureFingerprintImage() throws SensorException;
    int getLastCaptureQuality() throws SensorException;
    boolean isReady() throws SensorException;
    void initialize() throws SensorException;
    void shutdown() throws SensorException;
    String getSensorInfo() throws SensorException;
}
```

### BiometricAuthenticationSystem
```java
// Register
User user = system.registerUser(
    String userId,
    String username,
    String email,
    String department,
    byte[] fingerprintData
);

// Authenticate
AuthenticationResult result = system.authenticate(
    String userId,
    byte[] fingerprintData
);

// Capture from sensor
byte[] data = system.captureFingerprintFromSensor();
byte[] data = system.captureMultipleSamples(int numSamples);
```

---

## Quality Scoring

```
Quality Score (0-100):
├─ 0-50:   POOR (rejected)
├─ 50-70:  LOW (risky)
├─ 70-85:  GOOD (acceptable)
└─ 85-100: EXCELLENT (ideal)
```

### Check Quality
```java
byte[] fingerprint = sensor.captureFingerprintImage();
int quality = sensor.getLastCaptureQuality();
if (quality < 70) {
    // Low quality - ask user to retry
}
```

---

## Error Handling

### Catch Sensor Errors
```java
try {
    sensor.initialize();
    byte[] data = sensor.captureFingerprintImage();
} catch (SensorException e) {
    System.err.println("Sensor error: " + e.getMessage());
    // Fall back or notify user
}
```

### Check Sensor Ready
```java
if (!sensor.isReady()) {
    throw new SensorException("Sensor not ready");
}
```

---

## Debugging

### Enable Verbose Logging
Add to code:
```java
System.out.println("Sensor: " + sensor.getSensorInfo());
System.out.println("Device count: " + deviceCount);
System.out.println("Quality: " + quality);
```

### Check Windows Biometric Framework
```java
System.out.println("WBF Available: " + WinBioAPI.isAvailable());
```

---

## Dependencies

### Maven
```xml
<!-- JNA for Windows API access -->
<dependency>
    <groupId>net.java.dev.jna</groupId>
    <artifactId>jna</artifactId>
    <version>5.13.0</version>
</dependency>

<dependency>
    <groupId>net.java.dev.jna</groupId>
    <artifactId>jna-platform</artifactId>
    <version>5.13.0</version>
</dependency>
```

### Windows Requirements
- Windows 7 or later
- Fingerprint scanner + drivers (for real sensor)
- No additional software needed

---

## Testing

### Unit Tests
```java
@Test
public void testSensorInitialization() throws Exception {
    FingerprintSensor sensor = SensorFactory.createSensor();
    assertNotNull(sensor);
    assertNotNull(sensor.getSensorInfo());
}

@Test
public void testAutoDetection() throws Exception {
    FingerprintSensor sensor = SensorFactory.createSensor();
    assertTrue(sensor instanceof FingerprintSensor);
}
```

### Manual Testing
1. Run application
2. Check sensor type in output
3. Capture fingerprint
4. Verify quality score
5. Register and authenticate

---

## Performance Tips

1. **Reuse Session**
   ```java
   sensor.initialize();
   for (int i = 0; i < 3; i++) {
       sensor.captureFingerprintImage();  // Reuse
   }
   sensor.shutdown();
   ```

2. **Handle Timeouts**
   ```java
   // Capture takes up to 5 seconds
   try {
       byte[] data = sensor.captureFingerprintImage();
   } catch (SensorException e) {
       // Timeout - try again
   }
   ```

3. **Multi-Sample Capture**
   ```java
   // Get best of 3 captures
   byte[] data = system.captureMultipleSamples(3);
   ```

---

## Common Issues

### "No fingerprint sensors detected"
**Fix:** Install driver from manufacturer website

### "Windows Biometric Framework not available"
**Fix:** Requires Windows 7+; use simulated sensor for testing

### "Fingerprint quality too low"
**Fix:** Clean scanner, better finger placement, retry

### "Failed to open session"
**Fix:** Run as Administrator, close other biometric apps

---

## Windows API Reference

### WinBioOpenSession
Opens biometric session with device pool
```java
int WinBioOpenSession(
    int factor,           // WINBIO_TYPE_FINGERPRINT
    int poolType,         // WINBIO_POOL_SYSTEM
    int flags,            // 0
    PointerByReference sessionHandle
);
```

### WinBioCaptureSample
Captures fingerprint
```java
int WinBioCaptureSample(
    Pointer sessionHandle,
    int purpose,          // 0x01 = enrollment
    int flags,            // WINBIO_DATA_TYPE_RAW
    PointerByReference sample,
    PointerByReference sampleSize,
    PointerByReference rejectDetail
);
```

### WinBioEnumBiometricUnits
Lists available devices
```java
int WinBioEnumBiometricUnits(
    int factor,
    PointerByReference unitSchemaArray,
    PointerByReference unitCount
);
```

---

## Code Examples

### Basic Registration
```java
BiometricAuthenticationSystem system = 
    new BiometricAuthenticationSystem();

byte[] fingerprint = system.captureFingerprintFromSensor();
User user = system.registerUser(
    "user123",
    "John Doe",
    "john@example.com",
    "Engineering",
    fingerprint
);
System.out.println("Registered: " + user.getUsername());
```

### Basic Authentication
```java
byte[] fingerprint = system.captureFingerprintFromSensor();
AuthenticationResult result = 
    system.authenticate("user123", fingerprint);

if (result.isSuccess()) {
    System.out.println("Welcome!");
} else {
    System.out.println("Access denied");
}
```

### Force Real Sensor
```java
// Change in SystemConfig
public static final String SENSOR_TYPE = "windows";

// Rebuild and run
BiometricAuthenticationSystem system = 
    new BiometricAuthenticationSystem();
// Will use WindowsBiometricSensor or throw error
```

---

## Frequently Used Commands

```bash
# Build
mvn clean install -DskipTests

# Clean
mvn clean

# Run tests
mvn test

# Package
mvn package

# Check for Java 11
java -version

# Check Windows
systeminfo | find "OS"
```

---

## Resources

### Documentation Files
- `WINDOWS_BIOMETRIC_INTEGRATION.md` - Complete technical docs
- `WINDOWS_FINGERPRINT_SETUP.md` - Setup and installation
- `ARCHITECTURE_DIAGRAMS.md` - System diagrams
- `IMPLEMENTATION_SUMMARY.md` - Feature summary

### External Links
- [Windows Biometric Framework](https://docs.microsoft.com/windows/win32/secbiomet)
- [JNA Project](https://github.com/java-native-access/jna)
- [WinBio API Reference](https://docs.microsoft.com/windows/win32/api/winbio)

---

## Quick Checklist

- [ ] Dependencies in pom.xml
- [ ] SystemConfig.SENSOR_TYPE set
- [ ] BiometricAuthenticationSystem uses SensorFactory
- [ ] Build passes: `mvn clean install`
- [ ] Application runs: `java -jar ...jar`
- [ ] Sensor detects correctly
- [ ] Fingerprint captures successfully
- [ ] Quality scores show correctly
- [ ] Registration works
- [ ] Authentication works

---

## Support

For issues:
1. Check `WINDOWS_FINGERPRINT_SETUP.md` for setup
2. Review `WINDOWS_BIOMETRIC_INTEGRATION.md` for details
3. Check error messages in console
4. Run PowerShell diagnostics
5. Verify driver installation

---

**Last Updated:** December 2024
**Version:** 1.0.0
**Status:** Production Ready ✅
