package com.library.anishelf.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Model class representing a forum post from Discord
 */
public class ForumPost {
    private String id;
    private String title;
    private String content;
    private String authorName;
    private String authorAvatarUrl;
    private LocalDateTime createdAt;
    private int viewCount;
    private int commentCount;
    private List<String> tags;
    private boolean isPinned;
    private List<ForumComment> comments;

    /**
     * Instantiates a new Forum post.
     */
    public ForumPost() {
        this.tags = new ArrayList<>();
        this.comments = new ArrayList<>();
    }

    public ForumPost(String id, String title, String content, String authorName,
                     String authorAvatarUrl, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorName = authorName;
        this.authorAvatarUrl = authorAvatarUrl;
        this.createdAt = createdAt;
        this.viewCount = 0;
        this.commentCount = 0;
        this.isPinned = false;
        this.tags = new ArrayList<>();
        this.comments = new ArrayList<>();
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
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title.
     *
     * @param title the title
     */
    public void setTitle(String title) {
        this.title = title;
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
     * Gets view count.
     *
     * @return the view count
     */
    public int getViewCount() {
        return viewCount;
    }

    /**
     * Sets view count.
     *
     * @param viewCount the view count
     */
    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    /**
     * Gets comment count.
     *
     * @return the comment count
     */
    public int getCommentCount() {
        return commentCount;
    }

    /**
     * Sets comment count.
     *
     * @param commentCount the comment count
     */
    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    /**
     * Gets tags.
     *
     * @return the tags
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * Sets tags.
     *
     * @param tags the tags
     */
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    /**
     * Is pinned boolean.
     *
     * @return the boolean
     */
    public boolean isPinned() {
        return isPinned;
    }

    /**
     * Sets pinned.
     *
     * @param pinned the pinned
     */
    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    /**
     * Gets comments.
     *
     * @return the comments
     */
    public List<ForumComment> getComments() {
        return comments;
    }

    /**
     * Sets comments.
     *
     * @param comments the comments
     */
    public void setComments(List<ForumComment> comments) {
        this.comments = comments;
    }

    /**
     * Add comment.
     *
     * @param comment the comment
     */
    public void addComment(ForumComment comment) {
        if (this.comments == null) {
            this.comments = new ArrayList<>();
        }
        this.comments.add(comment);
        this.commentCount = this.comments.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ForumPost forumPost = (ForumPost) o;
        return Objects.equals(id, forumPost.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ForumPost{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", author='" + authorName + '\'' +
                ", createdAt=" + createdAt +
                ", commentCount=" + commentCount +
                '}';
    }
}