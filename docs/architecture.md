# Cấu trúc dự án

```txt
AniShelf/
├── .github/
│   └── workflows/
│       └── docker-image.yml
├── .mvn/
|   └── wrapper/
|       └── maven-wrapper.properties
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/library/backend/
│   │   │   └── resources/
|   |   |       ├── database/
│   │   │       └── application.properties
│   │   └── test/java/com/library/backend/
│   ├── Dockerfile
│   └── pom.xml
├── docs/
├── frontend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/library/frontend/
│   │   │   └── resources/com/library/frontend/
│   │   └── test/java/com/library/frontend/
│   └── pom.xml
├── .gitattribute
├── .gitignore
├── docker-compose.yml
├── LICENSE
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

## Giải thích chi tiết các thành phần

- **Thư mục gốc `AniShelf/`**
    - **Chứa toàn bộ dự án:** Đây là thư mục gốc chứa các module, tài liệu, cấu hình, và tập tin điều khiển dự án.
- **`.github/`**
    - **Mục đích:** Chứa cấu hình cho GitHub Actions – hệ thống CI/CD.
    - **`workflows/`**
        - **Chứa file workflow:** Các quy trình tự động như build, test, và deploy sẽ được định nghĩa ở đây.
        - **`docker-image.yml`:**
            - File cấu hình cho việc xây dựng image Docker cho **backend**.
            - Quy trình này có thể được tích hợp với GitHub Actions để tự động build và đẩy image lên Docker Hub khi có commit mới.
- **`.mvn/`**
    - **Mục đích:** Chứa các file hỗ trợ cho Maven Wrapper giúp người dùng không cần cài đặt Maven riêng.
    - **`wrapper/`**
        - **`maven-wrapper.properties`:**
            - File cấu hình cho Maven Wrapper, quy định phiên bản Maven sẽ được sử dụng.
- **`backend/`**
    - **Chứa mã nguồn và tài nguyên cho phần backend của ứng dụng.**
    - **`src/`**
        - **`main/`**
            - **`java/com/library/backend/`:**
                - Chứa mã nguồn Java cho backend sử dụng **Spring Framework**.
                - Bao gồm các thành phần như `controller` (xử lý HTTP request), `service` (logic nghiệp vụ), `repository` (truy xuất dữ liệu), và `model` (định nghĩa các entity).
            - **`resources/`:**
                - **`database/`:**
                    - Có thể chứa các file SQL, script migration hoặc cấu hình liên quan đến database (ví dụ: file schema, data seed).
                - **`application.properties`:**
                    - File cấu hình cho **Spring Boot**, chứa các thông số kết nối đến database (PostgreSQL trên Oracle Cloud Server), cấu hình port, log, …
        - **`test/java/com/library/backend/`:**
            - Chứa các **unit test** cho backend sử dụng `JUnit` nhằm kiểm thử các chức năng riêng lẻ của ứng dụng.
    - **`Dockerfile`:**
        - File định nghĩa cách xây dựng image Docker cho backend.
        - Bao gồm các bước cài đặt môi trường (cài `Java`, copy mã nguồn, cấu hình chạy ứng dụng).
    - **`pom.xml`:**
        - File cấu hình của `Maven` cho module backend, khai báo dependency, plugin build, và các cấu hình liên quan đến dự án Spring.
- **`docs/`**
    - **Chứa tài liệu:**
        - Bao gồm các tài liệu như kiến trúc ứng dụng, hướng dẫn sử dụng, và tài liệu về các API được sử dụng trong backend.
- **`frontend/`**
    - **Chứa mã nguồn cho giao diện người dùng (UI) phát triển bằng `JavaFX`.**
    - **`src/`**
        - **`main/`**
            - **`java/com/library/frontend/`:**
                - Chứa mã nguồn Java cho UI, định nghĩa các controller, logic giao diện, …
            - **`resources/com/library/frontend/`:**
                - Chứa các file tài nguyên của giao diện như `FXML`, `CSS`, hình ảnh, icon…
        - **`test/java/com/library/frontend/`:**
            - Chứa các **unit test** cho frontend sử dụng `TestFX` để kiểm thử giao diện.
    - **`pom.xml`:**
        - File cấu hình `Maven` cho module frontend, khai báo các dependency cần thiết cho JavaFX và TestFX.
- **`.gitattribute`:**
    - **Chức năng:** Quản lý các thuộc tính của file trong Git, như quy tắc kết hợp file, xử lý end-of-line, và các thuộc tính khác khi làm việc với repository.
- **`.gitignore`:**
    - **Chứa danh sách các file và thư mục cần loại trừ khi commit lên Git:**
        - Bao gồm các file build, log, file tạm và các file nhạy cảm khác.
- **`docker-compose.yml`:**
    - **Định nghĩa cấu hình Docker Compose:**
        - Dùng để chạy nhiều container Docker cùng lúc.
        - Trong dự án này, chủ yếu dùng để khởi chạy container backend (đã được container hóa qua `Dockerfile`) và liên kết đến database trên cloud.
- **`LICENSE`:**
    - **Thông tin giấy phép:** Quy định các quyền sử dụng, phân phối và sửa đổi mã nguồn của dự án.
- **`mvnw` và `mvnw.cmd`:**
    - **Maven Wrapper:**
        - Các script cho phép chạy Maven mà không cần phải cài đặt Maven toàn cục trên máy.
        - `mvnw` dành cho Unix/Linux/Mac và `mvnw.cmd` dành cho Windows.
- **`pom.xml` (thư mục gốc):**
    - **File cấu hình Maven tổng hợp:**
        - Nếu dự án là **Multi-Module Maven Project**, file này sẽ định nghĩa các module con (`backend`, `frontend`) cũng như các cấu hình và dependency chung cho toàn bộ dự án.
- **`README.md`:**
    - **Giới thiệu và hướng dẫn dự án:**
        - Chứa các thông tin tổng quan về dự án, cách cài đặt, chạy ứng dụng, và liên kết đến tài liệu chi tiết trong thư mục `docs/`.

## Tóm tắt vận hành và triển khai

- **Backend:**
    - Được container hóa qua `Docker` (file `Dockerfile`).
    - Sử dụng `Spring` kết nối đến PostgreSQL trên Oracle Cloud.
    - Kiểm thử bằng `JUnit`.
    - Build và deploy tự động qua `GitHub Actions` (file cấu hình trong `.github/workflows/docker-image.yml`).
- **Frontend:**
    - Phát triển giao diện desktop bằng `JavaFX`.
    - Kiểm thử giao diện bằng `TestFX`.
    - Không được container hóa, chạy cục bộ.
- **Docker Compose:**
    - File `docker-compose.yml` giúp khởi chạy container backend và liên kết tới database trên cloud, giúp các thành viên chỉ cần kéo image từ Docker Hub và chạy ứng dụng một cách nhanh chóng.

