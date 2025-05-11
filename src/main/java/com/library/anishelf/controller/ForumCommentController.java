package com.library.anishelf.controller;

import com.library.anishelf.model.ForumComment;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

/**
 * Controller for forum comment component
 */
public class ForumCommentController implements Initializable {

    @FXML
    private HBox commentContainer;
    
    @FXML
    private ImageView authorAvatar;
    
    @FXML
    private Label authorNameLabel;
    
    @FXML
    private Label timeLabel;
    
    @FXML
    private Label contentLabel;
    
    @FXML
    private Button likeButton;
    
    @FXML
    private FontIcon likeIcon;
    
    @FXML
    private Label likeCountLabel;
    
    @FXML
    private Button replyButton;
    
    private ForumComment comment;
    private boolean isLiked = false;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Thiết lập hiệu ứng hover cho các nút
        setupButtonHoverEffects();
    }
    
    /**
     * Thiết lập dữ liệu cho comment
     */
    public void setData(ForumComment comment) {
        this.comment = comment;
        
        // Thiết lập nội dung
        contentLabel.setText(comment.getContent());
        
        // Thiết lập thông tin tác giả
        authorNameLabel.setText(comment.getAuthorName());
        
        // Thiết lập thời gian (dạng tương đối: "2 giờ trước", "3 ngày trước", vv)
        timeLabel.setText(formatRelativeTime(comment.getCreatedAt()));
        
        // Thiết lập số lượt thích
        likeCountLabel.setText(String.valueOf(comment.getLikeCount()));
        
        // Tải avatar từ URL
        if (comment.getAuthorAvatarUrl() != null && !comment.getAuthorAvatarUrl().isEmpty()) {
            try {
                if (comment.getAuthorAvatarUrl().startsWith("http")) {
                    // URL từ internet
                    authorAvatar.setImage(new Image(comment.getAuthorAvatarUrl(), true));
                } else if (comment.getAuthorAvatarUrl().startsWith("/")) {
                    // Đường dẫn tài nguyên
                    authorAvatar.setImage(new Image(getClass().getResourceAsStream(comment.getAuthorAvatarUrl())));
                } else {
                    // Đường dẫn file
                    authorAvatar.setImage(new Image("file:" + comment.getAuthorAvatarUrl()));
                }
            } catch (Exception e) {
                // Nếu không tải được, sử dụng avatar mặc định
                authorAvatar.setImage(new Image(getClass().getResourceAsStream("/image/default/avatar.png")));
            }
        } else {
            authorAvatar.setImage(new Image(getClass().getResourceAsStream("/image/default/avatar.png")));
        }
    }
    
    /**
     * Thiết lập hiệu ứng hover cho các nút
     */
    private void setupButtonHoverEffects() {
        // Hiệu ứng hover cho nút like
        likeButton.setOnMouseEntered(event -> 
            likeButton.getStyleClass().add("comment-interaction-button-hover"));
            
        likeButton.setOnMouseExited(event -> 
            likeButton.getStyleClass().remove("comment-interaction-button-hover"));
            
        // Hiệu ứng hover cho nút reply
        replyButton.setOnMouseEntered(event -> 
            replyButton.getStyleClass().add("comment-interaction-button-hover"));
            
        replyButton.setOnMouseExited(event -> 
            replyButton.getStyleClass().remove("comment-interaction-button-hover"));
    }
    
    /**
     * Xử lý sự kiện khi nhấn nút thích
     */
    @FXML
    public void onLikeButtonClicked(ActionEvent event) {
        isLiked = !isLiked;
        
        if (isLiked) {
            // Tăng số lượt thích
            int likes = Integer.parseInt(likeCountLabel.getText()) + 1;
            likeCountLabel.setText(String.valueOf(likes));
            
            // Đổi màu nút like
            likeButton.getStyleClass().add("comment-liked-button");
            likeIcon.setIconColor(javafx.scene.paint.Color.valueOf("#1e88e5"));
        } else {
            // Giảm số lượt thích
            int likes = Integer.parseInt(likeCountLabel.getText()) - 1;
            if (likes < 0) likes = 0;
            likeCountLabel.setText(String.valueOf(likes));
            
            // Bỏ màu nút like
            likeButton.getStyleClass().remove("comment-liked-button");
            likeIcon.setIconColor(null); // Reset to default
        }
    }
    
    /**
     * Xử lý sự kiện khi nhấn nút trả lời
     */
    @FXML
    public void onReplyButtonClicked(ActionEvent event) {
        // Hiện tại chỉ in thông báo, trong ứng dụng thực tế sẽ mở form trả lời
        System.out.println("Trả lời bình luận của: " + comment.getAuthorName());
    }
    
    /**
     * Format thời gian tương đối (ví dụ: "2 phút trước", "3 giờ trước")
     * @param dateTime Thời gian cần format
     * @return Chuỗi thời gian tương đối
     */
    private String formatRelativeTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(dateTime, now);
        
        long seconds = duration.getSeconds();
        
        if (seconds < 60) {
            return "Vừa xong";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            return minutes + " phút trước";
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            return hours + " giờ trước";
        } else {
            long days = seconds / 86400;
            if (days < 30) {
                return days + " ngày trước";
            } else if (days < 365) {
                long months = days / 30;
                return months + " tháng trước";
            } else {
                long years = days / 365;
                return years + " năm trước";
            }
        }
    }
}