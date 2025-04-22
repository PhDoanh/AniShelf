package com.library.frontend;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.library.frontend.models.HomePage;

/**
 * Lớp khởi động chính của ứng dụng JavaFX
 * Cấu hình và hiển thị giao diện người dùng
 */
public class FrontendApplication extends Application {

    @Override
    public void start(Stage stage) {
        // Khởi tạo trang chủ với chế độ "Guest" mặc định
        HomePage homePage = new HomePage();
        
        // Tạo scene với nội dung từ trang chủ
        Scene scene = new Scene(homePage.render(), 1280, 720);
        
        // Thêm stylesheet cho giao diện
        scene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());
        
        // Cấu hình và hiển thị cửa sổ chính
        stage.setTitle("AniShelf - Thư viện Truyện tranh");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // Thiết lập múi giờ mặc định
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        launch(args);
    }
}