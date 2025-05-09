package com.library.anishelf.controller;

import com.library.anishelf.model.Author;
import com.library.anishelf.service.BookService;
import com.library.anishelf.util.SceneManagerUtil;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import com.library.anishelf.model.Book;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static com.library.anishelf.controller.SuggestedBookCardController.executor;

public class HorizontalTypeBookCardController {
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

    private String [] colors = {"DABEEA","E0FFCC"};

    /**
     * thiết lập dữ liệu cho book card.
     * @param book sách truyền vào
     */
    public void setData(Book book) {
        this.book = book;
        bookNameLabel.setText(book.getTitle());
        String author = "";
        List<Author> authorList = book.getAuthors();
        for(int i = 0;i<authorList.size();i++) {
            author += authorList.get(i).getName() + ",";
        }
        authorNameLabel.setText(author);
        starImage.setImage(starImage(book.getRate()));
        hBox.setStyle("-fx-background-color: #" + colors[(int)(Math.random() * colors.length)]);

        Task<Image> loadImageTask = BookService.getInstance().createLoadImageTask(book);

        loadImageTask.setOnSucceeded(event -> {
            if (bookImage.getScene() != null) {
                bookImage.setImage(loadImageTask.getValue());
            }
        });

        executor.submit(loadImageTask);
    }

    /**
     * ấn vào sách.
     * @param mouseEvent khi chuột trỏ vào
     */
    public void onBookMouseClicked(MouseEvent mouseEvent) {
        try {
            // Prevent event bubbling for better handling
            mouseEvent.consume();
            
            // Check if book is null before proceeding
            if (book == null) {
                System.err.println("Book object is null!");
                return;
            }
            
            FXMLLoader fxmlLoader = new FXMLLoader();
            URL resource = SceneManagerUtil.class.getResource(BOOK_FXML);
            if (resource == null) {
                System.err.println("Could not find resource: " + BOOK_FXML);
                return;
            }
            
            fxmlLoader.setLocation(resource);
            VBox newContent = fxmlLoader.load();

            BookController bookController = fxmlLoader.getController();
            if (bookController == null) {
                System.err.println("Could not get BookController!");
                return;
            }
            
            bookController.setBook(book);
            
            // Update content in a JavaFX thread-safe manner
            if (sceneManagerUtil != null) {
                sceneManagerUtil.updateSceneContainer(newContent);
            } else {
                System.err.println("SceneManagerUtil is null!");
                // Fallback to static instance if instance variable is null
                SceneManagerUtil.getInstance().updateSceneContainer(newContent);
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading book view: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * chuyển từ rate sang Inage.
     * @param numOfStar số rate
     * @return ảnh của rate
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
