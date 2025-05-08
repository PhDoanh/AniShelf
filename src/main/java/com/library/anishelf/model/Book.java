package com.library.anishelf.model;

import java.util.ArrayList;
import java.util.List;

import com.library.anishelf.model.enums.BookStatus;

/**
 * The type Book.
 */
public class Book {
    /**
     * The constant DEFAULT_IMAGE_PATH.
     */
    public static final String DEFAULT_IMAGE_PATH = "/image/default/book.png";
    private long isbn;
    private String title;
    private String imagePath;
    private String summary;
    private String location;
    private String preview = "No link available";
    private int quantity;
    private int loanedBooksCount;
    private int lostBooksCount;
    private int reservedBooksCount;
    private int rate;
    private BookStatus bookStatus;
    private List<Author> authors;
    private List<Category> categories;

    /**
     * Instantiates a new Book.
     */
    public Book() {
        authors = new ArrayList<Author>();
        categories = new ArrayList<>();
    }

    /**
     * Instantiates a new Book.
     *
     * @param book the book
     */
    public Book(Book book) {
        this.isbn = book.getIsbn();
        this.title = book.getTitle();
        this.imagePath = book.getImagePath();
        this.summary = book.getSummary();
        this.location = book.getLocation();
        this.quantity = book.getQuantity();
        this.loanedBooksCount = book.getLoanedBooksCount();
        this.lostBooksCount = book.getLostBooksCount();
        this.authors = book.getAuthors();
        this.categories = book.getCategories();
    }

    /**
     * Instantiates a new Book.
     *
     * @param isbn       the isbn
     * @param title      the title
     * @param imagePath  the image path
     * @param summary    the summary
     * @param location   the location
     * @param authors    the authors
     * @param categories the categories
     * @param quantity   the quantity
     */
    public Book(long isbn, String title, String imagePath, String summary
            , String location, List<Author> authors, List<Category> categories, int quantity) {
        this.isbn = isbn;
        this.title = title;
        this.summary = summary;
        this.imagePath = imagePath;
        this.location = location;
        this.authors = authors;
        this.categories = categories;
        this.quantity = quantity;
    }

    /**
     * Instantiates a new Book.
     *
     * @param isbn       the isbn
     * @param title      the title
     * @param summary    the summary
     * @param location   the location
     * @param authors    the authors
     * @param categories the categories
     * @param quantity   the quantity
     */
    public Book(long isbn, String title, String summary
            , String location, List<Author> authors, List<Category> categories, int quantity) {
        this.isbn = isbn;
        this.title = title;
        this.summary = summary;
        this.imagePath = DEFAULT_IMAGE_PATH;
        this.location = location;
        this.authors = authors;
        this.categories = categories;
        this.quantity = quantity;
    }

    /**
     * Instantiates a new Book.
     *
     * @param isbn       the isbn
     * @param title      the title
     * @param imagePath  the image path
     * @param summary    the summary
     * @param location   the location
     * @param authors    the authors
     * @param categories the categories
     */
    public Book(long isbn, String title, String imagePath, String summary
            , String location, List<Author> authors, List<Category> categories) {
        this.isbn = isbn;
        this.title = title;
        this.summary = summary;
        this.imagePath = imagePath;
        this.location = location;
        this.loanedBooksCount = 0;
        this.lostBooksCount = 0;
        this.authors = authors;
        this.categories = categories;
    }

    /**
     * Instantiates a new Book.
     *
     * @param isbn       the isbn
     * @param title      the title
     * @param summary    the summary
     * @param location   the location
     * @param authors    the authors
     * @param categories the categories
     */
    public Book(long isbn, String title, String summary
            , String location, List<Author> authors, List<Category> categories) {
        this.isbn = isbn;
        this.title = title;
        this.summary = summary;
        this.imagePath = DEFAULT_IMAGE_PATH;
        this.location = location;
        this.loanedBooksCount = 0;
        this.lostBooksCount = 0;
        this.authors = authors;
        this.categories = categories;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Book) {
            Book book = (Book) o;
            return this.isbn == book.getIsbn();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(isbn);
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
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets image path.
     *
     * @return the image path
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Gets summary.
     *
     * @return the summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * Gets location.
     *
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Gets quantity.
     *
     * @return the quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Gets loaned books count.
     *
     * @return the loaned books count
     */
    public int getLoanedBooksCount() {
        return loanedBooksCount;
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
     * Gets authors.
     *
     * @return the authors
     */
    public List<Author> getAuthors() {
        return authors;
    }

    /**
     * Gets categories.
     *
     * @return the categories
     */
    public List<Category> getCategories() {
        return categories;
    }

    /**
     * Gets reserved books count.
     *
     * @return the reserved books count
     */
    public int getReservedBooksCount() {
        return reservedBooksCount;
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
     * Sets isbn.
     *
     * @param isbn the isbn
     */
    public void setIsbn(long isbn) {
        this.isbn = isbn;
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
     * Sets image path.
     *
     * @param imagePath the image path
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * Sets summary.
     *
     * @param summary the summary
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * Sets location.
     *
     * @param location the location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Sets quantity.
     *
     * @param quantity the quantity
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Sets loaned books count.
     *
     * @param loanedBooksCount the loaned books count
     */
    public void setLoanedBooksCount(int loanedBooksCount) {
        this.loanedBooksCount = loanedBooksCount;
    }

    /**
     * Sets lost books count.
     *
     * @param lostBooksCount the lost books count
     */
    public void setLostBooksCount(int lostBooksCount) {
        this.lostBooksCount = lostBooksCount;
    }

    /**
     * Sets authors.
     *
     * @param authors the authors
     */
    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    /**
     * Sets categories.
     *
     * @param categories the categories
     */
    public void setCategories(List<Category> categories) {
        this.categories = categories;
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
     * Sets reserved books count.
     *
     * @param reservedBooksCount the reserved books count
     */
    public void setReservedBooksCount(int reservedBooksCount) {
        this.reservedBooksCount = reservedBooksCount;
    }

    /**
     * Gets preview.
     *
     * @return the preview
     */
    public String getPreview() {
        return preview;
    }

    /**
     * Sets preview.
     *
     * @param preview the preview
     */
    public void setPreview(String preview) {
        this.preview = preview;
    }

    /**
     * Gets .
     *
     * @return the
     */
    public BookStatus getstatus() {
        return bookStatus;
    }

    /**
     * Sets book status.
     *
     * @param bookStatus the book status
     */
    public void setBookStatus(BookStatus bookStatus) {
        this.bookStatus = bookStatus;
    }

    @Override
    public String toString() {
        return isbn + "";
    }
}
