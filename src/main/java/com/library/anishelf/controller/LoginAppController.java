package com.library.anishelf.controller;


import com.library.anishelf.service.command.Command;
import com.library.anishelf.service.command.LoginCommand;
import com.library.anishelf.dao.AccountDAO;
import com.library.anishelf.model.enums.Role;
import javafx.animation.TranslateTransition;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class LoginAppController extends BasicAppController {

    @FXML
    private Button forgotPasswordAppButton;

    @FXML
    private Button loginAppButton;

    @FXML
    private Button faceIDButton;


    @FXML
    private PasswordField passwordAppText;

    @FXML
    private Button registerAppButton;

    @FXML
    private Button swichtButton;

    @FXML
    public ImageView imageCondition;

    @FXML
    private Rectangle switchBarNavigation;

    @FXML
    private TextField usernameTextField;
    @FXML
    private StackPane mainContainer;

    private Role role = Role.NONE;
    protected static final ExecutorService executor = Executors.newFixedThreadPool(4);

    public void initialize() {
        initializeSwitchBar();
        initializeImageStatus();
    }

    private void switchRoleUser() {
        if (role.equals(Role.ADMIN)) {
            role = Role.NONE;
        } else if (role.equals(Role.NONE)) {
            role = Role.ADMIN;
        }
    }

    @FXML
    void handleForgotPasswordButton(ActionEvent event) {
        openForgotPasswordAppView();
    }

    @FXML
    void handleLoginButton(ActionEvent event) {
        String username = usernameTextField.getText();
        String password = passwordAppText.getText();
        if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
            Stage stage = (Stage) registerAppButton.getScene().getWindow();

            Command loginCommand = new LoginCommand(stage, role, username, password);
            commandProcessor.setCommand(loginCommand);
            commandProcessor.executeCommand();

        } else {
            CustomerAlter.showMessage("Không được để trống!");
        }
    }

    @FXML
    void handleRegisterButton(ActionEvent event) {
        openRegisterAppView();
    }

    @FXML
    void handleSwitchButton(ActionEvent event) {
        switchRoleUser();
        initializeSwitchBar();
        initializeImageStatus();
    }

    @FXML
    void onFaceIDButtonAction(ActionEvent event) {
        if (role.equals(Role.ADMIN)) {
            Task<Integer> loginbyFaceID = new Task<>() {
                protected Integer call() throws Exception {
                    return AccountDAO.getInstance().adminLoginByFaceID();
                }
            };

            loginbyFaceID.setOnSucceeded(event1 -> {
                try {
                    int adminID = loginbyFaceID.getValue(); // Lấy giá trị từ Task
                    if (adminID > 0) {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/AdminMenu.fxml"));
                        Parent root = fxmlLoader.load();
                        AdminSideMenuController controller = fxmlLoader.getController();
                        controller.setCurrentAdminId(adminID);
                        Scene scene = new Scene(root);
                        Stage stage = (Stage) registerAppButton.getScene().getWindow();
                        stage.setResizable(true);
                        stage.setWidth(stage.getWidth());
                        stage.setHeight(stage.getHeight());
                        stage.setScene(scene);
                        stage.show();
                    } else {
                        CustomerAlter.showMessage("Không nhận ra khuôn mặt.");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            executor.submit(loginbyFaceID);
        } else {
            Task<Integer> loginbyFaceID = new Task<>() {
                protected Integer call() throws Exception {
                    return AccountDAO.getInstance().userLoginByFaceID();
                }
            };

            loginbyFaceID.setOnSucceeded(event1 -> {
                try {
                    int memberID = loginbyFaceID.getValue();
                    if (memberID > 0) {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/UserMenu-view.fxml"));
                        Parent root = fxmlLoader.load();

                        UserMenuController userMenu = fxmlLoader.getController();
                        Stage stage = (Stage) registerAppButton.getScene().getWindow();
                        userMenu.setMemberID(memberID);
                        stage.setResizable(true);
                        stage.setWidth(stage.getWidth());
                        stage.setHeight(stage.getHeight());
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();
                    } else {
                        CustomerAlter.showMessage("Không nhận ra khuôn mặt.");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            executor.submit(loginbyFaceID);

        }
    }

    private void openForgotPasswordAppView() {
        loadAppView("/view/ForgotPassword-view.fxml", false);
    }

    private void openRegisterAppView() {
        loadAppView("/view/UserResign-view.fxml", false);
    }

    private void loadAppView(String fxmlPath, boolean resizable) {
        try {

            // Tải cửa sổ đăng ký
            Stage stage = (Stage) registerAppButton.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            stage.setResizable(true);
            stage.setWidth(stage.getWidth());
            stage.setHeight(stage.getHeight());
            stage.setScene(new Scene(root));

        } catch (IOException e) {
            System.out.println("Lỗi khi tải giao diện: " + e.getMessage());
        }
    }

    private void initializeSwitchBar() {
        TranslateTransition transition = new TranslateTransition();
        transition.setNode(switchBarNavigation);
        transition.setDuration(Duration.seconds(0.5));
        if (role.equals(Role.ADMIN)) {
            transition.setToX(-55);
        } else {
            transition.setToX(0);
        }
        transition.play();
    }

    private void initializeImageStatus() {
        if (role.equals(Role.ADMIN)) {
            String imagePath = "file:src/main/resources/image/customer/login/Admin.gif";
            imageCondition.setImage(new Image(imagePath));
        } else if (role.equals(Role.NONE)) {
            String imagePath = "file:src/main/resources/image/customer/login/User.gif";
            imageCondition.setImage(new Image(imagePath));
        }
    }

}
