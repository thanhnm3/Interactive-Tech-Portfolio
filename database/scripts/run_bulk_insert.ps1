# PowerShell script để chạy bulk data generator trên Windows
# Sử dụng: .\run_bulk_insert.ps1 [scale]

param(
    [double]$Scale = 1.0
)

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Bulk Data Generator - Portfolio Database" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Scale factor: $Scale" -ForegroundColor Yellow
Write-Host ""

# Kiểm tra Python
if (-not (Get-Command python -ErrorAction SilentlyContinue)) {
    Write-Host "Error: Python not found. Please install Python 3.8+" -ForegroundColor Red
    exit 1
}

# Kiểm tra dependencies
try {
    python -c "import psycopg2" 2>$null
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Installing dependencies..." -ForegroundColor Yellow
        pip install -r requirements.txt
    }
} catch {
    Write-Host "Installing dependencies..." -ForegroundColor Yellow
    pip install -r requirements.txt
}

# Chạy script
Write-Host "Starting data generation..." -ForegroundColor Green
python generate_bulk_data.py --scale $Scale

Write-Host ""
Write-Host "Done!" -ForegroundColor Green
