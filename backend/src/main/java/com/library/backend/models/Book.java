package com.library.backend.models;

import java.sql.Timestamp;

public class Book {
    private Long ISBN;
    private String title;
    private String author;
    private String category;
    private String imagePath;
    private String description;
    private String placeAt;
    private String preview;
    private int quantity;
    private int numberLostBook;
    private int numberLoanedBook;
    private int numberReservedBook;
    private int rate;
    private BookStatus status;
    private Timestamp addedAtTimestamp;

    // Enum for BookStatus
    public enum BookStatus {
        AVAILABLE, UNAVAILABLE
    }

    // Default constructor
    public Book() {}

    // Constructor with required fields
    public Book(Long ISBN, String title) {
        this.ISBN = ISBN;
        this.title = title;
        this.imagePath = "bookImage/default.png";
        this.rate = 5;
        this.status = BookStatus.AVAILABLE;
        this.addedAtTimestamp = new Timestamp(System.currentTimeMillis());
    }

    // Getters and Setters
    public Long getISBN() {
        return ISBN;
    }

    public void setISBN(Long ISBN) {
        this.ISBN = ISBN;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlaceAt() {
        return placeAt;
    }

    public void setPlaceAt(String placeAt) {
        this.placeAt = placeAt;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getNumberLostBook() {
        return numberLostBook;
    }

    public void setNumberLostBook(int numberLostBook) {
        this.numberLostBook = numberLostBook;
    }

    public int getNumberLoanedBook() {
        return numberLoanedBook;
    }

    public void setNumberLoanedBook(int numberLoanedBook) {
        this.numberLoanedBook = numberLoanedBook;
    }

    public int getNumberReservedBook() {
        return numberReservedBook;
    }

    public void setNumberReservedBook(int numberReservedBook) {
        this.numberReservedBook = numberReservedBook;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public Timestamp getAddedAtTimestamp() {
        return addedAtTimestamp;
    }

    public void setAddedAtTimestamp(Timestamp addedAtTimestamp) {
        this.addedAtTimestamp = addedAtTimestamp;
    }

    @Override
    public String toString() {
        // just needed info for debugging
        return String.format(
            "Book{ISBN=%d, title='%s', status=%s, quantity=%d}", 
            ISBN, 
            title, 
            status, 
            quantity
        );
    }
}