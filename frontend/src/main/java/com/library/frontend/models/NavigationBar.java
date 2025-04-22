package com.library.frontend.models;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * Lớp quản lý thanh điều hướng xuất hiện trên mọi trang.
 * Chứa các icon và chức năng điều hướng chính của ứng dụng.
 */
public class NavigationBar {

    private final HBox barContainer;
    private final String logo = "AniShelf";
    
    // Các icon điều hướng
    private Button homeIcon;
    private Button bookmarkIcon;
    private Button staticsIcon;
    private Button bookManageIcon;
    private Button memberManageIcon;
    private Button createNewIcon;
    private Button searchIcon;
    private Button notificationIcon;
    private Avatar avatar;
    
    // Lưu trữ loại người dùng để hiển thị các chức năng phù hợp
    private String userType; // "Guest", "Member", hoặc "Admin"
    
    /**
     * Khởi tạo thanh điều hướng với loại người dùng
     * 
     * @param userType Loại người dùng: "Guest", "Member", hoặc "Admin"
     */
    public NavigationBar(String userType) {
        this.userType = userType;
        this.barContainer = new HBox(10);
        barContainer.getStyleClass().add("navigation-bar");
        initializeComponents();
    }
    
    /**
     * Khởi tạo các thành phần của thanh điều hướng
     */
    private void initializeComponents() {
        // Logo
        Label logoLabel = new Label(logo);
        logoLabel.getStyleClass().add("app-logo");
        
        // Icon trang chủ - hiển thị cho tất cả người dùng
        homeIcon = createIconButton("home-icon", "Trang chủ");
        
        // Các icon chỉ hiển thị cho người dùng đã đăng nhập (Member/Admin)
        bookmarkIcon = createIconButton("bookmark-icon", "Đánh dấu sách");
        staticsIcon = createIconButton("statics-icon", "Thống kê");
        
        // Các icon chỉ hiển thị cho Admin
        bookManageIcon = createIconButton("book-manage-icon", "Quản lý sách");
        memberManageIcon = createIconButton("member-manage-icon", "Quản lý thành viên");
        createNewIcon = createIconButton("create-new-icon", "Tạo mới");
        
        // Các icon hiển thị cho tất cả người dùng
        searchIcon = createIconButton("search-icon", "Tìm kiếm");
        notificationIcon = createIconButton("notification-icon", "Thông báo");
        
        // Avatar người dùng
        avatar = new Avatar(userType);
        
        // Spacer để đẩy các icon phía sau ra góc phải
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Thêm các thành phần vào thanh điều hướng
        barContainer.getChildren().add(logoLabel);
        barContainer.getChildren().add(homeIcon);
        
        // Thêm các icon tùy theo loại người dùng
        if (!"Guest".equals(userType)) {
            barContainer.getChildren().addAll(bookmarkIcon, staticsIcon);
        }
        
        if ("Admin".equals(userType)) {
            barContainer.getChildren().addAll(bookManageIcon, memberManageIcon, createNewIcon);
        }
        
        // Thêm spacer và các icon phía bên phải
        barContainer.getChildren().addAll(spacer, searchIcon, notificationIcon, avatar.render());
    }
    
    /**
     * Tạo một nút icon với tooltip mô tả
     * 
     * @param iconStyleClass Tên class CSS của icon
     * @param tooltipText Nội dung mô tả hiển thị khi hover
     * @return Button chứa icon với tooltip
     */
    private Button createIconButton(String iconStyleClass, String tooltipText) {
        Button button = new Button();
        button.getStyleClass().add(iconStyleClass);
        
        // Tạo tooltip hiển thị khi hover
        Tooltip tooltip = new Tooltip(tooltipText);
        Tooltip.install(button, tooltip);
        
        return button;
    }
    
    /**
     * Render thanh điều hướng thành Node để hiển thị
     * 
     * @return HBox chứa thanh điều hướng
     */
    public Node render() {
        return barContainer;
    }
    
    /**
     * Cập nhật loại người dùng và hiển thị lại thanh điều hướng
     * 
     * @param userType Loại người dùng mới
     */
    public void updateUserType(String userType) {
        this.userType = userType;
        barContainer.getChildren().clear();
        initializeComponents();
    }
}