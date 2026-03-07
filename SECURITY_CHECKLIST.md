# Security Checklist ✅

## Before Every Git Commit

Run through this checklist:

### 1. Check Staged Files
```bash
git status
```
**Verify:** No `.env`, `application-*.yml`, or `docker-compose.yml` files

### 2. Search for Personal Information
```bash
# Search for your email
git diff --cached | grep -i "your_email@gmail.com"

# Search for passwords
git diff --cached | grep -i "password"

# Search for secrets
git diff --cached | grep -i "secret"
```
**Verify:** No personal information found

### 3. Check .gitignore is Working
```bash
git check-ignore .env
git check-ignore src/main/resources/application-dev.yml
git check-ignore docker-compose.yml
```
**Verify:** All commands output the filename (means they're ignored)

### 4. Review Changes
```bash
git diff --cached
```
**Verify:** No sensitive data in the diff

---

## First Time Setup

- [ ] Copied `.env.example` to `.env`
- [ ] Filled in personal credentials in `.env`
- [ ] Copied `application-dev.yml.example` to `application-dev.yml`
- [ ] Updated `application-dev.yml` with credentials
- [ ] Copied `docker-compose.yml.example` to `docker-compose.yml` (if using Docker)
- [ ] Updated admin email in `V2__Insert_initial_data.sql`
- [ ] Generated strong JWT secret
- [ ] Created Gmail App Password
- [ ] Tested application with new credentials

---

## Files That Should NEVER Be Committed

❌ `.env`
❌ `.env.local`
❌ `.env.production`
❌ `src/main/resources/application-dev.yml`
❌ `src/main/resources/application-prod.yml`
❌ `docker-compose.yml`
❌ Any file with real passwords/secrets

---

## Files That SHOULD Be Committed

✅ `.env.example`
✅ `application-dev.yml.example`
✅ `application-prod.yml.example`
✅ `docker-compose.yml.example`
✅ `.gitignore`
✅ `SECURITY_SETUP.md`
✅ Source code files (without hardcoded secrets)

---

## Quick Fix: If You Accidentally Committed Secrets

```bash
# 1. Remove from Git (keeps local file)
git rm --cached .env

# 2. Commit the removal
git commit -m "Remove sensitive file"

# 3. Push changes
git push

# 4. IMMEDIATELY change all exposed credentials!
```

---

## Emergency: Secrets Pushed to Public Repo

1. **Immediately revoke/change:**
   - Gmail App Password
   - Database passwords
   - JWT secret
   - All API keys

2. **Remove from Git history:**
   ```bash
   # Use BFG Repo-Cleaner
   java -jar bfg.jar --delete-files .env
   git reflog expire --expire=now --all
   git gc --prune=now --aggressive
   git push --force
   ```

3. **Notify team members**

---

## Daily Security Habits

- ✅ Always run `git status` before committing
- ✅ Review `git diff` before committing
- ✅ Never share `.env` files
- ✅ Use different passwords for dev/prod
- ✅ Rotate secrets regularly
- ✅ Keep `.gitignore` updated

---

## Verification Commands

```bash
# Check what's being tracked by Git
git ls-files | grep -E '\.env|application-dev\.yml|docker-compose\.yml'
# Should return NOTHING

# Check what's ignored
git status --ignored
# Should show your sensitive files as ignored

# Scan for potential secrets
git diff --cached | grep -iE 'password|secret|key|token'
# Should return NOTHING (or only example/template values)
```

---

**When in doubt, DON'T commit! Ask for help first. 🔒**
