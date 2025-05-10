package com.library.anishelf.controller;

import com.library.anishelf.model.Author;
import com.library.anishelf.model.Book;
import com.library.anishelf.model.BookItem;
import com.library.anishelf.service.BookService;
import com.library.anishelf.util.fxmlLoader;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static com.library.anishelf.controller.BookSuggestionCardController.executor;

public class BookCard2Controller {
    private static final String BOOK_FXML_VIEW = "/view/Book-view.fxml";

    private Book currentBook;
    private BookItem bookItem;
    private HistoryController historyBookController;

    @FXML
    private Label writerNameLabel, nameOfBookLabel;
    @FXML
    private ImageView bookImageView, starImageView;
    @FXML
    private VBox vBoxContainer;
    @FXML
    Button cancelReservedBookButton;

    /**
     * thiết lập dữ liệu cho currentBook card
     * @param book currentBook truyền vào card
     */
    public void setDataBook(Book book) {
        this.currentBook =book;
        nameOfBookLabel.setText(book.getTitle());
        String author = "";
        List<Author> authorList = book.getAuthors();
        for(int i = 0;i<authorList.size();i++) {
            author += authorList.get(i).getAuthorName() + ",";
        }
        writerNameLabel.setText(author);
        if(book.getRate() == 0) {
            book.setRate(BookService.getInstance().isContainInAllBooks(book).getRate());
        }
        starImageView.setImage(initializestarImage(book.getRate()));

        Task<Image> loadImageTask = BookService.getInstance().createLoadImageTask(book);

        loadImageTask.setOnSucceeded(event -> {
            if (bookImageView.getScene() != null) {
                bookImageView.setImage(loadImageTask.getValue());
            }
        });

        executor.submit(loadImageTask);
    }

    /**
     * vào sách.
     * @param mouseEvent khi chuột ấn vào
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
            
            // Update content in a JavaFX thread-safe manner using the singleton
            com.library.anishelf.util.fxmlLoader.getInstance().updateContentBox(newContent);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading currentBook view: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * xoá sách đã đặt trước.
     * @param actionEvent khi ấn vào nút
     */
    public void handleCancelReservedButton(ActionEvent actionEvent) {
        try {
            historyBookController.deleteBookReserved(this.bookItem, vBoxContainer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * nếu là sách đã đặt trước thì hển thị nút x ở trên ảnh.
     * @param historyController controller
     * @param bookItem sách đã đặt trước
     */
    public void initializeReservedBook(HistoryController historyController, BookItem bookItem) {
        this.historyBookController = historyController;
        this.bookItem = bookItem;
        cancelReservedBookButton.setVisible(true);
    }

    /**
     * từ rate sang Image
     * @param numOfStar số rate
     * @return ảnh
     */
    private Image initializestarImage(int numOfStar) {
        String imagePath = "/image/book/" + numOfStar + "Star.png";
        if (getClass().getResourceAsStream(imagePath) == null) {
            System.out.println("Image not found: " + imagePath);
            return new Image(getClass().getResourceAsStream("/image/book/1Star.png"));
        }
        return new Image(getClass().getResourceAsStream(imagePath));
    }
}
