# PowerShell script để cleanup dữ liệu database
# Sử dụng: .\cleanup_data.ps1

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Database Cleanup Script" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

# Copy SQL file vào container
Write-Host "Copying cleanup script to container..." -ForegroundColor Yellow
docker cp "$PSScriptRoot\cleanup_data.sql" portfolio-postgres:/tmp/cleanup_data.sql

if ($LASTEXITCODE -ne 0) {
    Write-Host "Error: Failed to copy SQL file to container" -ForegroundColor Red
    exit 1
}

# Chạy cleanup script
Write-Host "Running cleanup script..." -ForegroundColor Yellow
docker-compose exec -T postgres psql -U portfolio_user -d portfolio -f /tmp/cleanup_data.sql

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "✓ Cleanup completed successfully!" -ForegroundColor Green
} else {
    Write-Host ""
    Write-Host "✗ Cleanup failed!" -ForegroundColor Red
    exit 1
}
