# API Documentation

## Base URL
```
http://localhost:8080/api
```

## Authentication Endpoints

### POST /auth/scan
Initiate fingerprint scan

**Request:**
```json
{
  "userId": 1,
  "scanData": "base64_encoded_scan_data",
  "deviceInfo": "Windows Desktop",
  "ipAddress": "192.168.1.100"
}
```

**Response (Success 200):**
```json
{
  "success": true,
  "message": "Fingerprint scan completed successfully",
  "data": {
    "scanId": "SCAN-1702408974532847",
    "encryptedScanData": "base64_encrypted_data",
    "qualityScore": 87,
    "success": true,
    "message": "High quality scan captured"
  },
  "timestamp": "2024-12-12T14:30:00"
}
```

**Response (Error):**
```json
{
  "success": false,
  "message": "User not found",
  "data": null,
  "errorCode": "USER_NOT_FOUND",
  "timestamp": "2024-12-12T14:30:00"
}
```

---

### POST /auth/verify
Verify fingerprint and obtain JWT token

**Request:**
```json
{
  "userId": 1,
  "scanId": "SCAN-1702408974532847",
  "encryptedScanData": "base64_encrypted_data",
  "ipAddress": "192.168.1.100",
  "deviceInfo": "Windows Desktop"
}
```

**Response (Success 200):**
```json
{
  "success": true,
  "message": "Fingerprint verified successfully",
  "data": {
    "matched": true,
    "confidenceScore": 0.92,
    "message": "Fingerprint matched successfully",
    "jwtToken": "eyJhbGciOiJIUzUxMiJ9...",
    "userId": 1
  },
  "timestamp": "2024-12-12T14:30:00"
}
```

**Response (Verification Failed 401):**
```json
{
  "success": false,
  "message": "Fingerprint verification failed",
  "data": {
    "matched": false,
    "confidenceScore": 0.45,
    "message": "Fingerprint did not match"
  },
  "errorCode": "VERIFICATION_FAILED",
  "timestamp": "2024-12-12T14:30:00"
}
```

---

## User Endpoints

### GET /user/{id}
Get user profile information

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200):**
```json
{
  "success": true,
  "message": "User retrieved successfully",
  "data": {
    "id": 1,
    "username": "alice",
    "email": "alice@bioauth.com",
    "firstName": "Alice",
    "lastName": "Johnson",
    "role": "USER",
    "status": "ACTIVE",
    "createdAt": "2024-12-12T14:00:00",
    "biometricTemplateUpdatedAt": "2024-12-12T14:15:00"
  },
  "timestamp": "2024-12-12T14:30:00"
}
```

---

### PUT /user/{id}/template
Update user biometric template

**Request:**
```json
"base64_encrypted_template_data"
```

**Response (200):**
```json
{
  "success": true,
  "message": "Biometric template updated successfully",
  "data": {
    "id": 1,
    "username": "alice",
    "biometricTemplateUpdatedAt": "2024-12-12T14:35:00",
    ...
  },
  "timestamp": "2024-12-12T14:35:00"
}
```

---

### PUT /user/{id}/status/enable
Enable user account

**Response (200):**
```json
{
  "success": true,
  "message": "User enabled successfully",
  "data": {
    "id": 1,
    "status": "ACTIVE",
    ...
  },
  "timestamp": "2024-12-12T14:35:00"
}
```

---

### PUT /user/{id}/status/disable
Disable user account

**Response (200):**
```json
{
  "success": true,
  "message": "User disabled successfully",
  "data": {
    "id": 1,
    "status": "INACTIVE",
    ...
  },
  "timestamp": "2024-12-12T14:35:00"
}
```

---

## Admin Endpoints

### GET /admin/users
Get all users

**Response (200):**
```json
{
  "success": true,
  "message": "Users retrieved successfully",
  "data": [
    {
      "id": 1,
      "username": "alice",
      "email": "alice@bioauth.com",
      "firstName": "Alice",
      "lastName": "Johnson",
      "role": "USER",
      "status": "ACTIVE",
      "createdAt": "2024-12-12T14:00:00"
    },
    ...
  ],
  "timestamp": "2024-12-12T14:30:00"
}
```

---

### GET /admin/users/role/{role}
Get users by role (ADMIN or USER)

**Example:** `GET /admin/users/role/ADMIN`

**Response (200):**
```json
{
  "success": true,
  "message": "Users retrieved successfully",
  "data": [...],
  "timestamp": "2024-12-12T14:30:00"
}
```

---

### GET /admin/logs
Get all authentication logs

**Response (200):**
```json
{
  "success": true,
  "message": "Authentication logs retrieved successfully",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "timestamp": "2024-12-12T14:30:00",
      "outcome": "SUCCESS",
      "confidenceScore": 0.92,
      "ipAddress": "192.168.1.100",
      "deviceInfo": "Windows Desktop",
      "authenticationMethod": "BIOMETRIC"
    },
    ...
  ],
  "timestamp": "2024-12-12T14:30:00"
}
```

---

### GET /admin/logs/user/{userId}
Get authentication logs for specific user

**Response (200):**
```json
{
  "success": true,
  "message": "User authentication logs retrieved successfully",
  "data": [...],
  "timestamp": "2024-12-12T14:30:00"
}
```

---

### PUT /admin/user/{id}/status/enable
Admin: Enable user account

**Response (200):**
```json
{
  "success": true,
  "message": "User enabled successfully",
  "data": {...},
  "timestamp": "2024-12-12T14:30:00"
}
```

---

### PUT /admin/user/{id}/status/disable
Admin: Disable user account

**Response (200):**
```json
{
  "success": true,
  "message": "User disabled successfully",
  "data": {...},
  "timestamp": "2024-12-12T14:30:00"
}
```

---

### PUT /admin/user/{id}/status/lock
Admin: Lock user account

**Response (200):**
```json
{
  "success": true,
  "message": "User locked successfully",
  "data": {
    "id": 1,
    "status": "LOCKED",
    ...
  },
  "timestamp": "2024-12-12T14:30:00"
}
```

---

### GET /admin/stats
Get system statistics

**Response (200):**
```json
{
  "success": true,
  "message": "System statistics retrieved successfully",
  "data": {
    "totalUsers": 5,
    "totalAuthAttempts": 42,
    "activeUsers": 4
  },
  "timestamp": "2024-12-12T14:30:00"
}
```

---

## Error Codes

| Code | Status | Meaning |
|------|--------|---------|
| USER_NOT_FOUND | 404 | User does not exist |
| BAD_REQUEST | 400 | Invalid request format |
| FORBIDDEN | 403 | Account locked/inactive |
| VERIFICATION_FAILED | 401 | Fingerprint did not match |
| SCAN_ERROR | 500 | Scan operation failed |
| ERROR | 500 | General server error |

---

## Authentication

### JWT Token
Include in Authorization header:
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

Token contains:
- User ID
- Username
- Role (ADMIN/USER)
- Expiration (24 hours from issue)

### Role-Based Access

**USER Role:**
- GET /user/{id}
- PUT /user/{id}/template
- PUT /user/{id}/status/*

**ADMIN Role:**
- All USER endpoints plus:
- GET /admin/users
- GET /admin/users/role/*
- GET /admin/logs
- GET /admin/logs/user/*
- PUT /admin/user/{id}/status/*
- GET /admin/stats

---

## Data Types

### User Status
- ACTIVE
- INACTIVE
- LOCKED
- SUSPENDED

### Auth Outcome
- SUCCESS
- FAILED
- LOCKED_OUT
- SENSOR_ERROR

### User Role
- ADMIN
- USER

---

## Rate Limiting
Currently not implemented. Recommended for production.

## Pagination
Currently not implemented. Use limit/offset parameters for large result sets in production.

## Sorting
Currently returns results in creation order. Add sort parameters for production.
