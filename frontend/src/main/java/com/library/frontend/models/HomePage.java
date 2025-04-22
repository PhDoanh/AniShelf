package com.library.frontend.models;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Trang chủ của ứng dụng AniShelf
 * Hiển thị theo hai chế độ: Tham quan (Guest) và Chính thức (Admin/Member)
 */
public class HomePage extends Page {
    
    private String mode; // "Guest" hoặc "Official"
    private NavigationBar navigationBar;
    private BorderPane mainLayout;
    
    // Các thành phần giao diện
    private VBox bookCarouselSection;
    private VBox forumPostsSection;
    private VBox rankingListSection;
    
    /**
     * Khởi tạo trang chủ với chế độ mặc định là "Guest"
     */
    public HomePage() {
        super("home", "Trang chủ - AniShelf");
        this.mode = "Guest";
        this.navigationBar = new NavigationBar("Guest");
        initializeComponents();
    }
    
    /**
     * Khởi tạo trang chủ với chế độ xác định
     * 
     * @param mode Chế độ: "Guest" hoặc "Official"
     * @param userType Loại người dùng: "Guest", "Member", hoặc "Admin"
     */
    public HomePage(String mode, String userType) {
        super("home", "Trang chủ - AniShelf");
        this.mode = mode;
        this.navigationBar = new NavigationBar(userType);
        initializeComponents();
    }
    
    /**
     * Khởi tạo các thành phần giao diện
     */
    private void initializeComponents() {
        mainLayout = new BorderPane();
        
        // Thêm thanh điều hướng vào phần đầu trang
        mainLayout.setTop(navigationBar.render());
        
        // Tạo container cho nội dung chính
        ScrollPane contentScrollPane = new ScrollPane();
        contentScrollPane.setFitToWidth(true);
        VBox contentContainer = new VBox(20);
        contentContainer.setPadding(new Insets(20));
        
        // Tạo các phần nội dung
        createBookCarouselSection();
        createForumPostsSection();
        createRankingListSection();
        
        // Thêm các phần nội dung vào container
        contentContainer.getChildren().addAll(bookCarouselSection, forumPostsSection, rankingListSection);
        
        // Thêm container vào ScrollPane
        contentScrollPane.setContent(contentContainer);
        
        // Thêm ScrollPane vào phần trung tâm của BorderPane
        mainLayout.setCenter(contentScrollPane);
    }
    
    /**
     * Tạo phần danh sách sách cuộn ngang
     */
    private void createBookCarouselSection() {
        bookCarouselSection = new VBox(10);
        
        // Tiêu đề phần
        Label title = new Label("Sách đề xuất");
        title.getStyleClass().add("section-title");
        
        // Các nút chọn tiêu chí hiển thị
        HBox criteriaButtons = new HBox(10);
        Button popularBtn = new Button("Phổ biến");
        Button favoriteBtn = new Button("Được yêu thích");
        
        popularBtn.setOnAction(e -> updateBookCarousel("popular"));
        favoriteBtn.setOnAction(e -> updateBookCarousel("favorite"));
        
        criteriaButtons.getChildren().addAll(popularBtn, favoriteBtn);
        
        // Danh sách sách cuộn ngang (placeholder)
        HBox booksContainer = new HBox(15);
        booksContainer.setPrefHeight(200);
        
        // Thêm các phần tử mẫu (sẽ thay thế bằng dữ liệu thực tế sau)
        for (int i = 0; i < 5; i++) {
            VBox bookCard = createSampleBookCard("Sách " + (i + 1));
            booksContainer.getChildren().add(bookCard);
        }
        
        // Thêm tất cả vào phần danh sách sách
        bookCarouselSection.getChildren().addAll(title, criteriaButtons, booksContainer);
    }
    
    /**
     * Tạo phần danh sách bài đăng từ diễn đàn
     */
    private void createForumPostsSection() {
        forumPostsSection = new VBox(10);
        
        // Tiêu đề phần
        Label title = new Label("Bài đăng từ diễn đàn");
        title.getStyleClass().add("section-title");
        
        // Danh sách bài đăng (placeholder)
        VBox postsContainer = new VBox(10);
        
        // Thêm các bài đăng mẫu
        for (int i = 0; i < 3; i++) {
            HBox postItem = createSampleForumPost("Tiêu đề bài đăng " + (i + 1));
            postsContainer.getChildren().add(postItem);
        }
        
        // Thêm vào phần bài đăng diễn đàn
        forumPostsSection.getChildren().addAll(title, postsContainer);
    }
    
    /**
     * Tạo phần bảng xếp hạng sách
     */
    private void createRankingListSection() {
        rankingListSection = new VBox(10);
        
        // Tiêu đề phần
        Label title = new Label("Bảng xếp hạng sách");
        title.getStyleClass().add("section-title");
        
        // Danh sách xếp hạng (placeholder)
        VBox rankingContainer = new VBox(5);
        
        // Thêm các mục xếp hạng mẫu
        for (int i = 0; i < 5; i++) {
            HBox rankItem = createSampleRankingItem(i + 1, "Sách xếp hạng " + (i + 1));
            rankingContainer.getChildren().add(rankItem);
        }
        
        // Thêm vào phần bảng xếp hạng
        rankingListSection.getChildren().addAll(title, rankingContainer);
    }
    
    /**
     * Tạo thẻ sách mẫu
     * 
     * @param title Tiêu đề sách
     * @return VBox chứa thẻ sách
     */
    private VBox createSampleBookCard(String title) {
        VBox card = new VBox(5);
        card.getStyleClass().add("book-card");
        card.setPrefWidth(120);
        card.setPrefHeight(180);
        
        // Placeholder cho ảnh bìa sách
        Label imgPlaceholder = new Label("Book Cover");
        imgPlaceholder.setPrefSize(120, 150);
        imgPlaceholder.getStyleClass().add("book-cover-placeholder");
        
        // Tiêu đề sách
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("book-title");
        
        card.getChildren().addAll(imgPlaceholder, titleLabel);
        return card;
    }
    
    /**
     * Tạo mục bài đăng diễn đàn mẫu
     * 
     * @param title Tiêu đề bài đăng
     * @return HBox chứa thông tin bài đăng
     */
    private HBox createSampleForumPost(String title) {
        HBox postItem = new HBox(10);
        postItem.getStyleClass().add("forum-post-item");
        
        // Tiêu đề bài đăng
        Label titleLabel = new Label(title);
        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        
        // Thời gian đăng
        Label timeLabel = new Label("1 giờ trước");
        timeLabel.getStyleClass().add("post-time");
        
        postItem.getChildren().addAll(titleLabel, timeLabel);
        return postItem;
    }
    
    /**
     * Tạo mục xếp hạng mẫu
     * 
     * @param rank Thứ hạng
     * @param title Tiêu đề sách
     * @return HBox chứa thông tin xếp hạng
     */
    private HBox createSampleRankingItem(int rank, String title) {
        HBox rankItem = new HBox(10);
        rankItem.getStyleClass().add("ranking-item");
        
        // Thứ hạng
        Label rankLabel = new Label("#" + rank);
        rankLabel.getStyleClass().add("rank-number");
        
        // Tiêu đề sách
        Label titleLabel = new Label(title);
        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        
        // Điểm đánh giá
        Label ratingLabel = new Label((5.0 - rank * 0.3) + "★");
        ratingLabel.getStyleClass().add("rating");
        
        rankItem.getChildren().addAll(rankLabel, titleLabel, ratingLabel);
        return rankItem;
    }
    
    /**
     * Cập nhật danh sách sách theo tiêu chí
     * 
     * @param criteria Tiêu chí: "popular" hoặc "favorite"
     */
    private void updateBookCarousel(String criteria) {
        // TODO: Cập nhật danh sách sách dựa trên tiêu chí
        System.out.println("Cập nhật danh sách sách theo tiêu chí: " + criteria);
    }
    
    /**
     * Chuyển đổi chế độ hiển thị
     * 
     * @param mode Chế độ mới: "Guest" hoặc "Official"
     * @param userType Loại người dùng: "Guest", "Member", hoặc "Admin"
     */
    public void switchMode(String mode, String userType) {
        this.mode = mode;
        this.navigationBar.updateUserType(userType);
        // TODO: Cập nhật nội dung hiển thị theo chế độ mới
    }
    
    @Override
    public Parent render() {
        return mainLayout;
    }
    
    @Override
    public void handleEvent(String event) {
        super.handleEvent(event);
        // TODO: Xử lý các sự kiện đặc biệt của trang chủ
    }
}