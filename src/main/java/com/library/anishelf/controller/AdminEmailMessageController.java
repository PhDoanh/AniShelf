package com.library.anishelf.controller;

import com.library.anishelf.util.NotificationManagerUtil;
import com.library.anishelf.service.ServiceHandler;
import com.library.anishelf.service.EmailService;
import com.library.anishelf.util.EmailUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * The type Admin email message controller.
 */
public class AdminEmailMessageController extends BasicController {
    private EmailUtil emailUtil;

    @FXML
    private TextArea detailText;

    @FXML
    private Button sendButton;

    @FXML
    private TextField toText;

    @FXML
    private TextField topicText;

    /**
     * On send button action.
     *
     * @param event the event
     */
    @FXML
    void onSendButtonAction(ActionEvent event) {
        String to = toText.getText();
        String detail = detailText.getText();
        if (checkEmail(to, detail)) {
            ServiceHandler mailServiceHandler = new EmailService(to, topicText.getText(), detail);
            serviceInvoker.setServiceHandler(mailServiceHandler);
            if (serviceInvoker.invokeService()) {
                System.out.println("Đã gửi mail! ");
                loadStartStatus();
            }
        }
    }

    private boolean checkEmail(String toEmail, String detail) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (toEmail == null || !toEmail.matches(emailRegex)) {
            NotificationManagerUtil.showInfo("Email không hợp lệ");
            return false;
        }

        if (detail == null || detail.isEmpty()) {
            NotificationManagerUtil.showInfo("Nội dung mail không được để trống");
            return false;
        }

        return true;
    }

    private void loadStartStatus() {
        topicText.setText(null);
        detailText.setText(null);
        toText.setText(null);
    }

    /**
     * Sets to email.
     *
     * @param toEmail the to email
     */
    public void setToEmail(String toEmail) {
        this.toText.setText(toEmail);
    }
}
