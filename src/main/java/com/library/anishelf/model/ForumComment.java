package com.library.anishelf.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Model class representing a comment on a forum post
 */
public class ForumComment {
    private String id;
    private String content;
    private String authorName;
    private String authorAvatarUrl;
    private LocalDateTime createdAt;
    private int likeCount;
    private String postId; // Reference to parent post
    
    public ForumComment() {
    }
    
    public ForumComment(String id, String content, String authorName, 
                       String authorAvatarUrl, LocalDateTime createdAt, String postId) {
        this.id = id;
        this.content = content;
        this.authorName = authorName;
        this.authorAvatarUrl = authorAvatarUrl;
        this.createdAt = createdAt;
        this.likeCount = 0;
        this.postId = postId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorAvatarUrl() {
        return authorAvatarUrl;
    }

    public void setAuthorAvatarUrl(String authorAvatarUrl) {
        this.authorAvatarUrl = authorAvatarUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForumComment comment = (ForumComment) o;
        return Objects.equals(id, comment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ForumComment{" +
                "id='" + id + '\'' +
                ", author='" + authorName + '\'' +
                ", createdAt=" + createdAt +
                ", postId='" + postId + '\'' +
                '}';
    }
}