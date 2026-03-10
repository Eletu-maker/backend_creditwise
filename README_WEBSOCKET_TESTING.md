# WebSocket Testing - Complete Guide

## 📋 Overview

This guide helps you test the WebSocket messaging system between Officers and Clients in the CreditWise application.

## 🎯 What You'll Test

- ✅ Admin OTP login
- ✅ Officer and Client authentication
- ✅ Officer-Client assignment
- ✅ Real-time WebSocket messaging
- ✅ Permission validation

---

## 🚀 Quick Start (Choose Your Path)

### Option A: With OTP (Recommended for Production)
Follow: `OTP_QUICK_SETUP.md` (5 steps, ~10 minutes)

### Option B: Without OTP (Quick Testing)
Follow: `QUICK_START.md` (3 steps, ~5 minutes)

---

## 📁 Documentation Files

| File | Purpose | When to Use |
|------|---------|-------------|
| `OTP_QUICK_SETUP.md` | Set up Gmail OTP in 5 steps | First time setup |
| `EMAIL_OTP_SETUP_GUIDE.md` | Detailed email troubleshooting | If OTP not working |
| `QUICK_START.md` | Test without OTP setup | Quick testing |
| `WEBSOCKET_TESTING_GUIDE.md` | Complete WebSocket guide | Full documentation |
| `CreditWise-WebSocket-Test.postman_collection.json` | Postman requests | Import into Postman |
| `websocket-test-client.html` | Browser test client | WebSocket testing |

---

## 🔧 Setup Steps

### 1. Email Configuration (For OTP)

**Quick Setup:**
```bash
# 1. Get Gmail App Password from: https://myaccount.google.com/apppasswords
# 2. Update .env file:
EMAIL_PASSWORD=your-16-char-app-password

# 3. Restart application
```

**Detailed Guide:** See `OTP_QUICK_SETUP.md`

### 2. Import Postman Collection

1. Open Postman
2. Click **Import**
3. Select `CreditWise-WebSocket-Test.postman_collection.json`
4. Collection will appear in your workspace

### 3. Test Email (Optional but Recommended)

```http
POST http://localhost:8080/api/v1/test/send-test-email?email=usmaneletu2@gmail.com
```

---

## 🧪 Testing Flow

### Phase 1: Authentication

#### With OTP:
```
1. POST /api/v1/auth/admin/initiate-otp-login
   → Check email for OTP

2. POST /api/v1/auth/admin/verify-otp-login
   → Get admin token
```

#### Without OTP (Fallback):
```
1. POST /api/v1/auth/admin/login
   Body: { "email": "usmaneletu2@gmail.com", "password": "password" }
   → Get admin token
```

#### Officer & Client:
```
2. POST /api/v1/auth/login
   Body: { "email": "john.officer@creditwise.com", "password": "password" }
   → Get officer token

3. POST /api/v1/auth/login
   Body: { "email": "jane.client@creditwise.com", "password": "password" }
   → Get client token
```

### Phase 2: Create Assignment

```http
POST /api/v1/admin/assign-officer-to-client?officerId=22222222-2222-2222-2222-222222222222&clientId=44444444-4444-4444-4444-444444444444
Authorization: Bearer {adminToken}
```

### Phase 3: Test WebSocket

1. **Open `websocket-test-client.html` in browser**
2. **Tab 1:** Enter officer token → Connect
3. **Tab 2:** Enter client token → Connect
4. **Send messages** between tabs using UUIDs

---

## 📊 Default Test Accounts

| Role | Email | Password | UUID |
|------|-------|----------|------|
| Admin | usmaneletu2@gmail.com | password | 11111111-1111-1111-1111-111111111111 |
| Officer | john.officer@creditwise.com | password | 22222222-2222-2222-2222-222222222222 |
| Client | jane.client@creditwise.com | password | 44444444-4444-4444-4444-444444444444 |

---

## 🔍 Verification Checklist

- [ ] Email test successful (if using OTP)
- [ ] Admin login successful (OTP or password)
- [ ] Officer login successful
- [ ] Client login successful
- [ ] Assignment created successfully
- [ ] WebSocket connection established (Officer)
- [ ] WebSocket connection established (Client)
- [ ] Message sent from Officer to Client
- [ ] Message received by Client
- [ ] Message sent from Client to Officer
- [ ] Message received by Officer
- [ ] Error message when sending to unassigned user

---

## 🐛 Troubleshooting

### Email Issues
**Problem:** "Mail server connection failed"
**Solution:** See `EMAIL_OTP_SETUP_GUIDE.md` → Troubleshooting section

### WebSocket Issues
**Problem:** "Could not connect to ws://localhost:8080/ws"
**Solution:** 
1. Check application is running
2. Verify JWT token is valid
3. Check browser console for errors

### Authentication Issues
**Problem:** "Invalid or expired OTP"
**Solution:**
1. OTPs expire after 5 minutes
2. Request a new OTP
3. Use password login as fallback

### Assignment Issues
**Problem:** "Messages can only be sent between assigned officers and clients"
**Solution:**
1. Verify assignment exists: `GET /api/v1/admin/officer-client-assignments`
2. Create assignment if missing
3. Check using correct UUIDs

---

## 📞 API Endpoints Reference

### Authentication
```
POST /api/v1/auth/admin/initiate-otp-login
POST /api/v1/auth/admin/verify-otp-login
POST /api/v1/auth/admin/login (fallback)
POST /api/v1/auth/login
GET  /api/v1/auth/me
```

### Admin
```
POST /api/v1/admin/assign-officer-to-client
GET  /api/v1/admin/officer-client-assignments
POST /api/v1/admin/officers
GET  /api/v1/admin/officers
GET  /api/v1/admin/clients
```

### Testing
```
POST /api/v1/test/send-test-email
```

### WebSocket
```
WS   ws://localhost:8080/ws
STOMP /app/chat/{receiverId}
STOMP /user/queue/messages
```

---

## 🎓 Learning Resources

### Understanding the Code
- **WebSocket Config:** `src/main/java/com/creditwise/config/WebSocketConfig.java`
- **Message Handler:** `src/main/java/com/creditwise/config/WebSocketMessageHandler.java`
- **Auth Interceptor:** `src/main/java/com/creditwise/config/WebSocketAuthInterceptor.java`
- **Permission Validation:** `src/main/java/com/creditwise/service/AssignmentValidationService.java`

### Key Concepts
- **STOMP Protocol:** WebSocket messaging protocol
- **SockJS:** WebSocket fallback for older browsers
- **JWT Authentication:** Token-based security
- **User Queues:** `/user/{userId}/queue/messages`

---

## 🔐 Security Notes

⚠️ **Important:**
1. Never commit `.env` file to Git
2. Use App Passwords, not regular Gmail passwords
3. OTPs expire after 5 minutes
4. JWT tokens expire after 24 hours
5. In production, don't return OTP in API response

---

## ✅ Success Criteria

Your WebSocket system is working correctly when:

1. ✅ Admin can login with OTP
2. ✅ Officer and Client can login
3. ✅ Assignment can be created
4. ✅ Both users connect to WebSocket
5. ✅ Messages appear in real-time
6. ✅ Unassigned users get error messages
7. ✅ Messages persist in database

---

## 📝 Next Steps

After successful testing:

1. **Create more test users** using the admin endpoints
2. **Test multiple conversations** simultaneously
3. **Test error scenarios** (expired tokens, invalid UUIDs)
4. **Review message history** in database
5. **Implement frontend** using the HTML client as reference

---

## 🆘 Need Help?

1. Check the specific guide for your issue:
   - Email problems → `EMAIL_OTP_SETUP_GUIDE.md`
   - Quick testing → `QUICK_START.md`
   - Full details → `WEBSOCKET_TESTING_GUIDE.md`

2. Check application logs for detailed error messages

3. Use the test endpoint to verify email configuration

4. Use password login as fallback if OTP not working

---

## 📦 Files Included

```
├── OTP_QUICK_SETUP.md                          # 5-step OTP setup
├── EMAIL_OTP_SETUP_GUIDE.md                    # Detailed email guide
├── QUICK_START.md                              # Quick testing guide
├── WEBSOCKET_TESTING_GUIDE.md                  # Complete documentation
├── README_WEBSOCKET_TESTING.md                 # This file
├── CreditWise-WebSocket-Test.postman_collection.json  # Postman requests
└── websocket-test-client.html                  # Browser test client
```

---

**Happy Testing! 🚀**
