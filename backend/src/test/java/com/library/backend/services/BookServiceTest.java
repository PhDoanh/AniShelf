package com.library.backend.services;

import com.library.backend.models.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;

class BookServiceTest {
    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookService = new BookService();
    }

    @Test
    void testGetBookByISBN() throws SQLException {
        // Test with a known ISBN from sample data
        Book book = bookService.getBookByISBN(9784088725093L);
        assertNotNull(book);
        assertEquals("One Piece Volume 1", book.getTitle());
    }

    @Test
    void testGetBookByISBNInvalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            bookService.getBookByISBN(null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            bookService.getBookByISBN(-1L);
        });
    }

    @Test
    void testSearchBooksByTitle() throws SQLException {
        List<Book> books = bookService.searchBooksByTitle("One Piece");
        assertFalse(books.isEmpty());
        assertTrue(books.stream().anyMatch(b -> b.getTitle().contains("One Piece")));
    }

    @Test
    void testSearchBooksByTitleEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            bookService.searchBooksByTitle("");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            bookService.searchBooksByTitle(null);
        });
    }

    @Test
    void testGetAllBooks() throws SQLException {
        List<Book> books = bookService.getAllBooks();
        assertNotNull(books);
        assertFalse(books.isEmpty());
    }
}