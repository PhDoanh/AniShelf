package com.library.frontend.models;

import java.time.LocalDateTime;

public class ForumPost {
    private String title;
    private String summary;
    private String author;
    private LocalDateTime postDate;

    // Constructor, getters, setters
    public ForumPost(String title, String summary, String author, LocalDateTime postDate) {
        this.title = title;
        this.summary = summary;
        this.author = author;
        this.postDate = postDate;
    }

    // Getters, setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getPostDate() {
        return postDate;
    }

    public void setPostDate(LocalDateTime postDate) {
        this.postDate = postDate;
    }
}