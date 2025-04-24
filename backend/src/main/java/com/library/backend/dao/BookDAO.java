package com.library.backend.dao;

import com.library.backend.models.Book;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    public List<Book> findAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM \"Books\"";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Book book = new Book();
                book.setISBN(rs.getLong("ISBN"));
                book.setTitle(rs.getString("title"));
                book.setImagePath(rs.getString("image_path"));
                book.setDescription(rs.getString("description"));
                book.setPlaceAt(rs.getString("placeAt"));
                book.setQuantity(rs.getInt("quantity"));
                book.setRate(rs.getInt("rate"));
                book.setStatus(Book.BookStatus.valueOf(rs.getString("BookStatus")));
                book.setAddedAtTimestamp(rs.getTimestamp("added_at_timestamp"));
                books.add(book);
            }
        }
        return books;
    }

    public Book findBookByISBN(Long ISBN) throws SQLException {
        String sql = "SELECT * FROM \"Books\" WHERE \"ISBN\" = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, ISBN);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Book book = new Book();
                    book.setISBN(rs.getLong("ISBN"));
                    book.setTitle(rs.getString("title"));
                    book.setImagePath(rs.getString("image_path"));
                    book.setDescription(rs.getString("description"));
                    book.setPlaceAt(rs.getString("placeAt"));
                    book.setQuantity(rs.getInt("quantity"));
                    book.setRate(rs.getInt("rate"));
                    book.setStatus(Book.BookStatus.valueOf(rs.getString("BookStatus")));
                    book.setAddedAtTimestamp(rs.getTimestamp("added_at_timestamp"));
                    return book;
                }
            }
        }
        return null;
    }

    public List<Book> findBooksByTitle(String titleKeyword) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM \"Books\" WHERE LOWER(title) LIKE LOWER(?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, "%" + titleKeyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Book book = new Book();
                    book.setISBN(rs.getLong("ISBN"));
                    book.setTitle(rs.getString("title"));
                    book.setImagePath(rs.getString("image_path"));
                    book.setDescription(rs.getString("description"));
                    book.setPlaceAt(rs.getString("placeAt"));
                    book.setQuantity(rs.getInt("quantity"));
                    book.setRate(rs.getInt("rate"));
                    book.setStatus(Book.BookStatus.valueOf(rs.getString("BookStatus")));
                    book.setAddedAtTimestamp(rs.getTimestamp("added_at_timestamp"));
                    books.add(book);
                }
            }
        }
        return books;
    }
}