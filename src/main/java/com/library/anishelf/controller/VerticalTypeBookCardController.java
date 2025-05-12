package com.library.anishelf.controller;

import com.library.anishelf.model.Author;
import com.library.anishelf.model.Book;
import com.library.anishelf.model.BookItem;
import com.library.anishelf.service.BookService;
import com.library.anishelf.util.SceneManagerUtil;
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

import static com.library.anishelf.controller.SuggestedBookCardController.executor;

/**
 * The type Vertical type book card controller.
 */
public class VerticalTypeBookCardController {
    private static final String BOOK_FXML = "/view/Book.fxml";

    private Book book;
    private BookItem bookItem;
    private ReservedBorrowedHistoryPageController reservedBorrowedHistoryPageController;

    @FXML
    private Label authorNameLabel, bookNameLabel;
    @FXML
    private ImageView bookImage, starImage;
    @FXML
    private VBox vBox;
    /**
     * The Cancel reserved button.
     */
    @FXML
    Button cancelReservedButton;

    /**
     * thiết lập dữ liệu cho book card
     *
     * @param book book truyền vào card
     */
    public void setData(Book book) {
        this.book = book;
        bookNameLabel.setText(book.getTitle());
        String author = "";
        List<Author> authorList = book.getAuthors();
        for (int i = 0; i < authorList.size(); i++) {
            author += authorList.get(i).getName() + ",";
        }
        authorNameLabel.setText(author);
        if (book.getRate() == 0) {
            book.setRate(BookService.getInstance().findBookInAllBooks(book).getRate());
        }
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
     * vào truyện.
     *
     * @param mouseEvent khi chuột ấn vào
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

            // Update content in a JavaFX thread-safe manner using the singleton
            SceneManagerUtil.getInstance().updateSceneContainer(newContent);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading book view: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * xoá truyện đã đặt trước.
     *
     * @param actionEvent khi ấn vào nút
     */
    public void onCancelReservedButtonAction(ActionEvent actionEvent) {
        try {
            reservedBorrowedHistoryPageController.deleteBookReserved(this.bookItem, vBox);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * nếu là truyện đã đặt trước thì hển thị nút x ở trên ảnh.
     *
     * @param reservedBorrowedHistoryPageController controller
     * @param bookItem                              truyện đã đặt trước
     */
    public void setReservedBook(ReservedBorrowedHistoryPageController reservedBorrowedHistoryPageController, BookItem bookItem) {
        this.reservedBorrowedHistoryPageController = reservedBorrowedHistoryPageController;
        this.bookItem = bookItem;
        cancelReservedButton.setVisible(true);
    }

    /**
     * từ rate sang Image
     *
     * @param numOfStar số rate
     * @return ảnh
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
