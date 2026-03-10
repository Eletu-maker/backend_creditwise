# CreditWise Backend - Technologies Used

## Core Framework & Language

### Java Ecosystem
- **Java 17** - Programming language
- **Spring Boot 3.3.4** - Application framework
- **Maven** - Build automation and dependency management

---

## Spring Framework Modules

### Spring Boot Starters
1. **spring-boot-starter-web**
   - RESTful API development
   - Embedded Tomcat server
   - Spring MVC

2. **spring-boot-starter-data-jpa**
   - Object-Relational Mapping (ORM)
   - Hibernate implementation
   - Repository pattern support

3. **spring-boot-starter-security**
   - Authentication and authorization
   - Password encryption
   - Method-level security

4. **spring-boot-starter-validation**
   - Bean validation (JSR-380)
   - Request validation
   - Custom validators

5. **spring-boot-starter-websocket**
   - Real-time bidirectional communication
   - STOMP protocol support
   - WebSocket message handling

6. **spring-boot-starter-mail**
   - Email sending capabilities
   - SMTP integration
   - JavaMail API

7. **spring-boot-starter-actuator**
   - Application monitoring
   - Health checks
   - Metrics and endpoints

8. **spring-boot-starter-test**
   - JUnit 5
   - Mockito
   - Spring Test utilities

---

## Security & Authentication

### JWT (JSON Web Tokens)
- **jjwt-api 0.12.5** - JWT API
- **jjwt-impl 0.12.5** - JWT implementation
- **jjwt-jackson 0.12.5** - JSON processing for JWT

### Security Features
- **Spring Security** - Authentication & authorization framework
- **BCrypt** - Password hashing algorithm
- **Role-Based Access Control (RBAC)** - Permission management

---

## Database Technologies

### Relational Databases
1. **MySQL 8.0**
   - Primary production database
   - mysql-connector-j driver
   - Docker containerized

2. **H2 Database**
   - In-memory database
   - Development and testing
   - Embedded mode support

### Database Migration
- **Flyway Core** - Version control for database
- **Flyway MySQL** - MySQL-specific migrations
- SQL-based migration scripts

---

## API Documentation

### OpenAPI/Swagger
- **SpringDoc OpenAPI 2.6.0**
  - Interactive API documentation
  - Swagger UI interface
  - Automatic endpoint discovery
  - Request/response schema generation

---

## Development Tools

### Code Generation & Boilerplate Reduction
- **Lombok** - Annotation-based code generation
  - @Getter/@Setter
  - @Builder/@SuperBuilder
  - @NoArgsConstructor/@AllArgsConstructor
  - @Data/@Entity

### Aspect-Oriented Programming
- **Spring AOP (AspectJ)** - Cross-cutting concerns
  - Audit logging
  - Method interception
  - Transaction management

---

## Configuration Management

### Environment Configuration
- **dotenv-java 2.2.4** - Environment variable management
- **.env files** - Local configuration
- **Spring Profiles** - Environment-specific settings (dev, prod)
- **YAML Configuration** - application.yml files

---

## Containerization & DevOps

### Docker
- **Docker** - Application containerization
- **Docker Compose 3.8** - Multi-container orchestration
- **Dockerfile** - Container image definition

### Container Images
- **MySQL 8.0 Image** - Database container
- **Custom Java Application Image** - Spring Boot app container

---

## Email Services

### SMTP Integration
- **JavaMail API** - Email sending
- **Mailtrap** - Email testing service
- **SMTP Protocol** - Email delivery
- **TLS/SSL** - Secure email transmission

---

## Real-Time Communication

### WebSocket Stack
- **WebSocket Protocol** - Full-duplex communication
- **STOMP (Simple Text Oriented Messaging Protocol)** - Messaging protocol
- **SockJS** - WebSocket fallback support
- **In-Memory Message Broker** - Message routing

---

## Testing Technologies

### Testing Frameworks
1. **JUnit 5** - Unit testing framework
2. **Mockito** - Mocking framework
3. **Spring Security Test** - Security testing utilities
4. **Spring Boot Test** - Integration testing support

### Testing Tools
- **Postman** - API testing
- **WebSocket Test Client** - Real-time communication testing

---

## Data Persistence & ORM

### JPA/Hibernate
- **Jakarta Persistence API (JPA)** - ORM specification
- **Hibernate** - JPA implementation
- **Entity Relationships** - @OneToOne, @OneToMany, @ManyToOne, @ManyToMany
- **Lazy/Eager Loading** - Performance optimization
- **Cascade Operations** - Related entity management

---

## Validation & Data Integrity

### Bean Validation
- **Jakarta Validation (JSR-380)** - Validation API
- **Hibernate Validator** - Validation implementation
- Annotations: @NotNull, @NotBlank, @Email, @Min, @Max, @Size, @Valid

---

## Logging & Monitoring

### Logging
- **SLF4J** - Logging facade
- **Logback** - Logging implementation
- **Spring Boot Logging** - Auto-configured logging

### Audit Trail
- **Custom Audit Logging** - AOP-based logging
- **Entity Auditing** - @CreatedDate, @LastModifiedDate
- **AuditorAware** - User tracking

---

## Design Patterns & Architectural Patterns

### Patterns Implemented
1. **Repository Pattern** - Data access abstraction
2. **Service Layer Pattern** - Business logic separation
3. **DTO Pattern** - Data transfer objects
4. **Builder Pattern** - Object construction (Lombok @Builder)
5. **Dependency Injection** - Spring IoC container
6. **Aspect-Oriented Programming** - Cross-cutting concerns
7. **MVC Pattern** - Model-View-Controller architecture
8. **RESTful API Design** - Resource-based endpoints

---

## Security Protocols & Standards

### Security Standards
- **OAuth 2.0 Concepts** - Token-based authentication
- **JWT (RFC 7519)** - JSON Web Token standard
- **HTTPS/TLS** - Secure communication
- **CORS** - Cross-Origin Resource Sharing
- **CSRF Protection** - Cross-Site Request Forgery prevention

---

## Data Formats & Protocols

### Data Exchange
- **JSON** - Primary data format
- **XML** - Configuration files
- **YAML** - Application configuration
- **SQL** - Database queries and migrations

### Communication Protocols
- **HTTP/HTTPS** - RESTful API communication
- **WebSocket (RFC 6455)** - Real-time communication
- **STOMP** - Messaging protocol over WebSocket
- **SMTP** - Email transmission

---

## Build & Deployment

### Build Tools
- **Maven** - Dependency management and build automation
- **Maven Compiler Plugin** - Java compilation
- **Spring Boot Maven Plugin** - Executable JAR creation

### Deployment
- **Docker** - Containerization
- **Docker Compose** - Multi-container deployment
- **Embedded Tomcat** - Application server

---

## Version Control & Code Quality

### Version Control
- **Git** - Source code management
- **.gitignore** - File exclusion rules
- **.gitattributes** - Git attributes configuration

### Code Quality Tools
- **Lombok** - Reduces boilerplate code
- **Spring Boot DevTools** - Development productivity
- **Validation Annotations** - Input validation

---

## Additional Libraries & Utilities

### Utility Libraries
- **Jackson** - JSON processing (included with Spring Boot)
- **Commons Libraries** - Utility functions
- **Java Time API** - Date and time handling
- **UUID** - Unique identifier generation

---

## Development Environment

### Supported Platforms
- **Windows** - Primary development OS
- **Linux** - Docker containers
- **Cross-platform** - Java portability

### IDEs & Tools
- **Any Java IDE** - IntelliJ IDEA, Eclipse, VS Code
- **Postman** - API testing
- **Docker Desktop** - Container management
- **MySQL Workbench** - Database management (optional)

---

## Summary by Category

### Backend Framework
✅ Spring Boot 3.3.4 (Web, Security, Data JPA, WebSocket, Mail, Actuator, Validation)

### Language & Runtime
✅ Java 17, Maven

### Database
✅ MySQL 8.0, H2, Flyway migrations

### Security
✅ Spring Security, JWT (jjwt 0.12.5), BCrypt

### Real-Time Communication
✅ WebSocket, STOMP protocol

### API Documentation
✅ SpringDoc OpenAPI 2.6.0, Swagger UI

### Email
✅ JavaMail, Mailtrap SMTP

### Development Tools
✅ Lombok, Spring AOP, dotenv-java

### Testing
✅ JUnit 5, Mockito, Spring Test

### Containerization
✅ Docker, Docker Compose

### Configuration
✅ YAML, .env files, Spring Profiles

### Protocols
✅ HTTP/HTTPS, WebSocket, STOMP, SMTP, REST

### Data Formats
✅ JSON, XML, YAML, SQL

---

## Technology Stack Summary

**Total Technologies**: 50+ distinct technologies, frameworks, and tools integrated into a cohesive enterprise-grade application.

This is a modern, production-ready tech stack following industry best practices for security, scalability, and maintainability.
