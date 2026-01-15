#!/bin/bash
# Shell script để chạy bulk data generator
# Sử dụng: ./run_bulk_insert.sh [scale]

SCALE=${1:-1.0}

echo "=========================================="
echo "Bulk Data Generator - Portfolio Database"
echo "=========================================="
echo "Scale factor: $SCALE"
echo ""

# Kiểm tra Python
if ! command -v python3 &> /dev/null; then
    echo "Error: python3 not found. Please install Python 3.8+"
    exit 1
fi

# Kiểm tra dependencies
if ! python3 -c "import psycopg2" &> /dev/null; then
    echo "Installing dependencies..."
    pip3 install -r requirements.txt
fi

# Chạy script
python3 generate_bulk_data.py --scale "$SCALE"

echo ""
echo "Done!"
