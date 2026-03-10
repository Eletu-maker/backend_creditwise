# Message Assignment Validation Documentation

## Overview
This feature restricts messaging to only allow communication between officers and clients who are assigned to each other. Admin users can communicate with anyone.

## Implementation Details

### 1. AssignmentValidationService
A new service that validates messaging permissions based on assignment relationships.

**Key Methods:**
- `validateMessagePermission(UUID senderId, UUID receiverId)` - Throws UnauthorizedException if users are not properly assigned
- `areUsersAssigned(UUID userId1, UUID userId2)` - Returns boolean indicating if users are assigned

### 2. Validation Rules
1. **Admin users** can message anyone (no restrictions)
2. **Officers** can only message **Clients** they are assigned to
3. **Clients** can only message **Officers** they are assigned to
4. **Client-to-Client** or **Officer-to-Officer** communication is **not allowed**
5. Only **ACTIVE** assignments are considered valid

### 3. Integration Points

#### WebSocket Handler
Modified `WebSocketMessageHandler` to validate assignments before sending messages:
```java
// Validate that sender and receiver are assigned to each other
try {
    assignmentValidationService.validateMessagePermission(sender.getId(), receiverId);
} catch (UnauthorizedException e) {
    // Send error message back to sender
    MessageDto errorDto = new MessageDto();
    errorDto.setMessageText("Error: " + e.getMessage());
    messagingTemplate.convertAndSendToUser(sender.getId().toString(), "/queue/messages", errorDto);
    return;
}
```

#### REST Message Service
Modified `MessageServiceImpl` to include validation:
```java
// Validate that sender and receiver are assigned to each other (unless admin involved)
assignmentValidationService.validateMessagePermission(senderId, receiverId);
```

### 4. New REST Endpoints

#### Check Assignment Status (Admin Only)
```
GET /api/v1/messages/assignment/check/{userId1}/{userId2}
```
Returns whether two specific users are assigned to each other.

#### Check Messaging Permission (User)
```
GET /api/v1/messages/can-message/{otherUserId}
```
Returns whether the authenticated user can message the specified user.

## Usage Examples

### 1. WebSocket Messaging (Postman)
When using WebSocket to send messages, if users are not assigned:
- The message will not be delivered
- An error message will be sent back to the sender
- Error format: "Error: Messages can only be sent between assigned officers and clients"

### 2. REST API Calls
```bash
# Check if you can message a user
curl -X GET "http://localhost:8080/api/v1/messages/can-message/{userId}" \
  -H "Authorization: Bearer {your-jwt-token}"

# Admin checking assignment between two users
curl -X GET "http://localhost:8080/api/v1/messages/assignment/check/{userId1}/{userId2}" \
  -H "Authorization: Bearer {admin-jwt-token}"
```

## Testing

### Unit Tests
Run the AssignmentValidationServiceTest:
```bash
./mvnw test -Dtest=AssignmentValidationServiceTest
```

Run the MessageServiceConversationTest:
```bash
./mvnw test -Dtest=MessageServiceConversationTest
```

### Manual Testing Scenarios

1. **Valid Officer-Client Assignment**: Officer messages assigned client → ✅ Success
2. **Valid Client-Officer Assignment**: Client messages assigned officer → ✅ Success
3. **Invalid Assignment**: Officer messages unassigned client → ❌ Error
4. **Admin Communication**: Admin messages any user → ✅ Success
5. **Client-Client Communication**: Client messages another client → ❌ Error
6. **Officer-Officer Communication**: Officer messages another officer → ❌ Error

## Error Handling

When messaging is not allowed, users receive:
- **WebSocket**: Error message delivered to user's message queue
- **REST API**: 401 Unauthorized with descriptive message
- **Message Format**: "Messages can only be sent between assigned officers and clients"

## Database Requirements

The system requires:
- `officer_client_assignments` table with proper relationships
- ACTIVE assignment status for valid communication
- User roles properly set (OFFICER, CLIENT, ADMIN)