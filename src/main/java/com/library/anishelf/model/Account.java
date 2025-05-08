package com.library.anishelf.model;

import com.library.anishelf.model.enums.AccountStatus;

/**
 * The type Account.
 */
public abstract class Account {
    /**
     * The Id.
     */
    protected int accId;
    /**
     * The Username.
     */
    protected String username;
    /**
     * The Password.
     */
    protected String password;
    /**
     * The Person.
     */
    protected Person person;
    /**
     * The Status.
     */
    protected AccountStatus status;
    /**
     * The Created date.
     */
    protected String createdDate;

    /**
     * Instantiates a new Account.
     *
     * @param person the person
     */
    public Account(Person person) {
        this.person = person;
    }

    /**
     * Instantiates a new Account.
     *
     * @param username the username
     * @param password the password
     * @param person   the person
     */
    public Account(String username, String password, Person person) {
        this.username = username;
        this.password = password;
        this.person = person;
    }

    /**
     * Instantiates a new Account.
     *
     * @param accId          the id
     * @param username    the username
     * @param password    the password
     * @param status      the status
     * @param createdDate the created date
     * @param person      the person
     */
    public Account(int accId, String username, String password, AccountStatus status, String createdDate, Person person) {
        this.accId = accId;
        this.username = username;
        this.password = password;
        this.status = status;
        this.createdDate = createdDate;
        this.person = person;
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets person.
     *
     * @return the person
     */
    public Person getPerson() {
        return person;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public AccountStatus getStatus() {
        return status;
    }

    /**
     * Sets status.
     *
     * @param status the status
     */
    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    /**
     * Reset password boolean.
     *
     * @return the boolean
     */
    public boolean resetPassword() {
        return false;
    }

    /**
     * Change password boolean.
     *
     * @param password the password
     * @return the boolean
     */
    public boolean changePassword(String password) {
        return false;
    }

    /**
     * Sets username.
     *
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets password.
     *
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets person.
     *
     * @param person the person
     */
    public void setPerson(Person person) {
        this.person = person;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getAccId() {
        return accId;
    }

    /**
     * Sets id.
     *
     * @param accId the id
     */
    public void setAccId(int accId) {
        this.accId = accId;
    }

    /**
     * Gets created date.
     *
     * @return the created date
     */
    public String getCreatedDate() {
        return createdDate;
    }

    /**
     * Sets created date.
     *
     * @param createdDate the created date
     */
    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
