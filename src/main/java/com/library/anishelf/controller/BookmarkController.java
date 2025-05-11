package com.library.anishelf.controller;

import com.library.anishelf.model.Book;
import com.library.anishelf.model.BookMark;
import com.library.anishelf.service.BookService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class BookmarkController implements Initializable {
    @FXML
    private HBox suggestHBox;
    @FXML
    private HBox bookmarkHBox;

    private BookService bookService = BookService.getInstance();
    private List<BookMark> bookMarkList;
    private List<Book> popularBooks;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            bookMarkList = BookService.getInstance().getBookmarks();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < bookMarkList.size(); i++) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/view/HorizontalTypeBookCard.fxml"));
                HBox cardBox = fxmlLoader.load();
                HorizontalTypeBookCardController cardController = fxmlLoader.getController();
                cardController.setData(bookMarkList.get(i).getBook());
                bookmarkHBox.getChildren().add(cardBox);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        popularBooks = bookService.getMostPopularBooks();
        for (int i = 0; i < popularBooks.size(); i++) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/view/VerticalTypeBookCard.fxml"));
                VBox cardBox = fxmlLoader.load();
                VerticalTypeBookCardController cardController = fxmlLoader.getController();
                cardController.setData(popularBooks.get(i));
                suggestHBox.getChildren().add(cardBox);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (i == 5) {
                break;
            }
        }
    }

    /**
     * thêm truyện đánh dấu.
     * @param book truyện
     * @throws IOException ném ngoại lệ
     */
    public void addBookmark(Book book) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/HorizontalTypeBookCard.fxml"));
        HBox cardBox = fxmlLoader.load();
        HorizontalTypeBookCardController cardController = fxmlLoader.getController();
        cardController.setData(book);
        bookmarkHBox.getChildren().add(cardBox);
    }

    /**
     * xoá truyện đánh dấu.
     * @param book truyện
     * @throws IOException ném ngoại lệ
     */
    public void deleteBookmark(Book book) throws IOException {
        int index = findBookMark(book.getIsbn());
        if(index!=-1) {
            bookmarkHBox.getChildren().remove(index);
        }
    }

    /**
     * tìm truyện.
     * @param ISBN isbn
     * @return truyện cần tìm
     */
    private int findBookMark(long ISBN) {
        for (int i = 0;i<bookMarkList.size();i++) {
            if (bookMarkList.get(i).getBook().getIsbn() == ISBN) {
                return i;
            }
        }
        return -1;
    }
}
