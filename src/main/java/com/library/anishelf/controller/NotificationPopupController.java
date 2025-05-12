package com.library.anishelf.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * The type Notification popup controller.
 */
public class NotificationPopupController {

    @FXML
    private Label alertMessageLabel;

    @FXML
    private Button cancelButton;

    @FXML
    private Button confirmButton;

    private boolean userConfirmed = false;

    /**
     * On cancel button action.
     *
     * @param event the event
     */
    @FXML
    void onCancelButtonAction(ActionEvent event) {
        closeAlert();
    }

    /**
     * On confirm button action.
     *
     * @param event the event
     */
    @FXML
    void onConfirmButtonAction(ActionEvent event) {
        userConfirmed = true;
        closeAlert();
    }

    /**
     * Sets alert message.
     *
     * @param message the message
     */
    public void setAlertMessage(String message) {
        userConfirmed = false;
        alertMessageLabel.setText(message);
    }

    private void closeAlert() {
        Stage stage = (Stage) alertMessageLabel.getScene().getWindow();
        stage.close();
    }

    /**
     * Is user confirmed boolean.
     *
     * @return the boolean
     */
    public boolean isUserConfirmed() {
        return userConfirmed;
    }

    /**
     * Reset.
     */
    public void reset() {
        confirmButton.setText("Xác nhận");
        cancelButton.setText("Hủy");
        userConfirmed = false; // Đặt lại xác nhận
    }

    /**
     * Sets notification mode.
     */
    public void setNotificationMode() {
        confirmButton.setText("Ok");
        cancelButton.setText("Okeee");
    }
}
