package com.library.anishelf.model;

/**
 * The type Book mark.
 */
public class BookMark {
    private Member member;
    private Book book;

    /**
     * Instantiates a new Book mark.
     *
     * @param member the member
     * @param book   the book
     */
    public BookMark(Member member, Book book) {
        this.member = member;
        this.book = book;
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
     * Sets member.
     *
     * @param member the member
     */
    public void setMember(Member member) {
        this.member = member;
    }

    /**
     * Gets book.
     *
     * @return the book
     */
    public Book getBook() {
        return book;
    }

    /**
     * Sets book.
     *
     * @param book the book
     */
    public void setBook(Book book) {
        this.book = book;
    }
}
