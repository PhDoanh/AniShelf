package com.library.frontend.models;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

/**
 * Lớp quản lý menu thả xuống khi di chuột qua avatar
 */
public class AvatarMenu {
    private final Popup menuPopup;
    private final VBox menuContainer;
    private final String userType;
    
    // Các nút chức năng menu
    private Button joinBtn;       // Dành cho Guest
    private Button profileBtn;    // Dành cho Member/Admin
    private Button settingsBtn;   // Dành cho Member/Admin
    private Button forumBtn;      // Dành cho tất cả
    private Button donateBtn;     // Dành cho tất cả
    private Button reportBtn;     // Dành cho tất cả
    private Button byeBtn;        // Dành cho Member/Admin
    
    /**
     * Khởi tạo menu với loại người dùng
     * 
     * @param userType Loại người dùng: "Guest", "Member", hoặc "Admin"
     */
    public AvatarMenu(String userType) {
        this.userType = userType;
        this.menuPopup = new Popup();
        this.menuContainer = new VBox(5);
        menuContainer.setPadding(new Insets(10));
        menuContainer.getStyleClass().add("avatar-menu");
        
        initializeComponents();
        
        menuPopup.getContent().add(menuContainer);
        menuPopup.setAutoHide(true);
    }
    
    /**
     * Khởi tạo các thành phần của menu theo loại người dùng
     */
    private void initializeComponents() {
        // Các nút chức năng dành cho Guest
        if ("Guest".equals(userType)) {
            joinBtn = createMenuButton("Tham gia thư viện", e -> handleJoin());
            menuContainer.getChildren().add(joinBtn);
        }
        
        // Các nút chức năng dành cho Member/Admin
        if (!"Guest".equals(userType)) {
            profileBtn = createMenuButton("Hồ sơ người dùng", e -> handleProfile());
            settingsBtn = createMenuButton("Cài đặt ứng dụng", e -> handleSettings());
            byeBtn = createMenuButton("Rời thư viện", e -> handleLogout());
            
            menuContainer.getChildren().addAll(profileBtn, settingsBtn);
        }
        
        // Các nút chức năng dành cho tất cả
        forumBtn = createMenuButton("Diễn đàn", e -> handleForum());
        donateBtn = createMenuButton("Ủng hộ", e -> handleDonate());
        reportBtn = createMenuButton("Báo cáo sự cố", e -> handleReport());
        
        menuContainer.getChildren().addAll(forumBtn, donateBtn, reportBtn);
        
        if (!"Guest".equals(userType)) {
            menuContainer.getChildren().add(byeBtn);
        }
    }
    
    /**
     * Hiển thị menu thả xuống
     * 
     * @param anchor Node mà menu sẽ hiển thị bên dưới
     */
    public void show(Node anchor) {
        double x = anchor.localToScreen(anchor.getBoundsInLocal()).getMinX();
        double y = anchor.localToScreen(anchor.getBoundsInLocal()).getMaxY();
        
        menuPopup.show(anchor, x, y);
    }
    
    /**
     * Tạo nút chức năng trong menu
     * 
     * @param text Nội dung hiển thị
     * @param handler Xử lý sự kiện khi bấm nút
     * @return Button đã cấu hình
     */
    private Button createMenuButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.getStyleClass().add("menu-button");
        button.setOnAction(handler);
        return button;
    }
    
    // Các phương thức xử lý sự kiện cho các nút
    
    private void handleJoin() {
        // TODO: Hiển thị cửa sổ đăng nhập/đăng ký
        System.out.println("Hiển thị cửa sổ đăng nhập/đăng ký");
    }
    
    private void handleProfile() {
        // TODO: Điều hướng đến trang hồ sơ người dùng
        System.out.println("Đi đến trang hồ sơ người dùng");
    }
    
    private void handleSettings() {
        // TODO: Điều hướng đến trang cài đặt
        System.out.println("Đi đến trang cài đặt ứng dụng");
    }
    
    private void handleForum() {
        // TODO: Mở URL diễn đàn Discord
        System.out.println("Mở Discord AniSelf");
    }
    
    private void handleDonate() {
        // TODO: Mở trang ủng hộ
        System.out.println("Mở trang ủng hộ");
    }
    
    private void handleReport() {
        // TODO: Mở kênh báo cáo sự cố
        System.out.println("Mở kênh báo cáo sự cố");
    }
    
    private void handleLogout() {
        // TODO: Xử lý đăng xuất
        System.out.println("Đăng xuất khỏi ứng dụng");
    }
}