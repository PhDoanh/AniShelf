package com.library.frontend.models;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Lớp cơ sở trừu tượng cho các cửa sổ nổi trong ứng dụng
 */
public abstract class Dialog {
    protected String title;
    protected String message;
    protected Stage dialogStage;
    protected BorderPane mainLayout;
    
    /**
     * Khởi tạo một dialog cơ bản
     * 
     * @param title Tiêu đề của dialog
     * @param message Thông điệp hiển thị
     */
    public Dialog(String title, String message) {
        this.title = title;
        this.message = message;
        
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.DECORATED);
        dialogStage.setTitle(title);
        
        initializeLayout();
    }
    
    /**
     * Khởi tạo bố cục cơ bản của dialog
     */
    protected void initializeLayout() {
        mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(15));
        
        // Phần tiêu đề
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("dialog-title");
        
        // Phần thông điệp
        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add("dialog-message");
        messageLabel.setWrapText(true);
        
        // Đặt phần tiêu đề và thông điệp vào vbox
        VBox contentBox = new VBox(10, titleLabel, messageLabel);
        mainLayout.setCenter(contentBox);
        
        // Nút đóng mặc định ở dưới cùng
        Button closeButton = new Button("Đóng");
        closeButton.setOnAction(e -> close());
        
        HBox buttonBox = new HBox(10, closeButton);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        
        // Đẩy nút sang bên phải
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        buttonBox.getChildren().add(0, spacer);
        
        mainLayout.setBottom(buttonBox);
    }
    
    /**
     * Hiển thị dialog
     * Lớp con có thể ghi đè để thêm chức năng đặc biệt trước khi hiển thị
     */
    public void show() {
        Scene scene = new Scene(mainLayout);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }
    
    /**
     * Đóng dialog
     */
    public void close() {
        dialogStage.close();
    }
}