# AniShelf - Kệ sách của những người đam mê Anime

## Hướng dẫn build và run ứng dụng

Các yêu cầu tiên quyết:
- JDK 21 trở lên (kiểm tra phiên bản hiện tại bằng lệnh `java --version`)

### Build ứng dụng
Build backend:
```bash
.\mvnw.cmd clean package -f backend/pom.xml
```

Build frontend:
```bash
.\mvnw.cmd clean package -f frontend/pom.xml
```

Build toàn bộ project:
```bash
.\mvnw.cmd clean install -U
```

### Run ứng dụng
Run ứng dụng frontend:
```bash
.\mvnw.cmd javafx:run -pl .\frontend
```
