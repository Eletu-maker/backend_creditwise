# Payment & Subscription Setup Checklist

## Quick Setup Guide

### Step 1: Get Paystack Account
- [ ] Sign up at https://paystack.com
- [ ] Verify your email
- [ ] Complete business verification (for live mode)
- [ ] Get your API keys from Settings → API Keys

### Step 2: Configure Environment Variables
- [ ] Open `.env` file
- [ ] Add your Paystack secret key:
```env
PAYSTACK_SECRET_KEY=sk_test_your_key_here
PAYSTACK_API_URL=https://api.paystack.co
```

### Step 3: Run Database Migration
```bash
# The migration will run automatically on startup
# Or manually run:
mvn flyway:migrate
```

### Step 4: Start the Application
```bash
mvn spring-boot:run
```

### Step 5: Test the System

#### 5.1 Register a Test Client
```bash
POST http://localhost:8080/api/v1/auth/register
Content-Type: application/json

{
  "email": "testclient@example.com",
  "password": "password123",
  "firstName": "Test",
  "lastName": "Client",
  "phoneNumber": "08012345678"
}
```

#### 5.2 Login as Client
```bash
POST http://localhost:8080/api/v1/auth/login
Content-Type: application/json

{
  "email": "testclient@example.com",
  "password": "password123"
}
```
Save the JWT token from response.

#### 5.3 Initialize Payment
```bash
POST http://localhost:8080/api/v1/payments/initialize
Authorization: Bearer <client_jwt_token>
Content-Type: application/json

{
  "email": "testclient@example.com",
  "amount": 5000.00
}
```
Save the reference from response.

#### 5.4 Complete Payment
- Use Paystack test card:
  - Card: 4084084084084081
  - CVV: 408
  - Expiry: Any future date
  - PIN: 0000
  - OTP: 123456

#### 5.5 Verify Payment
```bash
POST http://localhost:8080/api/v1/payments/verify
Content-Type: application/json

{
  "reference": "<reference_from_step_5.3>"
}
```

#### 5.6 Check Subscription Status
```bash
GET http://localhost:8080/api/v1/payments/subscription/status
Authorization: Bearer <client_jwt_token>
```

#### 5.7 Admin Assigns Officer
```bash
POST http://localhost:8080/api/v1/admin/assign-officer-to-client?officerId=<officer_uuid>&clientId=<client_uuid>
Authorization: Bearer <admin_jwt_token>
```

### Step 6: Verify Everything Works
- [ ] Payment initialized successfully
- [ ] Payment verified successfully
- [ ] Subscription created with 1-month duration
- [ ] Admin can assign officer to subscribed client
- [ ] Admin CANNOT assign officer to non-subscribed client

---

## Common Issues & Solutions

### Issue: "Error initializing payment: Unauthorized"
**Cause**: Invalid or missing Paystack API key
**Solution**: 
1. Check `.env` file has correct `PAYSTACK_SECRET_KEY`
2. Ensure key starts with `sk_test_` (test mode) or `sk_live_` (live mode)
3. Restart application after updating .env

### Issue: "Client must have an active subscription"
**Cause**: Client hasn't completed payment or subscription expired
**Solution**:
1. Check subscription status: `GET /api/v1/payments/subscription/status`
2. If no subscription, complete payment flow
3. If expired, make new payment

### Issue: Payment verification fails
**Cause**: Payment not completed on Paystack or invalid reference
**Solution**:
1. Ensure payment was completed on Paystack page
2. Check reference is correct
3. Try verifying again (idempotent operation)

### Issue: Scheduled task not running
**Cause**: Scheduling not enabled
**Solution**:
1. Check `@EnableScheduling` annotation in `CreditwiseBackendApplication.java`
2. Restart application

---

## Testing Checklist

### Payment Flow
- [ ] Client can initialize payment
- [ ] Paystack returns valid reference
- [ ] Payment record created with PENDING status
- [ ] Client can complete payment on Paystack
- [ ] Payment verification updates status to SUCCESS
- [ ] Subscription is created/extended after successful payment

### Subscription Management
- [ ] New subscription created with 1-month duration
- [ ] Existing subscription extended by 1 month
- [ ] Subscription status is ACTIVE after payment
- [ ] `isActive()` method returns true for valid subscriptions
- [ ] Expired subscriptions return false for `isActive()`

### Access Control
- [ ] Admin CANNOT assign officer without subscription
- [ ] Admin CAN assign officer with active subscription
- [ ] Error message is clear when subscription missing
- [ ] Subscription check happens before assignment

### Scheduled Tasks
- [ ] Expiration task runs daily at midnight
- [ ] Expired subscriptions status changed to EXPIRED
- [ ] Logs show number of subscriptions deactivated

### API Endpoints
- [ ] All payment endpoints require authentication
- [ ] Only clients can access their own payment history
- [ ] Admin can view any payment by reference
- [ ] Subscription status endpoint works correctly

---

## Production Deployment Checklist

### Before Going Live
- [ ] Replace test Paystack key with live key
- [ ] Update `PAYSTACK_SECRET_KEY` in production .env
- [ ] Test with real (small amount) payment
- [ ] Set up Paystack webhooks for real-time updates
- [ ] Configure proper logging and monitoring
- [ ] Set up payment failure notifications
- [ ] Test subscription expiration process
- [ ] Document payment amounts and pricing
- [ ] Set up customer support for payment issues
- [ ] Implement payment receipt emails
- [ ] Add terms and conditions for subscriptions
- [ ] Test refund process (if applicable)

### Security Checklist
- [ ] Paystack keys stored in environment variables
- [ ] .env file in .gitignore
- [ ] HTTPS enabled for all payment endpoints
- [ ] JWT tokens properly validated
- [ ] Payment verification done server-side only
- [ ] Sensitive data not logged
- [ ] Rate limiting on payment endpoints
- [ ] CORS properly configured

---

## Quick Reference

### Default Subscription Settings
- **Duration**: 1 month
- **Currency**: NGN (Nigerian Naira)
- **Default Amount**: 5000 NGN
- **Auto-Renew**: Disabled (manual renewal)
- **Grace Period**: None

### Important Endpoints
```
POST   /api/v1/payments/initialize              - Initialize payment
POST   /api/v1/payments/verify                  - Verify payment
GET    /api/v1/payments/history                 - Payment history
GET    /api/v1/payments/subscription/status     - Check subscription
POST   /api/v1/payments/subscription/cancel     - Cancel subscription
POST   /api/v1/admin/assign-officer-to-client   - Assign officer (requires subscription)
```

### Test Credentials
```
Paystack Test Card: 4084084084084081
CVV: 408
PIN: 0000
OTP: 123456
```

---

## Support Resources

- **Paystack Documentation**: https://paystack.com/docs
- **Paystack Dashboard**: https://dashboard.paystack.com
- **Test Cards**: https://paystack.com/docs/payments/test-payments
- **API Reference**: https://paystack.com/docs/api

---

## Next Steps

After completing this checklist:
1. Review `PAYMENT_SUBSCRIPTION_GUIDE.md` for detailed documentation
2. Test all scenarios in `PAYMENT_SUBSCRIPTION_GUIDE.md`
3. Set up monitoring and alerts
4. Train admin users on the system
5. Prepare customer-facing documentation
6. Plan for production deployment

---

**Status**: ✅ Payment and subscription system fully implemented and ready for testing!
