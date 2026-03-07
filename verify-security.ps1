# Security Verification Script (PowerShell)
# Run this before committing to Git

Write-Host " Security Verification Script" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""

$Errors = 0
$Warnings = 0

# Check 1: Verify .gitignore exists
Write-Host " Check 1: Verifying .gitignore exists..." -ForegroundColor Yellow
if (Test-Path ".gitignore") {
    Write-Host " .gitignore found" -ForegroundColor Green
} else {
    Write-Host " .gitignore not found!" -ForegroundColor Red
    $Errors++
}
Write-Host ""

# Check 2: Verify sensitive files are ignored
Write-Host " Check 2: Verifying sensitive files are ignored..." -ForegroundColor Yellow
$SensitiveFiles = @(
    ".env",
    "src/main/resources/application-dev.yml",
    "src/main/resources/application-prod.yml",
    "docker-compose.yml"
)

foreach ($file in $SensitiveFiles) {
    $ignored = git check-ignore $file 2>$null
    if ($ignored) {
        Write-Host " $file is ignored" -ForegroundColor Green
    } else {
        if (Test-Path $file) {
            Write-Host " $file exists but is NOT ignored!" -ForegroundColor Red
            $Errors++
        } else {
            Write-Host "  $file doesn't exist (will create from template)" -ForegroundColor Yellow
            $Warnings++
        }
    }
}
Write-Host ""

# Check 3: Verify template files exist
Write-Host " Check 3: Verifying template files exist..." -ForegroundColor Yellow
$TemplateFiles = @(
    ".env.example",
    "src/main/resources/application-dev.yml.example",
    "docker-compose.yml.example"
)

foreach ($file in $TemplateFiles) {
    if (Test-Path $file) {
        Write-Host " $file exists" -ForegroundColor Green
    } else {
        Write-Host " $file not found!" -ForegroundColor Red
        $Errors++
    }
}
Write-Host ""

# Check 4: Check for secrets in staged files
Write-Host " Check 4: Checking for secrets in staged files..." -ForegroundColor Yellow
$stagedDiff = git diff --cached
if ($stagedDiff -match 'password.*=.*[^example]|secret.*=.*[^example]|api[_-]?key') {
    Write-Host " Potential secrets found in staged files!" -ForegroundColor Red
    Write-Host "Run: git diff --cached | Select-String -Pattern 'password|secret|key'" -ForegroundColor Yellow
    $Errors++
} else {
    Write-Host " No obvious secrets in staged files" -ForegroundColor Green
}
Write-Host ""

# Check 5: Check for personal email in staged files
Write-Host " Check 5: Checking for personal emails in staged files..." -ForegroundColor Yellow
if ($stagedDiff -match '@gmail\.com|@hotmail\.com|@yahoo\.com' -and $stagedDiff -notmatch 'example') {
    Write-Host "  Personal email addresses found in staged files" -ForegroundColor Yellow
    Write-Host "Review: git diff --cached | Select-String -Pattern '@gmail|@hotmail|@yahoo'" -ForegroundColor Yellow
    $Warnings++
} else {
    Write-Host " No personal emails in staged files" -ForegroundColor Green
}
Write-Host ""

# Check 6: Verify config files have placeholders
Write-Host " Check 6: Verifying template files have placeholders..." -ForegroundColor Yellow
if (Test-Path ".env.example") {
    $content = Get-Content ".env.example" -Raw
    if ($content -match "your_") {
        Write-Host " .env.example has placeholders" -ForegroundColor Green
    } else {
        Write-Host "  .env.example might have real credentials" -ForegroundColor Yellow
        $Warnings++
    }
}
Write-Host ""

# Check 7: List files that will be committed
Write-Host " Check 7: Files to be committed..." -ForegroundColor Yellow
$stagedFiles = git diff --cached --name-only
if (-not $stagedFiles) {
    Write-Host "  No files staged for commit" -ForegroundColor Yellow
} else {
    $stagedFiles | ForEach-Object { Write-Host $_ }
}
Write-Host ""

# Summary
Write-Host "================================" -ForegroundColor Cyan
Write-Host " Summary" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host "Errors: $Errors" -ForegroundColor $(if ($Errors -eq 0) { "Green" } else { "Red" })
Write-Host "Warnings: $Warnings" -ForegroundColor $(if ($Warnings -eq 0) { "Green" } else { "Yellow" })
Write-Host ""

if ($Errors -eq 0 -and $Warnings -eq 0) {
    Write-Host " All checks passed! Safe to commit." -ForegroundColor Green
    exit 0
} elseif ($Errors -eq 0) {
    Write-Host "  Warnings found. Review before committing." -ForegroundColor Yellow
    exit 0
} else {
    Write-Host " Errors found! DO NOT commit until fixed." -ForegroundColor Red
    Write-Host ""
    Write-Host "Next steps:" -ForegroundColor Yellow
    Write-Host "1. Fix the errors listed above"
    Write-Host "2. Run this script again"
    Write-Host "3. Review: git status"
    Write-Host "4. Review: git diff --cached"
    exit 1
}
