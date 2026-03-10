# Testing OTP Admin Login in Postman

## Prerequisites
- Application running on `http://localhost:8080`
- Gmail App Password configured in `.env` file
- Admin email: `usmaneletu2@gmail.com`

---

## Step 1: Initiate OTP Login

### Request:
```http
POST http://localhost:8080/api/v1/auth/admin/initiate-otp-login
Content-Type: application/json

{
    "email": "usmaneletu2@gmail.com"
}
```

### Expected Response:
```json
{
    "success": true,
    "data": "123456",
    "message": "OTP sent to admin email"
}
```

**Note:** In development mode, the OTP code is returned in the response. In production, it should only be sent via email.

### What Happens:
1. System generates a 6-digit OTP code
2. OTP is saved to database with 5-minute expiry
3. Email is sent to `usmaneletu2@gmail.com`
4. OTP code is returned in response (dev mode only)

---

## Step 2: Check Your Email

1. Open your Gmail inbox for `usmaneletu2@gmail.com`
2. Look for email with subject: "Your OTP Code for Admin Login"
3. Copy the 6-digit OTP code from the email

**Email will look like:**
```
Hello,

You have requested to login to the admin panel. Please use the following OTP to complete your login:

┌─────────┐
│ 123456  │
└─────────┘

This OTP is valid for 5 minutes only. If you did not request this, please ignore this email.

Best regards,
The CreditWise Team
```

---

## Step 3: Verify OTP and Login

### Request:
```http
POST http://localhost:8080/api/v1/auth/admin/verify-otp-login
Content-Type: application/json

{
    "email": "usmaneletu2@gmail.com",
    "otpCode": "123456"
}
```

**Replace `123456` with the actual OTP from your email or the response from Step 1**

### Expected Response:
```json
{
    "success": true,
    "data": {
        "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c21hbmVsZXR1MkBnbWFpbC5jb20iLCJpYXQiOjE3MDk4MjM0NTYsImV4cCI6MTcwOTkwOTg1Nn0.abc123...",
        "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
        "user": {
            "id": "11111111-1111-1111-1111-111111111111",
            "firstName": "Admin",
            "lastName": "User",
            "email": "usmaneletu2@gmail.com",
            "role": "ADMIN",
            "enabled": true
        }
    },
    "message": "Admin authenticated successfully with OTP"
}
```

### What Happens:
1. System validates the OTP code
2. Checks if OTP is not expired (< 5 minutes old)
3. Checks if OTP hasn't been used before
4. Generates JWT token for authentication
5. Marks OTP as used
6. Returns authentication token

---

## Step 4: Use the Token

Copy the `token` value from the response and use it in subsequent requests:

### Example: Get All Clients
```http
GET http://localhost:8080/api/v1/admin/clients
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...YOUR_TOKEN_HERE
```

---

## Troubleshooting

### Issue 1: "User not found with email"
**Cause:** Email doesn't exist in database or typo in email

**Solution:**
- Verify email is exactly: `usmaneletu2@gmail.com`
- Check database has admin user with this email
- Restart application if you just changed the migration file

### Issue 2: "Access denied. You are not an admin"
**Cause:** User exists but role is not ADMIN

**Solution:**
- Check user role in database
- Ensure migration file has `role = 'ADMIN'`

### Issue 3: "Invalid or expired OTP"
**Cause:** OTP is wrong, expired (>5 minutes), or already used

**Solution:**
- Request a new OTP (Step 1)
- Use OTP within 5 minutes
- Each OTP can only be used once

### Issue 4: "Mail server connection failed"
**Cause:** Gmail App Password not configured or incorrect

**Solution:**
1. Check `.env` file has correct Gmail App Password
2. Verify `EMAIL_USERNAME` and `EMAIL_PASSWORD` are set
3. Test email configuration:
   ```http
   POST http://localhost:8080/api/v1/test/send-test-email?email=usmaneletu2@gmail.com
   ```

### Issue 5: Email not received
**Cause:** Email delay or in spam folder

**Solution:**
1. Check spam/junk folder
2. Wait 1-2 minutes (sometimes delayed)
3. Use OTP from API response (dev mode)
4. Check application logs for email errors

---

## Alternative: Password Login (Fallback)

If OTP is not working, you can use password login:

```http
POST http://localhost:8080/api/v1/auth/admin/login
Content-Type: application/json

{
    "email": "usmaneletu2@gmail.com",
    "password": "password"
}
```

**Default password:** `password`

---

## Testing Flow Summary

```
1. POST /initiate-otp-login
   ↓
2. Check email for OTP
   ↓
3. POST /verify-otp-login with OTP
   ↓
4. Receive JWT token
   ↓
5. Use token in Authorization header
```

---

## Security Notes

⚠️ **Important:**
- OTPs expire after 5 minutes
- Each OTP can only be used once
- Old OTPs are automatically cleaned up
- In production, don't return OTP in API response
- Always use HTTPS in production
- Rotate Gmail App Password regularly

---

## Quick Test Commands

### Test 1: Initiate OTP
```bash
curl -X POST http://localhost:8080/api/v1/auth/admin/initiate-otp-login \
  -H "Content-Type: application/json" \
  -d '{"email":"usmaneletu2@gmail.com"}'
```

### Test 2: Verify OTP
```bash
curl -X POST http://localhost:8080/api/v1/auth/admin/verify-otp-login \
  -H "Content-Type: application/json" \
  -d '{"email":"usmaneletu2@gmail.com","otpCode":"123456"}'
```

---

## Expected Timeline

- **OTP Generation:** < 1 second
- **Email Delivery:** 5-30 seconds
- **OTP Validity:** 5 minutes
- **Token Expiry:** 24 hours

---

**Happy Testing! 🚀**
