# 🔐 Biometric Fingerprint Authentication System

A CompFull-stack biometric fingerprint authentication system with real-time scanning, REST API backend, and professional web UI.

![Status](https://img.shields.io/badge/Status-Production%20Ready-brightgreen) ![Version](https://img.shields.io/badge/Version-1.0.0-blue) ![License](https://img.shields.io/badge/License-MIT-green)

---

## ✨ Key Features

### 🖐️ Real Biometric Authentication
- USB fingerprint scanner integration (Secugen, Crossmatch, NITGEN, etc.)
- SourceAFIS fingerprint matching algorithm
- Quality validation (minimum 70% threshold)
- High-resolution capture (500x500 DPI)

### 👥 User Management
- User registration with encrypted fingerprint templates
- Account lockout after 3 failed attempts (5-minute lockout)
- User roles and department assignment
- Account activation/deactivation

### 🔒 Enterprise Security
- AES-256 encryption for fingerprint templates
- JWT token-based authentication
- HTTPS/TLS ready
- Complete audit trail with IP tracking
- Rate limiting and brute-force protection

### 🎨 Professional Interface
- Responsive web UI (HTML5/CSS3/JavaScript)
- User dashboard
- Admin controls
- Audit log viewer
- Real-time statistics

### 🚀 Production Ready
- Spring Boot REST API
- SQLite persistent database
- Docker containerization
- Comprehensive error handling
- Performance optimized

---

## 🏗️ System Architecture

Frontend (HTML/CSS/JS)
        ↓ REST API
Spring Boot API (Java)
        ↓ JDBC
SQLite Database
        ↓ USB
Fingerprint Scanner Hardware

---

## 📁 Project Structure

```
Biometric-Fingerprint-Authentication-System/
├── frontend/               # Web UI (HTML/CSS/JavaScript)
├── backend/                # Spring Boot REST API
├── data/                   # SQLite database
├── docker/                 # Docker configuration
├── docs/                   # Documentation
└── *.md                    # Guides
```

---

## 🛠️ Technology Stack

- **Frontend**: HTML5, CSS3, JavaScript ES6+
- **Backend**: Spring Boot 2.7.14
- **Database**: SQLite 3.43+
- **Biometric**: SourceAFIS 3.4.0
- **Security**: AES-256, JWT
- **DevOps**: Docker, Docker Compose
- **Java**: OpenJDK 11+

---

## 📡 API Endpoints

### User Management
- `POST /api/v1/users/register` - Register new user
- `GET /api/v1/users/{userId}` - Get user details
- `GET /api/v1/users` - Get all users
- `POST /api/v1/users/{userId}/lock` - Lock user account
- `POST /api/v1/users/{userId}/unlock` - Unlock user account
- `DELETE /api/v1/users/{userId}` - Delete user
- `GET /api/v1/users/stats/overview` - Get user statistics

### Authentication
- `POST /api/v1/auth/authenticate` - Authenticate user with fingerprint
- `GET /api/v1/auth/verify` - Verify authentication status

### Audit Logs
- `GET /api/v1/logs/recent?count={n}` - Get recent logs (default 10)
- `GET /api/v1/logs/user/{userId}` - Get logs by user
- `GET /api/v1/logs/events/{eventType}` - Get logs by event type
- `GET /api/v1/logs/all` - Get all logs
- `GET /api/v1/logs/stats` - Get log statistics

---

## 🔐 Security Features

### Data Protection
- ✅ AES-256 encryption for all stored fingerprint templates
- ✅ SHA-256 hashing for fingerprint pattern matching
- ✅ Secure key management with rotating master keys
- ✅ End-to-end encryption for data in transit

### Authentication Security
- ✅ JWT token-based session management
- ✅ Rate limiting to prevent brute-force attacks
- ✅ Automatic account lockout after failed attempts
- ✅ Multi-layer authentication verification

### Audit & Compliance
- ✅ Complete audit trail with timestamp and IP tracking
- ✅ GDPR-compliant data handling procedures
- ✅ Secure log retention and rotation policies
- ✅ Real-time monitoring of suspicious activities

---

## 📱 Supported Hardware

| Manufacturer | Model | Resolution | Status |
|---|---|---|---|
| Secugen | Hamster Pro | 500x500 DPI | ✅ Recommended |
| Crossmatch | L Scan | 500x500 DPI | ✅ Supported |
| Neurotechnology | Verifinger | 500x500 DPI | ✅ Supported |
| NITGEN | Esozen | 500x500 DPI | ✅ Supported |
| FocalTech | Various Models | 500x500 DPI | ✅ Supported |
| Digital Persona | U.are.U 4500 | 500x500 DPI | ✅ Supported |
| Fujitsu | PalmSecure | 500x500 DPI | ✅ Supported |

---
