# AniShelf - Kệ sách của những người đam mê Anime

## Chuẩn bị môi trường

Đảm bảo các yêu cầu tiên quyết đã được cài đặt:
- JDK (phiên bản tương thích)
- Maven (sẽ được tự động tải khi sử dụng Maven Wrapper)

## Quy trình build và run

Bước 1: Tải mã nguồn về máy tính.
```bash
git clone https://github.com/PhDoanh/AniShelf.git
```

Bước 2: Chuyển vào thư mục dự án
```bash
cd AniShelf
```

Bước 3: Tải xuống các phụ thuộc và biên dịch mã nguồn
```bash
mvnw.cmd clean install
```

*P/s: Sử dụng `-DskipTests` để bỏ qua các test nếu cần*

Bước 4: Chạy ứng dụng JavaFX
```bash
.\mvnw.cmd clean javafx:run
```



