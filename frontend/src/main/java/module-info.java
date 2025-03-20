// Đây là file mẫu để tham khảo

module com.library.frontend {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.library.frontend to javafx.fxml;
    exports com.library.frontend;
}