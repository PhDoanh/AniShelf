package com.library.anishelf.model;

import com.library.anishelf.model.enums.BookReservationStatus;

/**
 * The type Book reservation.
 */
public class BookReservation {
    private int id;
    private Member member;
    private BookItem bookItem;
    private String reservationDate;
    private String expectedReturnDate;
    private BookReservationStatus reservationStatus;

    /**
     * Instantiates a new Book reservation.
     *
     * @param member             the member
     * @param bookItem           the book item
     * @param reservationDate    the reservation date
     * @param expectedReturnDate the expected return date
     */
    public BookReservation(Member member, BookItem bookItem, String reservationDate, String expectedReturnDate) {
        this.member = member;
        this.bookItem = bookItem;
        this.reservationDate = reservationDate;
        this.expectedReturnDate = expectedReturnDate;
    }

    /**
     * Instantiates a new Book reservation.
     *
     * @param id                 the id
     * @param member             the member
     * @param bookItem           the book item
     * @param reservationDate    the reservation date
     * @param expectedReturnDate the expected return date
     * @param reservationStatus  the reservation status
     */
    public BookReservation(int id, Member member, BookItem bookItem
            , String reservationDate, String expectedReturnDate, BookReservationStatus reservationStatus) {
        this.id = id;
        this.member = member;
        this.bookItem = bookItem;
        this.reservationDate = reservationDate;
        this.expectedReturnDate = expectedReturnDate;
        this.reservationStatus = reservationStatus;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(int id) {
        this.id = id;
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
     * Gets book item.
     *
     * @return the book item
     */
    public BookItem getBookItem() {
        return bookItem;
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
     * Gets reservation date.
     *
     * @return the reservation date
     */
    public String getReservationDate() {
        return reservationDate;
    }

    /**
     * Sets reservation date.
     *
     * @param reservationDate the reservation date
     */
    public void setReservationDate(String reservationDate) {
        this.reservationDate = reservationDate;
    }

    /**
     * Gets expected return date.
     *
     * @return the expected return date
     */
    public String getExpectedReturnDate() {
        return expectedReturnDate;
    }

    /**
     * Sets expected return date.
     *
     * @param expectedReturnDate the expected return date
     */
    public void setExpectedReturnDate(String expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }

    /**
     * Gets reservation status.
     *
     * @return the reservation status
     */
    public BookReservationStatus getReservationStatus() {
        return reservationStatus;
    }

    /**
     * Sets reservation status.
     *
     * @param reservationStatus the reservation status
     */
    public void setReservationStatus(BookReservationStatus reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(id).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BookReservation) {
            BookReservation reservation = (BookReservation) obj;
            return id == reservation.getId();
        }
        return false;
    }
}
