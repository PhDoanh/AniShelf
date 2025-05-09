package com.library.anishelf.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class NotificationPopupController {

    @FXML
    private Label alertMessageLabel;

    @FXML
    private Button cancelButton;

    @FXML
    private Button confirmButton;

    private boolean userConfirmed = false;

    @FXML
    void onCancelButtonAction(ActionEvent event) {
        closeAlert();
    }

    @FXML
    void onConfirmButtonAction(ActionEvent event) {
        userConfirmed = true;
        closeAlert();
    }

    public void setAlertMessage(String message) {
        userConfirmed = false;
        alertMessageLabel.setText(message);
    }

    private void closeAlert() {
        Stage stage = (Stage) alertMessageLabel.getScene().getWindow();
        stage.close();
    }

    public boolean isUserConfirmed() {
        return userConfirmed;
    }

    public void reset() {
        confirmButton.setText("Xác nhận");
        cancelButton.setText("Hủy");
        userConfirmed = false; // Đặt lại xác nhận
    }

    public void setNotificationMode() {
        confirmButton.setText("Ok");
        cancelButton.setText("Okeee");
    }
}
