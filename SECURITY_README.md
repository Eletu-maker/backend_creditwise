# 🔒 Security & Configuration Guide

## Quick Start

### For First Time Setup:
1. Read: `SECURITY_SETUP.md` (Complete guide)
2. Follow: `SECURITY_CHECKLIST.md` (Step-by-step)
3. Before each commit: Check `SECURITY_CHECKLIST.md`

### For Team Members Cloning the Repo:
```bash
# 1. Copy example files
cp .env.example .env
cp src/main/resources/application-dev.yml.example src/main/resources/application-dev.yml

# 2. Edit with your own credentials
nano .env  # or use your preferred editor

# 3. Never commit these files!
```

---

## 📁 File Structure

```
├── .env                          ❌ NEVER COMMIT (your credentials)
├── .env.example                  ✅ Commit (template)
├── docker-compose.yml            ❌ NEVER COMMIT (your credentials)
├── docker-compose.yml.example    ✅ Commit (template)
├── src/main/resources/
│   ├── application-dev.yml       ❌ NEVER COMMIT (your credentials)
│   ├── application-dev.yml.example ✅ Commit (template)
│   ├── application-prod.yml      ❌ NEVER COMMIT (your credentials)
│   └── application-prod.yml.example ✅ Commit (template)
```

---

## 🚨 What's Protected

Your `.gitignore` now protects:

- ✅ Environment files (`.env`, `.env.*`)
- ✅ Application configs with credentials
- ✅ Docker compose with passwords
- ✅ Personal Postman collections
- ✅ IDE settings
- ✅ Build artifacts
- ✅ Log files

---

## ⚡ Quick Commands

### Before Committing
```bash
# Check what will be committed
git status

# Verify sensitive files are ignored
git check-ignore .env
```

### If You Accidentally Committed Secrets
```bash
# Remove from Git (keeps local file)
git rm --cached .env
git commit -m "Remove sensitive file"
git push

# THEN: Change all exposed credentials immediately!
```

---

## 📚 Documentation

| File | Purpose |
|------|---------|
| `SECURITY_SETUP.md` | Complete setup guide with best practices |
| `SECURITY_CHECKLIST.md` | Quick checklist before every commit |
| `SECURITY_README.md` | This file - quick reference |

---

## 🔑 Required Credentials

You need to set up:

1. **Gmail App Password** (for OTP emails)
   - Get from: https://myaccount.google.com/apppasswords
   
2. **JWT Secret** (for authentication)
   - Generate: `openssl rand -base64 32`
   
3. **Database Password** (for production)
   - Generate: `openssl rand -base64 16`

---

## ✅ Verification

After setup, verify:

```bash
# 1. Check .gitignore is working
git check-ignore .env
# Output: .env ✅

# 2. Check no secrets in staged files
git diff --cached | grep -i password
# Output: (nothing) ✅

# 3. Check application runs
./mvnw spring-boot:run
# Should start without errors ✅
```

---

## 🆘 Need Help?

1. **Setup issues:** See `SECURITY_SETUP.md`
2. **Before commit:** Check `SECURITY_CHECKLIST.md`
3. **Exposed secrets:** Follow emergency steps in `SECURITY_CHECKLIST.md`

---

## 🎯 Remember

- **NEVER** commit `.env` files
- **ALWAYS** use `.env.example` as template
- **CHANGE** default passwords
- **ROTATE** secrets regularly
- **VERIFY** before pushing to Git

---

**Security is not optional! 🔒**
