package com.library.anishelf.model;

import com.library.anishelf.model.enums.BookItemStatus;

/**
 * The type Book item.
 */
public class BookItem extends Book {
    private int bookBarcode;
    private BookItemStatus bookItemStatus;
    private String remarks = "";

    /**
     * Instantiates a new Book item.
     */
    public BookItem() {

    }

    /**
     * Instantiates a new Book item.
     *
     * @param ISBN           the isbn
     * @param bookItemStatus the book item status
     * @param remarks        the remarks
     */
    public BookItem(int ISBN, BookItemStatus bookItemStatus, String remarks) {
        super.setIsbn(ISBN);
        this.bookItemStatus = bookItemStatus;
        this.remarks = remarks;
    }

    /**
     * Instantiates a new Book item.
     *
     * @param bookBarcode    the book barcode
     * @param bookItemStatus the book item status
     * @param remarks        the remarks
     * @param book           the book
     */
    public BookItem(int bookBarcode, BookItemStatus bookItemStatus, String remarks, Book book) {
        super(book);
        this.bookBarcode = bookBarcode;
        this.bookItemStatus = bookItemStatus;
        this.remarks = remarks;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BookItem) {
            BookItem bookItem = (BookItem) obj;
            return bookItem.getBookBarcode() == this.bookBarcode;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.bookBarcode);
    }

    /**
     * Gets book barcode.
     *
     * @return the book barcode
     */
    public int getBookBarcode() {
        return bookBarcode;
    }

    /**
     * Sets book barcode.
     *
     * @param bookBarcode the book barcode
     */
    public void setBookBarcode(int bookBarcode) {
        this.bookBarcode = bookBarcode;
    }

    /**
     * Gets book item status.
     *
     * @return the book item status
     */
    public BookItemStatus getBookItemStatus() {
        return this.bookItemStatus;
    }

    /**
     * Gets remarks.
     *
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * Sets book item status.
     *
     * @param bookItemStatus the book item status
     */
    public void setBookItemStatus(BookItemStatus bookItemStatus) {
        this.bookItemStatus = bookItemStatus;
    }

    /**
     * Sets remarks.
     *
     * @param remarks the remarks
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
