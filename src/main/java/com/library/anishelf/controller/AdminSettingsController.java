package com.library.anishelf.controller;

import com.library.anishelf.util.config.AppConfigUtil;
import com.library.anishelf.util.config.AppInfo;
import com.library.anishelf.util.FaceidRecognizer;
import com.library.anishelf.util.FaceidUnregister;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AdminSettingsController {

    @FXML
    private Button passwordChangeButton;

    @FXML
    private Button closeSettingButton;

    @FXML
    private Button contactUsButton;

    @FXML
    private Button generalSettingFeatureButton;

    @FXML
    private AnchorPane generalSettingPane;

    @FXML
    private Button hihihiButton;

    @FXML
    private AnchorPane hihihiPane;

    @FXML
    private ImageView imageIBView;

    @FXML
    private Label ibLabel;

    @FXML
    private Button securityAppButton;

    @FXML
    private AnchorPane securityAppPane;

    @FXML
    private Button setFaceIDButton;

    @FXML
    private ChoiceBox<String> themeContainer;

    private Button currentActiveButton = null;

    private AppConfigUtil configManager;
    private AppInfo appSettings;
    private int adminId;

    public void setAdminId(int adminId) {
        this.adminId = adminId;
        themeContainer.getItems().addAll("Normal", "Dark", "Pink", "Gold");

        themeContainer.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals("Normal")) {
                CustomerAlter.showMessage("Muốn sử dụng chức năng này thì hãy nạp Vip nhé");
                themeContainer.setValue("Normal");
            }
        });

        configManager = new AppConfigUtil();
        try {
            appSettings = configManager.loadSettings();
            // Đảm bảo appSettings không null, nếu null thì tạo mặc định
            if (appSettings == null) {
                appSettings = new AppInfo("Normal", 50, "chua co", false);
            }
        } catch (Exception e) {
            // Xử lý bất kỳ lỗi nào có thể xảy ra khi tải cài đặt
            e.printStackTrace();
            appSettings = new AppInfo("Normal", 50, "chua co", false);
        }

        // Thiết lập giá trị cho theme box
        try {
            themeContainer.setValue(appSettings.getMode());
        } catch (Exception e) {
            // Xử lý nếu có lỗi khi thiết lập giá trị
            e.printStackTrace();
            themeContainer.setValue("Normal");
        }

        // Kiểm tra FaceID
        appSettings.setHaveFaceID(isHaveFaceID(adminId));
        if (!appSettings.isHaveFaceID()) {
            setFaceIDButton.setText("Thiết lập faceID");
            setFaceIDButton.setStyle("-fx-text-fill: black;");
        } else {
            setFaceIDButton.setText("Xóa faceID");
            setFaceIDButton.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    void handleContactRequest(ActionEvent event) {
        imageIBView.setVisible(true);
        ibLabel.setVisible(true);
        PauseTransition pause = new PauseTransition(Duration.seconds(3.5));
        pause.setOnFinished(event1 -> {
            imageIBView.setVisible(false);
            ibLabel.setVisible(false);
        });
        pause.play();
    }

    @FXML
    void handleThemeSelection(MouseEvent event) {

    }

    public void initialize() {

    }

    @FXML
    void handlePasswordChange(ActionEvent event) {
        CustomerAlter.showMessage("Oh no lỗi rồi. Hãy vào mục support để bọn tớ trợ giúp cậu nhé!");
    }

    @FXML
    void onGeneralSettingButtonAction(ActionEvent event) {
        hihihiPane.setVisible(false);
        generalSettingPane.setVisible(true);
        securityAppPane.setVisible(false);
        initalizeActiveButton(generalSettingFeatureButton);
    }

    @FXML
    void onHihihiButtonAction(ActionEvent event) {
        hihihiPane.setVisible(true);
        generalSettingPane.setVisible(false);
        securityAppPane.setVisible(false);
        initalizeActiveButton(hihihiButton);
    }

    @FXML
    void handleSecurityNavigation(ActionEvent event) {
        hihihiPane.setVisible(false);
        generalSettingPane.setVisible(false);
        securityAppPane.setVisible(true);
        initalizeActiveButton(securityAppButton);
    }

    @FXML
    void onSetFaceIDButtonAction(ActionEvent event) {
        if (setFaceIDButton.getText().equals("Thiết lập faceID")) {
            setFaceID(adminId);
        } else if (setFaceIDButton.getText().equals("Xóa faceID")) {
            FaceidUnregister.unregisterUser(adminId +"", FaceidUnregister.ADMIN);
            setFaceIDButton.setText("Thiết lập faceID");
            setFaceIDButton.setStyle("-fx-text-fill: black;");
            appSettings.setHaveFaceID(false);
            configManager.saveSettings(appSettings);
        }
    }

    @FXML
    void handleClose(ActionEvent event) {
        Stage stage = (Stage) closeSettingButton.getScene().getWindow();
        stage.close();
    }

    public void resetSettingsView() {
        hihihiPane.setVisible(false);
        generalSettingPane.setVisible(true);
        securityAppPane.setVisible(false);
        initalizeActiveButton(generalSettingFeatureButton);
    }

    private void initalizeActiveButton(Button button) {
        if (currentActiveButton != null) {
            // Reset lại kiểu dáng của nút trước đó
            currentActiveButton.setStyle("");
        }

        // Gán nút hiện tại vào trạng thái đang hoạt động
        currentActiveButton = button;

        // Đặt kiểu dáng in đậm màu cho nút đang được chọn
        currentActiveButton.setStyle("-fx-background-color: #FFF;"); // Kiểu CSS
    }

    private boolean isHaveFaceID(int adminID) {
        try {
            // Đường dẫn tới thư mục chứa dữ liệu FaceID chung
            Path faceIDFolder = (Path) Paths.get("src/main/resources/face/training_data");
            // Đường dẫn tới thư mục riêng của adminId
            Path newFaceIDFolder = faceIDFolder.resolve(String.valueOf(adminID));

            // Kiểm tra nếu thư mục chung không tồn tại thì tạo nó
            if (Files.notExists(faceIDFolder)) {
                Files.createDirectories(faceIDFolder);
            }

            // Kiểm tra thư mục FaceID của adminId
            if (Files.exists(newFaceIDFolder)) {
                return true; // Thư mục đã tồn tại
            } else {
                System.out.println("Khong coooooooo");
                return false;
            }
        } catch (Exception e) {
            // Xử lý ngoại lệ (nếu có lỗi)
            e.printStackTrace();
            return false;
        }
    }

    private void setFaceID(int adminID) {
        try {
            // Đường dẫn tới thư mục chứa dữ liệu FaceID chung
            Path faceIDFolder = (Path) Paths.get("src/main/resources/face/training_data");
            // Đường dẫn tới thư mục riêng của adminId
            Path newFaceIDFolder = faceIDFolder.resolve(String.valueOf(adminID));

            // Kiểm tra nếu thư mục chung không tồn tại thì tạo nó
            if (Files.notExists(faceIDFolder)) {
                Files.createDirectories(faceIDFolder);
            }

            // Kiểm tra thư mục FaceID của adminId
            if (!Files.exists(newFaceIDFolder)) {
                Files.createDirectories(newFaceIDFolder);
            }
            FaceidRecognizer.registerUser(adminID+"", FaceidRecognizer.ADMIN);
            setFaceIDButton.setText("Xóa faceID");
            setFaceIDButton.setStyle("-fx-text-fill: red;");
            appSettings.setHaveFaceID(true);
            configManager.saveSettings(appSettings);
        } catch (Exception e) {
            // Xử lý ngoại lệ (nếu có lỗi)
            e.printStackTrace();
        }
    }

}
