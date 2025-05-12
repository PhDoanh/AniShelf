package com.library.anishelf.controller;

import com.library.anishelf.util.NotificationManagerUtil;
import com.library.anishelf.service.RegistrationService;
import com.library.anishelf.service.ServiceHandler;
import com.library.anishelf.service.ServiceInvoker;
import com.library.anishelf.model.Person;
import com.library.anishelf.model.enums.Gender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The type Registration page controller.
 */
public class RegistrationPageController extends BasicController {

    @FXML
    private Hyperlink backStepLink;

    @FXML
    private Hyperlink haveAccountButton;

    @FXML
    private DatePicker birthDate;

    @FXML
    private Button continueButton;

    @FXML
    private TextField emailText;

    @FXML
    private TextField firstNameText;

    @FXML
    private ComboBox<Gender> genderBox;

    @FXML
    private TextField lastNameText;

    @FXML
    private PasswordField passwordText;

    @FXML
    private TextField phoneNumberText;

    @FXML
    private PasswordField rePasswordText;

    @FXML
    private Button resignButton;

    @FXML
    private AnchorPane resignStep1Pane;

    @FXML
    private AnchorPane resignStep2Pane;

    @FXML
    private TextField usernameText;

    private boolean isStep1;
    private ServiceInvoker serviceInvoker = new ServiceInvoker();

    /**
     * Initialize.
     */
    @FXML
    public void initialize() {
        isStep1 = true;

        // Apply AtlantaFX styling to buttons
        continueButton.getStyleClass().add("accent");
        resignButton.getStyleClass().add("accent");

        // Initialize gender combo box
        genderBox.getItems().addAll(Gender.MALE, Gender.FEMALE, Gender.OTHER);
        genderBox.getStyleClass().add("rounded");
    }

    /**
     * On back step action.
     *
     * @param event the event
     */
    @FXML
    void onBackStepAction(ActionEvent event) {
        isStep1 = true;
        switchToStep1();
    }

    /**
     * On continue button action.
     *
     * @param event the event
     */
    @FXML
    void onContinueButtonAction(ActionEvent event) {
        if (checkInformation()) {
            isStep1 = false;
            switchToStep2();
        }
    }

    @FXML
    private void onResignButtonAction(ActionEvent event) {
        if (checkInformation()) {
            Person person = createPersonFromInput();
            String username = usernameText.getText();
            String password = passwordText.getText();

            Stage stage = (Stage) resignButton.getScene().getWindow();
            ServiceHandler resignServiceHandler = new RegistrationService(stage, person, username, password);
            serviceInvoker.setServiceHandler(resignServiceHandler);
            if (serviceInvoker.invokeService()) {
                openLoginView();
            }
        }
    }

    /**
     * On have account button action.
     *
     * @param event the event
     */
    @FXML
    void onHaveAccountButtonAction(ActionEvent event) {
        openLoginView();
    }

    private void openLoginView() {
        try {
            Stage stage = (Stage) (isStep1 ? resignStep1Pane : resignStep2Pane).getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/view/UserLoginPage.fxml"));
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkInformation() {
        if (isStep1) {
            // Bước 1: Kiểm tra thông tin cá nhân
            String firstName = firstNameText.getText().trim();
            String lastName = lastNameText.getText().trim();
            String birth = birthDate.getValue() != null ? birthDate.getValue().toString() : ""; // Kiểm tra ngày sinh
            Gender gender = genderBox.getSelectionModel().getSelectedItem();
            String email = emailText.getText().trim();

            if (firstName.isEmpty()) {
                NotificationManagerUtil.showInfo("Họ không được để trống.");
                return false;
            }

            if (lastName.isEmpty()) {
                NotificationManagerUtil.showInfo("Tên không được để trống.");
                return false;
            }

            if (birth.isEmpty()) {
                NotificationManagerUtil.showInfo("Ngày sinh không được để trống.");
                return false;
            }

            if (gender == null) {
                NotificationManagerUtil.showInfo("Vui lòng chọn giới tính.");
                return false;
            }

            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) { // Biểu thức chính quy cho email
                NotificationManagerUtil.showInfo("Email không hợp lệ.");
                return false;
            }

            return true;

        } else {
            // Bước 2: Kiểm tra thông tin tài khoản
            String username = usernameText.getText().trim();
            String phoneNumber = phoneNumberText.getText().trim();
            String password = passwordText.getText().trim();
            String rePassword = rePasswordText.getText().trim();

            if (username.isEmpty()) {
                NotificationManagerUtil.showInfo("Tên người dùng không được để trống.");
                return false;
            }

            if (phoneNumber.isEmpty() || !phoneNumber.matches("\\d{10}")) {
                NotificationManagerUtil.showInfo("Số điện thoại không hợp lệ (cần 10 chữ số)");
                return false;
            }

            if (password.isEmpty()) {
                NotificationManagerUtil.showInfo("Mật khẩu không được để trống.");
                return false;
            }

            if (!password.equals(rePassword)) {
                NotificationManagerUtil.showInfo("Mật khẩu không khớp.");
                return false;
            }
            return true;
        }
    }

    private Person createPersonFromInput() {
        Person person = new Person();
        person.setFirstName(lastNameText.getText());
        person.setLastName(firstNameText.getText());
        person.setBirthdate(birthDate.getValue().toString());
        person.setGender(genderBox.getValue());
        person.setEmail(emailText.getText());
        person.setPhone(phoneNumberText.getText());
        return person;
    }

    private void switchToStep1() {
        resignStep1Pane.setVisible(true);
        resignStep2Pane.setVisible(false);
        backStepLink.setVisible(false);
    }

    private void switchToStep2() {
        resignStep1Pane.setVisible(false);
        resignStep2Pane.setVisible(true);
        backStepLink.setVisible(true);
    }
}
