# CreditWise Backend

A production-ready Spring Boot REST API for a digital credit education platform.

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Setup](#setup)
- [Database Setup](#database-setup)
- [Running the Application](#running-the-application)
- [Testing](#testing)
- [API Endpoints](#api-endpoints)
- [Deployment](#deployment)

## Overview

CreditWise is a digital credit education platform that helps users understand and improve their credit health. The platform connects clients with credit officers who provide personalized guidance and credit improvement plans.

## Tech Stack

- Java 17+
- Spring Boot 3.x
- Spring Web (REST)
- Spring Data JPA
- Spring Security
- JWT Authentication (Access + Refresh tokens)
- MySQL (production)
- H2 (testing)
- Flyway (schema migrations)
- Lombok
- Bean Validation (Jakarta)
- OpenAPI / Swagger
- Spring Actuator (health, metrics)
- SLF4J + Logback
- Maven

## Architecture

The application follows a clean architecture with the following layers:

1. **Controller Layer**: REST endpoints
2. **Service Layer**: Business logic
3. **Repository Layer**: Data access
4. **Entity Layer**: Domain models
5. **Security Layer**: Authentication and authorization

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher (for production)
- Docker (optional, for containerization)

## Setup

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd creditwise-backend
   ```

2. Install dependencies:
   ```bash
   mvn clean install
   ```

## Database Setup

### For Development (H2 In-Memory Database)

The application uses H2 in-memory database by default in development mode. No additional setup is required.

### For Production (MySQL)

1. Create a MySQL database:
   ```sql
   CREATE DATABASE creditwise;
   ```

2. Update the database configuration in `src/main/resources/application-prod.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/creditwise
       username: your_username
       password: your_password
   ```

3. Set environment variables (recommended):
   ```bash
   export DB_URL=jdbc:mysql://localhost:3306/creditwise
   export DB_USERNAME=your_username
   export DB_PASSWORD=your_password
   export JWT_SECRET=your_super_secret_jwt_key
   ```

## Running the Application

### Development Mode

```bash
mvn spring-boot:run
```

Or with profiles:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Production Mode

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### Using JAR file

1. Build the application:
   ```bash
   mvn clean package
   ```

2. Run the JAR:
   ```bash
   java -jar target/creditwise-backend-0.0.1-SNAPSHOT.jar
   ```

## Testing

### Run Unit Tests

```bash
mvn test
```

### Run Integration Tests

```bash
mvn verify
```

### Run Tests with Coverage

```bash
mvn clean test jacoco:report
```

## API Endpoints

### Authentication

- `POST /api/v1/auth/register-client` - Register a new client
- `POST /api/v1/auth/login` - Login
- `POST /api/v1/auth/refresh-token` - Refresh access token
- `POST /api/v1/auth/logout` - Logout

### Admin

- `POST /api/v1/admin/officers` - Create officer
- `GET /api/v1/admin/stats` - Get platform statistics
- `POST /api/v1/admin/content` - Upload educational content

### Officer

- `GET /api/v1/officers/clients` - Get assigned clients
- `POST /api/v1/plans` - Create credit improvement plan
- `GET /api/v1/officers/credit-reports` - Get client credit reports

### Client

- `POST /api/v1/appointments` - Book appointment
- `POST /api/v1/credit-reports/upload` - Upload credit report
- `GET /api/v1/plans/my` - Get my credit plans

### Documentation

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Deployment

### Docker (Recommended)

1. Build the Docker image:
   ```bash
   docker build -t creditwise-backend .
   ```

2. Run the container:
   ```bash
   docker run -p 8080:8080 creditwise-backend
   ```

### Traditional Deployment

1. Build the JAR:
   ```bash
   mvn clean package
   ```

2. Run the application:
   ```bash
   java -jar target/creditwise-backend-0.0.1-SNAPSHOT.jar
   ```

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_URL` | Database URL | `jdbc:h2:mem:testdb` (dev) |
| `DB_USERNAME` | Database username | `sa` (dev) |
| `DB_PASSWORD` | Database password | `` (dev) |
| `JWT_SECRET` | JWT secret key | `mySecretKeyForDevelopmentOnlyDoNotUseInProduction` (dev) |

## Monitoring

- Health checks: `http://localhost:8080/actuator/health`
- Metrics: `http://localhost:8080/actuator/metrics`
- Info: `http://localhost:8080/actuator/info`

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a pull request

## License

This project is licensed under the MIT License.