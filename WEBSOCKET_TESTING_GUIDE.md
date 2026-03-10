# WebSocket Testing Guide - Step by Step

## Prerequisites
- Spring Boot application running on `http://localhost:8080`
- Postman installed
- Browser for HTML test client

## Default Test Accounts

Your database already has these test accounts:

| Role | Email | Password | UUID |
|------|-------|----------|------|
| Admin | usmaneletu2@gmail.com | password | 11111111-1111-1111-1111-111111111111 |
| Officer | john.officer@creditwise.com | password | 22222222-2222-2222-2222-222222222222 |
| Client | jane.client@creditwise.com | password | 44444444-4444-4444-4444-444444444444 |

---

## Phase 1: Setup and Authentication (Postman)

### Step 1: Login as Admin
```http
POST http://localhost:8080/api/v1/auth/admin/login
Content-Type: application/json

{
    "email": "usmaneletu2@gmail.com",
    "password": "password"
}
```

**Expected Response:**
```json
{
    "success": true,
    "data": {
        "token": "eyJhbGciOiJIUzI1NiJ9...",
        "refreshToken": "...",
        "user": {
            "id": "11111111-1111-1111-1111-111111111111",
            "email": "usmaneletu2@gmail.com",
            "role": "ADMIN"
        }
    }
}
```

**Action:** Copy the `token` value - this is your `{{adminToken}}`

---

### Step 2: Login as Officer
```http
POST http://localhost:8080/api/v1/auth/login
Content-Type: application/json

{
    "email": "john.officer@creditwise.com",
    "password": "password"
}
```

**Action:** Copy the `token` - this is your `{{officerToken}}`

---

### Step 3: Login as Client
```http
POST http://localhost:8080/api/v1/auth/login
Content-Type: application/json

{
    "email": "jane.client@creditwise.com",
    "password": "password"
}
```

**Action:** Copy the `token` - this is your `{{clientToken}}`

---

### Step 4: Assign Officer to Client
```http
POST http://localhost:8080/api/v1/admin/assign-officer-to-client?officerId=22222222-2222-2222-2222-222222222222&clientId=44444444-4444-4444-4444-444444444444
Authorization: Bearer {{adminToken}}
```

**Expected Response:**
```json
{
    "success": true,
    "message": "Officer assigned to client successfully"
}
```

---

### Step 5: Verify Assignment
```http
GET http://localhost:8080/api/v1/admin/officer-client-assignments
Authorization: Bearer {{adminToken}}
```

**Expected:** You should see the assignment between John Officer and Jane Client

---

## Phase 2: Test WebSocket with HTML Client (Recommended)

### Step 1: Open HTML Test Client
1. Open `websocket-test-client.html` in your browser
2. You'll see a simple interface with connection controls

### Step 2: Connect as Officer
1. **JWT Token field:** Paste the `{{officerToken}}` from Step 2 above
2. **Click "Connect"**
3. **Expected:** See "✅ Connected to WebSocket successfully"

### Step 3: Open Second Browser Tab
1. Open another tab with the same HTML file
2. **JWT Token field:** Paste the `{{clientToken}}` from Step 3 above
3. **Click "Connect"**
4. **Expected:** See "✅ Connected to WebSocket successfully"

### Step 4: Send Message from Officer to Client
**In Officer Tab:**
1. **Receiver ID:** `44444444-4444-4444-4444-444444444444` (Client UUID)
2. **Message:** "Hello from officer!"
3. **Click "Send Message"**

**Expected Result:**
- Officer tab shows the sent message
- Client tab receives the message

### Step 5: Send Message from Client to Officer
**In Client Tab:**
1. **Receiver ID:** `22222222-2222-2222-2222-222222222222` (Officer UUID)
2. **Message:** "Hello from client!"
3. **Click "Send Message"**

**Expected Result:**
- Client tab shows the sent message
- Officer tab receives the message

---

## Phase 3: Test Invalid Scenarios

### Test 1: Create Unassigned Client
```http
POST http://localhost:8080/api/v1/auth/register-client
Content-Type: application/json

{
    "firstName": "Bob",
    "lastName": "Unassigned",
    "email": "bob@test.com",
    "password": "password123",
    "phoneNumber": "5551234567",
    "dateOfBirth": "1990-01-01",
    "address": "456 Test Ave"
}
```

**Action:** Copy the new client's UUID from response

### Test 2: Try Sending to Unassigned Client
**In Officer Tab:**
1. **Receiver ID:** Paste the new client's UUID
2. **Message:** "This should fail"
3. **Click "Send Message"**

**Expected Result:**
```
Error: Messages can only be sent between assigned officers and clients
```

---

## Phase 4: Test WebSocket in Postman (Advanced)

### Step 1: Create WebSocket Request
1. In Postman: **New → WebSocket Request**
2. **URL:** `ws://localhost:8080/ws`
3. **Headers Tab:**
   - Key: `Authorization`
   - Value: `Bearer {{officerToken}}`
4. **Click "Connect"**

### Step 2: Send STOMP Connect Frame
In the message field, send:
```
CONNECT
Authorization:Bearer YOUR_OFFICER_TOKEN_HERE
accept-version:1.1,1.0
heart-beat:10000,10000

```
**Note:** Replace `YOUR_OFFICER_TOKEN_HERE` and ensure empty line at end

### Step 3: Subscribe to Messages
```
SUBSCRIBE
id:sub-0
destination:/user/queue/messages

```

### Step 4: Send Message
```
SEND
destination:/app/chat/44444444-4444-4444-4444-444444444444
content-type:application/json

{"messageText":"Hello from Postman!"}
```

---

## Troubleshooting

### Connection Failed (400 Error)
- **Cause:** Invalid or expired JWT token
- **Solution:** Get a fresh token by logging in again

### Connection Failed (403 Error)
- **Cause:** WebSocket endpoint not accessible
- **Solution:** Ensure `/ws/**` is in permitAll() in WebSecurityConfig

### Message Not Received
- **Cause:** Users not assigned to each other
- **Solution:** Run Step 4 in Phase 1 to create assignment

### Email Error (OTP)
- **Cause:** Gmail SMTP connection issue
- **Solution:** Use the new `/admin/login` endpoint with password instead

### Token Expired
- **Cause:** JWT tokens expire after 24 hours
- **Solution:** Login again to get fresh tokens

---

## Quick Test Commands

### Get Current User Info
```http
GET http://localhost:8080/api/v1/auth/me
Authorization: Bearer {{yourToken}}
```

### Check All Assignments
```http
GET http://localhost:8080/api/v1/admin/officer-client-assignments
Authorization: Bearer {{adminToken}}
```

### Create New Officer
```http
POST http://localhost:8080/api/v1/admin/officers
Authorization: Bearer {{adminToken}}
Content-Type: application/json

{
    "firstName": "Sarah",
    "lastName": "Officer",
    "email": "sarah@test.com",
    "password": "password123",
    "phoneNumber": "5559876543"
}
```

---

## Success Criteria

✅ Officer and Client can connect to WebSocket
✅ Messages sent from Officer appear in Client tab
✅ Messages sent from Client appear in Officer tab
✅ Unassigned users receive error messages
✅ Both users see message history in real-time

---

## Notes

- Default password for all test accounts: `password`
- JWT tokens expire after 24 hours (86400000 ms)
- WebSocket uses STOMP protocol over SockJS
- Messages are persisted in the database
- Admin can message anyone regardless of assignments
