package com.library.backend.services;

import com.library.backend.dao.BookDAO;
import com.library.backend.models.Book;
import java.sql.SQLException;
import java.util.List;

public class BookService {
    private final BookDAO bookDAO;

    public BookService() {
        this.bookDAO = new BookDAO();
    }

    public List<Book> getAllBooks() throws SQLException {
        return bookDAO.findAllBooks();
    }

    public Book getBookByISBN(Long ISBN) throws SQLException {
        if (ISBN == null || ISBN <= 0) {
            throw new IllegalArgumentException("Invalid ISBN");
        }
        return bookDAO.findBookByISBN(ISBN);
    }

    public List<Book> searchBooksByTitle(String titleKeyword) throws SQLException {
        if (titleKeyword == null || titleKeyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Search keyword cannot be empty");
        }
        return bookDAO.findBooksByTitle(titleKeyword.trim());
    }
}