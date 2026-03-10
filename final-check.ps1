# Final Security Check - Simple Version
# This checks for REAL personal information, not placeholders

Write-Host " Final Security Check" -ForegroundColor Cyan
Write-Host "=======================" -ForegroundColor Cyan
Write-Host ""

$Issues = 0

# Check for YOUR actual email (replace with your email)
Write-Host "Checking for real personal email..." -ForegroundColor Yellow
$realEmail = "usmaneletu2@gmail.com"  # Your actual email
$stagedDiff = git diff --cached

if ($stagedDiff -match $realEmail) {
    Write-Host " FOUND YOUR REAL EMAIL in staged files!" -ForegroundColor Red
    $Issues++
} else {
    Write-Host " No real personal email found" -ForegroundColor Green
}

# Check for real passwords (not placeholders)
Write-Host "Checking for real passwords..." -ForegroundColor Yellow
$password1 = "xeqxhtpngrhojkhb"
$password2 = "oectnkjyriqoitti"
if ($stagedDiff -match $password1 -or $stagedDiff -match $password2) {
    Write-Host " FOUND REAL PASSWORD in staged files!" -ForegroundColor Red
    $Issues++
} else {
    Write-Host " No real passwords found" -ForegroundColor Green
}

# Check that sensitive files are not staged
Write-Host "Checking sensitive files are not staged..." -ForegroundColor Yellow
$stagedFiles = git diff --cached --name-only
$sensitiveInStaged = $stagedFiles | Where-Object { 
    $_ -eq ".env" -or 
    $_ -eq "src/main/resources/application-dev.yml" -or 
    $_ -eq "src/main/resources/application-prod.yml" -or
    ($_ -eq "docker-compose.yml" -and $_ -notmatch "example")
}

if ($sensitiveInStaged) {
    Write-Host " Sensitive files are staged:" -ForegroundColor Red
    $sensitiveInStaged | ForEach-Object { Write-Host "   $_" -ForegroundColor Red }
    $Issues++
} else {
    Write-Host " No sensitive files staged" -ForegroundColor Green
}

Write-Host ""
Write-Host "=======================" -ForegroundColor Cyan
if ($Issues -eq 0) {
    Write-Host " ALL CHECKS PASSED!" -ForegroundColor Green
    Write-Host "Safe to commit and push!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Next steps:" -ForegroundColor Yellow
    Write-Host "  git commit -m `"Add security improvements and protect sensitive data`""
    Write-Host "  git push"
    exit 0
} else {
    Write-Host " ISSUES FOUND: $Issues" -ForegroundColor Red
    Write-Host "DO NOT COMMIT until fixed!" -ForegroundColor Red
    exit 1
}
