package com.library.anishelf.controller;

import com.library.anishelf.util.NotificationManagerUtil;
import com.library.anishelf.dao.AccountDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ForgotPasswordPageController extends BasicController {

    @FXML
    private Button continueButton;

    @FXML
    private TextField emailText;

    @FXML
    private HBox enterEmailHBox;

    @FXML
    private HBox enterVerifyHBox;

    @FXML
    private TextField otpText;

    @FXML
    private TextField passwordText;

    @FXML
    private Button rePasswordButton;

    @FXML
    private HBox rePasswordPane;

    @FXML
    private TextField rePasswordText;

    @FXML
    private Button returnLoginButton;

    @FXML
    private Button sendAgainButton;

    @FXML
    private Button toEmailButton;

    @FXML
    private Button verifyButton;

    private boolean isStep1;
    private boolean isStep2;
    private boolean isStep3;

    public void initialize() {
        isStep1 = true;
    }

    @FXML
    void onReturnLoginButtonAction(ActionEvent event) {
        openLoginView();
    }

    @FXML
    void onContinueButtonAction(ActionEvent event) {
        if(checkEmail()) {
            isStep1 = !isStep1;
            isStep2 = true;
            isStep3 = false;
            enterEmailHBox.setVisible(false);
            enterVerifyHBox.setVisible(true);
            rePasswordPane.setVisible(false);
        }
    }

    @FXML
    void onSendAgainButton(ActionEvent event) {
        if(checkEmail()) {
            NotificationManagerUtil.showInfo("Đã gửi lại mã OTP");
        }
    }

    @FXML
    void onVerifyButtonAction(ActionEvent event) {
        if(checkOTP()) {
            isStep1 = false;
            isStep2 = false;
            isStep3 = true;
            NotificationManagerUtil.showInfo("Xác minh OTP thành công");
            enterEmailHBox.setVisible(false);
            enterVerifyHBox.setVisible(false);
            rePasswordPane.setVisible(true);
        }
    }

    @FXML
    void onRePasswordButtonAction(ActionEvent event) {
        if(checkPassword()) {
            try {
            AccountDAO.getInstance().changePassword(emailText.getText(),passwordText.getText());
            NotificationManagerUtil.showInfo("Đổi mật khẩu thành công");
            openLoginView();
            } catch (Exception e) {
                NotificationManagerUtil.showError("Đổi mật khẩu thất bại");
            }
        }
    }

    private boolean checkPassword() {
        String password1 = passwordText.getText();
        String password2 = rePasswordText.getText();
        if(password1.isEmpty() || password1.equals(" ")) {
            NotificationManagerUtil.showInfo("Mật khẩu không được để trống");
            return false;
        }
        if(!password1.equals(password2)) {
            NotificationManagerUtil.showInfo("Mật khẩu không khớp");
            return false;
        }
        return true;
    }

    private boolean checkEmail() {
        try {
            return AccountDAO.getInstance().initiatePasswordReset(emailText.getText().toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            NotificationManagerUtil.showInfo("Email không tồn tại!");
        }
        return false;
    }

    private boolean checkOTP() {
        String otp = otpText.getText();
        try {
            return AccountDAO.getInstance().validateOTP(emailText.getText(), otp);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            NotificationManagerUtil.showInfo("Mã OTP không hợp lệ");
        }
        return false;
    }

    private void openLoginView() {
        try {
            Stage stage = (Stage) verifyButton.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/view/UserLoginPage.fxml"));
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onToEmailButtonAction(ActionEvent actionEvent) {
        isStep1 = !isStep1;
        isStep2 = true;
        isStep3 = false;
        enterEmailHBox.setVisible(true);
        enterVerifyHBox.setVisible(false);
        rePasswordPane.setVisible(false);
    }
}
