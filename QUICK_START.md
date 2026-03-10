# Quick Start - WebSocket Testing in 5 Minutes

## Step 1: Login to Get Tokens (Postman)

### Admin Login
```http
POST http://localhost:8080/api/v1/auth/admin/login
Content-Type: application/json

{
    "email": "usmaneletu2@gmail.com",
    "password": "password"
}
```
**Copy the token from response**

### Officer Login
```http
POST http://localhost:8080/api/v1/auth/login
Content-Type: application/json

{
    "email": "john.officer@creditwise.com",
    "password": "password"
}
```
**Copy the token from response**

### Client Login
```http
POST http://localhost:8080/api/v1/auth/login
Content-Type: application/json

{
    "email": "jane.client@creditwise.com",
    "password": "password"
}
```
**Copy the token from response**

---

## Step 2: Create Assignment (Postman)

```http
POST http://localhost:8080/api/v1/admin/assign-officer-to-client?officerId=22222222-2222-2222-2222-222222222222&clientId=44444444-4444-4444-4444-444444444444
Authorization: Bearer YOUR_ADMIN_TOKEN
```

---

## Step 3: Test WebSocket (Browser)

### Open HTML Client
1. Open `websocket-test-client.html` in browser
2. Paste **Officer Token**
3. Click **Connect**

### Open Second Tab
1. Open same HTML file in new tab
2. Paste **Client Token**
3. Click **Connect**

### Send Messages
**Officer Tab:**
- Receiver ID: `44444444-4444-4444-4444-444444444444`
- Message: "Hello!"
- Click Send

**Client Tab:**
- Receiver ID: `22222222-2222-2222-2222-222222222222`
- Message: "Hi there!"
- Click Send

---

## Done! ✅

You should see messages appearing in both tabs in real-time.

## Default UUIDs
- **Officer:** `22222222-2222-2222-2222-222222222222`
- **Client:** `44444444-4444-4444-4444-444444444444`
- **Admin:** `11111111-1111-1111-1111-111111111111`

## Default Password
All accounts use: `password`
