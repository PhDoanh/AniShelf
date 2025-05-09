package com.library.anishelf.controller;

import com.library.anishelf.util.config.AppConfigUtil;
import com.library.anishelf.util.config.AppInfo;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AdminSettingPageController {

    @FXML
    private Button changePasswordButton;

    @FXML
    private Button closeButton;

    @FXML
    private Button contactButton;

    @FXML
    private Button generalSettingButton;

    @FXML
    private AnchorPane generalSettingPane;

    @FXML
    private Button hihiButton;

    @FXML
    private AnchorPane hihiPane;

    @FXML
    private Label ibLabel;

    @FXML
    private Button securityButton;

    @FXML
    private AnchorPane securityPane;

    @FXML
    private ChoiceBox<String> themeBox;

    private Button currentActiveButton = null;

    private AppConfigUtil appConfigUtil;
    private AppInfo settings;
    private int adminID;

    public void setAdminID(int adminID) {
        this.adminID = adminID;
        themeBox.getItems().addAll("Normal", "Dark", "Pink", "Gold");

        themeBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals("Normal")) {
                CustomerAlter.showMessage("Muốn sử dụng chức năng này thì hãy nạp Vip nhé");
                themeBox.setValue("Normal");
            }
        });

        appConfigUtil = new AppConfigUtil();
        try {
            settings = appConfigUtil.loadSettings();
            // Đảm bảo settings không null, nếu null thì tạo mặc định
            if (settings == null) {
                settings = new AppInfo("Normal", 50, "chua co");
            }
        } catch (Exception e) {
            // Xử lý bất kỳ lỗi nào có thể xảy ra khi tải cài đặt
            e.printStackTrace();
            settings = new AppInfo("Normal", 50, "chua co");
        }

        // Thiết lập giá trị cho theme box
        try {
            themeBox.setValue(settings.getMode());
        } catch (Exception e) {
            // Xử lý nếu có lỗi khi thiết lập giá trị
            e.printStackTrace();
            themeBox.setValue("Normal");
        }
    }

    @FXML
    void onContactButtonAction(ActionEvent event) {
        ibLabel.setVisible(true);
        PauseTransition pause = new PauseTransition(Duration.seconds(3.5));
        pause.setOnFinished(event1 -> {
            ibLabel.setVisible(false);
        });
        pause.play();
    }

    @FXML
    void onThemeBoxButtonAction(MouseEvent event) {

    }

    public void initialize() {

    }

    @FXML
    void onChangePasswordButtonAction(ActionEvent event) {
        CustomerAlter.showMessage("Oh no lỗi rồi. Hãy vào mục support để bọn tớ trợ giúp cậu nhé!");
    }

    @FXML
    void onGeneralSettingButtonAction(ActionEvent event) {
        hihiPane.setVisible(false);
        generalSettingPane.setVisible(true);
        securityPane.setVisible(false);
        setActiveButton(generalSettingButton);
    }

    @FXML
    void onHihiButtonAction(ActionEvent event) {
        hihiPane.setVisible(true);
        generalSettingPane.setVisible(false);
        securityPane.setVisible(false);
        setActiveButton(hihiButton);
    }

    @FXML
    void onSecurityButtonAction(ActionEvent event) {
        hihiPane.setVisible(false);
        generalSettingPane.setVisible(false);
        securityPane.setVisible(true);
        setActiveButton(securityButton);
    }

    @FXML
    void onCloseButtonAction(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    public void reset() {
        hihiPane.setVisible(false);
        generalSettingPane.setVisible(true);
        securityPane.setVisible(false);
        setActiveButton(generalSettingButton);
    }

    private void setActiveButton(Button button) {
        if (currentActiveButton != null) {
            // Reset lại kiểu dáng của nút trước đó
            currentActiveButton.setStyle("");
        }

        // Gán nút hiện tại vào trạng thái đang hoạt động
        currentActiveButton = button;

        // Đặt kiểu dáng in đậm màu cho nút đang được chọn
        currentActiveButton.setStyle("-fx-background-color: #FFF;"); // Kiểu CSS
    }
}
