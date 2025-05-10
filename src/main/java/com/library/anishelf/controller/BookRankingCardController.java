package com.library.anishelf.controller;

import com.library.anishelf.model.Author;
import com.library.anishelf.model.Book;
import com.library.anishelf.service.BookService;
import com.library.anishelf.util.fxmlLoader;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static com.library.anishelf.controller.BookSuggestionCardController.executor;

public class BookRankingCardController {
    private fxmlLoader fxmlLoader = fxmlLoader.getInstance();

    private static final String BOOK_FXML_VIEW = "/view/Book-view.fxml";

    private Book currentBook;

    @FXML
    private Label writerNameLabel;

    @FXML
    private ImageView bookImageView;

    @FXML
    private Label nameOfBookLabel;

    @FXML
    private Label rankingOfBook;

    @FXML
    private HBox hBoxContainer;

    @FXML
    private ImageView starImageVỉew;

    /**
     * thiết lập dữ liệu cho card.
     * @param book sách
     * @param rank hạng
     */
    public void setDataItem(Book book, String rank) {
        this.currentBook = book;
        nameOfBookLabel.setText("currentBook name");
        writerNameLabel.setText("author");
        nameOfBookLabel.setText(book.getTitle());
        String author = "";
        List<Author> authorList = book.getAuthors();
        for(int i = 0;i<authorList.size();i++) {
            author += authorList.get(i).getAuthorName() + ",";
        }
        writerNameLabel.setText(author);
        starImageVỉew.setImage(setUpStarImage(book.getRate()));
        rankingOfBook.setText(rank);

        Task<Image> loadImageTask = BookService.getInstance().createLoadImageTask(book);

        loadImageTask.setOnSucceeded(event -> {
            if (bookImageView.getScene() != null) {
                bookImageView.setImage(loadImageTask.getValue());
            }
        });

        executor.submit(loadImageTask);
    }

    /**
     * chọn đọc sách.
     * @param actionEvent khi ấn vào
     */
    public void handleReadButton(ActionEvent actionEvent) {
        try {
            // Load FXML
            FXMLLoader fxmlLoader = new FXMLLoader();
            URL resource = fxmlLoader.class.getResource(BOOK_FXML_VIEW);
            fxmlLoader.setLocation(resource);

            VBox newContent = fxmlLoader.load();

            BookAppController bookAppController = fxmlLoader.getController();
            if (currentBook != null) {
                bookAppController.setCurrentBook(currentBook);
                bookAppController.setDataBook();
            } else {
                System.err.println("Book object is null!");
            }

            this.fxmlLoader.updateContentBox(newContent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * từ rate sang Inage.
     * @param numOfStar rate
     * @return Image
     */
    private Image setUpStarImage(int numOfStar) {
        String imagePath = "/image/book/" + numOfStar + "Star.png";
        if (getClass().getResourceAsStream(imagePath) == null) {
            System.out.println("Image not found: " + imagePath);
            return new Image(getClass().getResourceAsStream("/image/book/1Star.png"));
        }
        return new Image(getClass().getResourceAsStream(imagePath));
    }
}
