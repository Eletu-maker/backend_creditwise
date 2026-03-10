# Simple Security Check
Write-Host "Final Security Check" -ForegroundColor Cyan
Write-Host "====================" -ForegroundColor Cyan
Write-Host ""

$Issues = 0

# Check for real email
Write-Host "Checking for real personal email in config files..." -ForegroundColor Yellow
$realEmail = "usmaneletu2@gmail.com"
$stagedDiff = git diff --cached
# Filter out documentation files
$configDiff = $stagedDiff | Select-String -Pattern "^\+\+\+ b/" | Where-Object { 
    $_ -notmatch "\.md$" -and $_ -notmatch "SECURITY" -and $_ -notmatch "README"
}

$foundInConfigs = $false
foreach ($line in $stagedDiff) {
    if ($line -match "^\+\+\+ b/.*\.(yml|yaml|properties|env)" -and $line -notmatch "example") {
        $inConfigFile = $true
    }
    if ($line -match "^\+\+\+ b/" -and $line -notmatch "\.(yml|yaml|properties|env)") {
        $inConfigFile = $false
    }
    if ($inConfigFile -and $line -match $realEmail -and $line -match "^\+") {
        Write-Host "ERROR: Found your real email in config file!" -ForegroundColor Red
        $foundInConfigs = $true
        $Issues++
        break
    }
}

if (-not $foundInConfigs) {
    Write-Host "OK: No real personal email in config files" -ForegroundColor Green
}

# Check for real passwords
Write-Host "Checking for real passwords..." -ForegroundColor Yellow
$password1 = "xeqxhtpngrhojkhb"
$password2 = "oectnkjyriqoitti"
if ($stagedDiff -match $password1 -or $stagedDiff -match $password2) {
    Write-Host "ERROR: Found real password in staged files!" -ForegroundColor Red
    $Issues++
} else {
    Write-Host "OK: No real passwords found" -ForegroundColor Green
}

# Check sensitive files not staged
Write-Host "Checking sensitive files are not being added..." -ForegroundColor Yellow
$stagedFiles = git diff --cached --name-status
$hasSensitive = $false

foreach ($line in $stagedFiles) {
    $parts = $line -split '\s+'
    $status = $parts[0]
    $file = $parts[1]
    
    # Only flag if file is being Added or Modified, not Deleted
    if (($status -eq "A" -or $status -eq "M") -and
        ($file -eq ".env" -or 
         $file -eq "src/main/resources/application-dev.yml" -or 
         $file -eq "src/main/resources/application-prod.yml" -or
         ($file -eq "docker-compose.yml" -and $file -notmatch "example"))) {
        Write-Host "ERROR: Sensitive file being added: $file" -ForegroundColor Red
        $hasSensitive = $true
        $Issues++
    }
}

if (-not $hasSensitive) {
    Write-Host "OK: No sensitive files being added" -ForegroundColor Green
}

Write-Host ""
Write-Host "====================" -ForegroundColor Cyan
if ($Issues -eq 0) {
    Write-Host "ALL CHECKS PASSED!" -ForegroundColor Green
    Write-Host "Safe to commit and push!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Next steps:" -ForegroundColor Yellow
    Write-Host '  git commit -m "Add security improvements"'
    Write-Host "  git push"
    exit 0
} else {
    Write-Host "ISSUES FOUND: $Issues" -ForegroundColor Red
    Write-Host "DO NOT COMMIT until fixed!" -ForegroundColor Red
    exit 1
}
