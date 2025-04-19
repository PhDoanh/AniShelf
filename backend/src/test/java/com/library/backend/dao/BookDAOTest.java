package com.library.backend.dao;

import com.library.backend.models.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;

class BookDAOTest {
    private BookDAO bookDAO;

    @BeforeEach
    void setUp() {
        bookDAO = new BookDAO();
    }

    @Test
    void testFindAllBooks() throws SQLException {
        List<Book> books = bookDAO.findAllBooks();
        assertNotNull(books);
        assertFalse(books.isEmpty());
        // Verify first book from sample data
        assertTrue(books.stream()
            .anyMatch(b -> b.getISBN().equals(9784088725093L) && 
                         b.getTitle().equals("One Piece Volume 1")));
    }

    @Test
    void testFindBookByISBN() throws SQLException {
        // Test with known ISBN from sample data
        Book book = bookDAO.findBookByISBN(9784088725093L);
        assertNotNull(book);
        assertEquals("One Piece Volume 1", book.getTitle());
        assertEquals("Shelf A-1", book.getPlaceAt());
    }

    @Test
    void testFindBookByISBNNotFound() throws SQLException {
        // Test with non-existent ISBN
        Book book = bookDAO.findBookByISBN(1234567890L);
        assertNull(book);
    }

    @Test
    void testFindBooksByTitle() throws SQLException {
        List<Book> books = bookDAO.findBooksByTitle("One Piece");
        assertNotNull(books);
        assertFalse(books.isEmpty());
        assertTrue(books.stream()
            .allMatch(b -> b.getTitle().toLowerCase().contains("one piece")));
    }

    @Test
    void testFindBooksByTitleNoMatch() throws SQLException {
        List<Book> books = bookDAO.findBooksByTitle("NonExistentTitle");
        assertTrue(books.isEmpty());
    }
}