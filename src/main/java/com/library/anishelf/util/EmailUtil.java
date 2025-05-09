package com.library.anishelf.util;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * The type Email util.
 */
public class EmailUtil implements Runnable {
    private String recipientEmail;
    private String subject;
    private String emailBody;

    /**
     * Instantiates a new Email util.
     *
     * @param recipientEmail the recipient email
     * @param subject        the subject
     * @param emailBody      the email body
     */
    public EmailUtil(String recipientEmail, String subject, String emailBody) {
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.emailBody = emailBody;
    }

    @Override
    public void run() {
        sendEmailSync();
    }

    /**
     * Send email sync.
     */
    public void sendEmailSync() {
        String from = "anishelflms@gmail.com";
        String host = "smtp.gmail.com";
        String password = "grtb fziw ssvz ulnj";

        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.debug", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject(subject);
            message.setText(emailBody);

            Transport.send(message);
            System.out.println("Email đã được gửi tới " + recipientEmail);

        } catch (MessagingException mex) {
            System.err.println("Lỗi khi gửi email: " + mex.getMessage());
            if (mex instanceof AuthenticationFailedException) {
                System.err.println("Lỗi xác thực email. Vui lòng kiểm tra lại email và mật khẩu ứng dụng.");
            }
            mex.printStackTrace();
        }
    }

    /**
     * Send email async.
     *
     * @param toEmail the to email
     * @param subject the subject
     * @param body    the body
     */
    public static void sendEmailAsync(String toEmail, String subject, String body) {
        EmailUtil emailUtil = new EmailUtil(toEmail, subject, body);
        Thread emailThread = new Thread(emailUtil);
        emailThread.start(); // Khởi chạy luồng gửi email
    }
}
