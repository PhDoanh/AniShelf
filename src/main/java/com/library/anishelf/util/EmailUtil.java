package com.library.anishelf.util;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * Tiện ích gửi email cho ứng dụng AniShelf.
 * Hỗ trợ gửi email đồng bộ và bất đồng bộ.
 */
public class EmailUtil implements Runnable {
    private static final String TAG = "EmailUtil";
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String FROM_EMAIL = "anishelflms@gmail.com";
    private static final String EMAIL_PASSWORD = "grtb fziw ssvz ulnj";

    private String recipientEmail;
    private String subject;
    private String emailBody;
    
    // Logger instance
    private static final RuntimeDebugUtil logger = RuntimeDebugUtil.getInstance();

    /**
     * Khởi tạo đối tượng EmailUtil.
     *
     * @param recipientEmail Email người nhận
     * @param subject Tiêu đề email
     * @param emailBody Nội dung email
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
     * Khởi tạo cấu hình email.
     *
     * @return Session đã cấu hình
     */
    private static Session createSession() {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.port", SMTP_PORT);
        properties.put("mail.debug", "false"); // Chỉ bật khi cần debug

        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, EMAIL_PASSWORD);
            }
        });
    }

    /**
     * Gửi email đồng bộ (chạy trên luồng hiện tại).
     */
    public void sendEmailSync() {
        logger.debug(TAG, "Chuẩn bị gửi email đến " + recipientEmail);
        
        Session session = createSession();

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject(subject);
            message.setText(emailBody);

            Transport.send(message);
            logger.info(TAG, "Email đã được gửi thành công tới " + recipientEmail);

        } catch (MessagingException mex) {
            if (mex instanceof AuthenticationFailedException) {
                logger.error(TAG, "Lỗi xác thực email. Vui lòng kiểm tra lại email và mật khẩu ứng dụng.", mex);
            } else {
                logger.error(TAG, "Lỗi khi gửi email đến " + recipientEmail, mex);
            }
        }
    }

    /**
     * Gửi email bất đồng bộ (chạy trên luồng riêng).
     *
     * @param toEmail Email người nhận
     * @param subject Tiêu đề email
     * @param body Nội dung email
     */
    public static void sendEmailAsync(String toEmail, String subject, String body) {
        if (toEmail == null || toEmail.isEmpty()) {
            logger.warning(TAG, "Không thể gửi email: địa chỉ email người nhận trống");
            return;
        }
        
        logger.debug(TAG, "Tạo luồng gửi email bất đồng bộ đến " + toEmail);
        EmailUtil emailUtil = new EmailUtil(toEmail, subject, body);
        Thread emailThread = new Thread(emailUtil);
        emailThread.setName("Email-Thread-" + System.currentTimeMillis());
        emailThread.start();
    }
    
    /**
     * Gửi email thông báo lỗi.
     * 
     * @param toEmail Email người nhận
     * @param errorMessage Thông báo lỗi
     */
    public static void sendErrorNotification(String toEmail, String errorMessage) {
        String subject = "AniShelf - Thông báo lỗi hệ thống";
        String body = "Hệ thống AniShelf ghi nhận lỗi sau:\n\n" + errorMessage + 
                      "\n\nThời gian: " + new java.util.Date() + 
                      "\n\nĐây là email tự động, vui lòng không trả lời.";
        
        sendEmailAsync(toEmail, subject, body);
    }
}
