package com.library.anishelf.controller;

import com.library.anishelf.dao.BookItemDAO;
import com.library.anishelf.dao.BookMarkDAO;
import com.library.anishelf.dao.BookReservationDAO;
import com.library.anishelf.dao.CommentDAO;
import com.library.anishelf.model.*;
import com.library.anishelf.model.enums.BookItemStatus;
import com.library.anishelf.service.BookService;
import com.library.anishelf.util.SceneManagerUtil;
import com.library.anishelf.util.RuntimeDebugUtil;
import com.library.anishelf.util.ThemeManagerUtil;
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

import static com.library.anishelf.controller.SuggestedBookCardController.executor;

public class BookController {
    private static final String DASHBOARD_FXML = "/view/UserHomePage.fxml";
    private static final String HISTORY_FXML = "/view/ReservedBorrowedHistoryPage.fxml";
    private static final String BOOKMARK_FXML = "/view/Bookmark.fxml";
    private static final String COMMENT_FXML = "/view/Comment.fxml";
    
    // Thêm RuntimeDebugUtil và TAG
    private static final RuntimeDebugUtil logger = RuntimeDebugUtil.getInstance();
    private static final String TAG = "BookController";

    private Book book;

    @FXML
    VBox bookBox,commentsVBox;
    @FXML
    private ChoiceBox<String> starChoiceBox;
    @FXML
    private Label authorNameLabel,bookNameLabel;
    @FXML
    private ImageView bookImage, starImage;
    @FXML
    private Text contentText;
    @FXML
    HBox categoryHbox;
    @FXML
    Button bookmarkButton;

    /**
     * hàm khởi tạo.
     */
    public void initialize() {
        starChoiceBox.getItems().addAll("tất cả", "5 sao", "4 sao", "3 sao", "2 sao", "1 sao");
        starChoiceBox.setValue("tất cả");

        starChoiceBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (oldValue != null && !oldValue.equals(newValue)) {
                    showComment(fromRateToInt(starChoiceBox.getValue()));
                }
            }
        });
        ThemeManagerUtil.getInstance().addPane(bookBox);
    }

    /**
     * thiết lập dữ liệu cho sách.
     */
    public void setData() {
        Task<Image> loadImageTask = BookService.getInstance().createLoadImageTask(book);

        loadImageTask.setOnSucceeded(event -> {
            if (bookImage.getScene() != null) {
                bookImage.setImage(loadImageTask.getValue());
            }
        });

        executor.submit(loadImageTask);

        bookNameLabel.setText(book.getTitle());
        String author = "";
        List<Author> authorList = book.getAuthors();
        for (int i = 0; i < authorList.size(); i++) {
            author += authorList.get(i).getName() + ",";
        }
        authorNameLabel.setText(author);
        starImage.setImage(starImage(book.getRate()));
        contentText.setText(book.getSummary());

        List<Comment> comments = new ArrayList<>();
        try {
            Map<String, Object> criteria = new HashMap<>();
            criteria.put("ISBN", book.getIsbn());
            comments = CommentDAO.getInstance().findByCriteria(criteria);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < comments.size(); i++) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource(COMMENT_FXML));
                VBox cardBox = fxmlLoader.load();
                CommentController cardController = fxmlLoader.getController();
                cardController.setData(comments.get(i));
                commentsVBox.getChildren().add(cardBox);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        categoryHbox.getChildren().clear();
        List<Category> categories = book.getCategories();
        for (int i = 0; i < categories.size(); i++) {
            Label label = new Label(categories.get(i).toString());
            label.getStyleClass().add("label-4");
            categoryHbox.getChildren().add(label);
        }
        //mark
        if (BookService.getInstance().isBookMarked(book.getIsbn())) {
            bookmarkButton.setText("Huỷ Đánh Dấu");
        }
    }

    /**
     * hiển thị các comment.
     * @param star rate của comment
     */
    private void showComment(int star) {
        commentsVBox.getChildren().clear();
        List<Comment> comments = new ArrayList<>();
        try {
            Map<String, Object> criteria = new HashMap<>();
            criteria.put("ISBN", book.getIsbn());
            comments = CommentDAO.getInstance().findByCriteria(criteria);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < comments.size(); i++) {
            if (star == 0 || comments.get(i).getRate() == star) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource(COMMENT_FXML));
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
    public void onBackButtonAction(ActionEvent event) {
        VBox content = (VBox) SceneManagerUtil.getInstance().loadScene(DASHBOARD_FXML);
        if (content != null) {
            if (bookBox.getChildren().contains(content)) {
                bookBox.getChildren().remove(content);
            }
            SceneManagerUtil.getInstance().updateSceneContainer(content);
            SceneManagerUtil.getInstance().highlightBackButton();
        }
    }

    /**
     * đặt trước sách.
     * @param actionEvent khi ấn vào
     */
    public void onReserveBookButtonAction(ActionEvent actionEvent) {
        boolean confirmYes = CustomerAlter.showAlter("Bạn muốn đặt trước sách chứ gì?");
        if (confirmYes) {
            try {
                Map<String, Object> criteria = new HashMap<>();
                criteria.put("ISBN", book.getIsbn());
                criteria.put("BookItemStatus", "AVAILABLE");
                List<BookItem> bookItems = BookItemDAO.getInstance().findByCriteria(criteria);
                if (bookItems.size() > 0) {
                    BookItem bookItem = BookItemDAO.getInstance().findById(bookItems.get(0).getBookBarcode());
                    bookItem.setBookItemStatus(BookItemStatus.RESERVED);
                    BookItemDAO.getInstance().updateEntity(bookItem);
                    BookReservation bookReservation = new BookReservation(NavigationBarController.getMember()
                            , bookItem, LocalDate.now().toString()
                            , LocalDate.now().plusDays(3).toString());
                    try {
                        BookReservationDAO.getInstance().insert(bookReservation);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    ReservedBorrowedHistoryPageController reservedBorrowedHistoryPageController =
                            SceneManagerUtil.getInstance().getController(HISTORY_FXML);
                    if (reservedBorrowedHistoryPageController != null) {
                        reservedBorrowedHistoryPageController.addReservedBook(bookReservation);
                    }
                    CustomerAlter.showMessage("Đã ghi nhận, vui lòng mượn trong 3 ngày");

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
    public void onBookmarkButtonAction(ActionEvent actionEvent) {
        if (bookmarkButton.getText().equals("Đánh dấu")) {
            markedBook();
        } else {
            cancelMarkedBook();
        }
    }

    /**
     * huỷ đánh dấu.
     */
    private void cancelMarkedBook() {
        boolean confirmYes = CustomerAlter.showAlter("Bạn muốn huỷ đánh dấu sách này à?");
        if (confirmYes) {
            try {
                BookmarkController bookmarkController =
                        SceneManagerUtil.getInstance().getController(BOOKMARK_FXML);
                if (bookmarkController != null) {
                    bookmarkController.deleteBookmark(this.book);
                }
                Map<String, Object> criteria = new HashMap<>();
                criteria.put("ISBN", book.getIsbn());
                List<BookItem> bookItem = BookItemDAO.getInstance().findByCriteria(criteria);
                BookMark bookMark = new BookMark(NavigationBarController.getMember()
                        , bookItem.getFirst());
                try {
                    BookMarkDAO.getInstance().deleteEntity(bookMark);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                BookService.getInstance().removeMarkedBook(bookMark);
                bookmarkButton.setText("Đánh dấu");
                CustomerAlter.showMessage("Đã huỷ đánh dấu sách nè");
            } catch (RuntimeException | IOException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * đánh dấu sách.
     */
    private void markedBook() {
        boolean confirmYes = CustomerAlter.showAlter("Bạn muốn đánh dấu sách này à?");
        if (confirmYes) {
            try {
                BookmarkController bookmarkController =
                        SceneManagerUtil.getInstance().getController(BOOKMARK_FXML);
                if (bookmarkController != null) {
                    bookmarkController.addBookmark(this.book);
                }
                Map<String, Object> criteria = new HashMap<>();
                criteria.put("ISBN", book.getIsbn());
                List<BookItem> bookItem = BookItemDAO.getInstance().findByCriteria(criteria);
                BookMark bookMark = new BookMark(NavigationBarController.getMember()
                        , bookItem.getFirst());
                bookMark.getBook().setRate(book.getRate());
                try {
                    BookMarkDAO.getInstance().insert(bookMark);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                BookService.getInstance().addMarkedBook(bookMark);
                bookmarkButton.setText("Huỷ đánh dấu");
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
    private Image starImage(int numOfStar) {
        System.out.println(numOfStar);
        String imagePath = "/image/general/" + numOfStar + "star.png";
        if (getClass().getResourceAsStream(imagePath) == null) {
            System.out.println("Image not found: " + imagePath);
            return new Image(getClass().getResourceAsStream("/image/general/5star.png"));
        }
        return new Image(getClass().getResourceAsStream(imagePath));
    }

    /**
     * trả về sách.
     * @return sách
     */
    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
        setData();
    }

    /**
     * từ rate choiceBox sang int
     * @param rate rate
     * @return số rate
     */
    private int fromRateToInt(String rate) {
        char firstChar = rate.charAt(0);
        if (Character.isAlphabetic(firstChar)) {
            return 0;
        }
        return Character.getNumericValue(firstChar);
    }

    /**
     * mở preview.
     * @param mouseEvent sự kiện chuột
     */
    public void onOpenPreviewMouseClicked(MouseEvent mouseEvent) {
        try {
            // Kiểm tra URL có null hoặc rỗng không
            if (book.getPreview() == null || book.getPreview().trim().isEmpty()) {
                CustomerAlter.showMessage("Không có preview của sách này");
                logger.debug(TAG, "URL preview trống hoặc null");
                return;
            }
            
            // Kiểm tra nếu hệ thống hỗ trợ Desktop API
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    // Kiểm tra URL hợp lệ
                    URI uri = new URI(book.getPreview());
                    desktop.browse(uri); // Mở trình duyệt với URL
                    logger.info(TAG, "Đang mở liên kết: " + book.getPreview());
                } else {
                    logger.warning(TAG, "Desktop không hỗ trợ mở trình duyệt.");
                    CustomerAlter.showMessage("Trình duyệt không được hỗ trợ trên hệ thống này");
                }
            } else {
                logger.warning(TAG, "Desktop API không được hỗ trợ trên hệ thống này.");
                CustomerAlter.showMessage("Không thể mở trình duyệt trên hệ thống này");
            }
        } catch (Exception e) {
            logger.error(TAG, "Lỗi khi mở liên kết: " + (book.getPreview() != null ? book.getPreview() : "null"), e);
            CustomerAlter.showMessage("Không có preview của sách này hoặc liên kết không hợp lệ");
        }
    }

    /**
     * mở web .
     * @param url link
     */
    private void openWebLink(String url) {
        try {
            // Kiểm tra URL có null hoặc rỗng không
            if (url == null || url.trim().isEmpty()) {
                CustomerAlter.showMessage("Không có preview của sách này");
                logger.debug(TAG, "URL preview trống hoặc null");
                return;
            }
            
            // Kiểm tra nếu hệ thống hỗ trợ Desktop API
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    // Kiểm tra URL hợp lệ
                    URI uri = new URI(url);
                    desktop.browse(uri); // Mở trình duyệt với URL
                    logger.info(TAG, "Đang mở liên kết: " + url);
                } else {
                    logger.warning(TAG, "Desktop không hỗ trợ mở trình duyệt.");
                    CustomerAlter.showMessage("Trình duyệt không được hỗ trợ trên hệ thống này");
                }
            } else {
                logger.warning(TAG, "Desktop API không được hỗ trợ trên hệ thống này.");
                CustomerAlter.showMessage("Không thể mở trình duyệt trên hệ thống này");
            }
        } catch (Exception e) {
            logger.error(TAG, "Lỗi khi mở liên kết: " + url, e);
            CustomerAlter.showMessage("Không có preview của sách này hoặc liên kết không hợp lệ");
        }
    }

}
