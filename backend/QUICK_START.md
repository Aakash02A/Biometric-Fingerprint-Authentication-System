# Quick Start Guide

## 1. Prerequisites
- Java 11 or higher
- Maven 3.6+
- MySQL 8.0+

## 2. Database Setup

### Option A: Manual Setup
```sql
CREATE DATABASE bioauth_db;
USE bioauth_db;
```

### Option B: Docker (if available)
```bash
docker run -d \
  --name bioauth-mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=bioauth_db \
  -p 3306:3306 \
  mysql:8.0
```

## 3. Configure Application

Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bioauth_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
```

## 4. Build Project

```bash
cd backend
mvn clean install
```

## 5. Run Application

```bash
mvn spring-boot:run
```

Server starts at: `http://localhost:8080`

## 6. Verify Installation

Check health endpoint:
```bash
curl http://localhost:8080/api/auth/scan
```

## 7. Test Endpoints

### Login with admin credentials:
```bash
curl -X POST http://localhost:8080/api/auth/scan \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "deviceInfo": "Test Device", "ipAddress": "127.0.0.1"}'
```

### Get all users (requires ADMIN):
```bash
curl http://localhost:8080/api/admin/users
```

## 8. Sample Test Credentials

After application starts, these users are automatically created:

```
Admin:     admin / admin123
User 1:    alice / alice123
User 2:    bob / bob123
User 3:    carol / carol123
User 4:    david / david123 (inactive)
```

## 9. Common Issues

**MySQL Connection Error**
- Ensure MySQL service is running
- Check credentials in application.properties
- Verify database exists

**Port 8080 Already in Use**
- Change port in application.properties: `server.port=8081`

**Build Fails**
- Ensure Java 11+ is installed: `java -version`
- Clear Maven cache: `mvn clean`

## 10. Next Steps

- Check [README.md](README.md) for detailed documentation
- Review API endpoints documentation
- Explore controller classes for implementation details
- Check sample data in DataInitializationConfig.java
