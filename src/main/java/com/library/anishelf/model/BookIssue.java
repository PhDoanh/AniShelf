package com.library.anishelf.model;

import com.library.anishelf.model.enums.BookIssueStatus;

/**
 * The type Book issue.
 */
public class BookIssue {
    private int issueID;
    private Member member;
    private BookItem bookItem;
    private String issueDate;
    private String dueDate;
    private String actualReturnDate;
    private BookIssueStatus status;

    /**
     * Instantiates a new Book issue.
     *
     * @param issueID          the issue id
     * @param member           the member
     * @param bookItem         the book item
     * @param issueDate        the issue date
     * @param dueDate          the due date
     * @param actualReturnDate the actual return date
     * @param status           the status
     */
    public BookIssue(int issueID, Member member, BookItem bookItem
            , String issueDate, String dueDate, String actualReturnDate, BookIssueStatus status) {
        this.issueID = issueID;
        this.member = member;
        this.bookItem = bookItem;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.actualReturnDate = actualReturnDate;
        this.status = status;
    }

    /**
     * Instantiates a new Book issue.
     *
     * @param member    the member
     * @param bookItem  the book item
     * @param issueDate the issue date
     * @param dueDate   the due date
     */
    public BookIssue(Member member, BookItem bookItem, String issueDate, String dueDate) {
        this.member = member;
        this.bookItem = bookItem;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
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
     * Sets book item.
     *
     * @param bookItem the book item
     */
    public void setBookItem(BookItem bookItem) {
        this.bookItem = bookItem;
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
     * Gets book item.
     *
     * @return the book item
     */
    public BookItem getBookItem() {
        return bookItem;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public BookIssueStatus getStatus() {
        return status;
    }

    /**
     * Sets status.
     *
     * @param status the status
     */
    public void setStatus(BookIssueStatus status) {
        this.status = status;
    }

    /**
     * Gets issue id.
     *
     * @return the issue id
     */
    public int getIssueID() {
        return issueID;
    }

    /**
     * Sets issue id.
     *
     * @param issueID the issue id
     */
    public void setIssueID(int issueID) {
        this.issueID = issueID;
    }

    /**
     * Gets issue date.
     *
     * @return the issue date
     */
    public String getIssueDate() {
        return issueDate;
    }

    /**
     * Sets issue date.
     *
     * @param issueDate the issue date
     */
    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    /**
     * Gets due date.
     *
     * @return the due date
     */
    public String getDueDate() {
        return dueDate;
    }

    /**
     * Sets due date.
     *
     * @param dueDate the due date
     */
    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Gets actual return date.
     *
     * @return the actual return date
     */
    public String getActualReturnDate() {
        return actualReturnDate;
    }

    /**
     * Sets actual return date.
     *
     * @param actualReturnDate the actual return date
     */
    public void setActualReturnDate(String actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BookIssue) {
            BookIssue bookIssue = (BookIssue) o;
            return this.issueID == bookIssue.getIssueID();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(this.issueID).hashCode();
    }
}
