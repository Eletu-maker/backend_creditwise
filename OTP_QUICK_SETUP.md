# Quick OTP Setup - 5 Steps

## Step 1: Get Gmail App Password (5 minutes)

1. Go to: https://myaccount.google.com/apppasswords
2. Sign in if needed
3. If you see "App passwords unavailable":
   - First enable 2-Step Verification: https://myaccount.google.com/signinoptions/two-step-verification
   - Then come back to App passwords
4. Select:
   - App: **Mail**
   - Device: **Other (Custom name)** → Type "CreditWise"
5. Click **Generate**
6. **Copy the 16-character password** (remove spaces)
   - Example: `abcd efgh ijkl mnop` → Copy as: `abcdefghijklmnop`

---

## Step 2: Update .env File

Open `.env` file in your project root and update:

```env
EMAIL_PASSWORD=abcdefghijklmnop
```

**Replace with your actual App Password from Step 1**

---

## Step 3: Restart Application

Stop and restart your Spring Boot application.

---

## Step 4: Test Email (Postman)

```http
POST http://localhost:8080/api/v1/test/send-test-email?email=usmaneletu2@gmail.com
```

**Check your email** - you should receive a test email within 1 minute.

✅ If received → Email is working!
❌ If not received → Check spam folder or review `EMAIL_OTP_SETUP_GUIDE.md`

---

## Step 5: Test OTP Login (Postman)

### 5A. Request OTP
```http
POST http://localhost:8080/api/v1/auth/admin/initiate-otp-login
Content-Type: application/json

{
    "email": "usmaneletu2@gmail.com"
}
```

**Response will include the OTP code** (in dev mode):
```json
{
    "success": true,
    "data": "123456",
    "message": "OTP sent to admin email"
}
```

**Also check your email** for the OTP.

### 5B. Verify OTP
```http
POST http://localhost:8080/api/v1/auth/admin/verify-otp-login
Content-Type: application/json

{
    "email": "usmaneletu2@gmail.com",
    "otpCode": "123456"
}
```

**Replace `123456` with your actual OTP**

**Success Response:**
```json
{
    "success": true,
    "data": {
        "token": "eyJhbGciOiJIUzI1NiJ9...",
        "user": {
            "role": "ADMIN"
        }
    }
}
```

---

## Done! ✅

You can now use OTP for admin login. The token from Step 5B is your admin JWT token for WebSocket testing.

---

## Troubleshooting

**Problem:** "Connection timeout" or "Mail server connection failed"

**Solution:**
1. Make sure you used the **App Password**, not your regular Gmail password
2. Remove all spaces from the App Password
3. Check `.env` file is in the project root
4. Restart the application after changing `.env`

**Problem:** "Invalid credentials"

**Solution:**
1. Regenerate a new App Password
2. Make sure 2FA is enabled on your Gmail account

**Problem:** Email not received

**Solution:**
1. Check spam/junk folder
2. Wait 2-3 minutes (sometimes delayed)
3. Try the test endpoint first to verify email is working

---

## Fallback Option

If you can't get Gmail working right now, you can use password login:

```http
POST http://localhost:8080/api/v1/auth/admin/login
Content-Type: application/json

{
    "email": "usmaneletu2@gmail.com",
    "password": "password"
}
```

This works immediately without email configuration.
