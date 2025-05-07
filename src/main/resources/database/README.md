# Database Setup Guide
**Database Engine**: PostgreSQL

**Schema Version**: 1.0.0

**Last Updated**: 2025-04-17

## 📁 Directory Structure

| Path                         | Mô tả                                                    |
| ---------------------------- | -------------------------------------------------------- |
| `/core/types.sql`          | Định nghĩa các kiểu dữ liệu tùy chỉnh (custom types)      |
| `/core/tables.sql`         | Chứa toàn bộ định nghĩa bảng và ràng buộc (constraints)  |
| `/indexes/indexes.sql`       | Tạo các chỉ mục để tối ưu truy vấn                       |
| `/triggers/triggers.sql`     | Định nghĩa trigger và function phục vụ tự động hoá logic |
| `/mock_data/sample_data.sql` | Dữ liệu mẫu phục vụ cho phát triển và kiểm thử           |

## ⚙️ Setup Instructions
1. Đảm bảo bạn đã cài đặt và khởi động PostgreSQL.

2. Tạo một database mới để dùng cho hệ thống.

3. Chạy file init.sql để thiết lập toàn bộ cơ sở dữ liệu:

```bash
psql -d your_database_name -f init.sql
```

**Lưu ý**: File `sample_data.sql` sẽ được gọi mặc định bên trong `init.sql`. Nếu không muốn load dữ liệu mẫu (ở môi trường production), hãy comment dòng `\i mock_data/sample_data.sql`.

## 🔍 Tính Năng Cơ Sở Dữ Liệu
- Cập nhật trạng thái sách tự động dựa trên trạng thái item.

- Tính toán đánh giá (rating) từ bình luận người dùng.

- Quản lý đặt chỗ và mượn sách với tự động cập nhật trạng thái.

- Hệ thống chỉ mục (indexes) toàn diện để tối ưu hóa hiệu năng truy vấn.
