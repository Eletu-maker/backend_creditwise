# CreditWise Backend - Complete Feature Summary

## Overview
CreditWise is a comprehensive credit counseling and financial management platform that connects clients with credit officers for personalized credit improvement plans. The system provides real-time messaging, appointment scheduling, educational content, and credit monitoring capabilities.

## Technology Stack
- **Framework**: Spring Boot 3.3.4
- **Language**: Java 17+
- **Database**: PostgreSQL with Flyway migrations
- **Security**: JWT-based authentication with role-based access control
- **Real-time Communication**: WebSocket (STOMP protocol)
- **Email**: JavaMail with Mailtrap integration for testing
- **Build Tool**: Maven

## User Roles
1. **ADMIN** - System administrators with full access
2. **OFFICER** - Credit counseling officers who manage clients
3. **CLIENT** - End users seeking credit improvement services

---

## Core Features

### 1. Authentication & Authorization

#### Standard Login
- **Endpoint**: `POST /api/v1/auth/login`
- Email and password authentication
- JWT token generation with 24-hour expiration
- Role-based access control

#### Client Registration
- **Endpoint**: `POST /api/v1/auth/register`
- Self-service registration for new clients
- Automatic profile creation
- Email validation

#### Admin OTP Login
- **Initiate OTP**: `POST /api/v1/auth/admin/initiate-otp-login`
- **Verify OTP**: `POST /api/v1/auth/admin/verify-otp`
- 6-digit OTP sent via email
- 5-minute expiration window
- Automatic cleanup of expired OTPs (runs every 10 minutes)
- Secure admin access without password

#### Security Features
- JWT-based stateless authentication
- Password encryption with BCrypt
- CORS configuration for cross-origin requests
- Role-based method security with @PreAuthorize
- Audit logging for all entity changes

---

### 2. User Management

#### Admin Operations
- **Create Officer**: `POST /api/v1/admin/officers`
  - Set max active clients capacity (1-100)
  - Define specialization and bio
  - Automatic user account creation

- **List All Officers**: `GET /api/v1/admin/officers`
- **List All Clients**: `GET /api/v1/admin/clients`
- **Get User Profile**: `GET /api/v1/admin/users/{userId}`

#### Client Profile Management
- **Get My Profile**: `GET /api/v1/clients/profile`
- **Update Profile**: `PUT /api/v1/clients/profile`
- **Upload Credit Report**: `POST /api/v1/clients/credit-report`
- **Get Credit Health Score**: `GET /api/v1/clients/credit-health-score`

#### Officer Profile Management
- **Get My Profile**: `GET /api/v1/officers/profile`
- **Update Profile**: `PUT /api/v1/officers/profile`
- **Get My Clients**: `GET /api/v1/officers/my-clients`
- **Get Client Details**: `GET /api/v1/officers/clients/{clientId}`

---

### 3. Officer-Client Assignment System

#### Assignment Management
- **Assign Client to Officer**: `POST /api/v1/admin/assignments`
  - Validates officer capacity (max active clients)
  - Prevents duplicate active assignments
  - Throws `OfficerCapacityExceededException` if officer is at capacity

- **Get All Assignments**: `GET /api/v1/admin/assignments`
- **Get Officer's Assignments**: `GET /api/v1/admin/assignments/officer/{officerId}`
- **Get Client's Assignment**: `GET /api/v1/admin/assignments/client/{clientId}`
- **Unassign Client**: `DELETE /api/v1/admin/assignments/{assignmentId}`

#### Assignment Features
- One active assignment per client at a time
- Officer capacity enforcement
- Assignment status tracking (ACTIVE, COMPLETED, CANCELLED)
- Automatic validation before messaging and plan creation

---

### 4. Real-Time Messaging System

#### WebSocket Configuration
- **Connection Endpoint**: `ws://localhost:8080/ws`
- **Protocol**: STOMP over WebSocket
- **Authentication**: JWT token in handshake headers
- **Message Broker**: Simple in-memory broker

#### Messaging Features
- **Send Message**: `POST /api/v1/messages/send`
  - Text, file, or system messages
  - Validates officer-client assignment
  - Generates unique conversation IDs
  - Real-time delivery via WebSocket

- **Get Conversation**: `GET /api/v1/messages/conversation/{userId}`
  - Retrieves full message history between two users
  - Ordered by timestamp

- **Get Unread Messages**: `GET /api/v1/messages/unread`
- **Get Unread Count**: `GET /api/v1/messages/unread/count`
- **Mark as Read**: `PUT /api/v1/messages/{messageId}/read`

#### WebSocket Destinations
- **Subscribe**: `/user/queue/messages` - Receive personal messages
- **Send**: `/app/chat.send` - Send messages to assigned users
- **Broadcast**: `/topic/public` - Public announcements

#### Security
- JWT validation on WebSocket handshake
- Permission checks before message delivery
- Only assigned officers and clients can message each other

---

### 5. Appointment Scheduling

#### Appointment Management
- **Create Appointment**: `POST /api/v1/appointments`
  - Types: CONSULTATION, REVIEW, FOLLOW_UP, EMERGENCY
  - Validates officer-client assignment
  - Prevents scheduling in the past

- **Get My Appointments**: `GET /api/v1/appointments/my`
  - Returns appointments for current user (officer or client)

- **Get Appointment Details**: `GET /api/v1/appointments/{id}`
- **Update Appointment**: `PUT /api/v1/appointments/{id}`
- **Cancel Appointment**: `DELETE /api/v1/appointments/{id}`

#### Appointment Statuses
- SCHEDULED - Initial state
- CONFIRMED - Confirmed by both parties
- CANCELLED - Cancelled by either party
- COMPLETED - Appointment finished
- NO_SHOW - Client didn't attend

#### Features
- Notification tracking (notified flag)
- Notes field for post-appointment documentation
- Reason field for appointment context
- DateTime validation

---

### 6. Credit Plan Management

#### Plan Creation & Management
- **Create Credit Plan**: `POST /api/v1/plans`
  - Officers create personalized plans for assigned clients
  - Validates officer-client assignment
  - Statuses: DRAFT, ACTIVE, COMPLETED, ARCHIVED

- **Get My Plans** (Client): `GET /api/v1/plans/my`
- **Get Plan by ID**: `GET /api/v1/plans/{id}`
- **Get Client's Plans** (Officer): `GET /api/v1/plans/client/{clientId}`
- **Update Plan**: `PUT /api/v1/plans/{id}`

#### Plan Features
- Title and detailed description
- Plan status tracking
- Officer-client relationship enforcement
- Only assigned officers can create/update plans
- Clients can view their own plans

#### Client Plan Status
- PENDING - No plan started
- STARTED - Plan in progress
- COMPLETED - Plan finished

---

### 7. Educational Content System

#### Content Management (Admin Only)
- **Create Content**: `POST /api/v1/content`
- **Update Content**: `PUT /api/v1/content/{id}`
- **Delete Content**: `DELETE /api/v1/content/{id}` (soft delete)

#### Content Categories
- CREDIT_TIPS
- FINANCIAL_EDUCATION
- DEBT_MANAGEMENT
- CREDIT_MONITORING

#### Content Viewing (Public)
- **Get Content by ID**: `GET /api/v1/content/{id}`
  - Automatically increments view count
- **Browse Content**: `GET /api/v1/content`
  - Filter by category or content type
  - Pagination support (default: 10 per page)

#### Content Engagement
- **Like/Unlike**: `POST /api/v1/content/{id}/like`
- **Dislike/Undislike**: `POST /api/v1/content/{id}/dislike`
- **Get Like Status**: `GET /api/v1/content/{id}/like-status`
- **Get Dislike Status**: `GET /api/v1/content/{id}/dislike-status`
- **Get Like Count**: `GET /api/v1/content/{id}/like-count`

#### Content Statuses
- ACTIVE - Visible to users
- INACTIVE - Hidden but not deleted
- DELETED - Soft deleted

---

### 8. Comment System

#### Comment Operations
- **Create Comment**: `POST /api/v1/comments/content/{contentId}`
  - Available to both clients and officers
  - Attached to educational content

- **Get Comments**: `GET /api/v1/comments/content/{contentId}`
  - Pagination support
  - Ordered by creation date

- **Update Comment**: `PUT /api/v1/comments/{commentId}`
  - Only comment author can update

- **Delete Comment**: `DELETE /api/v1/comments/{commentId}`
  - Only comment author can delete

---

### 9. Credit Health Monitoring

#### Credit Score Tracking
- Stores historical credit scores
- Tracks score changes over time
- Associated with client profiles
- Enables trend analysis

#### Credit Report Management
- Upload credit reports
- Store report metadata
- Link reports to client profiles

---

### 10. Audit & Logging

#### Audit Logging
- **AuditLoggingAspect**: Automatic logging of all service method calls
- Logs method name, arguments, execution time, and results
- Captures exceptions and errors

#### Entity Auditing
- **BaseEntity**: All entities inherit audit fields
  - createdAt, updatedAt timestamps
  - createdBy, updatedBy user tracking
  - status field for soft deletes

- **AuditorAwareImpl**: Automatic population of audit fields
- **AuditLog Entity**: Stores detailed audit trail

---

## Database Schema

### Core Tables
1. **users** - User accounts with roles
2. **client_profiles** - Client-specific information
3. **officer_profiles** - Officer-specific information
4. **officer_client_assignments** - Assignment relationships
5. **messages** - Chat messages
6. **appointments** - Scheduled appointments
7. **credit_plans** - Personalized credit improvement plans
8. **plans** - Plan templates
9. **plan_assignments** - Plan-to-client assignments
10. **contents** - Educational content
11. **comments** - Content comments
12. **content_likes** - Content engagement tracking
13. **user_content_reactions** - User reactions to content
14. **credit_health_scores** - Credit score history
15. **uploaded_credit_reports** - Credit report storage
16. **otps** - OTP codes for admin login
17. **audit_logs** - System audit trail

### Key Relationships
- User → ClientProfile (1:1)
- User → OfficerProfile (1:1)
- Officer → Client (Many:Many through assignments)
- Officer → Client → Messages (validated by assignment)
- Officer → Client → Appointments (validated by assignment)
- Officer → Client → CreditPlans (validated by assignment)
- Content → Comments (1:Many)
- Content → Likes (1:Many)
- User → Comments (1:Many)

---

## API Response Format

All API responses follow a consistent format:

```json
{
  "success": true/false,
  "data": <response_data>,
  "message": "Description of result"
}
```

---

## Security Features

1. **JWT Authentication**
   - 24-hour token expiration
   - Stateless authentication
   - Role-based access control

2. **Password Security**
   - BCrypt encryption
   - Minimum password requirements

3. **OTP Security**
   - 6-digit random codes
   - 5-minute expiration
   - Single-use tokens
   - Automatic cleanup

4. **Authorization**
   - Method-level security with @PreAuthorize
   - Role-based endpoint access
   - Assignment validation for officer-client interactions

5. **Data Protection**
   - Environment variable configuration
   - Sensitive data in .gitignore
   - Template files for deployment

---

## Email Integration

### Configuration
- SMTP server: Mailtrap (for testing)
- Port: 2525
- TLS enabled
- Configurable via environment variables

### Email Features
- OTP delivery for admin login
- Appointment notifications (planned)
- System notifications (planned)

---

## Testing Tools Provided

1. **WebSocket Test Client** (`websocket-test-client.html`)
   - Browser-based WebSocket testing
   - JWT authentication
   - Message sending/receiving

2. **Postman Collection** (`CreditWise-WebSocket-Test.postman_collection.json`)
   - Complete API testing suite
   - Pre-configured requests
   - Environment variables

3. **Security Verification Scripts**
   - `verify-security.ps1` - Comprehensive security checks
   - `simple-check.ps1` - Quick security validation
   - `final-check.ps1` - Pre-commit verification

---

## Configuration Files

1. **Application Properties**
   - `application.properties` - Base configuration
   - `application-dev.yml` - Development settings
   - `application-prod.yml` - Production settings

2. **Environment Variables** (`.env`)
   - Database credentials
   - JWT secret
   - Email configuration
   - Server settings

3. **Docker Support**
   - `Dockerfile` - Application containerization
   - `docker-compose.yml` - Multi-container setup
   - PostgreSQL database container

---

## Key Business Rules

1. **Officer Capacity**
   - Officers have a maximum client capacity (1-100)
   - Cannot assign more clients than capacity allows
   - Throws exception when capacity exceeded

2. **Assignment Validation**
   - Only one active assignment per client
   - Officers can only message/create plans for assigned clients
   - Assignment required for appointments

3. **Message Permissions**
   - Only assigned officers and clients can message each other
   - WebSocket connections validated on handshake
   - Message delivery validated before sending

4. **Content Management**
   - Only admins can create/edit content
   - All users can view active content
   - Soft delete preserves content history

5. **Plan Management**
   - Only assigned officers can create plans for clients
   - Clients can only view their own plans
   - Plan status tracks progress

---

## Error Handling

### Custom Exceptions
- `ResourceNotFoundException` - Entity not found (404)
- `UnauthorizedException` - Access denied (403)
- `OfficerCapacityExceededException` - Officer at max capacity (400)

### Global Exception Handler
- Consistent error response format
- Detailed error messages
- HTTP status code mapping
- Validation error handling

---

## Scheduled Tasks

1. **OTP Cleanup**
   - Runs every 10 minutes
   - Deletes expired OTPs
   - Keeps database clean

---

## Documentation Provided

1. **QUICK_START.md** - Getting started guide
2. **MAILTRAP_OTP_TEST_GUIDE.md** - OTP testing instructions
3. **EMAIL_OTP_SETUP_GUIDE.md** - Email configuration
4. **POSTMAN_OTP_TEST_GUIDE.md** - Postman testing guide
5. **README_WEBSOCKET_TESTING.md** - WebSocket testing guide
6. **SECURITY_SETUP.md** - Security configuration
7. **SECURITY_CHECKLIST.md** - Pre-deployment security checks
8. **MESSAGE_ASSIGNMENT_VALIDATION.md** - Assignment validation details

---

## Summary of Capabilities

The CreditWise backend provides a complete platform for:

✅ User authentication with multiple methods (password, OTP)
✅ Role-based access control (Admin, Officer, Client)
✅ Officer-client assignment management with capacity control
✅ Real-time messaging between assigned users via WebSocket
✅ Appointment scheduling and management
✅ Personalized credit improvement plan creation
✅ Educational content management and engagement
✅ Comment system for content discussion
✅ Credit health score tracking
✅ Comprehensive audit logging
✅ Email notifications via Mailtrap
✅ Secure configuration management
✅ Docker containerization support
✅ Complete API documentation and testing tools

The system is production-ready with proper security measures, error handling, and comprehensive testing capabilities.
