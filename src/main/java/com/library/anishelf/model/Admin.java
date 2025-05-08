package com.library.anishelf.model;

/**
 * The type Admin.
 */
public class Admin extends Account {
    /**
     * Instantiates a new Admin.
     *
     * @param username the username
     * @param password the password
     * @param person   the person
     */
    public Admin(String username, String password, Person person) {
        super(username, password, person);
    }

    /**
     * Add new book boolean.
     *
     * @param book the book
     * @return the boolean
     */
    public static boolean addNewBook(Book book) {
        return false;
    }

    /**
     * Remove book boolean.
     *
     * @param book the book
     * @return the boolean
     */
    public static boolean removeBook(Book book) {
        return false;
    }

    /**
     * Modify book details boolean.
     *
     * @param book the book
     * @return the boolean
     */
    public static boolean modifyBookDetails(Book book) {
        return false;
    }

    /**
     * Block member boolean.
     *
     * @param member the member
     * @return the boolean
     */
    public static boolean blockMember(Member member) {
        return false;
    }

    /**
     * Unblock member boolean.
     *
     * @param member the member
     * @return the boolean
     */
    public static boolean unblockMember(Member member) {
        return false;
    }

    /**
     * Add member boolean.
     *
     * @param member the member
     * @return the boolean
     */
    public static boolean addMember(Member member) {
        return false;
    }

    /**
     * Add issue book boolean.
     *
     * @param book   the book
     * @param member the member
     * @return the boolean
     */
    public static boolean addIssueBook(Book book, Member member) {
        return false;
    }

    /**
     * Update issued book details boolean.
     *
     * @param book   the book
     * @param member the member
     * @return the boolean
     */
    public static boolean updateIssuedBookDetails(Book book, Member member) {
        return false;
    }

    /**
     * Revoke issued book boolean.
     *
     * @param book   the book
     * @param member the member
     * @return the boolean
     */
    public static boolean revokeIssuedBook(Book book, Member member) {
        return false;
    }
}
