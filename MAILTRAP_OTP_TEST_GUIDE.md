# Testing OTP with Mailtrap

##  Overview

Your application is now configured to send emails to **Mailtrap** instead of real email addresses. This is perfect for testing!

---

##  Mailtrap Configuration

**Current Settings:**
- **Host:** `sandbox.smtp.mailtrap.io`
- **Port:** `2525`
- **Username:** `db9f2c245f59ff`
- **Password:** `fb4b533b17ccbe`

**Admin Email in Database:** `usmaneletu2@gmail.com`

---

##  Testing Steps

### Step 1: Test Email Configuration

**Request:**
```http
POST http://localhost:8080/api/v1/test/send-test-email?email=test@example.com
```

**Expected Response:**
```json
{
    "success": true,
    "data": "Test email sent successfully",
    "message": "Test email sent to: test@example.com"
}
```

### Step 2: Initiate OTP Login

**Request:**
```http
POST http://localhost:8080/api/v1/auth/admin/initiate-otp-login
Content-Type: application/json

{
    "email": "usmaneletu2@gmail.com"
}
```

**Expected Response:**
```json
{
    "success": true,
    "data": "123456",
    "message": "OTP sent to admin email"
}
```

**Note:** The OTP code is returned in the response AND sent to Mailtrap.

### Step 3: Check Mailtrap Inbox

1. **Go to Mailtrap:** https://mailtrap.io/
2. **Login** with your Mailtrap account
3. **Go to Email Testing** → **Inboxes**
4. **Select your inbox** (the one with username `db9f2c245f59ff`)
5. **Check for the OTP email**

**You should see an email with:**
- **To:** `usmaneletu2@gmail.com`
- **Subject:** "Your OTP Code for Admin Login"
- **Body:** Contains the 6-digit OTP code

### Step 4: Verify OTP

**Request:**
```http
POST http://localhost:8080/api/v1/auth/admin/verify-otp-login
Content-Type: application/json

{
    "email": "usmaneletu2@gmail.com",
    "otpCode": "123456"
}
```

**Replace `123456` with:**
- The OTP from the API response (Step 2), OR
- The OTP from the Mailtrap email

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
    },
    "message": "Admin authenticated successfully with OTP"
}
```

---

##  Postman Collection

### Request 1: Test Email
```
Method: POST
URL: http://localhost:8080/api/v1/test/send-test-email?email=admin@test.com
Headers: (none)
Body: (none)
```

### Request 2: Initiate OTP
```
Method: POST
URL: http://localhost:8080/api/v1/auth/admin/initiate-otp-login
Headers: Content-Type: application/json
Body: 
{
    "email": "usmaneletu2@gmail.com"
}
```

### Request 3: Verify OTP
```
Method: POST
URL: http://localhost:8080/api/v1/auth/admin/verify-otp-login
Headers: Content-Type: application/json
Body:
{
    "email": "usmaneletu2@gmail.com",
    "otpCode": "REPLACE_WITH_ACTUAL_OTP"
}
```

---

##  Advantages of Mailtrap

✅ **No Real Emails:** Won't spam your inbox
✅ **Instant Delivery:** Emails appear immediately in Mailtrap
✅ **Email Preview:** See exactly how emails look
✅ **Testing Safe:** Can use any email address
✅ **Debug Info:** See email headers, HTML/text versions
✅ **No Limits:** Send as many test emails as needed

---

##  Troubleshooting

### Issue 1: "Test email sent successfully" but no email in Mailtrap

**Cause:** Wrong Mailtrap credentials or inbox

**Solution:**
1. Check Mailtrap credentials in `.env` file
2. Verify you're looking at the correct inbox
3. Check Mailtrap account is active

### Issue 2: "Mail server connection failed"

**Cause:** Mailtrap credentials incorrect

**Solution:**
1. Login to Mailtrap.io
2. Go to **Email Testing** → **Inboxes**
3. Click on your inbox
4. Copy the **SMTP Settings**:
   - Host: `sandbox.smtp.mailtrap.io`
   - Port: `2525`
   - Username: (your username)
   - Password: (your password)
5. Update `.env` file with correct credentials

### Issue 3: OTP email not appearing

**Cause:** Email might be in different inbox or delayed

**Solution:**
1. Refresh Mailtrap inbox
2. Check all inboxes in your Mailtrap account
3. Wait 10-15 seconds and refresh
4. Use OTP from API response instead

---

##  Quick Test Flow

```
1. POST /test/send-test-email
   ↓ (Check Mailtrap - should receive test email)
   
2. POST /initiate-otp-login
   ↓ (Check Mailtrap - should receive OTP email)
   
3. Copy OTP from Mailtrap or API response
   ↓
   
4. POST /verify-otp-login with OTP
   ↓ (Should get JWT token)
   
5. Use JWT token for authenticated requests
```

---

##  Expected Timeline

- **Email to Mailtrap:** < 5 seconds
- **OTP Generation:** < 1 second
- **OTP Validity:** 5 minutes
- **JWT Token Expiry:** 24 hours

---

##  Useful Links

- **Mailtrap Login:** https://mailtrap.io/signin
- **Mailtrap Docs:** https://help.mailtrap.io/
- **Your Inbox:** https://mailtrap.io/inboxes (after login)

---

##  Pro Tips

1. **Keep Mailtrap open** in another tab while testing
2. **Use any email address** - they all go to Mailtrap
3. **Check both HTML and Text** versions of emails
4. **Use the API response OTP** for faster testing
5. **Clear Mailtrap inbox** periodically to avoid clutter

---

**Happy Testing with Mailtrap! 📧✨**