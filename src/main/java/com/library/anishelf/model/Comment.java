package com.library.anishelf.model;

/**
 * The type Comment.
 */
public class Comment {
    private int commentId;
    private String title;
    private String content;
    private int rate = 5;
    private Member member;
    private long isbn;

    /**
     * Instantiates a new Comment.
     *
     * @param title   the title
     * @param content the content
     * @param rate    the rate
     * @param member  the member
     * @param isbn    the isbn
     */
    public Comment(String title, String content, int rate, Member member, long isbn) {
        this.title = title;
        this.content = content;
        this.rate = rate;
        this.member = member;
        this.isbn = isbn;
    }

    /**
     * Instantiates a new Comment.
     *
     * @param commentId the comment id
     * @param title     the title
     * @param content   the content
     * @param rate      the rate
     * @param member    the member
     * @param isbn      the isbn
     */
    public Comment(int commentId, String title, String content, int rate, Member member, long isbn) {
        this.commentId = commentId;
        this.title = title;
        this.content = content;
        this.rate = rate;
        this.member = member;
        this.isbn = isbn;
    }

    /**
     * Gets comment id.
     *
     * @return the comment id
     */
    public int getCommentId() {
        return commentId;
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
     * Gets content.
     *
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * Gets rate.
     *
     * @return the rate
     */
    public int getRate() {
        return rate;
    }

    /**
     * Gets member.
     *
     * @return the member
     */
    public Member getMember() {
        return member;
    }

    /**
     * Gets isbn.
     *
     * @return the isbn
     */
    public long getIsbn() {
        return isbn;
    }

    /**
     * Sets comment id.
     *
     * @param commentId the comment id
     */
    public void setCommentId(int commentId) {
        this.commentId = commentId;
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
     * Sets content.
     *
     * @param content the content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Sets rate.
     *
     * @param rate the rate
     */
    public void setRate(int rate) {
        this.rate = rate;
    }

    /**
     * Sets member.
     *
     * @param member the member
     */
    public void setMember(Member member) {
        this.member = member;
    }

    /**
     * Sets isbn.
     *
     * @param isbn the isbn
     */
    public void setIsbn(int isbn) {
        this.isbn = isbn;
    }
}
