package com.library.anishelf.controller;

import com.library.anishelf.model.Author;
import com.library.anishelf.service.BookService;
import com.library.anishelf.util.fxmlLoader;
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

import static com.library.anishelf.controller.BookSuggestionCardController.executor;

public class BookCard1Controller {
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
    private HBox hBoxContainer;

    @FXML
    private ImageView starImageView;

    private String [] colorsView = {"DABEEA","E0FFCC"};

    /**
     * thiết lập dữ liệu cho currentBook card.
     * @param book sách truyền vào
     */
    public void setDataBook(Book book) {
        this.currentBook = book;
        nameOfBookLabel.setText(book.getTitle());
        String author = "";
        List<Author> authorList = book.getAuthors();
        for(int i = 0;i<authorList.size();i++) {
            author += authorList.get(i).getAuthorName() + ",";
        }
        writerNameLabel.setText(author);
        starImageView.setImage(setUpstarImage(book.getRate()));
        hBoxContainer.setStyle("-fx-background-color: #" + colorsView[(int)(Math.random() * colorsView.length)]);

        Task<Image> loadImageTask = BookService.getInstance().createLoadImageTask(book);

        loadImageTask.setOnSucceeded(event -> {
            if (bookImageView.getScene() != null) {
                bookImageView.setImage(loadImageTask.getValue());
            }
        });

        executor.submit(loadImageTask);
    }

    /**
     * ấn vào sách.
     * @param mouseEvent khi chuột trỏ vào
     */
    public void handleBookMouseClicked(MouseEvent mouseEvent) {
        try {
            // Prevent event bubbling for better handling
            mouseEvent.consume();
            
            // Check if currentBook is null before proceeding
            if (currentBook == null) {
                System.err.println("Book object is null!");
                return;
            }
            
            FXMLLoader fxmlLoader = new FXMLLoader();
            URL resource = fxmlLoader.class.getResource(BOOK_FXML_VIEW);
            if (resource == null) {
                System.err.println("Could not find resource: " + BOOK_FXML_VIEW);
                return;
            }
            
            fxmlLoader.setLocation(resource);
            VBox newContent = fxmlLoader.load();

            BookAppController bookAppController = fxmlLoader.getController();
            if (bookAppController == null) {
                System.err.println("Could not get BookAppController!");
                return;
            }
            
            bookAppController.setCurrentBook(currentBook);
            
            // Update content in a JavaFX thread-safe manner
            if (this.fxmlLoader != null) {
                this.fxmlLoader.updateContentBox(newContent);
            } else {
                System.err.println("fxmlLoader is null!");
                // Fallback to static instance if instance variable is null
                com.library.anishelf.util.fxmlLoader.getInstance().updateContentBox(newContent);
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading currentBook view: " + e.getMessage());
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
    private Image setUpstarImage(int numOfStar) {
        String imagePath = "/image/book/" + numOfStar + "Star.png";
        if (getClass().getResourceAsStream(imagePath) == null) {
            System.out.println("Image not found: " + imagePath);
            return new Image(getClass().getResourceAsStream("/image/book/1Star.png"));
        }
        return new Image(getClass().getResourceAsStream(imagePath));
    }

}
