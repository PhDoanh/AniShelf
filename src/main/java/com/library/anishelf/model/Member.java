package com.library.anishelf.model;

import com.library.anishelf.model.enums.AccountStatus;

/**
 * The type Member.
 */
public class Member extends Account {
    private int checkedOutBooksCount;
    private int lostBooksCount;

    /**
     * Instantiates a new Member.
     *
     * @param username the username
     * @param password the password
     * @param person   the person
     */
    public Member(String username, String password, Person person) {
        super(username, password, person);
    }

    /**
     * Instantiates a new Member.
     *
     * @param accountId   the account id
     * @param username    the username
     * @param password    the password
     * @param status      the status
     * @param createdDate the created date
     * @param person      the person
     */
    public Member(int accountId, String username, String password, AccountStatus status, String createdDate, Person person) {
        super(accountId, username, password, status, createdDate, person);
    }

    /**
     * Instantiates a new Member.
     *
     * @param person the person
     */
    public Member(Person person) {
        super(person);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Member) {
            Member member = (Member) obj;
            return getPerson().getId() == member.getPerson().getId();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(getPerson().getId());
    }

    @Override
    public String toString() {
        return getPerson().getId() + "";
    }

    /**
     * Gets checked out books count.
     *
     * @return the checked out books count
     */
    public int getCheckedOutBooksCount() {
        return checkedOutBooksCount;
    }

    /**
     * Sets checked out books count.
     *
     * @param checkedOutBooksCount the checked out books count
     */
    public void setCheckedOutBooksCount(int checkedOutBooksCount) {
        this.checkedOutBooksCount = checkedOutBooksCount;
    }

    /**
     * Gets lost books count.
     *
     * @return the lost books count
     */
    public int getLostBooksCount() {
        return lostBooksCount;
    }

    /**
     * Sets lost books count.
     *
     * @param lostBooksCount the lost books count
     */
    public void setLostBooksCount(int lostBooksCount) {
        this.lostBooksCount = lostBooksCount;
    }
}
