package com.library.backend;

import com.library.backend.dao.UserDAO;
import com.library.backend.services.UserService;
import com.library.backend.services.BookService;
import com.library.backend.models.Book;

import java.util.List;
import java.util.TimeZone;

public class BackendApplication {
    public static void main(String[] args) {
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

        try {
            System.out.println("Starting Backend Application...");

            // Initialize services
            BookService bookService = new BookService();
            UserService userService = new UserService();

            // Demonstrate book service functionality
            System.out.println("\nFetching all books:");
            List<Book> allBooks = bookService.getAllBooks();
            allBooks.forEach(System.out::println);

            System.out.println("\nSearching for 'One Piece' books:");
            List<Book> searchResults = bookService.searchBooksByTitle("One Piece");
            searchResults.forEach(System.out::println);

            System.out.println("\nFetching book by ISBN:");
            Book book = bookService.getBookByISBN(9784088725093L);
            System.out.println(book);

            // Example usage: Greet a user using existing UserService
            String greeting = userService.greetUser("John");
            System.out.println("\n" + greeting);

        } catch (Exception e) {
            System.err.println("Application error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}