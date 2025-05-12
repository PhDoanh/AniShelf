package com.library.anishelf.controller;

import com.library.anishelf.util.NotificationManagerUtil;
import com.library.anishelf.service.ServiceHandler;
import com.library.anishelf.service.AuthenticationService;
import com.library.anishelf.model.enums.Role;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import atlantafx.base.controls.ToggleSwitch;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * The type User login page controller.
 */
public class UserLoginPageController extends BasicController {

    @FXML
    private Hyperlink forgotPasswordLink;

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordText;

    @FXML
    private Hyperlink registerLink;

    @FXML
    private ToggleSwitch roleSwitch;

    @FXML
    private TextField usernameText;

    @FXML
    private StackPane mainPane;

    private Role role = Role.NONE;
    /**
     * The constant executor.
     */
    protected static final ExecutorService executor = Executors.newFixedThreadPool(4);

    /**
     * Initialize.
     */
    public void initialize() {
        // Initial role is NONE (User), toggle switch should be off
        roleSwitch.setSelected(false);

        // Add listener to update role based on toggle state
        roleSwitch.selectedProperty().addListener((observable, oldValue, newValue) -> {
            role = newValue ? Role.ADMIN : Role.NONE;
        });

        // Apply styles to components
        loginButton.getStyleClass().add("accent");
        usernameText.getStyleClass().add("rounded");
        passwordText.getStyleClass().add("rounded");
    }

    /**
     * On forgot password button action.
     *
     * @param event the event
     */
    @FXML
    void onForgotPasswordButtonAction(ActionEvent event) {
        openForgotPasswordView();
    }

    /**
     * On login button action.
     *
     * @param event the event
     */
    @FXML
    void onLoginButtonAction(ActionEvent event) {
        String username = usernameText.getText();
        String password = passwordText.getText();
        if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
            Stage stage = (Stage) loginButton.getScene().getWindow();

            ServiceHandler loginServiceHandler = new AuthenticationService(stage, role, username, password);
            serviceInvoker.setServiceHandler(loginServiceHandler);
            serviceInvoker.invokeService();

        } else {
            NotificationManagerUtil.showInfo("Thông tin đăng nhập không được để trống");
        }
    }

    /**
     * On register button action.
     *
     * @param event the event
     */
    @FXML
    void onRegisterButtonAction(ActionEvent event) {
        openRegisterView();
    }

    /**
     * On role switch action.
     *
     * @param event the event
     */
    @FXML
    void onRoleSwitchAction(MouseEvent event) {
        // The role is updated automatically via the listener we added in initialize()
    }

    private void openForgotPasswordView() {
        loadView("/view/ForgotPasswordPage.fxml", false);
    }

    private void openRegisterView() {
        loadView("/view/RegistrationPage.fxml", false);
    }

    private void loadView(String fxmlPath, boolean resizable) {
        try {
            // Tải cửa sổ đăng ký
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            stage.setResizable(true);
            stage.setWidth(stage.getWidth());
            stage.setHeight(stage.getHeight());
            stage.setScene(new Scene(root));

        } catch (IOException e) {
            System.out.println("Lỗi khi tải giao diện: " + e.getMessage());
        }
    }
}
