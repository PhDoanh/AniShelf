package com.library.frontend.models;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Cửa sổ đăng nhập vào hệ thống
 */
public class LoginDialog extends Dialog {
    
    private TextField usernameOrEmailField;
    private PasswordField passwordField;
    private Button loginButton;
    private Hyperlink registerLink;
    
    /**
     * Khởi tạo cửa sổ đăng nhập
     */
    public LoginDialog() {
        super("Đăng nhập AniShelf", "Đăng nhập để truy cập đầy đủ các tính năng của AniShelf");
        initializeLoginComponents();
    }
    
    /**
     * Khởi tạo các thành phần của form đăng nhập
     */
    private void initializeLoginComponents() {
        // Form đăng nhập
        GridPane form = new GridPane();
        form.setVgap(10);
        form.setHgap(10);
        form.setPadding(new Insets(20, 10, 10, 10));
        
        // Các trường nhập liệu
        usernameOrEmailField = new TextField();
        usernameOrEmailField.setPromptText("Email hoặc tên đăng nhập");
        
        passwordField = new PasswordField();
        passwordField.setPromptText("Mật khẩu");
        
        // Các nhãn
        Label usernameLabel = new Label("Email/Tên đăng nhập:");
        Label passwordLabel = new Label("Mật khẩu:");
        
        // Thêm các thành phần vào form
        form.add(usernameLabel, 0, 0);
        form.add(usernameOrEmailField, 1, 0);
        form.add(passwordLabel, 0, 1);
        form.add(passwordField, 1, 1);
        
        // Nút đăng nhập
        loginButton = new Button("Đăng nhập");
        loginButton.getStyleClass().add("primary-button");
        loginButton.setOnAction(e -> login());
        
        // Liên kết đến trang đăng ký
        registerLink = new Hyperlink("Chưa có tài khoản? Đăng ký ngay");
        registerLink.setOnAction(e -> openRegisterDialog());
        
        // Đặt nút và liên kết vào HBox để căn chỉnh
        HBox buttonBox = new HBox(10, loginButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        // Thêm form và các điều khiển vào VBox
        VBox loginBox = new VBox(15, form, buttonBox, registerLink);
        loginBox.setAlignment(Pos.CENTER);
        
        // Đặt vào trung tâm của dialog
        mainLayout.setCenter(loginBox);
        
        // Xóa nút đóng mặc định ở dưới cùng
        mainLayout.setBottom(null);
    }
    
    /**
     * Xử lý đăng nhập
     * 
     * @return true nếu đăng nhập thành công, false nếu thất bại
     */
    public boolean login() {
        String usernameOrEmail = usernameOrEmailField.getText().trim();
        String password = passwordField.getText();
        
        // TODO: Gọi đến backend để xác thực thông tin đăng nhập
        // Hiện tại chỉ có mô phỏng đơn giản
        if (usernameOrEmail.isEmpty() || password.isEmpty()) {
            showErrorMessage("Vui lòng nhập đầy đủ thông tin đăng nhập");
            return false;
        }
        
        // Mô phỏng xác thực đơn giản (sẽ được thay thế bằng logic thực tế)
        if ("admin".equals(usernameOrEmail) && "admin".equals(password)) {
            close();
            return true;
        } else if ("member".equals(usernameOrEmail) && "member".equals(password)) {
            close();
            return true;
        } else {
            showErrorMessage("Tên đăng nhập hoặc mật khẩu không đúng");
            return false;
        }
    }
    
    /**
     * Hiển thị thông báo lỗi
     * 
     * @param errorMessage Nội dung lỗi
     */
    private void showErrorMessage(String errorMessage) {
        Label errorLabel = new Label(errorMessage);
        errorLabel.getStyleClass().add("error-message");
        // TODO: Hiển thị thông báo lỗi trong dialog
    }
    
    /**
     * Mở cửa sổ đăng ký
     */
    public void openRegisterDialog() {
        close();
        RegisterDialog registerDialog = new RegisterDialog();
        registerDialog.show();
    }
}