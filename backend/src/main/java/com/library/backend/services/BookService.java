package com.library.backend.services;

import com.library.backend.dao.BookDAO;
import com.library.backend.models.Book;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Service layer for handling book-related business logic
 */
public class BookService {
    private final BookDAO bookDAO;

    public BookService() {
        this.bookDAO = new BookDAO();
    }

    /**
     * Retrieve all books from the database
     * 
     * @return List of all books
     * @throws SQLException if database error occurs
     */
    public List<Book> getAllBooks() throws SQLException {
        return bookDAO.findAllBooks();
    }

    /**
     * Get a book by its ISBN
     * 
     * @param ISBN The ISBN of the book
     * @return The book or null if not found
     * @throws SQLException if database error occurs
     * @throws IllegalArgumentException if ISBN is invalid
     */
    public Book getBookByISBN(Long ISBN) throws SQLException {
        if (ISBN == null || ISBN <= 0) {
            throw new IllegalArgumentException("Invalid ISBN");
        }
        return bookDAO.findBookByISBN(ISBN);
    }

    /**
     * Search books by title
     * 
     * @param titleKeyword The keyword to search for in titles
     * @return List of books matching the keyword
     * @throws SQLException if database error occurs
     * @throws IllegalArgumentException if keyword is empty
     */
    public List<Book> searchBooksByTitle(String titleKeyword) throws SQLException {
        if (titleKeyword == null || titleKeyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Search keyword cannot be empty");
        }
        return bookDAO.findBooksByTitle(titleKeyword.trim());
    }
    
    /**
     * Get popular books based on rating and borrow frequency
     * 
     * @param limit Maximum number of books to return
     * @return List of popular books
     * @throws SQLException if database error occurs
     */
    public List<Book> getPopularBooks(int limit) throws SQLException {
        List<Book> allBooks = bookDAO.findAllBooks();
        
        // Sort by rating first, then by borrow count (represented by numberLoanedBook)
        return allBooks.stream()
                .sorted(Comparator.comparing(Book::getRate).reversed()
                        .thenComparing(Book::getNumberLoanedBook, Comparator.reverseOrder()))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * Get favorite books based on user ratings
     * 
     * @param limit Maximum number of books to return
     * @return List of highest rated books
     * @throws SQLException if database error occurs
     */
    public List<Book> getFavoriteBooks(int limit) throws SQLException {
        List<Book> allBooks = bookDAO.findAllBooks();
        
        // Sort by rating only
        return allBooks.stream()
                .sorted(Comparator.comparing(Book::getRate).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * Get top ranked books based on a comprehensive ranking algorithm
     * 
     * @param limit Maximum number of books to return
     * @return List of top ranked books
     * @throws SQLException if database error occurs
     */
    public List<Book> getTopRankedBooks(int limit) throws SQLException {
        List<Book> allBooks = bookDAO.findAllBooks();
        
        // Comprehensive ranking based on rating, popularity, and availability
        return allBooks.stream()
                .sorted(Comparator.comparing(Book::getRate).reversed()
                        .thenComparing(Book::getNumberLoanedBook, Comparator.reverseOrder())
                        .thenComparing(book -> book.getQuantity() - book.getNumberLoanedBook() - book.getNumberReservedBook(), Comparator.reverseOrder()))
                .limit(limit)
                .collect(Collectors.toList());
    }
}