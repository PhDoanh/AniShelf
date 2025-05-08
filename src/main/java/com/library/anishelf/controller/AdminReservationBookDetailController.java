package com.library.anishelf.controller;

import com.library.anishelf.util.ImageCache;
import com.library.anishelf.service.command.AdminCommand;
import com.library.anishelf.service.command.Command;
import com.library.anishelf.dao.BookIssueDAO;
import com.library.anishelf.model.BookItem;
import com.library.anishelf.model.BookReservation;
import com.library.anishelf.model.Member;
import com.library.anishelf.model.enums.BookIssueStatus;
import com.library.anishelf.model.enums.BookReservationStatus;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminReservationBookDetailController extends BaseDetailController<BookReservation> {

    @FXML
    private Button addBookButton;

    @FXML
    private TextField writerNameText;

    @FXML
    private TextField ISBNText;

    @FXML
    private TextField bookItemNameText;

    @FXML
    private TextField borrowDateText;

    @FXML
    private TextField genreText;

    @FXML
    private Button deleteBookButton;

    @FXML
    private Button editReservationButton;

    @FXML
    private TextField emailUserText;

    @FXML
    private TextField genderUserText;

    @FXML
    private TextField userIDText;

    @FXML
    private TextField userNameText;

    @FXML
    private TextField userPhoneNumberText;

    @FXML
    private TextField returnReservationDateText;

    @FXML
    private Button saveReservationButton;

    @FXML
    private AnchorPane saveContainer;

    @FXML
    private Button scanBookButton;

    @FXML
    private Button scanMemberButton;

    @FXML
    private TextField conditionText;

    @FXML
    private ScrollPane recommendationPane;

    @FXML
    private TextField totalOfBorrowText;

    @FXML
    private TextField totalOfLostText;

    @FXML
    private VBox recommendationVbox;

    @FXML
    private HBox mainDetailPane;
    @FXML
    private ImageView memberImageView;
    @FXML
    private ImageView bookImageView;
    @FXML
    private ChoiceBox<BookReservationStatus> borrowCondition;
    @FXML
    private Label borrowIDLabel;
    @FXML
    private ListView<HBox> recommendationList;

    private Member user;
    private BookItem bookItem;
    private SuggestionTable recommendationTable;

    private boolean isSettingMember = false;
    private boolean isSettingBook = false;

    protected static final ExecutorService executorService = Executors.newFixedThreadPool(4);

    @Override
    protected void fetchBookItemDetails() {
        if(!getTitlePageStack().peek().equals(item.getReservationId()+"")) {
            getTitlePageStack().push(item.getReservationId() + "");
        }
        user = item.getMember();
        setUser(user);

        bookItem = item.getBookItem();
        setBookItem(bookItem);

        setDateIssueOfReservation();
    }

    @Override
    protected void updateAddModeUIUX() {
        editReservationButton.setVisible(!addMode);
        addBookButton.setVisible(addMode);

        scanBookButton.setVisible(addMode);
        scanMemberButton.setVisible(addMode);

        userIDText.setEditable(addMode);
        userNameText.setEditable(addMode);
        ISBNText.setEditable(addMode);
        bookItemNameText.setEditable(addMode);

        borrowDateText.setEditable(addMode);
        returnReservationDateText.setEditable(addMode);
        borrowCondition.setMouseTransparent(!addMode);

        scanBookButton.setMouseTransparent(!addMode);
        scanMemberButton.setMouseTransparent(!addMode);


        if (addMode) {
            saveReservationButton.setVisible(!addMode);
            saveContainer.setVisible(!addMode);

            userIDText.setText(null);
            setUserTextFieldNull();
            memberImageView.setImage(null);

            ISBNText.setText(null);
            setBookTextFieldNull();
            bookImageView.setImage(null);

            borrowIDLabel.setText(null);

            borrowCondition.setValue(BookReservationStatus.WAITING);
            //Xử lý ngày tháng mượn
            LocalDate borrowDate = LocalDate.now();
            LocalDate returnDate = borrowDate.plusWeeks(2); // Thêm 2 tuần

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            borrowDateText.setText(borrowDate.format(formatter));
            returnReservationDateText.setText(returnDate.format(formatter));
        }
    }

    @Override
    protected void updateEditModeUIUX() {
        editReservationButton.setVisible(!editMode);
        saveReservationButton.setVisible(editMode);
        deleteBookButton.setVisible(editMode);
        saveContainer.setVisible(editMode);

        userIDText.setEditable(editMode);
        ISBNText.setEditable(editMode);

        borrowDateText.setEditable(editMode);
        returnReservationDateText.setEditable(editMode);
        borrowCondition.setMouseTransparent(!editMode);

        scanBookButton.setMouseTransparent(editMode);
        scanMemberButton.setMouseTransparent(editMode);
    }

    @Override
    protected boolean validateItemBookInput() {

        return true;
    }

    @Override
    protected boolean getNewBookItemInformation() throws Exception {
        if (user == null) {
            CustomerAlter.showMessage("Hãy điền bạn đọc vào!");
            return false;
        }
        if (bookItem == null) {
            CustomerAlter.showMessage("Hãy điền thông tin sách mượn vào!");
            return false;
        }
        if (borrowDateText == null) {
            CustomerAlter.showMessage("Không được để trống ngày mượn!");
            return false;
        }
        String reformattedDate = reformatDate(borrowDateText.getText());
        String reformattedReturnDate = reformatDate(returnReservationDateText.getText());
        item = new BookReservation(user, bookItem, reformattedDate, reformattedReturnDate);
        item.setStatus(borrowCondition.getValue());
        if (borrowIDLabel != null && borrowIDLabel.getText() != null) {
            item.setReservationId(Integer.valueOf(borrowIDLabel.getText()));
        }
        return true;
    }
    @Override
    public String getItemType() {
        return "đơn đặt sách";
    }

    @FXML
    public void initialize() {
        borrowCondition.getItems().addAll(BookReservationStatus.values());

        recommendationTable = new SuggestionTable(this.recommendationPane, this.recommendationVbox, this.recommendationList);
        recommendationPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                // Scene đã được tạo, thêm event filter
                newScene.getRoot().addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                    if (recommendationPane.isVisible()) {
                        // Lấy tọa độ của điểm click trong không gian của recommendationPane
                        Point2D point = recommendationPane.sceneToLocal(event.getSceneX(), event.getSceneY());

                        // Kiểm tra xem click có nằm ngoài recommendationPane không
                        if (!recommendationPane.contains(point)) {
                            recommendationPane.setVisible(false);
                            recommendationVbox.getChildren().clear();
                        }
                    }
                });
            }
        });
        // Đăng ký listener để xử lý sự kiện click
        recommendationTable.setRowClickListener(new SuggestionRowClickListener() {
            @Override
            public void onRowClick(Object o) {
                if (o instanceof Member) {
                    isSettingMember = true;
                    setUser((Member) o);
                    recommendationVbox.getChildren().clear();
                    recommendationPane.setVisible(false);
                } else if (o instanceof BookItem) {
                    isSettingBook = true;
                    setBookItem((BookItem) o);
                    recommendationVbox.getChildren().clear();
                    recommendationPane.setVisible(false);
                }
            }
        });

        bookItemNameText.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                Stage stage = (Stage) bookItemNameText.getScene().getWindow();

                stage.widthProperty().addListener((obs, oldWidth, newWidth) -> {
                    if (Math.abs(newWidth.doubleValue() - oldWidth.doubleValue()) > 100) { // Kiểm tra chênh lệch
                        recommendationPane.setLayoutX(0);
                        recommendationPane.setLayoutY(0);
                        recommendationPane.setVisible(false); // Không hiển thị recommendationTable
                    } else {
                        Platform.runLater(() -> recommendationTable.updateSuggestionPaneForActiveField());
                    }
                });

                stage.heightProperty().addListener((obs, oldHeight, newHeight) -> {
                    if (Math.abs(newHeight.doubleValue() - oldHeight.doubleValue()) > 100) { // Kiểm tra chênh lệch
                        recommendationPane.setLayoutX(0);
                        recommendationPane.setLayoutY(0);
                        recommendationPane.setVisible(false); // Không hiển thị recommendationTable
                    } else {
                        Platform.runLater(() -> recommendationTable.updateSuggestionPaneForActiveField());
                    }
                });
            }
        });

        userNameText.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!isSettingMember && addMode) {
                    if (newValue != null && !newValue.isEmpty()) {
                        recommendationTable.loadFindData("memberName", newValue);
                        recommendationTable.updateSuggestionPanePosition(userNameText);
                    } else {
                        recommendationPane.setLayoutX(50);
                        recommendationPane.setLayoutY(50);
                        recommendationPane.setVisible(false);
                    }
                }
            }
        });

        // Lắng nghe sự thay đổi trong TextField tìm kiếm
        userIDText.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!isSettingMember && addMode) {
                    if (newValue != null && !newValue.isEmpty()) {
                        recommendationTable.loadFindData("memberID", newValue);
                        recommendationTable.updateSuggestionPanePosition(userIDText);
                    } else {
                        recommendationPane.setLayoutX(50);
                        recommendationPane.setLayoutY(50);
                        recommendationPane.setVisible(false);
                    }
                }
                isSettingMember = false;
            }
        });

        bookItemNameText.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!isSettingBook && addMode) {
                    if (newValue != null && !newValue.isEmpty()) {
                        recommendationTable.loadFindData("bookItemName", newValue);
                        recommendationTable.updateSuggestionPanePosition(bookItemNameText);
                    } else {
                        recommendationPane.setLayoutX(50);
                        recommendationPane.setLayoutY(50);
                        recommendationPane.setVisible(false);
                    }
                }
            }
        });

        ISBNText.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!isSettingBook && addMode) {
                    if (newValue != null && !newValue.isEmpty()) {
                        recommendationTable.loadFindData("bookBarCode", newValue);
                        recommendationTable.updateSuggestionPanePosition(ISBNText);
                    } else {
                        recommendationPane.setLayoutX(50);
                        recommendationPane.setLayoutY(50 );
                        recommendationPane.setVisible(false);
                    }
                }
                isSettingBook = false;
            }
        });


    }


    @FXML
    void handleSaveButtonClick(ActionEvent event) {
        saveChanges();
    }

    @FXML
    void handleAddButtonClick(ActionEvent event) {
        saveChanges();
    }

    @FXML
    void handleDeleteButtonClick(ActionEvent event) {
        deleteChanges();
    }

    @FXML
    void handleEditButtonClick(ActionEvent event) {
        getTitlePageStack().push("Edit");
        setEditMode(true);
    }

    @FXML
    void handleScanBookButtonClick(ActionEvent event) {
        bookItem = new BookItem();
        Command scanCommand = new AdminCommand("scan", bookItem);
        commandInvoker.setCommand(scanCommand);
        if (commandInvoker.executeCommand()) {
            bookItem = ((AdminCommand) scanCommand).getBookItemResult();
            setBookItem(bookItem);
        }
    }

    @FXML
    void handleScanMemberButtonClick(ActionEvent event) {
        user = new Member(null);
        Command scanCommand = new AdminCommand("scan", user);
        commandInvoker.setCommand(scanCommand);
        if (commandInvoker.executeCommand()) {
            user = ((AdminCommand) scanCommand).getMemberResult();
            setUser(user);
        }
    }

    public void loadInitalStatus() {
        setAddMode(false);
        setEditMode(false);
    }


    private void setUserTextFieldNull() {
        userNameText.setText(null);
        userPhoneNumberText.setText(null);
        emailUserText.setText(null);
        genderUserText.setText(null);
        totalOfBorrowText.setText(null);
        totalOfLostText.setText(null);
    }

    private void setBookTextFieldNull() {
        bookItemNameText.setText(null);
        genreText.setText(null);
        writerNameText.setText(null);

    }

    public void setUser(Member user) {
        this.user = user;
        userNameText.setText(user.getPerson().getLastName() + " " + user.getPerson().getFirstName());
        userIDText.setText(String.valueOf(user.getPerson().getId()));
        userPhoneNumberText.setText(user.getPerson().getPhone());
        emailUserText.setText(user.getPerson().getEmail());
        genderUserText.setText(user.getPerson().getGender().toString());
        totalOfBorrowText.setText(String.valueOf(user.getTotalBooksCheckOut()));
        try {
            File file = new File(user.getPerson().getImagePath());
            memberImageView.setImage(new Image(file.toURI().toString()));
        } catch (Exception e) {
            memberImageView.setImage(new Image(getClass().getResourceAsStream("/image/avatar/default.png")));
        }
        try {
            Map<String, Object> findCriteriaa = new HashMap<>();
            findCriteriaa.put("BookIssueStatus", BookIssueStatus.LOST);
            findCriteriaa.put("member_ID", user.getPerson().getId());
            int lostBook = BookIssueDAO.getInstance().searchByCriteria(findCriteriaa).size();
            totalOfLostText.setText(lostBook + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setBookItem(BookItem bookItem) {
        this.bookItem = bookItem;
        bookItemNameText.setText(bookItem.getTitle());
        ISBNText.setText(String.valueOf(bookItem.getBarcode()));
        genreText.setText(getCategories(bookItem.getCategories()));
        writerNameText.setText(getAuthors(bookItem.getAuthors()));
        // Tải ảnh bất đồng bộ
        Task<Image> loadImageTask = new Task<>() {
            @Override
            protected Image call() throws Exception {
                try {
                    Image image = ImageCache.getImageLRUCache().get(bookItem.getImagePath());
                    if(image != null) {
                        System.out.println("tai anh trong cache");
                        return image;
                    } else {
                        Image image1 = new Image(bookItem.getImagePath(), true);
                        ImageCache.getImageLRUCache().put(bookItem.getImagePath(), image1);
                        return new Image(image1.getUrl());
                    }
                } catch (Exception e) {
                    System.out.println("Khong co anh trong cache");
                    System.out.println("Length: " + bookItem.getImagePath().length());

                    File file = new File("bookImageView/default.png");
                    return new Image(file.toURI().toString());
                }
            }
        };

        loadImageTask.setOnSucceeded(event -> {
            if (bookImageView.getScene() != null) {
                bookImageView.setImage(loadImageTask.getValue());
            }
        });

        executorService.submit(loadImageTask);
    }

    private void setDateIssueOfReservation() {
        borrowIDLabel.setText(String.valueOf(item.getReservationId()));

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // hoặc định dạng phù hợp với dữ liệu của bạn
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            // Chuyển đổi và định dạng cho borrowDate
            if (item.getCreatedDate() != null) {
                LocalDate createdDate = LocalDate.parse(item.getCreatedDate(), inputFormatter);
                borrowDateText.setText(createdDate.format(outputFormatter));
            } else {
                borrowDateText.setText(""); // Hoặc giá trị mặc định khác
            }

            // Chuyển đổi và định dạng cho returnDate
            if (item.getDueDate() != null) {
                LocalDate dueDate = LocalDate.parse(item.getDueDate(), inputFormatter);
                returnReservationDateText.setText(dueDate.format(outputFormatter));
            } else {
                returnReservationDateText.setText(""); // Hoặc giá trị mặc định khác
            }
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            // Xử lý lỗi nếu cần (ví dụ: hiển thị thông báo lỗi cho người dùng)
            borrowDateText.setText(""); // Hoặc giá trị mặc định khác
            returnReservationDateText.setText(""); // Hoặc giá trị mặc định khác
        }
        borrowCondition.setValue(item.getStatus());
    }

}
