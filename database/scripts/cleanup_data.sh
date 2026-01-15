#!/bin/bash
# Shell script để cleanup dữ liệu database
# Sử dụng: ./cleanup_data.sh

echo "=========================================="
echo "Database Cleanup Script"
echo "=========================================="
echo ""

# Copy SQL file vào container
echo "Copying cleanup script to container..."
docker cp "$(dirname "$0")/cleanup_data.sql" portfolio-postgres:/tmp/cleanup_data.sql

if [ $? -ne 0 ]; then
    echo "Error: Failed to copy SQL file to container"
    exit 1
fi

# Chạy cleanup script
echo "Running cleanup script..."
docker-compose exec -T postgres psql -U portfolio_user -d portfolio -f /tmp/cleanup_data.sql

if [ $? -eq 0 ]; then
    echo ""
    echo "✓ Cleanup completed successfully!"
else
    echo ""
    echo "✗ Cleanup failed!"
    exit 1
fi
