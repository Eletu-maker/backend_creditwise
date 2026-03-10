# Gmail OTP Setup Guide for Admin Login

## Problem
You're getting this error:
```
Mail server connection failed. Couldn't connect to host, port: smtp.gmail.com, 587
```

## Solution: Set Up Gmail App Password

### Step 1: Enable 2-Factor Authentication on Gmail

1. Go to your Google Account: https://myaccount.google.com/
2. Click **"Security"** in the left sidebar
3. Under "How you sign in to Google", click **"2-Step Verification"**
4. Follow the prompts to enable 2FA (if not already enabled)
5. You'll need to verify with your phone

### Step 2: Generate App Password

1. After enabling 2FA, go back to **Security**
2. Under "How you sign in to Google", click **"App passwords"**
   - Direct link: https://myaccount.google.com/apppasswords
3. You may need to sign in again
4. In the "Select app" dropdown, choose **"Mail"**
5. In the "Select device" dropdown, choose **"Other (Custom name)"**
6. Type: **"CreditWise Backend"**
7. Click **"Generate"**
8. **Copy the 16-character password** (format: `abcd efgh ijkl mnop`)
   - Remove spaces when copying: `abcdefghijklmnop`

### Step 3: Update Your .env File

Open your `.env` file and update:

```env
# Mail Configuration
EMAIL_USERNAME=usmaneletu2@gmail.com
EMAIL_PASSWORD=abcdefghijklmnop
```

**Replace `abcdefghijklmnop` with your actual 16-character App Password (no spaces)**

### Step 4: Restart Your Application

Stop and restart your Spring Boot application to load the new configuration.

---

## Testing the Email Configuration

### Test 1: Send Test Email (Postman)

```http
POST http://localhost:8080/api/v1/test/send-test-email?email=usmaneletu2@gmail.com
```

**Expected Response:**
```json
{
    "success": true,
    "message": "Test email sent to: usmaneletu2@gmail.com"
}
```

**Check your email inbox** - you should receive a test email.

### Test 2: Initiate Admin OTP Login

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

**Note:** In development mode, the OTP code is returned in the response. In production, it should only be sent via email.

**Check your email** - you should receive an OTP email.

### Test 3: Verify OTP and Login

```http
POST http://localhost:8080/api/v1/auth/admin/verify-otp-login
Content-Type: application/json

{
    "email": "usmaneletu2@gmail.com",
    "otpCode": "123456"
}
```

**Replace `123456` with the actual OTP from your email or the response.**

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

## Troubleshooting

### Issue 1: "Invalid credentials" or "Authentication failed"

**Cause:** Wrong App Password or regular password used instead of App Password

**Solution:**
1. Make sure you're using the 16-character App Password, not your regular Gmail password
2. Remove all spaces from the App Password
3. Regenerate a new App Password if needed

### Issue 2: "Connection timeout"

**Cause:** Firewall or network blocking SMTP port 587

**Solution:**
1. Check if your firewall allows outbound connections on port 587
2. Try using port 465 with SSL instead:
   ```yaml
   mail:
     host: smtp.gmail.com
     port: 465
     properties:
       mail:
         smtp:
           auth: true
           ssl:
             enable: true
   ```

### Issue 3: "Username and Password not accepted"

**Cause:** 2FA not enabled or App Password not generated correctly

**Solution:**
1. Verify 2FA is enabled on your Google Account
2. Generate a new App Password
3. Make sure you're using the correct Gmail address

### Issue 4: OTP email not received

**Cause:** Email sent to spam or email service delay

**Solution:**
1. Check your spam/junk folder
2. Wait a few minutes (sometimes there's a delay)
3. Check the application logs for email sending errors
4. Use the test endpoint to verify email configuration

---

## Alternative: Use Console Logging for Development

If you can't get Gmail working, you can temporarily log OTPs to console:

### Create a Development Email Service

```java
@Service
@Profile("dev")
public class ConsoleEmailService implements EmailService {
    
    @Override
    public void sendOtpEmail(String to, String otpCode) {
        System.out.println("=================================");
        System.out.println("OTP EMAIL (Development Mode)");
        System.out.println("To: " + to);
        System.out.println("OTP Code: " + otpCode);
        System.out.println("=================================");
    }
    
    @Override
    public void sendEmail(String to, String subject, String text) {
        System.out.println("=================================");
        System.out.println("EMAIL (Development Mode)");
        System.out.println("To: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("=================================");
    }
}
```

This will print OTPs to your console instead of sending emails.

---

## Security Notes

⚠️ **Important Security Considerations:**

1. **Never commit App Passwords to Git**
   - Keep them in `.env` file
   - Add `.env` to `.gitignore`

2. **Production Configuration**
   - Use environment variables
   - Don't return OTP in API response
   - Implement rate limiting on OTP requests

3. **OTP Best Practices**
   - OTPs expire after 5 minutes (configurable)
   - OTPs are single-use only
   - Old OTPs are automatically cleaned up

---

## Complete Testing Flow

1. ✅ Generate Gmail App Password
2. ✅ Update `.env` file
3. ✅ Restart application
4. ✅ Test email with `/test/send-test-email`
5. ✅ Request OTP with `/auth/admin/initiate-otp-login`
6. ✅ Check email for OTP
7. ✅ Verify OTP with `/auth/admin/verify-otp-login`
8. ✅ Use admin token for WebSocket testing

---

## Quick Reference

**Gmail App Password URL:** https://myaccount.google.com/apppasswords

**Test Endpoint:** `POST /api/v1/test/send-test-email?email=YOUR_EMAIL`

**OTP Initiate:** `POST /api/v1/auth/admin/initiate-otp-login`

**OTP Verify:** `POST /api/v1/auth/admin/verify-otp-login`

**OTP Expiry:** 5 minutes (configurable in `application-dev.yml`)
