Phân Loại Các Tệp Theo Chức Năng Trong Mã Nguồn

# 1. Quản lý tài liệu (Bắt buộc)

## Model

- Book.java: Model chính cho sách

- BookItem.java: Model cho từng bản sao sách

- Author.java: Model tác giả

- Category.java: Model thể loại

## DAO

- BookDAO.java: Xử lý CRUD[^1] cho sách

- BookItemDAO.java: Xử lý CRUD[^1] cho bản sao sách

## Controller

- AdminBookDetailController.java: Quản lý chi tiết sách

- AdminBookTableController.java: Hiển thị danh sách sách

- AdminBookTableRowController.java: Hiển thị từng dòng sách

- BookController.java: Xử lý chức năng sách

- BookCard1Controller.java, BookCard2Controller.java: Hiển thị sách dạng card

- AdvancedSearchController.java: Tìm kiếm nâng cao

- MoreBookController.java: Hiển thị thêm sách

- CategoryController.java: Quản lý thể loại

## View (FXML)

- AdminBookDetail.fxml: Giao diện chi tiết sách

- AdminBookTable.fxml: Giao diện bảng sách

- AdminBookTableRow.fxml: Giao diện dòng sách

- Book-view.fxml: Giao diện xem sách

- BookCard1-view.fxml, BookCard2-view.fxml: Giao diện card sách

- AdvancedSearch-view.fxml: Giao diện tìm kiếm nâng cao

- MoreBook-view.fxml: Giao diện hiển thị thêm sách

- Category-view.fxml: Giao diện thể loại

# 2. Quản lý người dùng thư viện (Bắt buộc)

## Model

- Member.java: Model thành viên

- Account.java: Model tài khoản

- Person.java: Model thông tin cá nhân

- BookIssue.java: Model đơn mượn sách

- BookReservation.java: Model đặt trước sách

- Admin.java: Model quản trị viên

## DAO

- MemberDAO.java: Xử lý CRUD[^1] cho thành viên

- AccountDAO.java: Xử lý CRUD[^1] cho tài khoản

- BookIssueDAO.java: Xử lý CRUD[^1] cho đơn mượn

- BookReservationDAO.java: Xử lý CRUD[^1] cho đặt trước

## Controller

- AdminUserDetailController.java: Quản lý chi tiết thành viên

- AdminUserTableController.java: Hiển thị danh sách thành viên

- AdminBorrowDetailController.java: Quản lý mượn/trả sách

- AdminReservationDetailController.java: Quản lý đặt trước

- HistoryController.java: Xem lịch sử mượn/trả

- UserIssuesController.java: Quản lý đơn mượn của người dùng

- AdminIssueDetailController.java: Quản lý chi tiết vấn đề

- AdminRecentIssueController.java: Quản lý vấn đề gần đây

## View (FXML)

- AdminUserDetail.fxml: Giao diện chi tiết thành viên

- AdminUserTable.fxml: Giao diện bảng thành viên

- AdminBorrowDetail.fxml: Giao diện chi tiết mượn/trả

- AdminReservationDetail.fxml: Giao diện chi tiết đặt trước

- History-view.fxml: Giao diện lịch sử

- UserIssues-view.fxml: Giao diện vấn đề người dùng

- AdminIssueDetail.fxml: Giao diện chi tiết vấn đề

- AdminRecentIssuelTableRow.fxml: Giao diện dòng vấn đề gần đây

# 3. Xử lý lỗi (Bắt buộc)

## Controller

- CustomerAlter.java: Hiển thị thông báo lỗi

- AlertController.java: Xử lý các thông báo

- BaseDetailController.java: Xử lý lỗi cơ bản

- BaseTableController.java: Xử lý lỗi hiển thị bảng

- BaseController.java: Controller cơ sở

- BasePageController.java: Trang cơ sở

- BaseRowController.java: Dòng cơ sở

## View (FXML)

- Alert-view.fxml: Giao diện thông báo lỗi

# 4. Giao diện người dùng (Tự chọn)

## Controller

- AdminMenuController.java: Menu admin

- UserMenuController.java: Menu người dùng

- InterfaceSettingController.java: Cài đặt giao diện

- SettingController.java: Cài đặt chung

- AdminSettingController.java: Cài đặt admin

- ThemeManager.java: Quản lý theme

- Animation.java: Hiệu ứng

- DashBoardController.java: Quản lý bảng điều khiển

- BookRankingController.java: Quản lý xếp hạng sách

- BookSuggestionCardController.java: Quản lý gợi ý sách

- AdminDashboardBookCardController.java: Quản lý card sách trên bảng điều khiển

## View (FXML)

- AdminMenu.fxml: Giao diện menu admin

- UserMenu-view.fxml: Giao diện menu người dùng

- InterfaceSetting-view.fxml: Giao diện cài đặt

- Setting-view.fxml: Giao diện cài đặt chung

- AdminSetting.fxml: Giao diện cài đặt admin

- DashBoard-view.fxml: Giao diện bảng điều khiển

- BookRanking-view.fxml: Giao diện xếp hạng sách

- BookSuggestionCard-view.fxml: Giao diện card gợi ý sách

- AdminDashBoardMain.fxml: Giao diện bảng điều khiển chính

## Style (CSS)

- custom.css: CSS tùy chỉnh

- Theme files:

    - caspian-embedded.css

    - cupertino-dark.css, cupertino-light.css

    - dracula.css

    - nord-dark.css, nord-light.css

    - primer-dark.css, primer-light.css

# 5. Tích hợp API tra cứu (Tự chọn)

## Service

- BookAPIService.java: API tra cứu sách

- MusicInfoFetcher.java: API lấy thông tin nhạc

## Util

- EmailUtil.java: Gửi email

- BarcodeScanner.java: Quét mã vạch

## View (FXML)

- youtubeMusic.html: Giao diện nhạc YouTube

# 6. Đa luồng (Tự chọn)

## Util

- ImageCache.java: Cache hình ảnh

- LRUCache.java: Cache dữ liệu

- FXMLLoaderUtil.java: Load FXML bất đồng bộ

# 7. Chức năng sáng tạo (Tự chọn)

## Model

- BookMark.java: Đánh dấu sách

- Comment.java: Bình luận

- Report.java: Báo cáo

- Music.java: Nhạc nền

## Controller

- BookmarkController.java: Quản lý đánh dấu

- RatingBookController.java: Đánh giá sách

- MusicController.java: Phát nhạc

- TicTacToeController.java: Game giải trí

- UserReportController.java: Báo cáo người dùng

- CommentController.java: Quản lý bình luận

- MusicSearchCardController.java: Tìm kiếm nhạc

## View (FXML)

- Bookmark-view.fxml: Giao diện đánh dấu

- RatingBook-view.fxml: Giao diện đánh giá

- Music-view.fxml: Giao diện nhạc

- TicTacToe-view.fxml: Giao diện game

- UserReport-view.fxml: Giao diện báo cáo

- Comment-view.fxml: Giao diện bình luận

- MusicSearchCard-view.fxml: Giao diện tìm kiếm nhạc

- RatingBookCard-view.fxml: Giao diện card đánh giá

- UserReportCard-view.fxml: Giao diện card báo cáo

## Util

- FaceidLogin.java: Đăng nhập bằng khuôn mặt

- FaceidRecognizer.java: Nhận diện khuôn mặt

- FaceidUnregister.java: Hủy đăng ký khuôn mặt

- Sound.java: Âm thanh

# Các file hỗ trợ khác

## Database

- Database.java: Kết nối và quản lý database

- DatabaseQuery.java: Interface truy vấn database

- init.sql: Khởi tạo database

- Directories:

- triggers/: Các trigger database

- indexes/: Các index database

- mock_data/: Dữ liệu mẫu

- core/: Các file cốt lõi database

## Configuration

- app_config.txt: Cấu hình ứng dụng

- usr_config.txt: Cấu hình người dùng

## Authentication

- LoginController.java: Xử lý đăng nhập

- ResignController.java: Xử lý đăng ký

- ForgotPasswordController.java: Xử lý quên mật khẩu

- UserLogin.fxml: Giao diện đăng nhập

- AdminLogin-view.fxml: Giao diện đăng nhập admin

- ForgotPassword-view.fxml: Giao diện quên mật khẩu

- UserResign-view.fxml: Giao diện đăng ký

## Main

- Main.java: Điểm khởi đầu ứng dụng

## Resource Directories

- font/: Phông chữ

- image/: Hình ảnh

- sound/: Âm thanh

- face/: Dữ liệu nhận diện khuôn mặt

- META-INF/: Metadata

- i8n/: Hỗ trợ ngôn ngữ



### Chú thích

[^1]: Viết tắt của 4 thao tác cơ bản: Create, Read, Update, Delete. Mỗi entity đều có một DAO riêng để thực hiện thao tác CRUD này.