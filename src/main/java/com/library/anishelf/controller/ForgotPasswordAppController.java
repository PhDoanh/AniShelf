package com.library.anishelf.controller;

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

public class ForgotPasswordAppController extends BasicAppController {

    @FXML
    private Button continueVerifyButton;

    @FXML
    private TextField emailTextField;

    @FXML
    private HBox enterEmailHBoxContainer;

    @FXML
    private HBox enterVerifyHBoxContainer;

    @FXML
    private TextField otpTextField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private Button rewritePasswordButton;

    @FXML
    private HBox rewritePasswordPane;

    @FXML
    private TextField rewirtePasswordTextField;

    @FXML
    private Button returnToLoginButton;

    @FXML
    private Button sendCodeAgainButton;

    @FXML
    private Button toEmailStepButton;

    @FXML
    private Button toLoginAppButton;

    @FXML
    private Button verifyAppButton;

    private boolean isEmailEntryStep;
    private boolean isOTPVerificationStep;
    private boolean isRewritePasswordStep;

    public void initialize() {
        isEmailEntryStep = true;
    }

    @FXML
    void handleReturnLoginButtonClicked(ActionEvent event) {
        openLoginView();
    }

    @FXML
    void handleContinueButtonClicked(ActionEvent event) {
        if(checkEmailValid()) {
            isEmailEntryStep = !isEmailEntryStep;
            isOTPVerificationStep = true;
            isRewritePasswordStep = false;
            enterEmailHBoxContainer.setVisible(false);
            enterVerifyHBoxContainer.setVisible(true);
            rewritePasswordPane.setVisible(false);
        }
    }

    @FXML
    void handleSendAgainButtonClicked(ActionEvent event) {
        if(checkEmailValid()) {
            CustomerAlterApp.showAlertMessage("Đã gửi lại OTP");
        }
    }

    @FXML
    void handleVerifyButtonClicked(ActionEvent event) {
        if(checkOTPCorrect()) {
            isEmailEntryStep = false;
            isOTPVerificationStep = false;
            isRewritePasswordStep = true;
            CustomerAlterApp.showAlertMessage("OTP đúng rồi ó");
            enterEmailHBoxContainer.setVisible(false);
            enterVerifyHBoxContainer.setVisible(false);
            rewritePasswordPane.setVisible(true);
        }
    }

    @FXML
    void handleRePasswordButtonClicked(ActionEvent event) {
        if(checkPasswordTheSame()) {
            try {
            AccountDAO.getInstance().changePassword(emailTextField.getText(), passwordTextField.getText());
            CustomerAlterApp.showAlertMessage("Đổi mật khẩu rồi đó, đừng quên nữa nha");
            openLoginView();
            } catch (Exception e) {
                CustomerAlterApp.showAlertMessage("Không đổi được mật khẩu");
            }
        }
    }

    private boolean checkPasswordTheSame() {
        String password1 = passwordTextField.getText();
        String password2 = rewirtePasswordTextField.getText();
        if(password1.isEmpty() || password1.equals(" ")) {
            CustomerAlterApp.showAlertMessage("Đổi mật khẩu mà trống đổi làm gì");
            return false;
        }
        if(!password1.equals(password2)) {
            CustomerAlterApp.showAlertMessage("Nhập lại mật khẩu không khớp");
            return false;
        }
        return true;
    }

    private boolean checkEmailValid() {
        try {
            return AccountDAO.getInstance().resetPassword(emailTextField.getText().toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            CustomerAlterApp.showAlertMessage("Email không tồn tại");
        }
        return false;
    }

    private boolean checkOTPCorrect() {
        String otp = otpTextField.getText();
        try {
            return AccountDAO.getInstance().verifyOTP(emailTextField.getText(), otp);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            CustomerAlterApp.showAlertMessage("OTP không hợp lệ");
        }
        return false;
    }

    private void openLoginView() {
        try {
            Stage stage = (Stage) verifyAppButton.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/view/UserLogin.fxml"));
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleToLoginButtonClicked(ActionEvent actionEvent) {
        openLoginView();
    }

    public void handleToEmailButtonClicked(ActionEvent actionEvent) {
        isEmailEntryStep = !isEmailEntryStep;
        isOTPVerificationStep = true;
        isRewritePasswordStep = false;
        enterEmailHBoxContainer.setVisible(true);
        enterVerifyHBoxContainer.setVisible(false);
        rewritePasswordPane.setVisible(false);
    }
}
