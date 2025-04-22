package com.library.frontend.models;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

/**
 * Lớp quản lý avatar người dùng và menu thả xuống khi hover
 */
public class Avatar {
    private String imageUrl;
    private String userType; // Guest, Member, Admin
    private AvatarMenu avatarMenu;
    private final ImageView avatarImageView;
    
    /**
     * Khởi tạo avatar với loại người dùng
     * 
     * @param userType Loại người dùng: "Guest", "Member", hoặc "Admin"
     */
    public Avatar(String userType) {
        this.userType = userType;
        
        // Tạo đường dẫn ảnh avatar mặc định dựa trên loại người dùng
        this.imageUrl = getDefaultAvatarPath(userType);
        
        // Tạo ImageView để hiển thị avatar
        avatarImageView = new ImageView(new Image(imageUrl));
        avatarImageView.setFitHeight(40);
        avatarImageView.setFitWidth(40);
        
        // Tạo hiệu ứng hình tròn cho avatar
        Circle clip = new Circle(20, 20, 20);
        avatarImageView.setClip(clip);
        
        // Tạo menu thả xuống
        avatarMenu = new AvatarMenu(userType);
        
        // Thêm sự kiện hover để hiển thị/ẩn menu
        setupHoverEvents();
    }
    
    /**
     * Thiết lập sự kiện hover để hiển thị/ẩn menu
     */
    private void setupHoverEvents() {
        avatarImageView.setOnMouseEntered(e -> showMenu());
    }
    
    /**
     * Hiển thị menu thả xuống
     */
    public void showMenu() {
        // Hiển thị menu (sẽ được triển khai sau với FXML và JavaFX Stage)
        avatarMenu.show(avatarImageView);
    }
    
    /**
     * Lấy đường dẫn avatar mặc định dựa trên loại người dùng
     * 
     * @param userType Loại người dùng
     * @return Đường dẫn tới file hình ảnh
     */
    private String getDefaultAvatarPath(String userType) {
        // Đường dẫn mặc định có thể thay đổi sau
        return switch (userType) {
            case "Admin" -> "/img/placeholder/admin_avatar.png";
            case "Member" -> "/img/placeholder/member_avatar.png";
            default -> "/img/placeholder/guest_avatar.png";
        };
    }
    
    /**
     * Render avatar
     * 
     * @return Node chứa avatar
     */
    public Node render() {
        return avatarImageView;
    }
}