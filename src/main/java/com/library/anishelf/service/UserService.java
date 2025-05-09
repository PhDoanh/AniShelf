package com.library.anishelf.service;

import com.library.anishelf.controller.CustomerAlter;
import com.library.anishelf.dao.BookDAO;
import com.library.anishelf.dao.BookItemDAO;
import com.library.anishelf.dao.CommentDAO;
import com.library.anishelf.model.Book;
import com.library.anishelf.model.BookItem;
import com.library.anishelf.model.Comment;
import com.library.anishelf.model.Member;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserService {
    private String operation;
    private Object payload;
    private Member memberResult;

    private List<Book> bookList = new ArrayList<>();

    public UserService(String operation, Object payload) {
        this.operation = operation;
        this.payload = payload;
    }

    public Member getMemberResult() {
        return memberResult;
    }

    /**
     * các Service người dùng muốn thực thi.
     * Hiện tại có các lệnh add,delete,edit.
     *
     * @return true - thành công, false - thất bại
     */
    public boolean processOperation() {
        try {
            switch (operation) {
                case "add":
                    if (payload instanceof Book) {
                        BookDAO.getInstance().insert((Book) payload);
                    } else if (payload instanceof Comment) {
                        CommentDAO.getInstance().insert((Comment) payload);
                    } else if (payload instanceof BookItem) {
                        BookItemDAO.getInstance().insert((BookItem) payload);
                    }
                    return true;
                case "getPopularBooks":
                    this.payload = BookService.getInstance().getMostPopularBooks();
                    return true;
                case "getHighRankBooks":
                    this.payload = BookService.getInstance().getHighestRatedBooks();
                    return true;
                case "getAllBooks":
                    this.payload = BookService.getInstance().getAllAvailableBooks();
                    return true;
                case "searchBookByCategory":
                    Map<String, Object> criteria = (Map<String, Object>) payload;
                    this.payload = BookDAO.getInstance().findByCriteria(criteria);
                    return true;
                default:
                    return false;
            }
        } catch (SQLException e) {
            CustomerAlter.showAlter(e.getMessage());
            return false; // Thất bại
        }
    }

    public Object getPayload() {
        return this.payload;
    }
}
