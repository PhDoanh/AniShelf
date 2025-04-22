package com.library.frontend.models;

import javafx.scene.Parent;

/**
 * Lớp trừu tượng đại diện cho mọi trang giao diện trong ứng dụng.
 * Các trang cụ thể sẽ kế thừa lớp này và triển khai phương thức render().
 */
public abstract class Page {
    protected String pageId;  // id duy nhất của trang
    protected String title;   // tiêu đề trang
    
    /**
     * Khởi tạo một trang mới với ID và tiêu đề
     * 
     * @param pageId ID duy nhất của trang
     * @param title Tiêu đề hiển thị của trang
     */
    public Page(String pageId, String title) {
        this.pageId = pageId;
        this.title = title;
    }
    
    /**
     * Phương thức trừu tượng để hiển thị/render trang.
     * Các lớp con phải triển khai phương thức này.
     * 
     * @return Parent node chứa toàn bộ nội dung trang
     */
    public abstract Parent render();
    
    /**
     * Xử lý các sự kiện của trang
     * 
     * @param event Tên sự kiện cần xử lý
     */
    public void handleEvent(String event) {
        // Mặc định không làm gì, các lớp con có thể ghi đè
    }
    
    // Getters và Setters
    public String getPageId() {
        return pageId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
}