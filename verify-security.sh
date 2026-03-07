#!/bin/bash

# Security Verification Script
# Run this before committing to Git

echo "🔒 Security Verification Script"
echo "================================"
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

ERRORS=0
WARNINGS=0

# Check 1: Verify .gitignore exists
echo "📋 Check 1: Verifying .gitignore exists..."
if [ -f ".gitignore" ]; then
    echo -e "${GREEN}✅ .gitignore found${NC}"
else
    echo -e "${RED}❌ .gitignore not found!${NC}"
    ERRORS=$((ERRORS + 1))
fi
echo ""

# Check 2: Verify sensitive files are ignored
echo "📋 Check 2: Verifying sensitive files are ignored..."
SENSITIVE_FILES=(".env" "src/main/resources/application-dev.yml" "src/main/resources/application-prod.yml" "docker-compose.yml")

for file in "${SENSITIVE_FILES[@]}"; do
    if git check-ignore "$file" > /dev/null 2>&1; then
        echo -e "${GREEN}✅ $file is ignored${NC}"
    else
        if [ -f "$file" ]; then
            echo -e "${RED}❌ $file exists but is NOT ignored!${NC}"
            ERRORS=$((ERRORS + 1))
        else
            echo -e "${YELLOW}⚠️  $file doesn't exist (will create from template)${NC}"
            WARNINGS=$((WARNINGS + 1))
        fi
    fi
done
echo ""

# Check 3: Verify template files exist
echo "📋 Check 3: Verifying template files exist..."
TEMPLATE_FILES=(".env.example" "src/main/resources/application-dev.yml.example" "docker-compose.yml.example")

for file in "${TEMPLATE_FILES[@]}"; do
    if [ -f "$file" ]; then
        echo -e "${GREEN}✅ $file exists${NC}"
    else
        echo -e "${RED}❌ $file not found!${NC}"
        ERRORS=$((ERRORS + 1))
    fi
done
echo ""

# Check 4: Check for secrets in staged files
echo "📋 Check 4: Checking for secrets in staged files..."
if git diff --cached | grep -iE 'password.*=.*[^example]|secret.*=.*[^example]|api[_-]?key' > /dev/null; then
    echo -e "${RED}❌ Potential secrets found in staged files!${NC}"
    echo "Run: git diff --cached | grep -iE 'password|secret|key'"
    ERRORS=$((ERRORS + 1))
else
    echo -e "${GREEN}✅ No obvious secrets in staged files${NC}"
fi
echo ""

# Check 5: Check for personal email in staged files
echo "📋 Check 5: Checking for personal emails in staged files..."
if git diff --cached | grep -iE '@gmail\.com|@hotmail\.com|@yahoo\.com' | grep -v 'example' > /dev/null; then
    echo -e "${YELLOW}⚠️  Personal email addresses found in staged files${NC}"
    echo "Review: git diff --cached | grep -iE '@gmail|@hotmail|@yahoo'"
    WARNINGS=$((WARNINGS + 1))
else
    echo -e "${GREEN}✅ No personal emails in staged files${NC}"
fi
echo ""

# Check 6: Verify config files have placeholders
echo "📋 Check 6: Verifying template files have placeholders..."
if [ -f ".env.example" ]; then
    if grep -q "your_" ".env.example"; then
        echo -e "${GREEN}✅ .env.example has placeholders${NC}"
    else
        echo -e "${YELLOW}⚠️  .env.example might have real credentials${NC}"
        WARNINGS=$((WARNINGS + 1))
    fi
fi
echo ""

# Check 7: List files that will be committed
echo "📋 Check 7: Files to be committed..."
STAGED_FILES=$(git diff --cached --name-only)
if [ -z "$STAGED_FILES" ]; then
    echo -e "${YELLOW}⚠️  No files staged for commit${NC}"
else
    echo "$STAGED_FILES"
fi
echo ""

# Summary
echo "================================"
echo "📊 Summary"
echo "================================"
echo -e "Errors: ${RED}$ERRORS${NC}"
echo -e "Warnings: ${YELLOW}$WARNINGS${NC}"
echo ""

if [ $ERRORS -eq 0 ] && [ $WARNINGS -eq 0 ]; then
    echo -e "${GREEN}✅ All checks passed! Safe to commit.${NC}"
    exit 0
elif [ $ERRORS -eq 0 ]; then
    echo -e "${YELLOW}⚠️  Warnings found. Review before committing.${NC}"
    exit 0
else
    echo -e "${RED}❌ Errors found! DO NOT commit until fixed.${NC}"
    echo ""
    echo "Next steps:"
    echo "1. Fix the errors listed above"
    echo "2. Run this script again"
    echo "3. Review: git status"
    echo "4. Review: git diff --cached"
    exit 1
fi
