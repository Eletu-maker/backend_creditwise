# CreditWise Payment & Subscription System Guide

## Overview
This guide explains the complete payment and subscription architecture implemented in CreditWise. The system uses Paystack for payment processing and enforces subscription-based access to officer assignment services.

---

## Architecture Overview

### Key Components
1. **Payment System** - Handles payment initialization and verification via Paystack
2. **Subscription System** - Manages monthly subscriptions for clients
3. **Access Control** - Enforces subscription requirement for officer assignments
4. **Scheduled Tasks** - Automatically deactivates expired subscriptions

---

## How It Works

### 1. Client Registration
- Client registers via `/api/v1/auth/register`
- Account is created but NO officer is assigned yet
- Client cannot access officer services without subscription

### 2. Payment Flow

#### Step 1: Initialize Payment
**Endpoint**: `POST /api/v1/payments/initialize`

**Request**:
```json
{
  "email": "client@example.com",
  "amount": 5000.00
}
```

**Response**:
```json
{
  "success": true,
  "data": "ref_abc123xyz",
  "message": "Payment initialized successfully"
}
```

**What Happens**:
- System calls Paystack API to initialize payment
- Creates a payment record with status `PENDING`
- Returns Paystack reference for client to complete payment
- Client is redirected to Paystack payment page

#### Step 2: Complete Payment
- Client completes payment on Paystack's secure page
- Paystack processes the payment (card, bank transfer, USSD, etc.)

#### Step 3: Verify Payment
**Endpoint**: `POST /api/v1/payments/verify`

**Request**:
```json
{
  "reference": "ref_abc123xyz"
}
```

**Response**:
```json
{
  "success": true,
  "data": "Payment verified and subscription activated",
  "message": "Payment verified successfully"
}
```

**What Happens**:
- System calls Paystack API to verify payment status
- If payment is successful:
  - Updates payment record to `SUCCESS`
  - Creates/extends subscription for 1 month
  - Client can now be assigned to an officer

### 3. Subscription Activation
When payment is verified:
- **New Subscription**: Creates subscription from today to +1 month
- **Existing Subscription**: Extends end date by 1 month
- Subscription status set to `ACTIVE`

### 4. Officer Assignment (Admin Only)
**Endpoint**: `POST /api/v1/admin/assign-officer-to-client`

**Request**:
```
?officerId=uuid-here&clientId=uuid-here
```

**Validation**:
1. Checks if client has active subscription
2. If NO subscription → Returns 403 error
3. If subscription exists → Proceeds with assignment

**Response (No Subscription)**:
```json
{
  "success": false,
  "data": null,
  "message": "Client must have an active subscription before officer assignment"
}
```

### 5. Subscription Expiration
**Automatic Process** (Runs daily at midnight):
- Finds all subscriptions where `end_date < current_date`
- Changes status from `ACTIVE` to `EXPIRED`
- Client loses access to officer services
- Must renew subscription to continue

---

## API Endpoints

### Payment Endpoints

#### Initialize Payment
```
POST /api/v1/payments/initialize
Authorization: Bearer <client_jwt_token>
Content-Type: application/json

{
  "email": "client@example.com",
  "amount": 5000.00
}
```

#### Verify Payment
```
POST /api/v1/payments/verify
Content-Type: application/json

{
  "reference": "paystack_reference"
}
```

#### Get Payment History
```
GET /api/v1/payments/history
Authorization: Bearer <client_jwt_token>
```

#### Get Payment by Reference
```
GET /api/v1/payments/reference/{reference}
Authorization: Bearer <client_jwt_token>
```

### Subscription Endpoints

#### Check Subscription Status
```
GET /api/v1/payments/subscription/status
Authorization: Bearer <client_jwt_token>
```

**Response**:
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "userId": "uuid",
    "userEmail": "client@example.com",
    "startDate": "2026-03-08",
    "endDate": "2026-04-08",
    "subscriptionStatus": "ACTIVE",
    "autoRenew": false,
    "amount": 5000.00,
    "isActive": true
  },
  "message": "Subscription status retrieved successfully"
}
```

#### Cancel Subscription
```
POST /api/v1/payments/subscription/cancel
Authorization: Bearer <client_jwt_token>
```

---

## Database Schema

### Subscriptions Table
```sql
CREATE TABLE subscriptions (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    subscription_status VARCHAR(20) NOT NULL,
    auto_renew BOOLEAN DEFAULT FALSE,
    amount DOUBLE NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### Payments Table
```sql
CREATE TABLE payments (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    reference VARCHAR(255) UNIQUE NOT NULL,
    amount DOUBLE NOT NULL,
    payment_status VARCHAR(20) NOT NULL,
    payment_date TIMESTAMP,
    payment_method VARCHAR(50),
    currency VARCHAR(10) DEFAULT 'NGN',
    subscription_id VARCHAR(36),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (subscription_id) REFERENCES subscriptions(id)
);
```

---

## Configuration

### Environment Variables (.env)
```env
# Paystack Configuration
PAYSTACK_SECRET_KEY=sk_test_your_secret_key_here
PAYSTACK_API_URL=https://api.paystack.co
```

### Application Configuration (application-dev.yml)
```yaml
paystack:
  secret:
    key: ${PAYSTACK_SECRET_KEY:your_paystack_secret_key}
  api:
    url: ${PAYSTACK_API_URL:https://api.paystack.co}
```

---

## Paystack Integration

### Getting Paystack API Keys

1. **Sign up** at https://paystack.com
2. **Login** to dashboard: https://dashboard.paystack.com
3. **Navigate** to Settings → API Keys & Webhooks
4. **Copy** your Secret Key:
   - Test Key: `sk_test_...` (for development)
   - Live Key: `sk_live_...` (for production)

### Test Mode vs Live Mode
- **Test Mode**: Use test keys, no real money charged
- **Live Mode**: Use live keys, real transactions

### Test Cards (Development)
```
Card Number: 4084084084084081
CVV: 408
Expiry: Any future date
PIN: 0000
OTP: 123456
```

---

## Business Rules

### Subscription Rules
1. **Duration**: 1 month from payment date
2. **Renewal**: Manual (client must pay again)
3. **Grace Period**: None (expires immediately)
4. **Multiple Subscriptions**: System extends existing subscription

### Payment Rules
1. **Currency**: NGN (Nigerian Naira)
2. **Amount**: Configurable (default: 5000 NGN)
3. **Payment Methods**: All Paystack-supported methods
4. **Verification**: Required before subscription activation

### Access Control Rules
1. **No Subscription** → Cannot be assigned to officer
2. **Active Subscription** → Can be assigned to officer
3. **Expired Subscription** → Loses access to officer services
4. **Cancelled Subscription** → Immediate loss of access

---

## Testing the System

### Test Scenario 1: New Client Payment

1. **Register Client**
```bash
POST /api/v1/auth/register
{
  "email": "testclient@example.com",
  "password": "password123",
  "firstName": "Test",
  "lastName": "Client"
}
```

2. **Login Client**
```bash
POST /api/v1/auth/login
{
  "email": "testclient@example.com",
  "password": "password123"
}
```

3. **Initialize Payment**
```bash
POST /api/v1/payments/initialize
Authorization: Bearer <client_token>
{
  "email": "testclient@example.com",
  "amount": 5000.00
}
```

4. **Complete Payment on Paystack** (use test card)

5. **Verify Payment**
```bash
POST /api/v1/payments/verify
{
  "reference": "<reference_from_step_3>"
}
```

6. **Check Subscription Status**
```bash
GET /api/v1/payments/subscription/status
Authorization: Bearer <client_token>
```

7. **Admin Assigns Officer**
```bash
POST /api/v1/admin/assign-officer-to-client?officerId=<uuid>&clientId=<uuid>
Authorization: Bearer <admin_token>
```

### Test Scenario 2: Assignment Without Subscription

1. **Register Client** (without payment)
2. **Admin Tries to Assign Officer**
3. **Expected Result**: 403 Forbidden with message about subscription requirement

---

## Error Handling

### Common Errors

#### 1. No Active Subscription
```json
{
  "success": false,
  "data": null,
  "message": "Client must have an active subscription before officer assignment"
}
```

#### 2. Payment Verification Failed
```json
{
  "success": false,
  "data": null,
  "message": "Payment verification failed"
}
```

#### 3. Invalid Paystack Key
```json
{
  "success": false,
  "data": null,
  "message": "Error initializing payment: Unauthorized"
}
```

#### 4. Payment Not Found
```json
{
  "success": false,
  "data": null,
  "message": "Payment not found with reference: xyz"
}
```

---

## Scheduled Tasks

### Subscription Expiration Task
- **Schedule**: Daily at midnight (00:00)
- **Cron Expression**: `0 0 0 * * ?`
- **Action**: Deactivates expired subscriptions
- **Implementation**: `SubscriptionSchedulerServiceImpl`

---

## Security Considerations

1. **API Keys**: Never commit Paystack keys to Git
2. **Verification**: Always verify payments server-side
3. **Authorization**: Only clients can initialize their own payments
4. **Admin Only**: Only admins can assign officers
5. **Token Validation**: All endpoints require valid JWT

---

## Monitoring & Logging

### Payment Logs
```
INFO: Initializing payment for email: client@example.com with amount: 5000.0
INFO: Payment record created with reference: ref_abc123
INFO: Verifying payment with reference: ref_abc123
INFO: Payment verified and subscription activated for user: client@example.com
```

### Subscription Logs
```
INFO: Activating subscription for user: client@example.com
INFO: Created new subscription for user: client@example.com
INFO: Running scheduled task to deactivate expired subscriptions
INFO: Deactivated expired subscription for user: client@example.com
INFO: Deactivated 5 expired subscriptions
```

---

## Troubleshooting

### Issue: Payment initialization fails
**Solution**: Check Paystack API key in .env file

### Issue: Subscription not activated after payment
**Solution**: Check payment verification endpoint was called

### Issue: Admin can't assign officer
**Solution**: Verify client has active subscription

### Issue: Scheduled task not running
**Solution**: Ensure `@EnableScheduling` is present in main application class

---

## Future Enhancements

1. **Auto-Renewal**: Implement automatic subscription renewal
2. **Webhooks**: Add Paystack webhook for real-time payment updates
3. **Multiple Plans**: Support different subscription tiers
4. **Prorated Billing**: Handle mid-month subscriptions
5. **Payment Reminders**: Email notifications before expiration
6. **Refunds**: Implement refund processing
7. **Payment Analytics**: Dashboard for payment metrics

---

## Summary

The payment and subscription system ensures that:
- ✅ Clients must pay before accessing officer services
- ✅ Subscriptions are monthly and automatically expire
- ✅ Payments are securely processed via Paystack
- ✅ Admin cannot assign officers to non-subscribed clients
- ✅ System automatically manages subscription lifecycle
- ✅ All transactions are logged and auditable

This creates a sustainable business model where clients pay monthly for access to credit counseling services.
