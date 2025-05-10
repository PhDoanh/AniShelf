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

public class BookAppRankingController implements Initializable {

    @FXML
    VBox rankingContainer;

    private BookService bookAppService = BookService.getInstance();

    private List<Book> highRankAppBooks;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        highRankAppBooks = bookAppService.getHighRankBooks();

        for (int i = 0; i < highRankAppBooks.size(); i++) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/view/BookRankingCard-view.fxml"));
                HBox cardBox = fxmlLoader.load();
                BookRankingCardController cardController = fxmlLoader.getController();
                cardController.setDataItem(highRankAppBooks.get(i),String.valueOf(i+1));
                rankingContainer.getChildren().add(cardBox);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(i == 8) break;
        }
    }
}
