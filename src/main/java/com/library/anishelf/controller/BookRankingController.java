package com.library.anishelf.controller;

import com.library.anishelf.model.Book;
import com.library.anishelf.service.BookService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * The type Book ranking controller.
 */
public class BookRankingController implements Initializable {

    /**
     * The Ranking v box.
     */
    @FXML
    VBox rankingVBox;

    private BookService bookService = BookService.getInstance();

    private List<Book> highRankBooks;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        highRankBooks = bookService.getHighestRatedBooks();

        for (int i = 0; i < highRankBooks.size(); i++) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/view/BookRankingCard.fxml"));
                HBox cardBox = fxmlLoader.load();
                BookRankingCardController cardController = fxmlLoader.getController();
                cardController.setData(highRankBooks.get(i), String.valueOf(i + 1));
                rankingVBox.getChildren().add(cardBox);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (i == 8) {
                break;
            }
        }
    }
}
