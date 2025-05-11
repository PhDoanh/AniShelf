package com.library.anishelf.service;

import com.library.anishelf.util.NotificationManagerUtil;
import com.library.anishelf.util.EmailUtil;

/**
 * The type Email service.
 */
public class EmailService implements ServiceHandler {
    private String recipientEmail;
    private String subject;
    private String emailBody;

    /**
     * Instantiates a new Email service.
     *
     * @param recipientEmail the recipient email
     * @param subject        the subject
     * @param emailBody      the email body
     */
    public EmailService(String recipientEmail, String subject, String emailBody) {
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.emailBody = emailBody;
    }

    @Override
    public boolean handleRequest() {
        EmailUtil.sendEmailAsync(recipientEmail, subject, emailBody);
        NotificationManagerUtil.showInfo("Email đã được gửi tới " + recipientEmail);
        return true;
    }

}
