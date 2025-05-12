package com.library.anishelf.controller;

import com.library.anishelf.model.Author;
import com.library.anishelf.model.Book;
import com.library.anishelf.service.BookService;
import com.library.anishelf.util.SceneManagerUtil;
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

import static com.library.anishelf.controller.SuggestedBookCardController.executor;

/**
 * The type Book ranking card controller.
 */
public class BookRankingCardController {
    private SceneManagerUtil sceneManagerUtil = SceneManagerUtil.getInstance();

    private static final String BOOK_FXML = "/view/Book.fxml";

    private Book book;

    @FXML
    private Label authorNameLabel;

    @FXML
    private ImageView bookImage;

    @FXML
    private Label bookNameLabel;

    @FXML
    private Label ranking;

    @FXML
    private HBox hBox;

    @FXML
    private ImageView starImage;

    /**
     * thiết lập dữ liệu cho card.
     *
     * @param book truyện
     * @param rank hạng
     */
    public void setData(Book book, String rank) {
        this.book = book;
        bookNameLabel.setText("book name");
        authorNameLabel.setText("author");
        bookNameLabel.setText(book.getTitle());
        String author = "";
        List<Author> authorList = book.getAuthors();
        for (int i = 0; i < authorList.size(); i++) {
            author += authorList.get(i).getName() + ",";
        }
        authorNameLabel.setText(author);
        starImage.setImage(starImage(book.getRate()));
        ranking.setText(rank);

        Task<Image> loadImageTask = BookService.getInstance().createLoadImageTask(book);

        loadImageTask.setOnSucceeded(event -> {
            if (bookImage.getScene() != null) {
                bookImage.setImage(loadImageTask.getValue());
            }
        });

        executor.submit(loadImageTask);
    }

    /**
     * chọn đọc truyện.
     *
     * @param actionEvent khi ấn vào
     */
    public void onReadButtonAction(ActionEvent actionEvent) {
        try {
            // Load FXML
            FXMLLoader fxmlLoader = new FXMLLoader();
            URL resource = SceneManagerUtil.class.getResource(BOOK_FXML);
            fxmlLoader.setLocation(resource);

            VBox newContent = fxmlLoader.load();

            BookController bookController = fxmlLoader.getController();
            if (book != null) {
                bookController.setBook(book);
                bookController.setData();
            } else {
                System.err.println("Book object is null!");
            }

            sceneManagerUtil.updateSceneContainer(newContent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * từ rate sang Inage.
     *
     * @param numOfStar rate
     * @return Image
     */
    private Image starImage(int numOfStar) {
        String imagePath = "/image/general/" + numOfStar + "star.png";
        if (getClass().getResourceAsStream(imagePath) == null) {
            System.out.println("Image not found: " + imagePath);
            return new Image(getClass().getResourceAsStream("/image/general/1star.png"));
        }
        return new Image(getClass().getResourceAsStream(imagePath));
    }
}
