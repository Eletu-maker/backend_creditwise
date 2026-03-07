# Security Setup Guide

## 🔒 Protecting Your Personal Information

This guide helps you set up the application securely without exposing personal information to Git.

---

## ✅ What's Already Protected

The following files are now in `.gitignore` and will NOT be committed:

- ✅ `.env` - Your environment variables
- ✅ `src/main/resources/application-dev.yml` - Development config
- ✅ `src/main/resources/application-prod.yml` - Production config  
- ✅ `docker-compose.yml` - Docker credentials
- ✅ `*postman_collection.json` - Postman collections with your data

---

## 🚀 Initial Setup (First Time)

### Step 1: Create Your Environment File

```bash
# Copy the example file
cp .env.example .env
```

### Step 2: Edit .env with Your Information

Open `.env` and replace the placeholder values:

```env
# Database Configuration
DB_URL=jdbc:h2:mem:testdb
DB_USERNAME=your_username
DB_PASSWORD=your_secure_password

# Mail Configuration
EMAIL_USERNAME=your_email@gmail.com
EMAIL_PASSWORD=your_gmail_app_password

# JWT Configuration
JWT_SECRET=your_super_secret_jwt_key_at_least_32_characters_long
```

### Step 3: Create Application Configuration

```bash
# Copy the example files
cp src/main/resources/application-dev.yml.example src/main/resources/application-dev.yml
cp src/main/resources/application-prod.yml.example src/main/resources/application-prod.yml
```

### Step 4: Create Docker Compose File (if using Docker)

```bash
# Copy the example file
cp docker-compose.yml.example docker-compose.yml
```

Then edit `docker-compose.yml` with your credentials.

### Step 5: Update Admin Email in Database Migration

Edit `src/main/resources/db/migration/V2__Insert_initial_data.sql`:

```sql
-- Change this line to use YOUR email
INSERT INTO users (..., email, ...) 
VALUES (..., 'your_email@gmail.com', ...);
```

---

## 🔐 Generating Secure Secrets

### Generate JWT Secret

```bash
# On Linux/Mac
openssl rand -base64 32

# On Windows (PowerShell)
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Maximum 256 }))
```

### Generate Strong Passwords

```bash
# On Linux/Mac
openssl rand -base64 16

# On Windows (PowerShell)
-join ((48..57) + (65..90) + (97..122) | Get-Random -Count 16 | ForEach-Object {[char]$_})
```

---

## 📋 Checklist Before Committing

Before you push code to Git, verify:

- [ ] `.env` file is NOT in your commit
- [ ] `application-dev.yml` is NOT in your commit
- [ ] `application-prod.yml` is NOT in your commit
- [ ] `docker-compose.yml` is NOT in your commit
- [ ] No personal emails in code files
- [ ] No passwords in code files
- [ ] No API keys in code files

### How to Check

```bash
# Check what files will be committed
git status

# Check if sensitive files are tracked
git ls-files | grep -E '\.env$|application-dev\.yml|application-prod\.yml|docker-compose\.yml'

# If any sensitive files appear, remove them from Git
git rm --cached .env
git rm --cached src/main/resources/application-dev.yml
git rm --cached src/main/resources/application-prod.yml
git rm --cached docker-compose.yml
```

---

## 🔄 For Team Members

When a team member clones the repository:

### Step 1: Copy Example Files

```bash
cp .env.example .env
cp src/main/resources/application-dev.yml.example src/main/resources/application-dev.yml
cp docker-compose.yml.example docker-compose.yml
```

### Step 2: Fill in Their Own Credentials

Each team member should use their own:
- Email address
- Gmail App Password
- Database credentials
- JWT secret

### Step 3: Never Share Credentials

- Don't share `.env` files via email/chat
- Don't commit real credentials to Git
- Use secure password managers for sharing if needed

---

## 🌍 Environment-Specific Configuration

### Development (.env)
```env
DB_URL=jdbc:h2:mem:testdb
EMAIL_USERNAME=your_dev_email@gmail.com
JWT_SECRET=dev_secret_key_32_chars_minimum
```

### Staging (.env.staging)
```env
DB_URL=jdbc:mysql://staging-db:3306/creditwise
EMAIL_USERNAME=staging@company.com
JWT_SECRET=staging_secret_key_different_from_dev
```

### Production (.env.production)
```env
DB_URL=jdbc:mysql://prod-db:3306/creditwise
EMAIL_USERNAME=noreply@company.com
JWT_SECRET=production_secret_key_very_long_and_secure
```

---

## 🚨 What to Do If You Accidentally Committed Secrets

### Step 1: Remove from Git History

```bash
# Remove the file from Git but keep it locally
git rm --cached .env

# Commit the removal
git commit -m "Remove sensitive file from Git"

# Push the changes
git push
```

### Step 2: Change All Exposed Credentials

If you pushed secrets to a public repository:

1. **Immediately change:**
   - Gmail App Password (revoke and create new)
   - Database passwords
   - JWT secret
   - Any API keys

2. **Update your local `.env` file** with new credentials

3. **Notify your team** if it's a shared repository

### Step 3: Clean Git History (Advanced)

If secrets are in Git history:

```bash
# Use BFG Repo-Cleaner or git-filter-branch
# WARNING: This rewrites history!

# Install BFG
# Download from: https://rtyley.github.io/bfg-repo-cleaner/

# Remove file from all commits
java -jar bfg.jar --delete-files .env

# Clean up
git reflog expire --expire=now --all
git gc --prune=now --aggressive

# Force push (WARNING: Affects all team members)
git push --force
```

---

## 📝 Best Practices

### DO ✅

- Use `.env` files for local development
- Use environment variables in production
- Use different credentials for dev/staging/prod
- Rotate secrets regularly
- Use strong, unique passwords
- Enable 2FA on all accounts
- Use Gmail App Passwords, not regular passwords
- Review `.gitignore` before committing

### DON'T ❌

- Commit `.env` files to Git
- Share credentials via email/chat
- Use the same password everywhere
- Hardcode secrets in source code
- Use weak or default passwords
- Commit API keys or tokens
- Push to public repos without checking

---

## 🔍 Scanning for Secrets

### Using git-secrets

```bash
# Install git-secrets
# Mac: brew install git-secrets
# Linux: Follow instructions at https://github.com/awslabs/git-secrets

# Set up git-secrets
git secrets --install
git secrets --register-aws

# Add custom patterns
git secrets --add 'password\s*=\s*.+'
git secrets --add '[a-zA-Z0-9]{16,}'

# Scan repository
git secrets --scan
```

### Using gitleaks

```bash
# Install gitleaks
# Mac: brew install gitleaks
# Linux: Download from https://github.com/zricethezav/gitleaks

# Scan repository
gitleaks detect --source . --verbose
```

---

## 📞 Need Help?

If you're unsure about security:

1. Check if file is in `.gitignore`
2. Run `git status` before committing
3. Review changes with `git diff`
4. Ask team lead if uncertain
5. When in doubt, don't commit!

---

## 🎓 Additional Resources

- [GitHub: Removing sensitive data](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/removing-sensitive-data-from-a-repository)
- [OWASP: Secrets Management](https://cheatsheetseries.owasp.org/cheatsheets/Secrets_Management_Cheat_Sheet.html)
- [Gmail App Passwords](https://support.google.com/accounts/answer/185833)
- [12 Factor App: Config](https://12factor.net/config)

---

## ✅ Verification

After setup, verify security:

```bash
# 1. Check .gitignore is working
git check-ignore .env
# Should output: .env

# 2. Check no secrets in staged files
git diff --cached | grep -i password
# Should return nothing

# 3. Check file permissions
ls -la .env
# Should be readable only by you (600 or 644)
```

---

**Remember: Security is everyone's responsibility! 🔒**
