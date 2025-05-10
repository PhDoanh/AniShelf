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

public class BookmarkBookController implements Initializable {
    @FXML
    private HBox recommendationHBox;
    @FXML
    private HBox bookmarkContainer;

    private BookService bookAppService = BookService.getInstance();
    private List<BookMark> bookMarkListBook;
    private List<Book> popularListBooks;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            bookMarkListBook = BookService.getInstance().getMarkedBooks();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < bookMarkListBook.size(); i++) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/view/BookCard1-view.fxml"));
                HBox cardBox = fxmlLoader.load();
                BookCard1Controller cardController = fxmlLoader.getController();
                cardController.setDataBook(bookMarkListBook.get(i).getBook());
                bookmarkContainer.getChildren().add(cardBox);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        popularListBooks = bookAppService.getPopularBooks();
        for (int i = 0; i < popularListBooks.size(); i++) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/view/BookCard2-view.fxml"));
                VBox cardBox = fxmlLoader.load();
                BookCard2Controller cardController = fxmlLoader.getController();
                cardController.setDataBook(popularListBooks.get(i));
                recommendationHBox.getChildren().add(cardBox);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (i == 5) {
                break;
            }
        }
    }

    /**
     * thêm sách đánh dấu.
     * @param book sách
     * @throws IOException ném ngoại lệ
     */
    public void addBookmarkBook(Book book) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/BookCard1-view.fxml"));
        HBox cardBox = fxmlLoader.load();
        BookCard1Controller cardController = fxmlLoader.getController();
        cardController.setDataBook(book);
        bookmarkContainer.getChildren().add(cardBox);
    }

    /**
     * xoá sách đánh dấu.
     * @param book sách
     * @throws IOException ném ngoại lệ
     */
    public void deleteBookmarkBook(Book book) throws IOException {
        int index = findBookMarkBook(book.getISBN());
        if(index!=-1) {
            bookmarkContainer.getChildren().remove(index);
        }
    }

    /**
     * tìm sách.
     * @param ISBN isbn
     * @return sách cần tìm
     */
    private int findBookMarkBook(long ISBN) {
        for (int i = 0; i< bookMarkListBook.size(); i++) {
            if (bookMarkListBook.get(i).getBook().getISBN() == ISBN) {
                return i;
            }
        }
        return -1;
    }
}
