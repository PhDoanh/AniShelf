package com.library.backend.models;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Đại diện cho bài đăng từ diễn đàn AniShelf Discord
 */
public class ForumPost {
    private String postId;
    private String title;
    private String content;
    private String author;
    private LocalDateTime timestamp;
    private String category; // "news", "discussion", etc.
    
    public ForumPost() {
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Khởi tạo ForumPost với các thông tin cơ bản
     * 
     * @param postId ID bài đăng
     * @param title Tiêu đề bài đăng
     * @param content Nội dung bài đăng
     * @param author Tác giả bài đăng
     * @param category Phân loại bài đăng
     */
    public ForumPost(String postId, String title, String content, String author, String category) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.author = author;
        this.category = category;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Khởi tạo ForumPost với đầy đủ thông tin bao gồm thời gian
     * 
     * @param postId ID bài đăng
     * @param title Tiêu đề bài đăng
     * @param content Nội dung bài đăng
     * @param author Tác giả bài đăng
     * @param category Phân loại bài đăng
     * @param timestamp Thời gian đăng bài
     */
    public ForumPost(String postId, String title, String content, String author, String category, LocalDateTime timestamp) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.author = author;
        this.category = category;
        this.timestamp = timestamp;
    }
    
    /**
     * Lấy tóm tắt nội dung bài đăng
     * 
     * @param maxLength Độ dài tối đa của tóm tắt
     * @return Tóm tắt nội dung bài đăng
     */
    public String getSummary(int maxLength) {
        if (content == null || content.length() <= maxLength) {
            return content;
        }
        
        // Cắt nội dung đến độ dài tối đa và thêm "..."
        return content.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Lấy thời gian đăng bài dưới dạng chuỗi "x [đơn vị thời gian] trước"
     * Ví dụ: "5 phút trước", "2 giờ trước", "3 ngày trước"
     * 
     * @return Chuỗi thời gian tương đối
     */
    public String getTimeAgo() {
        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(timestamp, now);
        
        if (minutes < 60) {
            return minutes <= 0 ? "Vừa xong" : minutes + " phút trước";
        }
        
        long hours = ChronoUnit.HOURS.between(timestamp, now);
        if (hours < 24) {
            return hours + " giờ trước";
        }
        
        long days = ChronoUnit.DAYS.between(timestamp, now);
        if (days < 30) {
            return days + " ngày trước";
        }
        
        long months = ChronoUnit.MONTHS.between(timestamp, now);
        if (months < 12) {
            return months + " tháng trước";
        }
        
        long years = ChronoUnit.YEARS.between(timestamp, now);
        return years + " năm trước";
    }
    
    // Getters và Setters
    
    public String getPostId() {
        return postId;
    }
    
    public void setPostId(String postId) {
        this.postId = postId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    @Override
    public String toString() {
        return "ForumPost{" +
                "postId='" + postId + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", timestamp=" + timestamp +
                ", category='" + category + '\'' +
                '}';
    }
}
