package com.library.anishelf.controller;

import com.library.anishelf.service.command.Command;
import com.library.anishelf.service.command.CommandInvoker;
import com.library.anishelf.service.command.ResignCommand;
import com.library.anishelf.model.Person;
import com.library.anishelf.model.enums.Gender;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

import static com.almasb.fxgl.app.GameApplication.launch;


public class ResignAppController extends BasicAppController {

    @FXML
    private Button returnStepButton;

    @FXML
    private Button haveAccountButton;

    @FXML
    private DatePicker birthDateOfUser;

    @FXML
    private Button continueResignButton;

    @FXML
    private TextField emailTextField;

    @FXML
    private TextField firstNameTextField;

    @FXML
    private ChoiceBox<Gender> genderBoxContainer;

    @FXML
    private TextField lastNameTextField;


    @FXML
    private PasswordField passwordTextField;

    @FXML
    private TextField phoneNumberTextField;

    @FXML
    private PasswordField rewritePasswordText;

    @FXML
    private Button resignAppButton;

    @FXML
    private AnchorPane resignStep1PaneCotainer;

    @FXML
    private AnchorPane resignStep2PaneContainer;

    @FXML
    private Button returnLoginButton;


    @FXML
    private AnchorPane switchBarNavigate;

    @FXML
    private TextField usernameTextField;

    private boolean isBeingStep1;
    private CommandInvoker commandInvoker1 = new CommandInvoker();

    @FXML
    public void initialize() {
        isBeingStep1 = true;
        setSwitchBar();
        genderBoxContainer.getItems().addAll(Gender.MALE, Gender.FEMALE, Gender.OTHER);
    }

    @FXML
    void handleBackStepButtonClicked(ActionEvent event) {
        isBeingStep1 = !isBeingStep1;
        switchToStep1();
    }

    @FXML
    void handleContinueButtonClicked(ActionEvent event) {
        if(checkValidInformation()) {
            isBeingStep1 = !isBeingStep1;
            switchToStep2();
        }
    }

    @FXML
    private void handleResignButtonClicked(ActionEvent event) {
            if (checkValidInformation()) {

                Person person = createPersonObjectFromInput();
                String username = usernameTextField.getText();
                String password = passwordTextField.getText();

                Stage stage = (Stage) resignAppButton.getScene().getWindow();
                Command resignCommand = new ResignCommand(stage,person,username, password);
                commandInvoker1.setCommand(resignCommand);
                if(commandInvoker1.executeCommand()) {
                       openLoginView();
                }

            }

    }
    @FXML
    void handleHaveAccountButtonClicked(ActionEvent event) {
        boolean confirmYes = CustomerAlterApp.showAlterApp("Bạn có tài khoản rồi ư, thế đi đăng nhập nha?");
        if(confirmYes) {
            openLoginView();
        }
    }


    @FXML
    void handleReturnLoginButtonClicked(ActionEvent event) {
        openLoginView();
    }

    private void openLoginView() {
        try {
            Stage stage = (Stage) resignStep1PaneCotainer.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/view/UserLogin.fxml"));
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkValidInformation() {
        if(isBeingStep1) {
            // Bước 1: Kiểm tra thông tin cá nhân
            String firstName = firstNameTextField.getText().trim();
            String lastName = lastNameTextField.getText().trim();
            String birth = birthDateOfUser.getValue() != null ? birthDateOfUser.getValue().toString() : ""; // Kiểm tra ngày sinh
            Gender gender = genderBoxContainer.getSelectionModel().getSelectedItem();
            String email = emailTextField.getText().trim();

            // Kiểm tra tên
            if (firstName.isEmpty()) {
                CustomerAlterApp.showAlertMessage("Tên không được để trống.");
                return false;
            }

            if (lastName.isEmpty()) {
                CustomerAlterApp.showAlertMessage("Họ không được để trống.");
                return false;
            }

            // Kiểm tra ngày sinh
            if (birth.isEmpty()) {
                CustomerAlterApp.showAlertMessage( "Ngày sinh không được để trống.");
                return false;
            }

            // Kiểm tra giới tính
            if (gender == null) {
                CustomerAlterApp.showAlertMessage("Vui lòng chọn giới tính.");
                return false;
            }

            // Kiểm tra email
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) { // Biểu thức chính quy cho email
                CustomerAlterApp.showAlertMessage("Email không hợp lệ.");
                return false;
            }

            return true;

        } else {
            // Bước 2: Kiểm tra thông tin tài khoản
            String username = usernameTextField.getText().trim();
            String phoneNumber = phoneNumberTextField.getText().trim();
            String password = passwordTextField.getText().trim();
            String rePassword = rewritePasswordText.getText().trim();

            // Kiểm tra tên người dùng
            if (username.isEmpty()) {
                CustomerAlterApp.showAlertMessage("Tên người dùng không được để trống.");
                return false;
            }

            // Kiểm tra số điện thoại
            if (phoneNumber.isEmpty() || !phoneNumber.matches("\\d{10}")) { // Giả sử số điện thoại có 10 chữ số
                CustomerAlterApp.showAlertMessage("Số điện thoại không hợp lệ (cần có 10 chữ số).");
                return false;
            }

            // Kiểm tra mật khẩu
            if (password.isEmpty()) {
                CustomerAlterApp.showAlertMessage("Mật khẩu không được để trống.");
                return false;
            }

            // Kiểm tra xác nhận mật khẩu
            if (!password.equals(rePassword)) {
                CustomerAlterApp.showAlertMessage("Mật khẩu và xác nhận mật khẩu không khớp.");
                return false;
            }
            return true;
        }

    }

    private Person createPersonObjectFromInput() {
        Person person = new Person();
        person.setFirstName(lastNameTextField.getText());
        person.setLastName(firstNameTextField.getText());
        person.setDateOfBirth(birthDateOfUser.getValue().toString());
        person.setGender(genderBoxContainer.getValue());
        person.setEmail(emailTextField.getText());
        person.setPhone(phoneNumberTextField.getText());
        return person;
    }

    private void switchToStep1() {
        resignStep1PaneCotainer.setVisible(true);
        resignStep2PaneContainer.setVisible(false);
        returnStepButton.setVisible(false);
        setSwitchBar();
    }

    private void switchToStep2() {
        resignStep1PaneCotainer.setVisible(false);
        resignStep2PaneContainer.setVisible(true);
        returnStepButton.setVisible(true);
        setSwitchBar();
    }

    private void setSwitchBar() {
        TranslateTransition transition = new TranslateTransition();
        transition.setNode(switchBarNavigate);
        transition.setDuration(Duration.seconds(0.5));
        transition.setToX(isBeingStep1 ? 0 : 46); // Khoảng di chuyển dựa trên trạng thái
        transition.play();
    }


}
