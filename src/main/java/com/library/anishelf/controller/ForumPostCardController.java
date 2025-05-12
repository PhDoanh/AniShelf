package com.library.anishelf.controller;

import com.library.anishelf.model.ForumPost;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.event.EventHandler;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

/**
 * Controller for the forum post card component
 */
public class ForumPostCardController implements Initializable {

    @FXML
    private HBox postCardContainer;

    @FXML
    private ImageView authorAvatar;

    @FXML
    private Label titleLabel;

    @FXML
    private Label contentPreviewLabel;

    @FXML
    private Label authorLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private Label viewCountLabel;

    @FXML
    private Label commentCountLabel;

    @FXML
    private FlowPane tagsFlowPane;

    private ForumPost post;
    private EventHandler<MouseEvent> onClickHandler;
    private boolean isPinned = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Thêm event handler cho click vào card
        postCardContainer.setOnMouseClicked(event -> {
            if (onClickHandler != null) {
                onClickHandler.handle(event);
            }
        });

        // Thêm hiệu ứng hover
        postCardContainer.setOnMouseEntered(event ->
                postCardContainer.getStyleClass().add("forum-post-card-hover"));

        postCardContainer.setOnMouseExited(event ->
                postCardContainer.getStyleClass().remove("forum-post-card-hover"));
    }

    /**
     * Thiết lập dữ liệu cho card
     *
     * @param post Đối tượng ForumPost chứa thông tin bài đăng
     */
    public void setData(ForumPost post) {
        this.post = post;

        // Thiết lập tiêu đề và preview nội dung
        titleLabel.setText(post.getTitle());

        String content = post.getContent();
        if (content.length() > 150) {
            contentPreviewLabel.setText(content.substring(0, 150) + "...");
        } else {
            contentPreviewLabel.setText(content);
        }

        // Thiết lập thông tin tác giả
        authorLabel.setText(post.getAuthorName());

        // Thiết lập thời gian (dạng tương đối: "2 giờ trước", "3 ngày trước", vv)
        timeLabel.setText(formatRelativeTime(post.getCreatedAt()));

        // Thiết lập số lượt xem và bình luận
        viewCountLabel.setText(String.valueOf(post.getViewCount()));
        commentCountLabel.setText(String.valueOf(post.getCommentCount()));

        // Tải avatar từ URL
        if (post.getAuthorAvatarUrl() != null && !post.getAuthorAvatarUrl().isEmpty()) {
            try {
                authorAvatar.setImage(new Image(post.getAuthorAvatarUrl(), true));
            } catch (Exception e) {
                // Nếu không tải được, sử dụng avatar mặc định
                authorAvatar.setImage(new Image(getClass().getResourceAsStream("/image/default/avatar.png")));
            }
        } else {
            authorAvatar.setImage(new Image(getClass().getResourceAsStream("/image/default/avatar.png")));
        }

        // Hiển thị tags
        displayTags();
    }

    /**
     * Hiển thị các tag của bài đăng
     */
    private void displayTags() {
        tagsFlowPane.getChildren().clear();

        if (post.getTags() != null && !post.getTags().isEmpty()) {
            for (String tag : post.getTags()) {
                Label tagLabel = new Label(tag);
                tagLabel.getStyleClass().add("forum-tag");

                // Thêm padding
                VBox tagContainer = new VBox(tagLabel);
                tagContainer.setPadding(new Insets(0, 5, 0, 0));

                tagsFlowPane.getChildren().add(tagContainer);
            }
        }

        // Nếu là bài ghim, thêm tag "Ghim"
        if (isPinned) {
            Label pinnedTag = new Label("Ghim");
            pinnedTag.getStyleClass().addAll("forum-tag", "pinned-tag");

            VBox tagContainer = new VBox(pinnedTag);
            tagContainer.setPadding(new Insets(0, 5, 0, 0));

            tagsFlowPane.getChildren().add(0, tagContainer);
        }
    }

    /**
     * Thiết lập trạng thái ghim cho bài đăng
     *
     * @param pinned true nếu bài đăng được ghim
     */
    public void setPinned(boolean pinned) {
        this.isPinned = pinned;

        if (pinned) {
            postCardContainer.getStyleClass().add("pinned-post-card");
        } else {
            postCardContainer.getStyleClass().remove("pinned-post-card");
        }

        // Cập nhật lại tags để hiển thị tag "Ghim" nếu cần
        if (post != null) {
            displayTags();
        }
    }

    /**
     * Thiết lập handler cho sự kiện click vào card
     *
     * @param handler Event handler
     */
    public void setOnPostCardClicked(EventHandler<MouseEvent> handler) {
        this.onClickHandler = handler;
    }

    /**
     * Format thời gian tương đối (ví dụ: "2 phút trước", "3 giờ trước")
     *
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