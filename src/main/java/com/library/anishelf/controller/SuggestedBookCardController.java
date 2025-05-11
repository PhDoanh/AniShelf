package com.library.anishelf.controller;

import com.library.anishelf.dao.BookDAO;
import com.library.anishelf.model.Author;
import com.library.anishelf.model.Book;
import com.library.anishelf.service.BookService;
import com.library.anishelf.util.SceneManagerUtil;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class SuggestedBookCardController {
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
    private HBox hBox;

    @FXML
    private ImageView starImage;

    protected static final ExecutorService executor = Executors.newFixedThreadPool(4);


    /**
     * thiết lập dữ liệu cho card.
     * @param otherBook truyện
     */
    public void setData(Book otherBook) {
        this.book = BookService.getInstance().findBookInAllBooks(otherBook);

        if(this.book==null) {
            this.book = otherBook;
            book.setQuantity(10);
            book.setRate(5);
        }

        // Hiển thị thông tin cơ bản
        bookNameLabel.setText(book.getTitle());
        String author = book.getAuthors().stream()
                .map(Author::getName)
                .collect(Collectors.joining(", "));
        authorNameLabel.setText(author);
        starImage.setImage(starImage(book.getRate()));

        Task<Image> loadImageTask = BookService.getInstance().createLoadImageTask(book);

        loadImageTask.setOnSucceeded(event -> {
            if (bookImage.getScene() != null) {
                bookImage.setImage(loadImageTask.getValue());
            }
        });

        executor.submit(loadImageTask);
    }

    /**
     * xem truyện.
     * @param mouseEvent khi  ấn vào
     */
    public void onBookMouseClicked(MouseEvent mouseEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            URL resource = SceneManagerUtil.class.getResource(BOOK_FXML);
            fxmlLoader.setLocation(resource);

            VBox newContent = fxmlLoader.load();

            BookController bookController = fxmlLoader.getController();
            if (book != null) {
                bookController.setBook(book);
            } else {
                System.err.println("Book object is null!");
            }

            sceneManagerUtil.updateSceneContainer(newContent);

            try {
                BookDAO.getInstance().insert(book);
            } catch (SQLException e) {
                System.out.println("book already exist in database");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * từ sao sang image.
     * @param numOfStar số sao
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