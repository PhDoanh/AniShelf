package com.library.anishelf;

import atlantafx.base.theme.NordDark;
import com.library.anishelf.util.NotificationManagerUtil;
import com.library.anishelf.util.RuntimeDebugUtil;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    private static final String TAG = "Main";
    private static final boolean ENABLE_FILE_LOGGING = false;
    private static final boolean ENABLE_CONSOLE_LOGGING = true;
    private static final RuntimeDebugUtil logger = RuntimeDebugUtil.getInstance();

    @Override
    public void init() throws Exception {
        super.init();
        initializeLogger();
    }

    /**
     * Khởi tạo cấu hình logger dựa trên tùy chọn
     */
    private void initializeLogger() {
        // Cấu hình logger
        logger.enableFileLogging(ENABLE_FILE_LOGGING);      // Bật/tắt ghi log vào file
        logger.enableConsoleLogging(ENABLE_CONSOLE_LOGGING);   // Bật/tắt hiển thị log trên console

        // Tạo file log mới
        if (ENABLE_FILE_LOGGING) {
            logger.createNewLogWithTimestamp();
            logger.info(TAG, "Ứng dụng khởi động với chế độ gỡ lỗi ENABLED");
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        logger.debug(TAG, "Đang khởi tạo ứng dụng...");
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        logger.debug(TAG, "Đã thiết lập múi giờ: Asia/Ho_Chi_Minh");

        // Tải font từ resources
        try {
            Font.loadFont(getClass().getResourceAsStream("/font/NotoSansJP-Regular.ttf"), 14);
            Font.loadFont(getClass().getResourceAsStream("/font/NotoSansJP-Bold.ttf"), 14);
            Font.loadFont(getClass().getResourceAsStream("/font/NotoSansJP-Medium.ttf"), 14);
            Font.loadFont(getClass().getResourceAsStream("/font/NotoSansJP-Light.ttf"), 14);
            logger.debug(TAG, "Đã tải thành công các font NotoSans");
        } catch (Exception e) {
            logger.error(TAG, "Lỗi khi tải font NotoSans", e);
        }

        // Set AtlantaFX theme as the default theme
        try {
            Application.setUserAgentStylesheet(new NordDark().getUserAgentStylesheet());
            logger.debug(TAG, "Đã thiết lập theme NordDark");
        } catch (Exception e) {
            logger.error(TAG, "Lỗi khi thiết lập theme", e);
        }

        try {
            logger.debug(TAG, "Đang tải màn hình đăng nhập...");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/UserLoginPage.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // Thêm custom CSS
            scene.getStylesheets().add(getClass().getResource("/style/custom.css").toExternalForm());
            logger.debug(TAG, "Đã thêm custom CSS");

            // Thiết lập kích thước tối thiểu cho cửa sổ
            stage.setMinWidth(1024);
            stage.setMinHeight(768);

            //stage.setResizable(false);
            stage.setOnCloseRequest(event -> {
                logger.info(TAG, "Ứng dụng đang đóng...");
                // Đảm bảo logger đóng đúng cách trước khi thoát
                logger.shutdown();
                System.exit(0);
                Platform.exit();
            });
            stage.setTitle("AniShelf");
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/image/app_icon.png")));

            // Initialize notification manager with the main stage
            NotificationManagerUtil.setOwnerWindow(stage);
            logger.debug(TAG, "Đã khởi tạo hệ thống thông báo");

            stage.setScene(scene);
            stage.show();
            logger.info(TAG, "Ứng dụng đã khởi động thành công");
        } catch (IOException e) {
            logger.error(TAG, "Lỗi khi khởi tạo ứng dụng", e);
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        // Đảm bảo logger đóng đúng cách khi ứng dụng kết thúc
        logger.info(TAG, "Ứng dụng đã đóng");
        logger.shutdown();
    }
}
