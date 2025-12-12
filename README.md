# 🔐 Biometric Fingerprint Authentication System

A **production-ready**, full-stack biometric fingerprint authentication system with real-time scanning, REST API backend, and professional web UI.

![Status](https://img.shields.io/badge/Status-Production%20Ready-brightgreen) ![Version](https://img.shields.io/badge/Version-1.0.0-blue) ![License](https://img.shields.io/badge/License-MIT-green)

---

## 📋 Quick Navigation

- **🚀 [Quick Start](QUICKSTART.md)** - Get running in 5 minutes
- **📖 [Deployment Guide](DEPLOYMENT_GUIDE.md)** - Production setup instructions
- **🔧 [API Documentation](#api-documentation)** - REST API reference

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

## 🚀 Quick Start (5 Minutes)

### 1. Prerequisites
Check Java (need 11+) and Maven

### 2. Clone & Setup
```bash
cd Biometric-Fingerprint-Authentication-System
setup.bat  # Windows or setup.sh for Linux/Mac
```

### 3. Start Backend
```bash
java -jar backend/target/biometric-auth-api.jar
```

### 4. Open Frontend
Open in browser: file:///path/to/frontend/index.html

**API running at**: http://localhost:8080/v1

See [QUICKSTART.md](QUICKSTART.md) for detailed instructions.

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
- POST /v1/users/register
- GET /v1/users/{userId}
- DELETE /v1/users/{userId}

### Authentication
- POST /v1/auth/authenticate
- GET /v1/auth/verify

### Audit Logs
- GET /v1/logs/recent
- GET /v1/logs/user/{userId}
- GET /v1/logs/stats

---

## 🔐 Security Features

- ✅ AES-256 encryption for fingerprint templates
- ✅ JWT token-based authentication
- ✅ Complete audit trail
- ✅ Account lockout protection
- ✅ IP tracking and rate limiting

---

## 📱 Supported Hardware

| Scanner Model | Resolution | Status |
|---|---|---|
| Secugen Hamster Pro | 500x500 DPI | ✅ Recommended |
| Crossmatch L Scan | 500x500 DPI | ✅ Supported |
| Neurotechnology Verifinger | 500x500 DPI | ✅ Supported |
| NITGEN Esozen | 500x500 DPI | ✅ Supported |

---

## 🚀 Deployment

### Local Development
```bash
java -jar backend/target/biometric-auth-api.jar
```

### Docker
```bash
docker-compose up -d
```

See [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md) for production setup.

---

## 📝 Documentation

1. **[QUICKSTART.md](QUICKSTART.md)** - 5-minute setup guide
2. **[DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)** - Full deployment instructions
3. **[API_DOCUMENTATION.md](docs/API_DOCUMENTATION.md)** - Complete API reference

---

## 🧪 Testing

```bash
cd backend && mvn test
mvn clean package
java -jar target/biometric-auth-api.jar
```

---

## 📜 License

MIT License - see [LICENSE](LICENSE) file

---

## 📞 Support

- GitHub: [Open an issue](https://github.com/yourrepo/issues)
- Email: support@bioauth-system.local

---

**Version**: 1.0.0 | **Status**: ✅ Production Ready | **Last Updated**: January 2024

[🚀 Quick Start](QUICKSTART.md) • [📖 Full Guide](DEPLOYMENT_GUIDE.md) • [🔧 API Docs](docs/API_DOCUMENTATION.md)
