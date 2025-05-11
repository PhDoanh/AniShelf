package com.library.anishelf.controller;

import com.library.anishelf.model.ForumPost;
import com.library.anishelf.service.DiscordForumService;
import com.library.anishelf.util.SceneManagerUtil;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controller for the main forum page showing a list of forum posts
 */
public class ForumPageController implements Initializable {

    @FXML
    private VBox forumContainer;
    
    @FXML
    private VBox postsContainer;
    
    @FXML
    private FlowPane pinnedPostsFlowPane;
    
    @FXML
    private VBox pinnedPostsContainer;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private Button searchButton;
    
    @FXML
    private HBox loadingBox;
    
    private DiscordForumService discordForumService;
    private SceneManagerUtil sceneManagerUtil;
    private ExecutorService executorService;
    
    private static final String FORUM_POST_CARD_FXML = "/view/ForumPostCard.fxml";
    private static final String FORUM_POST_DETAIL_FXML = "/view/ForumPostDetailPage.fxml";
    
    // Discord API configuration
    private static final String DISCORD_TOKEN = "MTM3MDc2ODc5NDczMzEyMTU4OA.G-z5zG.XJoKKpmntVaF0fn4c0R047tkZZoHISMCFmPghc"; // Thay bằng token thực tế
    private static final String GUILD_ID = "1345636853637709876"; // ID của server Discord
    private static final String FORUM_CHANNEL_ID = "1370796726557868042"; // ID của kênh forum
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        discordForumService = DiscordForumService.getInstance();
        sceneManagerUtil = SceneManagerUtil.getInstance();
        executorService = Executors.newSingleThreadExecutor();
        
        // Thêm hiệu ứng focus cho searchField
        searchField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                searchField.getStyleClass().add("search-field-focused");
            } else {
                searchField.getStyleClass().remove("search-field-focused");
            }
        });
        
        // Kết nối tới Discord và tải dữ liệu
        connectToDiscordAndLoadData();
    }

    /**
     * Kết nối tới Discord và tải dữ liệu
     */
    private void connectToDiscordAndLoadData() {
        showLoading(true);
        
        Task<Boolean> connectTask = new Task<>() {
            @Override
            protected Boolean call() {
                // Kiểm tra xem đã kết nối chưa
                if (!discordForumService.isConnected()) {
                    return discordForumService.connect(DISCORD_TOKEN, GUILD_ID, FORUM_CHANNEL_ID);
                }
                return true;
            }
        };
        
        connectTask.setOnSucceeded(event -> {
            Boolean connected = connectTask.getValue();
            if (connected) {
                loadForumPosts();
            } else {
                showError("Không thể kết nối tới Discord. Vui lòng kiểm tra kết nối mạng và cấu hình.");
                showLoading(false);
            }
        });
        
        connectTask.setOnFailed(event -> {
            showError("Lỗi khi kết nối tới Discord: " + connectTask.getException().getMessage());
            showLoading(false);
        });
        
        executorService.submit(connectTask);
    }
    
    /**
     * Tải danh sách bài đăng từ forum
     */
    private void loadForumPosts() {
        showLoading(true);
        
        Task<List<ForumPost>> loadPostsTask = new Task<>() {
            @Override
            protected List<ForumPost> call() {
                return discordForumService.getForumPosts(30); // Lấy tối đa 30 bài đăng
            }
        };
        
        loadPostsTask.setOnSucceeded(event -> {
            List<ForumPost> posts = loadPostsTask.getValue();
            displayPosts(posts);
            loadPinnedPosts();
        });
        
        loadPostsTask.setOnFailed(event -> {
            showError("Lỗi khi tải bài đăng: " + loadPostsTask.getException().getMessage());
            showLoading(false);
        });
        
        executorService.submit(loadPostsTask);
    }
    
    /**
     * Tải danh sách bài ghim
     */
    private void loadPinnedPosts() {
        Task<List<ForumPost>> loadPinnedTask = new Task<>() {
            @Override
            protected List<ForumPost> call() {
                return discordForumService.getPinnedPosts();
            }
        };
        
        loadPinnedTask.setOnSucceeded(event -> {
            List<ForumPost> pinnedPosts = loadPinnedTask.getValue();
            displayPinnedPosts(pinnedPosts);
            showLoading(false);
        });
        
        loadPinnedTask.setOnFailed(event -> {
            showError("Lỗi khi tải bài ghim: " + loadPinnedTask.getException().getMessage());
            showLoading(false);
        });
        
        executorService.submit(loadPinnedTask);
    }
    
    /**
     * Hiển thị các bài đăng thường
     */
    private void displayPosts(List<ForumPost> posts) {
        Platform.runLater(() -> {
            postsContainer.getChildren().clear();
            
            if (posts.isEmpty()) {
                Label emptyLabel = new Label("Không có bài đăng nào");
                emptyLabel.getStyleClass().add("empty-message");
                postsContainer.getChildren().add(emptyLabel);
                return;
            }
            
            for (ForumPost post : posts) {
                try {
                    // Chỉ hiển thị bài không ghim ở danh sách chính
                    if (!post.isPinned()) {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(FORUM_POST_CARD_FXML));
                        HBox postCard = loader.load();
                        
                        ForumPostCardController controller = loader.getController();
                        controller.setData(post);
                        controller.setOnPostCardClicked(event -> openPostDetail(post.getId()));
                        
                        postsContainer.getChildren().add(postCard);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    /**
     * Hiển thị các bài ghim
     */
    private void displayPinnedPosts(List<ForumPost> pinnedPosts) {
        Platform.runLater(() -> {
            pinnedPostsFlowPane.getChildren().clear();
            
            // Ẩn container bài ghim nếu không có bài ghim nào
            pinnedPostsContainer.setVisible(!pinnedPosts.isEmpty());
            pinnedPostsContainer.setManaged(!pinnedPosts.isEmpty());
            
            if (!pinnedPosts.isEmpty()) {
                for (ForumPost post : pinnedPosts) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(FORUM_POST_CARD_FXML));
                        HBox postCard = loader.load();
                        
                        ForumPostCardController controller = loader.getController();
                        controller.setData(post);
                        controller.setOnPostCardClicked(event -> openPostDetail(post.getId()));
                        controller.setPinned(true);
                        
                        pinnedPostsFlowPane.getChildren().add(postCard);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    
    /**
     * Xử lý sự kiện khi nhấn nút tìm kiếm
     */
    @FXML
    public void onSearchButtonClicked(ActionEvent event) {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            loadForumPosts(); // Nếu tìm kiếm rỗng, tải lại tất cả bài đăng
            return;
        }
        
        showLoading(true);
        
        Task<List<ForumPost>> searchTask = new Task<>() {
            @Override
            protected List<ForumPost> call() {
                return discordForumService.searchPosts(query);
            }
        };
        
        searchTask.setOnSucceeded(event1 -> {
            List<ForumPost> searchResults = searchTask.getValue();
            displayPosts(searchResults);
            
            // Khi tìm kiếm, không hiển thị bài ghim
            pinnedPostsContainer.setVisible(false);
            pinnedPostsContainer.setManaged(false);
            
            showLoading(false);
        });
        
        searchTask.setOnFailed(event1 -> {
            showError("Lỗi khi tìm kiếm: " + searchTask.getException().getMessage());
            showLoading(false);
        });
        
        executorService.submit(searchTask);
    }
    
    /**
     * Mở trang chi tiết bài đăng
     */
    private void openPostDetail(String postId) {
        try {
            // Đánh dấu tăng lượt xem
            discordForumService.incrementViewCount(postId);
            
            // Tải trang chi tiết
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FORUM_POST_DETAIL_FXML));
            VBox detailView = loader.load();
            
            // Thiết lập controller
            ForumPostDetailController controller = loader.getController();
            controller.setPostId(postId);
            controller.setForumPageController(this);
            
            // Hiển thị trang chi tiết
            sceneManagerUtil.updateSceneContainer(detailView);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Không thể mở chi tiết bài đăng: " + e.getMessage());
        }
    }
    
    /**
     * Hiển thị loading indicator
     */
    private void showLoading(boolean show) {
        Platform.runLater(() -> {
            loadingBox.setVisible(show);
            loadingBox.setManaged(show);
        });
    }
    
    /**
     * Hiển thị thông báo lỗi
     */
    private void showError(String message) {
        Platform.runLater(() -> {
            // Thêm thông báo lỗi vào giao diện
            Label errorLabel = new Label(message);
            errorLabel.getStyleClass().add("error-message");
            
            postsContainer.getChildren().clear();
            postsContainer.getChildren().add(errorLabel);
        });
    }
    
    /**
     * Phương thức được gọi khi quay trở lại từ trang chi tiết
     */
    public void onReturnFromDetail() {
        // Tải lại danh sách bài đăng khi quay lại
        loadForumPosts();
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