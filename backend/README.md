# BioAuth Backend - Biometric Fingerprint Authentication System

Complete Spring Boot REST API backend for biometric fingerprint authentication with JWT security, mock biometric sensor simulation, and role-based access control.

## Project Structure

```
backend/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/bioauth/api/
│   │   │   ├── BiometricAuthenticationApiApplication.java
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── UserController.java
│   │   │   │   └── AdminController.java
│   │   │   ├── service/
│   │   │   │   ├── UserService.java
│   │   │   │   ├── BiometricService.java
│   │   │   │   └── AuthLogService.java
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── BiometricTemplateRepository.java
│   │   │   │   └── AuthLogRepository.java
│   │   │   ├── model/
│   │   │   │   ├── User.java
│   │   │   │   ├── BiometricTemplate.java
│   │   │   │   ├── AuthLog.java
│   │   │   │   └── ApiResponse.java
│   │   │   ├── dto/
│   │   │   │   ├── ScanRequest.java
│   │   │   │   ├── ScanResponse.java
│   │   │   │   ├── VerifyRequest.java
│   │   │   │   ├── VerifyResponse.java
│   │   │   │   ├── UserDTO.java
│   │   │   │   └── AuthLogDTO.java
│   │   │   ├── security/
│   │   │   │   └── JwtTokenProvider.java
│   │   │   ├── utils/
│   │   │   │   ├── AESEncryptionUtil.java
│   │   │   │   ├── SecurityUtil.java
│   │   │   │   └── BiometricSensorAPI.java
│   │   │   └── config/
│   │   │       └── DataInitializationConfig.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/...
└── README.md
```

## Architecture

### Layered Architecture

```
┌─────────────────────────────────────────┐
│         REST Controllers                │
│  (AuthController, UserController, etc)  │
└────────────────────┬────────────────────┘
                     │
┌────────────────────▼────────────────────┐
│            Services Layer               │
│  (UserService, BiometricService, etc)  │
└────────────────────┬────────────────────┘
                     │
┌────────────────────▼────────────────────┐
│         Repository / Data Access        │
│  (JPA Repositories)                     │
└────────────────────┬────────────────────┘
                     │
┌────────────────────▼────────────────────┐
│          Database (MySQL)               │
└─────────────────────────────────────────┘
```

## Features

### Authentication & Security
- **JWT Token Authentication**: Secure token-based authentication
- **Role-Based Access Control**: ADMIN and USER roles
- **AES Encryption**: Biometric templates encrypted at rest
- **Password Hashing**: SHA-256 with salt for password security
- **CORS Configuration**: Enabled for cross-origin requests

### Biometric Flow
- **Fingerprint Scan Simulation**: Mock BiometricSensorAPI generates realistic scan data
- **Template Matching**: Simulated fingerprint matching with confidence scoring
- **Quality Scoring**: Generated quality scores (60-99) for each scan
- **Enrollment**: Support for biometric template enrollment and re-enrollment

### API Endpoints

#### Authentication Endpoints
```
POST /api/auth/scan
- Request: ScanRequest (userId, scanData, deviceInfo, ipAddress)
- Response: ScanResponse (scanId, encryptedScanData, qualityScore)

POST /api/auth/verify
- Request: VerifyRequest (userId, scanId, encryptedScanData, ipAddress)
- Response: VerifyResponse (matched, confidenceScore, jwtToken)
```

#### User Endpoints
```
GET /api/user/{id}
- Get user profile information

PUT /api/user/{id}/template
- Update user biometric template

PUT /api/user/{id}/status/enable
- Enable user account

PUT /api/user/{id}/status/disable
- Disable user account
```

#### Admin Endpoints
```
GET /api/admin/users
- List all users

GET /api/admin/users/role/{role}
- List users by role (ADMIN, USER)

GET /api/admin/logs
- Get all authentication logs

GET /api/admin/logs/user/{userId}
- Get logs for specific user

PUT /api/admin/user/{id}/status/enable
- Admin enable user

PUT /api/admin/user/{id}/status/disable
- Admin disable user

PUT /api/admin/user/{id}/status/lock
- Admin lock user account

GET /api/admin/stats
- System statistics
```

## Models

### User
```java
id: Long
username: String (unique)
email: String (unique)
passwordHash: String
firstName: String
lastName: String
encryptedBiometricTemplate: String
role: UserRole (ADMIN, USER)
status: UserStatus (ACTIVE, INACTIVE, LOCKED, SUSPENDED)
createdAt: LocalDateTime
updatedAt: LocalDateTime
biometricTemplateUpdatedAt: LocalDateTime
```

### BiometricTemplate
```java
id: Long
userId: Long
encryptedTemplateData: String
templateHash: String
qualityScore: Integer
metadata: String
createdAt: LocalDateTime
isActive: Boolean
```

### AuthLog
```java
id: Long
userId: Long
timestamp: LocalDateTime
outcome: AuthOutcome (SUCCESS, FAILED, LOCKED_OUT, SENSOR_ERROR)
confidenceScore: Double
ipAddress: String
deviceInfo: String
errorMessage: String
authenticationMethod: String
```

## Setup Instructions

### Prerequisites
- Java 11+
- Maven 3.6+
- MySQL 8.0+

### Database Setup

1. Create MySQL database:
```sql
CREATE DATABASE bioauth_db;
USE bioauth_db;
```

2. Update `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bioauth_db
spring.datasource.username=root
spring.datasource.password=your_password
```

### Build and Run

1. Clone/Navigate to backend directory:
```bash
cd backend
```

2. Build with Maven:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

Or:
```bash
java -jar target/biometric-authentication-api-1.0.0.jar
```

Application runs on: `http://localhost:8080`

## Sample Data

The application automatically initializes sample data on first run:

### Test Credentials
```
Admin User:
  Username: admin
  Password: admin123
  Role: ADMIN

Regular Users:
  alice / alice123
  bob / bob123
  carol / carol123
  david / david123 (INACTIVE)
```

All users have biometric templates pre-enrolled for testing.

## Configuration

### JWT Configuration
```properties
jwt.secret=BioAuthSystemSecretKeyForJWTTokenGenerationAndValidation2024
jwt.expiration=86400000 (24 hours in milliseconds)
```

### Database Configuration
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bioauth_db
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
```

## API Usage Examples

### 1. Scan Fingerprint
```bash
curl -X POST http://localhost:8080/api/auth/scan \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "scanData": "base64_encoded_scan_data",
    "deviceInfo": "Windows Desktop",
    "ipAddress": "192.168.1.100"
  }'
```

### 2. Verify Fingerprint
```bash
curl -X POST http://localhost:8080/api/auth/verify \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "scanId": "SCAN-1234567890",
    "encryptedScanData": "encrypted_base64_scan",
    "ipAddress": "192.168.1.100",
    "deviceInfo": "Windows Desktop"
  }'
```

### 3. Get User Profile
```bash
curl -X GET http://localhost:8080/api/user/1 \
  -H "Authorization: Bearer <jwt_token>"
```

### 4. Get All Users (Admin)
```bash
curl -X GET http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer <admin_jwt_token>"
```

### 5. Get Authentication Logs (Admin)
```bash
curl -X GET http://localhost:8080/api/admin/logs \
  -H "Authorization: Bearer <admin_jwt_token>"
```

## Security Features

### Encryption
- **AES-256**: Biometric templates encrypted before storage
- **SHA-256**: Password hashing with Base64 encoding
- **JWT Tokens**: Signed with HS512 algorithm

### Access Control
- Role-based endpoint protection (USER vs ADMIN)
- User can only access own profile (enforced in service layer)
- Admin endpoints for system-wide management

### Audit Trail
- All authentication attempts logged with:
  - User ID
  - Timestamp
  - Success/Failure status
  - Confidence score
  - IP address
  - Device information

## Mock Biometric Sensor API

The `BiometricSensorAPI` class simulates:
- `generateFakeScan()`: Generates random fingerprint scan data
- `generateTemplateFromScan()`: Extracts template from scan
- `matchTemplates()`: Matches two templates with confidence score (0.0-1.0)
- `generateQualityScore()`: Quality score 60-99
- `isHighQualityScan()`: Validates quality >= 75
- `isMatchConfident()`: Validates confidence >= 0.85

## Dependencies

- Spring Boot 2.7.14
- Spring Data JPA
- Spring Security
- MySQL Connector 8.0.33
- JWT (jjwt 0.9.1)
- Jackson (JSON processing)
- Lombok (optional)

## Status Codes

- `200`: Successful request
- `201`: Resource created
- `400`: Bad request
- `401`: Unauthorized
- `403`: Forbidden (account locked/inactive)
- `404`: Not found
- `500`: Server error

## Error Handling

All responses include standardized error format:
```json
{
  "success": false,
  "message": "Error message",
  "data": null,
  "errorCode": "ERROR_CODE",
  "timestamp": "2024-12-12T14:30:00"
}
```

## Performance Considerations

- Connection pooling configured for database
- Response compression enabled
- CORS caching configured
- JPA query optimization with proper indexing
- Transactional boundaries properly defined

## Logging

Configured logging levels:
- `ROOT`: INFO
- `com.bioauth.api`: DEBUG
- `org.springframework.web`: DEBUG
- `org.hibernate.SQL`: DEBUG

Logs include full SQL queries and method execution details for debugging.

## Testing

Run tests with:
```bash
mvn test
```

Integration tests included for:
- User registration and authentication
- Biometric scanning and verification
- Authorization and access control
- Error handling

## Production Deployment

For production:
1. Update JWT secret to a strong, random value
2. Use environment variables for sensitive config
3. Enable HTTPS
4. Configure proper CORS origins
5. Set `spring.jpa.hibernate.ddl-auto=validate`
6. Configure database backups
7. Set up monitoring and logging
8. Use connection pooling (HikariCP)
9. Enable request/response logging

## Troubleshooting

### Connection refused to MySQL
- Ensure MySQL is running
- Check database URL and credentials
- Verify MySQL is listening on correct port

### JWT token invalid
- Check token expiration time
- Verify JWT secret matches configuration
- Ensure token is in Authorization header

### User not found
- Verify user ID exists in database
- Check sample data was initialized

## License

Part of Biometric Fingerprint Authentication System

## Support

For issues and questions, refer to the main project documentation.
