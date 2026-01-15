# Bulk Data Generator Scripts

Script tự động để insert dữ liệu lớn (khoảng 1 triệu dòng) vào database Portfolio.

## Yêu cầu

- Python 3.8+
- PostgreSQL database đang chạy
- psycopg2-binary library

## Cài đặt

```bash
# Cài đặt dependencies
pip install -r requirements.txt
```

## Sử dụng

### Chạy với cấu hình mặc định (1 triệu records)

**Lưu ý:** Script sẽ tự động xóa dữ liệu cũ trước khi insert dữ liệu mới.

```bash
python generate_bulk_data.py
```

### Tùy chỉnh tham số

```bash
# Thay đổi host/port
python generate_bulk_data.py --host localhost --port 5433

# Thay đổi batch size (mặc định: 1000)
python generate_bulk_data.py --batch-size 5000

# Scale dữ liệu (0.5 = một nửa, 2.0 = gấp đôi)
python generate_bulk_data.py --scale 0.5

# Skip cleanup (giữ lại dữ liệu cũ và thêm mới)
python generate_bulk_data.py --no-cleanup

# Thay đổi số luồng parallel (mặc định: 4)
python generate_bulk_data.py --workers 8

# Kết hợp các tham số
python generate_bulk_data.py --host localhost --port 5433 --batch-size 2000 --scale 1.5 --workers 4
```

### Cleanup dữ liệu riêng (không insert)

Nếu chỉ muốn xóa dữ liệu mà không insert lại:

**Windows (PowerShell):**
```powershell
.\cleanup_data.ps1
```

**Linux/Mac (Bash):**
```bash
chmod +x cleanup_data.sh
./cleanup_data.sh
```

**Hoặc chạy SQL trực tiếp:**
```bash
docker cp database/scripts/cleanup_data.sql portfolio-postgres:/tmp/cleanup_data.sql
docker-compose exec -T postgres psql -U portfolio_user -d portfolio -f /tmp/cleanup_data.sql
```

## Cấu hình dữ liệu mặc định

Script sẽ tạo ra:

- **Users**: 100,000 records
  - 10% ADMIN (10,000)
  - 30% MEMBER (30,000)
  - 60% GUEST (60,000)

- **Categories**: 1,000 records
  - Cấu trúc hierarchical (max depth: 3)
  - 5 children per parent

- **Products**: 200,000 records
  - Giá từ 1,000 - 5,000,000
  - Liên kết với categories

- **Orders**: 300,000 records
  - Phân bổ trong 365 ngày gần đây
  - Nhiều trạng thái khác nhau

- **Order Items**: ~390,000 records
  - Trung bình 1.3 items/order
  - Liên kết với orders và products

- **Audit Log**: 50,000 records
  - Ghi lại các hoạt động

- **Query History**: 10,000 records
  - Lịch sử query performance

**Tổng cộng: ~1,050,000 records**

## Tính năng

- ✅ **Parallel processing** - Sử dụng 4 luồng (có thể tùy chỉnh) để generate dữ liệu nhanh hơn
- ✅ Batch insert để tối ưu performance
- ✅ Connection pooling
- ✅ Progress tracking
- ✅ Error handling với rollback
- ✅ Respect foreign key constraints
- ✅ Dữ liệu realistic và đa dạng
- ✅ Transaction support
- ✅ Tự động cleanup dữ liệu cũ trước khi insert

## Lưu ý

1. **Script tự động xóa dữ liệu cũ** trước khi insert dữ liệu mới (mỗi lần chạy)
2. **Parallel processing** giúp tăng tốc đáng kể, đặc biệt với bảng lớn (products, orders)
3. Script sẽ mất vài phút để chạy hoàn tất (tùy vào hardware và số workers)
4. Đảm bảo database có đủ không gian lưu trữ
5. Có thể điều chỉnh `batch_size` và `--workers` để tối ưu cho hệ thống của bạn
6. Script sẽ tự động commit theo batch để tránh transaction quá lớn
7. Sử dụng `--no-cleanup` nếu muốn giữ lại dữ liệu cũ và thêm mới
8. **query_history** table được giữ lại vì được sử dụng trong stored procedures

## Troubleshooting

### Lỗi kết nối database
- Kiểm tra database đang chạy: `docker-compose ps postgres`
- Kiểm tra port mapping trong `docker-compose.yml`

### Out of memory
- Giảm `batch_size` xuống (ví dụ: 500)
- Giảm `scale` factor

### Foreign key constraint errors
- Đảm bảo chạy script theo thứ tự (script tự động xử lý)
- Kiểm tra dữ liệu seed ban đầu đã có chưa
