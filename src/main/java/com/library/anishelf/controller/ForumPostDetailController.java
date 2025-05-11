package com.library.anishelf.controller;

import com.library.anishelf.model.ForumComment;
import com.library.anishelf.model.ForumPost;
import com.library.anishelf.service.DiscordForumService;
import com.library.anishelf.util.SceneManagerUtil;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controller for the forum post detail page
 */
public class ForumPostDetailController implements Initializable {

    @FXML
    private VBox detailContainer;
    
    @FXML
    private Button backButton;
    
    @FXML
    private Label titleLabel;
    
    @FXML
    private ImageView authorAvatar;
    
    @FXML
    private Label authorNameLabel;
    
    @FXML
    private Label postTimeLabel;
    
    @FXML
    private Label viewCountLabel;
    
    @FXML
    private Label commentCountLabel;
    
    @FXML
    private FlowPane tagsFlowPane;
    
    @FXML
    private Label contentLabel;
    
    @FXML
    private Button likeButton;
    
    @FXML
    private FontIcon likeIcon;
    
    @FXML
    private Button shareButton;
    
    @FXML
    private ImageView userAvatar;
    
    @FXML
    private TextArea commentInput;
    
    @FXML
    private Button submitCommentButton;
    
    @FXML
    private VBox commentsContainer;
    
    private String postId;
    private ForumPost post;
    private ForumPageController forumPageController;
    private DiscordForumService discordForumService;
    private ExecutorService executorService;
    private boolean isLiked = false;
    
    private static final String FORUM_COMMENT_ITEM_FXML = "/view/ForumCommentItem.fxml";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm");
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        discordForumService = DiscordForumService.getInstance();
        executorService = Executors.newSingleThreadExecutor();
        
        // Thiết lập avatar người dùng hiện tại
        setCurrentUserAvatar();
        
        // Thiết lập hiệu ứng hover cho các nút
        setupButtonHoverEffects();
    }
    
    /**
     * Đặt ID của bài đăng và tải dữ liệu
     * @param postId ID của bài đăng cần hiển thị
     */
    public void setPostId(String postId) {
        this.postId = postId;
        loadPostDetails();
    }
    
    /**
     * Thiết lập tham chiếu đến controller trang chủ
     * @param controller Controller trang forum chính
     */
    public void setForumPageController(ForumPageController controller) {
        this.forumPageController = controller;
    }
    
    /**
     * Tải chi tiết bài đăng từ service
     */
    private void loadPostDetails() {
        Task<ForumPost> loadPostTask = new Task<>() {
            @Override
            protected ForumPost call() {
                return discordForumService.getForumPostById(postId);
            }
        };
        
        loadPostTask.setOnSucceeded(event -> {
            post = loadPostTask.getValue();
            if (post != null) {
                displayPostDetails();
                loadComments();
            } else {
                showError("Không thể tải thông tin bài đăng");
            }
        });
        
        loadPostTask.setOnFailed(event -> {
            showError("Lỗi khi tải bài đăng: " + loadPostTask.getException().getMessage());
        });
        
        executorService.submit(loadPostTask);
    }
    
    /**
     * Tải danh sách bình luận của bài đăng
     */
    private void loadComments() {
        Task<List<ForumComment>> loadCommentsTask = new Task<>() {
            @Override
            protected List<ForumComment> call() {
                return discordForumService.getCommentsForPost(postId);
            }
        };
        
        loadCommentsTask.setOnSucceeded(event -> {
            List<ForumComment> comments = loadCommentsTask.getValue();
            displayComments(comments);
        });
        
        loadCommentsTask.setOnFailed(event -> {
            // Chỉ hiển thị lỗi trong console, không cần báo lỗi trên UI
            System.err.println("Lỗi khi tải bình luận: " + loadCommentsTask.getException().getMessage());
        });
        
        executorService.submit(loadCommentsTask);
    }
    
    /**
     * Hiển thị chi tiết bài đăng lên giao diện
     */
    private void displayPostDetails() {
        Platform.runLater(() -> {
            // Thiết lập tiêu đề và nội dung
            titleLabel.setText(post.getTitle());
            contentLabel.setText(post.getContent());
            
            // Thiết lập thông tin tác giả
            authorNameLabel.setText(post.getAuthorName());
            
            // Format thời gian tạo
            LocalDateTime createdTime = post.getCreatedAt();
            if (createdTime != null) {
                postTimeLabel.setText(createdTime.format(DATE_FORMATTER));
            }
            
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
        });
    }
    
    /**
     * Hiển thị danh sách bình luận
     * @param comments Danh sách bình luận cần hiển thị
     */
    private void displayComments(List<ForumComment> comments) {
        Platform.runLater(() -> {
            commentsContainer.getChildren().clear();
            
            if (comments.isEmpty()) {
                Label emptyLabel = new Label("Chưa có bình luận nào. Hãy là người đầu tiên bình luận!");
                emptyLabel.getStyleClass().add("empty-comments-message");
                commentsContainer.getChildren().add(emptyLabel);
                return;
            }
            
            for (ForumComment comment : comments) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(FORUM_COMMENT_ITEM_FXML));
                    HBox commentItem = loader.load();
                    
                    ForumCommentController controller = loader.getController();
                    controller.setData(comment);
                    
                    commentsContainer.getChildren().add(commentItem);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
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
    }
    
    /**
     * Thiết lập avatar người dùng hiện tại
     */
    private void setCurrentUserAvatar() {
        try {
            // Lấy thông tin thành viên từ NavigationBarController
            if (NavigationBarController.getMember() != null && 
                NavigationBarController.getMember().getPerson() != null) {
                
                String imagePath = NavigationBarController.getMember().getPerson().getImagePath();
                
                if (imagePath != null && !imagePath.isEmpty()) {
                    try {
                        if (imagePath.startsWith("/")) {
                            // Đường dẫn tài nguyên
                            userAvatar.setImage(new Image(getClass().getResourceAsStream(imagePath)));
                        } else {
                            // Đường dẫn file
                            userAvatar.setImage(new Image("file:" + imagePath));
                        }
                    } catch (Exception e) {
                        userAvatar.setImage(new Image(getClass().getResourceAsStream("/image/default/avatar.png")));
                    }
                } else {
                    userAvatar.setImage(new Image(getClass().getResourceAsStream("/image/default/avatar.png")));
                }
            } else {
                userAvatar.setImage(new Image(getClass().getResourceAsStream("/image/default/avatar.png")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            userAvatar.setImage(new Image(getClass().getResourceAsStream("/image/default/avatar.png")));
        }
    }
    
    /**
     * Thiết lập hiệu ứng hover cho các nút
     */
    private void setupButtonHoverEffects() {
        // Hiệu ứng hover cho nút back
        backButton.setOnMouseEntered(event -> 
            backButton.getStyleClass().add("back-button-hover"));
            
        backButton.setOnMouseExited(event -> 
            backButton.getStyleClass().remove("back-button-hover"));
            
        // Hiệu ứng hover cho nút like
        likeButton.setOnMouseEntered(event -> 
            likeButton.getStyleClass().add("interaction-button-hover"));
            
        likeButton.setOnMouseExited(event -> 
            likeButton.getStyleClass().remove("interaction-button-hover"));
            
        // Hiệu ứng hover cho nút share
        shareButton.setOnMouseEntered(event -> 
            shareButton.getStyleClass().add("interaction-button-hover"));
            
        shareButton.setOnMouseExited(event -> 
            shareButton.getStyleClass().remove("interaction-button-hover"));
    }
    
    /**
     * Xử lý sự kiện khi nhấn nút quay lại
     */
    @FXML
    public void onBackButtonClicked(ActionEvent event) {
        if (forumPageController != null) {
            forumPageController.onReturnFromDetail();
        }
        
        VBox content = (VBox) SceneManagerUtil.getInstance().loadScene("/view/ForumPage.fxml");
        if (content != null) {
            SceneManagerUtil.getInstance().updateSceneContainer(content);
        }
    }
    
    /**
     * Xử lý sự kiện khi nhấn nút thích
     */
    @FXML
    public void onLikeButtonClicked(ActionEvent event) {
        isLiked = !isLiked;
        
        if (isLiked) {
            likeButton.getStyleClass().add("liked-button");
            likeIcon.setIconColor(javafx.scene.paint.Color.valueOf("#1e88e5"));
        } else {
            likeButton.getStyleClass().remove("liked-button");
            // Sử dụng màu mặc định thay vì null
            likeIcon.setIconColor(javafx.scene.paint.Color.BLACK);
        }
    }
    
    /**
     * Xử lý sự kiện khi nhấn nút chia sẻ
     */
    @FXML
    public void onShareButtonClicked(ActionEvent event) {
        // Hiện tại chỉ mô phỏng chức năng chia sẻ
        System.out.println("Đã chia sẻ bài đăng: " + post.getTitle());
    }
    
    /**
     * Xử lý sự kiện khi nhấn nút gửi bình luận
     */
    @FXML
    public void onSubmitCommentClicked(ActionEvent event) {
        String commentText = commentInput.getText().trim();
        if (commentText.isEmpty()) {
            return;
        }
        
        // Hiện tại chỉ mô phỏng việc thêm bình luận
        // Trong ứng dụng thực tế, sẽ gửi bình luận tới API Discord
        
        // Lấy tên người dùng từ đối tượng Person (sửa lỗi getName())
        String userName = "Người dùng";
        if (NavigationBarController.getMember() != null && 
            NavigationBarController.getMember().getPerson() != null) {
            // Thay vì gọi getName(), sử dụng getFullName() nếu có
            // Hoặc truy cập trực tiếp thuộc tính phù hợp từ đối tượng Person
            userName = NavigationBarController.getMember().getPerson().getFullName();
        }
        
        // Tạo comment mới với dữ liệu giả lập
        ForumComment newComment = new ForumComment(
                "local_" + System.currentTimeMillis(),
                commentText,
                userName, // Sử dụng tên người dùng đã lấy ở trên
                NavigationBarController.getMember().getPerson().getImagePath(),
                LocalDateTime.now(),
                postId
        );
        
        // Thêm vào danh sách bình luận ở cục bộ và hiển thị
        if (post != null) {
            post.addComment(newComment);
            
            // Cập nhật số lượng bình luận
            commentCountLabel.setText(String.valueOf(post.getCommentCount()));
            
            // Thêm bình luận mới vào danh sách hiển thị
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(FORUM_COMMENT_ITEM_FXML));
                HBox commentItem = loader.load();
                
                ForumCommentController controller = loader.getController();
                controller.setData(newComment);
                
                // Xóa thông báo "chưa có bình luận" nếu có
                if (commentsContainer.getChildren().size() == 1 && 
                    commentsContainer.getChildren().get(0) instanceof Label) {
                    commentsContainer.getChildren().clear();
                }
                
                commentsContainer.getChildren().add(0, commentItem);
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            // Xóa nội dung trong ô nhập
            commentInput.clear();
        }
    }
    
    /**
     * Hiển thị thông báo lỗi
     */
    private void showError(String message) {
        Platform.runLater(() -> {
            contentLabel.setText("Lỗi: " + message);
            contentLabel.getStyleClass().add("error-message");
        });
    }
    
    /**
     * Clean up resources when the controller is no longer needed
     */
    public void cleanup() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}