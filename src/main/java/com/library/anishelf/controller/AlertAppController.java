package com.library.anishelf.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AlertAppController {

    @FXML
    private Label appAlertMessageLabel;

    @FXML
    private Button cancelAlertButton;

    @FXML
    private Button confirmAlertButton;

    private boolean userAlertConfirmed = false;

    @FXML
    void handleCancelButton(ActionEvent event) {
        closeAppAlert();
    }

    @FXML
    void handleConfirmButton(ActionEvent event) {
        userAlertConfirmed = true;
        closeAppAlert();
    }

    public void setAppAlertMessage(String message) {
        userAlertConfirmed = false;
        appAlertMessageLabel.setText(message);
    }

    private void closeAppAlert() {
        Stage stage = (Stage) appAlertMessageLabel.getScene().getWindow();
        stage.close();
    }

    public boolean isUserAlertConfirmed() {
        return userAlertConfirmed;
    }

    public void resetAlert() {
        confirmAlertButton.setText("Xác nhận");
        cancelAlertButton.setText("Hủy");
        userAlertConfirmed = false; // Đặt lại xác nhận
    }

    public void setNotificationAppMode() {
        confirmAlertButton.setText("Ok");
        cancelAlertButton.setText("Okeee");
    }
}
