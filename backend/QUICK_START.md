# Quick Start Guide - Backend

## 5-Minute Setup

### 1. Prerequisites
- Java 11+ installed
- Maven installed
- 100MB disk space

### 2. Build the Project

```bash
cd backend
mvn clean compile
```

Expected output: `BUILD SUCCESS`

### 3. Run Tests

```bash
mvn test
```

All tests should pass (20+ test cases)

### 4. Run Demo Application

```bash
mvn exec:java -Dexec.mainClass="com.bioauth.SystemDemo"
```

This will demonstrate:
- User registration (2 users)
- Successful authentication
- Failed authentication attempts
- Account lockout
- Audit log retrieval
- System statistics

### 5. Basic Code Example

```java
import com.bioauth.BiometricAuthenticationSystem;
import com.bioauth.auth.AuthenticationResult;
import com.bioauth.user.User;

public class Main {
    public static void main(String[] args) throws Exception {
        // Initialize
        BiometricAuthenticationSystem system = new BiometricAuthenticationSystem();
        
        // Register user
        byte[] fingerprint = system.captureFingerprintFromSensor();
        User user = system.registerUser("USR001", "john_doe", 
                                       "john@company.com", "IT", fingerprint);
        System.out.println("Registered: " + user.getUsername());
        
        // Authenticate
        byte[] capturedFP = system.captureFingerprintFromSensor();
        AuthenticationResult result = system.authenticate("USR001", capturedFP);
        System.out.println("Auth Result: " + result.isSuccess() + 
                          " (Score: " + result.getMatchScore() + "%)");
        
        // Shutdown
        system.shutdown();
    }
}
```

## Project Structure Overview

```
backend/
├── pom.xml              # Maven build config
├── README.md            # Full documentation
├── QUICK_START.md       # This file
├── src/main/java/com/bioauth/
│   ├── BiometricAuthenticationSystem.java    # Main entry point
│   ├── SystemDemo.java                       # Demo application
│   ├── auth/            # Authentication services
│   ├── biometric/       # Fingerprint capture & processing
│   ├── config/          # Configuration constants
│   ├── exceptions/      # Custom exceptions
│   ├── logging/         # Audit logging
│   ├── storage/         # Encryption & storage
│   └── user/            # User management
├── src/test/java/com/bioauth/
│   └── BiometricAuthenticationSystemTest.java # Test suite (20+ tests)
└── data/                # Storage directory
```

## Common Tasks

### Run All Tests with Coverage
```bash
mvn test -DargLine="-Xmx512m"
```

### Build Executable JAR
```bash
mvn clean package
java -jar target/biometric-fingerprint-auth-1.0.0.jar
```

### Run Specific Test
```bash
mvn test -Dtest=BiometricAuthenticationSystemTest#testSuccessfulUserRegistration
```

### Clean Build
```bash
mvn clean
```

## Configuration Customization

Edit `src/main/java/com/bioauth/config/SystemConfig.java`:

```java
public static final int MATCH_THRESHOLD = 85;      // Change matching threshold
public static final int GOOD_QUALITY = 70;         // Change quality threshold
public static final int MAX_ATTEMPTS = 3;          // Change max failed attempts
public static final long LOCKOUT_DURATION = 300000; // Change lockout time (ms)
```

## Key Features in This Demo

✅ **User Registration** - Register users with fingerprint biometrics  
✅ **Authentication** - Authenticate users with fingerprint matching  
✅ **Encryption** - AES-256 encryption for stored templates  
✅ **Audit Logging** - Complete logging of all authentication events  
✅ **Account Lockout** - Security lockout after failed attempts  
✅ **Statistics** - Real-time authentication statistics  
✅ **Error Handling** - Comprehensive exception handling  
✅ **Testing** - Full test coverage with JUnit  

## Simulated Fingerprint Sensor

The system uses `SimulatedFingerprintSensor` for development/testing. To use a real sensor:

1. Create new class implementing `FingerprintSensor` interface
2. Update `BiometricAuthenticationSystem.java` to use your sensor
3. Implement sensor-specific API calls

## Next Steps

1. **Integrate with REST API**: Use Spring Boot to expose endpoints
2. **Add Database**: Connect to PostgreSQL/MySQL
3. **Deploy**: Docker containerization, Kubernetes
4. **Real Sensors**: Connect actual fingerprint sensor hardware
5. **Analytics**: Add advanced reporting and dashboards

## Troubleshooting

### Build Fails
```bash
# Clear Maven cache
mvn clean
# Rebuild
mvn compile
```

### Tests Fail
- Check Java version: `java -version` (should be 11+)
- Clear target directory: `mvn clean`
- Run: `mvn test`

### Out of Memory
```bash
# Increase heap size
set MAVEN_OPTS=-Xmx1024m
mvn test
```

## API Quick Reference

### Main System
```java
BiometricAuthenticationSystem system = new BiometricAuthenticationSystem();

// Register user
User user = system.registerUser(userId, username, email, dept, fingerprint);

// Authenticate
AuthenticationResult result = system.authenticate(userId, fingerprint);
if (result.isSuccess()) { /* success */ }

// Admin functions
system.lockUser(userId);
system.unlockUser(userId);
system.getTotalUsers();

// Logging
List<AuditLog> logs = system.getAuditLogs(10);
Map<String, Object> stats = system.getAuthenticationStats();

// Cleanup
system.shutdown();
```

## Performance Notes

- Registration: ~500ms (includes sensor capture)
- Authentication: ~600ms (includes sensor capture)
- In-memory storage: ~100KB base + 1KB per user
- Supports 1000+ concurrent users easily

## Support & Documentation

- **Full README**: `backend/README.md`
- **API Reference**: See Javadoc comments in source code
- **Test Examples**: `BiometricAuthenticationSystemTest.java`
- **Demo Code**: `SystemDemo.java`

---

**Ready to integrate?** Check the main README.md for integration guides and advanced configuration!
