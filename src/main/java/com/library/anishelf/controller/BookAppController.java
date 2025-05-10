package com.library.anishelf.controller;

import com.library.anishelf.util.Animation;
import com.library.anishelf.dao.BookItemDAO;
import com.library.anishelf.dao.BookMarkDAO;
import com.library.anishelf.dao.BookReservationDAO;
import com.library.anishelf.dao.CommentDAO;
import com.library.anishelf.model.*;
import com.library.anishelf.model.enums.BookItemStatus;
import com.library.anishelf.service.BookService;
import com.library.anishelf.util.fxmlLoader;
import com.library.anishelf.util.ThemeManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.library.anishelf.controller.BookSuggestionCardController.executor;

public class BookAppController {
    private static final String DASHBOARD_FXML_VIEW = "/view/DashBoard-view.fxml";
    private static final String HISTORY_FXML_VIEW = "/view/History-view.fxml";
    private static final String BOOKMARK_FXML_VIEW = "/view/Bookmark-view.fxml";
    private static final String SETTING_FXML_VIEW = "/view/Setting-view.fxml";
    private static final String COMMENT_FXML_VIEW = "/view/Comment-view.fxml";

    private Book currentBook;

    @FXML
    VBox bookBox,commentsVBox;
    @FXML
    private ChoiceBox<String> starSelectBox;
    @FXML
    private Label writerNameLabel, nameOfBookLabel;
    @FXML
    private ImageView bookImageView, starImageView;
    @FXML
    private Text contentDescriptionText;
    @FXML
    HBox categoryContainer;
    @FXML
    Button bookmarkAppButton;

    /**
     * hàm khởi tạo.
     */
    public void initialize() {
        starSelectBox.getItems().addAll("tất cả", "5 sao", "4 sao", "3 sao", "2 sao", "1 sao");
        starSelectBox.setValue("tất cả");

        starSelectBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (oldValue != null && !oldValue.equals(newValue)) {
                    showCommentBook(fromRateToNum(starSelectBox.getValue()));
                }
            }
        });
        ThemeManager.getInstance().addPane(bookBox);
    }

    /**
     * thiết lập dữ liệu cho sách.
     */
    public void setDataBook() {
        Task<Image> loadImageTask = BookService.getInstance().createLoadImageTask(currentBook);

        loadImageTask.setOnSucceeded(event -> {
            if (bookImageView.getScene() != null) {
                bookImageView.setImage(loadImageTask.getValue());
            }
        });

        executor.submit(loadImageTask);

        nameOfBookLabel.setText(currentBook.getTitle());
        String author = "";
        List<Author> authorList = currentBook.getAuthors();
        for (int i = 0; i < authorList.size(); i++) {
            author += authorList.get(i).getAuthorName() + ",";
        }
        writerNameLabel.setText(author);
        starImageView.setImage(setUpStarImage(currentBook.getRate()));
        contentDescriptionText.setText(currentBook.getDescription());

        List<Comment> comments = new ArrayList<>();
        try {
            Map<String, Object> criteria = new HashMap<>();
            criteria.put("ISBN", currentBook.getISBN());
            comments = CommentDAO.getInstance().searchByCriteria(criteria);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < comments.size(); i++) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource(COMMENT_FXML_VIEW));
                VBox cardBox = fxmlLoader.load();
                CommentController cardController = fxmlLoader.getController();
                cardController.setData(comments.get(i));
                commentsVBox.getChildren().add(cardBox);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        categoryContainer.getChildren().clear();
        List<Category> categories = currentBook.getCategories();
        for (int i = 0; i < categories.size(); i++) {
            Label label = new Label(categories.get(i).toString());
            label.getStyleClass().add("label-4");
            categoryContainer.getChildren().add(label);
        }
        //mark
        if (BookService.getInstance().isContainedInMarkedBookList(currentBook.getISBN())) {
            bookmarkAppButton.setText("Huỷ Đánh Dấu");
        }
    }

    /**
     * hiển thị các comment.
     * @param star rate của comment
     */
    private void showCommentBook(int star) {
        commentsVBox.getChildren().clear();
        List<Comment> comments = new ArrayList<>();
        try {
            Map<String, Object> criteria = new HashMap<>();
            criteria.put("ISBN", currentBook.getISBN());
            comments = CommentDAO.getInstance().searchByCriteria(criteria);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < comments.size(); i++) {
            if (star == 0 || comments.get(i).getRate() == star) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource(COMMENT_FXML_VIEW));
                    VBox cardBox = fxmlLoader.load();
                    CommentController cardController = fxmlLoader.getController();
                    cardController.setData(comments.get(i));
                    commentsVBox.getChildren().add(cardBox);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * quay về dashboard.
     * @param event khi ấn vào
     */
    public void handleBackButton(ActionEvent event) {
        VBox content = (VBox) fxmlLoader.getInstance().loadFXML(DASHBOARD_FXML_VIEW);
        if (content != null) {
            if (bookBox.getChildren().contains(content)) {
                bookBox.getChildren().remove(content);
            }
            fxmlLoader.getInstance().updateContentBox(content);
            fxmlLoader.getInstance().changeColorWhenBack();
        }
    }

    /**
     * đặt trước sách.
     * @param actionEvent khi ấn vào
     */
    public void handleReserveBookButton(ActionEvent actionEvent) {
        boolean confirmYes = CustomerAlter.showAlter("Bạn muốn đặt trước sách chứ gì?");
        if (confirmYes) {
            try {
                Map<String, Object> criteria = new HashMap<>();
                criteria.put("ISBN", currentBook.getISBN());
                criteria.put("BookItemStatus", "AVAILABLE");
                List<BookItem> bookItems = BookItemDAO.getInstance().searchByCriteria(criteria);
                if (bookItems.size() > 0) {
                    BookItem bookItem = BookItemDAO.getInstance().find(bookItems.get(0).getBarcode());
                    bookItem.setStatus(BookItemStatus.RESERVED);
                    BookItemDAO.getInstance().update(bookItem);
                    BookReservation bookReservation = new BookReservation(UserMenuController.getMember()
                            , bookItem, LocalDate.now().toString()
                            , LocalDate.now().plusDays(3).toString());
                    try {
                        BookReservationDAO.getInstance().add(bookReservation);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    HistoryController historyController =
                            fxmlLoader.getInstance().getController(HISTORY_FXML_VIEW);
                    if (historyController != null) {
                        historyController.addReservedBook(bookReservation);
                    }
                    Animation.getInstance().showMessage("Đã ghi nhận, vui lòng mượn trong 3 ngày");

                } else {
                    CustomerAlter.showMessage("Hết mất sách cho cậu mượn rùi T.T");
                }
            } catch (RuntimeException | IOException | SQLException e) {
                e.printStackTrace();
                CustomerAlter.showMessage("Lỗi :v");
            }
        }

    }

    /**
     * đánh dấu sách.
     * @param actionEvent khi ấn vào
     */
    public void handleBookmarkButton(ActionEvent actionEvent) {
        if (bookmarkAppButton.getText().equals("Đánh dấu")) {
            handleMarkedBook();
        } else {
            handleCancelMarkedBook();
        }
    }

    /**
     * huỷ đánh dấu.
     */
    private void handleCancelMarkedBook() {
        boolean confirmYes = CustomerAlter.showAlter("Bạn muốn huỷ đánh dấu sách này à?");
        if (confirmYes) {
            try {
                BookmarkController bookmarkController =
                        fxmlLoader.getInstance().getController(BOOKMARK_FXML_VIEW);
                if (bookmarkController != null) {
                    bookmarkController.deleteBookmark(this.currentBook);
                }
                Map<String, Object> criteria = new HashMap<>();
                criteria.put("ISBN", currentBook.getISBN());
                List<BookItem> bookItem = BookItemDAO.getInstance().searchByCriteria(criteria);
                BookMark bookMark = new BookMark(UserMenuController.getMember()
                        , bookItem.getFirst());
                try {
                    BookMarkDAO.getInstance().delete(bookMark);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                BookService.getInstance().deleteMarkedBook(bookMark);
                bookmarkAppButton.setText("Đánh dấu");
                CustomerAlter.showMessage("Đã huỷ đánh dấu sách nè");
            } catch (RuntimeException | IOException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * đánh dấu sách.
     */
    private void handleMarkedBook() {
        boolean confirmYes = CustomerAlter.showAlter("Bạn muốn đánh dấu sách này à?");
        if (confirmYes) {
            try {
                BookmarkController bookmarkController =
                        fxmlLoader.getInstance().getController(BOOKMARK_FXML_VIEW);
                if (bookmarkController != null) {
                    bookmarkController.addBookmark(this.currentBook);
                }
                Map<String, Object> criteria = new HashMap<>();
                criteria.put("ISBN", currentBook.getISBN());
                List<BookItem> bookItem = BookItemDAO.getInstance().searchByCriteria(criteria);
                BookMark bookMark = new BookMark(UserMenuController.getMember()
                        , bookItem.getFirst());
                bookMark.getBook().setRate(currentBook.getRate());
                try {
                    BookMarkDAO.getInstance().add(bookMark);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                BookService.getInstance().addMarkedBook(bookMark);
                bookmarkAppButton.setText("Huỷ đánh dấu");
                CustomerAlter.showMessage("Đã thêm vô đánh dấu sách nè");
            } catch (RuntimeException | IOException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * từ rate sang Image.
     * @param numOfStar số rate
     * @return ảnh của rate
     */
    private Image setUpStarImage(int numOfStar) {
        System.out.println(numOfStar);
        String imagePath = "/image/book/" + numOfStar + "Star.png";
        if (getClass().getResourceAsStream(imagePath) == null) {
            System.out.println("Image not found: " + imagePath);
            return new Image(getClass().getResourceAsStream("/image/book/5Star.png"));
        }
        return new Image(getClass().getResourceAsStream(imagePath));
    }

    /**
     * trả về sách.
     * @return sách
     */
    public Book getCurrentBook() {
        return currentBook;
    }

    public void setCurrentBook(Book currentBook) {
        this.currentBook = currentBook;
        setDataBook();
    }

    /**
     * từ rate choiceBox sang int
     * @param rate rate
     * @return số rate
     */
    private int fromRateToNum(String rate) {
        char firstChar = rate.charAt(0);
        if (Character.isAlphabetic(firstChar)) {
            return 0;
        }
        return Character.getNumericValue(firstChar);
    }

    /**
     * mở preview.
     * @param mouseEvent
     */
    public void handleOpenPreviewMouseClicked(MouseEvent mouseEvent) {
        System.out.println(currentBook.getPreview());
        handleOpenWebLink(currentBook.getPreview());
    }

    /**
     * mở web .
     * @param url link
     */
    private void handleOpenWebLink(String url) {
        try {
            // Kiểm tra nếu hệ thống hỗ trợ Desktop API
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    // Kiểm tra URL hợp lệ
                    URI uri = new URI(url);
                    desktop.browse(uri); // Mở trình duyệt với URL
                    System.out.println("Đang mở liên kết: " + url);
                } else {
                    System.out.println("Desktop không hỗ trợ mở trình duyệt.");
                }
            } else {
                System.out.println("Desktop API không được hỗ trợ trên hệ thống này.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            CustomerAlter.showMessage("Không có preview của sách này");
            System.out.println("Lỗi khi mở liên kết: " + url);
        }
    }

}
