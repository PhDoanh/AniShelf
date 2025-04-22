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
 * Cửa sổ đăng ký tài khoản mới
 */
public class RegisterDialog extends Dialog {
    
    private TextField emailField;
    private TextField usernameField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private Button registerButton;
    private Hyperlink loginLink;
    private Label errorLabel;
    
    /**
     * Khởi tạo cửa sổ đăng ký
     */
    public RegisterDialog() {
        super("Đăng ký tài khoản AniShelf", "Tạo tài khoản mới để truy cập đầy đủ các tính năng của AniShelf");
        initializeRegisterComponents();
    }
    
    /**
     * Khởi tạo các thành phần của form đăng ký
     */
    private void initializeRegisterComponents() {
        // Form đăng ký
        GridPane form = new GridPane();
        form.setVgap(10);
        form.setHgap(10);
        form.setPadding(new Insets(20, 10, 10, 10));
        
        // Các trường nhập liệu
        emailField = new TextField();
        emailField.setPromptText("Email");
        
        usernameField = new TextField();
        usernameField.setPromptText("Tên đăng nhập");
        
        passwordField = new PasswordField();
        passwordField.setPromptText("Mật khẩu");
        
        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Nhập lại mật khẩu");
        
        // Các nhãn
        Label emailLabel = new Label("Email:");
        Label usernameLabel = new Label("Tên đăng nhập:");
        Label passwordLabel = new Label("Mật khẩu:");
        Label confirmPasswordLabel = new Label("Nhập lại mật khẩu:");
        
        // Thêm các thành phần vào form
        form.add(emailLabel, 0, 0);
        form.add(emailField, 1, 0);
        form.add(usernameLabel, 0, 1);
        form.add(usernameField, 1, 1);
        form.add(passwordLabel, 0, 2);
        form.add(passwordField, 1, 2);
        form.add(confirmPasswordLabel, 0, 3);
        form.add(confirmPasswordField, 1, 3);
        
        // Thông báo lỗi
        errorLabel = new Label();
        errorLabel.getStyleClass().add("error-message");
        errorLabel.setVisible(false);
        
        // Nút đăng ký
        registerButton = new Button("Đăng ký");
        registerButton.getStyleClass().add("primary-button");
        registerButton.setOnAction(e -> register());
        
        // Liên kết đến trang đăng nhập
        loginLink = new Hyperlink("Đã có tài khoản? Đăng nhập ngay");
        loginLink.setOnAction(e -> openLoginDialog());
        
        // Đặt nút và liên kết vào HBox để căn chỉnh
        HBox buttonBox = new HBox(10, registerButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        // Thêm form và các điều khiển vào VBox
        VBox registerBox = new VBox(10, form, errorLabel, buttonBox, loginLink);
        registerBox.setAlignment(Pos.CENTER);
        
        // Đặt vào trung tâm của dialog
        mainLayout.setCenter(registerBox);
        
        // Xóa nút đóng mặc định ở dưới cùng
        mainLayout.setBottom(null);
    }
    
    /**
     * Xử lý đăng ký
     * 
     * @return true nếu đăng ký thành công, false nếu thất bại
     */
    public boolean register() {
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        // Kiểm tra dữ liệu nhập
        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showErrorMessage("Vui lòng nhập đầy đủ thông tin đăng ký");
            return false;
        }
        
        if (!isValidEmail(email)) {
            showErrorMessage("Email không hợp lệ");
            return false;
        }
        
        if (!password.equals(confirmPassword)) {
            showErrorMessage("Mật khẩu nhập lại không khớp");
            return false;
        }
        
        // TODO: Gọi đến backend để đăng ký tài khoản
        // Hiện tại chỉ có mô phỏng đơn giản
        
        // Mô phỏng đăng ký thành công
        close();
        showSuccessMessage();
        return true;
    }
    
    /**
     * Kiểm tra email có hợp lệ hay không
     * 
     * @param email Email cần kiểm tra
     * @return true nếu email hợp lệ, false nếu không hợp lệ
     */
    private boolean isValidEmail(String email) {
        // Regex cơ bản để kiểm tra email
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
    
    /**
     * Hiển thị thông báo lỗi
     * 
     * @param message Nội dung lỗi
     */
    private void showErrorMessage(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    
    /**
     * Hiển thị thông báo đăng ký thành công
     */
    private void showSuccessMessage() {
        // TODO: Hiển thị thông báo đăng ký thành công
        System.out.println("Đăng ký thành công!");
        
        // Mở cửa sổ đăng nhập sau khi đăng ký thành công
        openLoginDialog();
    }
    
    /**
     * Mở cửa sổ đăng nhập
     */
    private void openLoginDialog() {
        close();
        LoginDialog loginDialog = new LoginDialog();
        loginDialog.show();
    }
}