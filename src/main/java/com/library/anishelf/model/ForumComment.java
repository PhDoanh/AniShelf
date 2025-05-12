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

    /**
     * Instantiates a new Forum comment.
     */
    public ForumComment() {
    }

    /**
     * Instantiates a new Forum comment.
     *
     * @param id              the id
     * @param content         the content
     * @param authorName      the author name
     * @param authorAvatarUrl the author avatar url
     * @param createdAt       the created at
     * @param postId          the post id
     */
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

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets content.
     *
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets content.
     *
     * @param content the content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets author name.
     *
     * @return the author name
     */
    public String getAuthorName() {
        return authorName;
    }

    /**
     * Sets author name.
     *
     * @param authorName the author name
     */
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    /**
     * Gets author avatar url.
     *
     * @return the author avatar url
     */
    public String getAuthorAvatarUrl() {
        return authorAvatarUrl;
    }

    /**
     * Sets author avatar url.
     *
     * @param authorAvatarUrl the author avatar url
     */
    public void setAuthorAvatarUrl(String authorAvatarUrl) {
        this.authorAvatarUrl = authorAvatarUrl;
    }

    /**
     * Gets created at.
     *
     * @return the created at
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets created at.
     *
     * @param createdAt the created at
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets like count.
     *
     * @return the like count
     */
    public int getLikeCount() {
        return likeCount;
    }

    /**
     * Sets like count.
     *
     * @param likeCount the like count
     */
    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    /**
     * Gets post id.
     *
     * @return the post id
     */
    public String getPostId() {
        return postId;
    }

    /**
     * Sets post id.
     *
     * @param postId the post id
     */
    public void setPostId(String postId) {
        this.postId = postId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
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