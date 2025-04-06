module com.library.frontend {
    requires com.library.backend;
    requires javafx.controls;
    requires javafx.fxml;


    opens com.library.frontend to javafx.fxml;
    exports com.library.frontend;
}