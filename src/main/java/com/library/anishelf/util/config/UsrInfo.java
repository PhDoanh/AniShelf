package com.library.anishelf.util.config;

public class UsrInfo {
    private String id;
    private String color;

    public UsrInfo(String id, String color) {
        this.id = id;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    // Phương thức toString để in thông tin của user
    @Override
    public String toString() {
        return "ID: " + id + ", Color: " + color;
    }

    // Phương thức để kiểm tra ID người dùng
    public boolean matchesId(String searchId) {
        return this.id.equals(searchId);
    }

    public String toFileString() {
        return id + " " + color;
    }
}
