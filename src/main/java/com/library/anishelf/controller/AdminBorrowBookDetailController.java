package com.library.anishelf.controller;

import com.library.anishelf.util.ImageCache;
import com.library.anishelf.service.command.AdminCommand;
import com.library.anishelf.service.command.Command;
import com.library.anishelf.dao.BookIssueDAO;
import com.library.anishelf.model.*;
import com.library.anishelf.model.enums.BookIssueStatus;
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

public class AdminBorrowBookDetailController extends BaseDetailController<BookIssue> {

    @FXML
    private Button addButton;

    @FXML
    private TextField authorTextField;

    @FXML
    private TextField barcodeTextField;

    @FXML
    private TextField bookNameTextField;

    @FXML
    private TextField borrowBookDateText;

    @FXML
    private TextField genreText;

    @FXML
    private Button deleteeeButton;

    @FXML
    private Button edittttButton;

    @FXML
    private TextField UserEmailText;

    @FXML
    private TextField userGenderText;

    @FXML
    private TextField memberIDText;

    @FXML
    private TextField memberNameText;

    @FXML
    private TextField phoneNumberText;

    @FXML
    private TextField returnDateText;

    @FXML
    private Button saveButton;

    @FXML
    private AnchorPane saveContainer;

    @FXML
    private Button scanBookButton;

    @FXML
    private Button scanMemberButton;

    @FXML
    private TextField conditionText;

    @FXML
    private ScrollPane suggestionPane;

    @FXML
    private TextField totalOFBorrowBookText;

    @FXML
    private TextField totalOfLostBookText;

    @FXML
    private VBox recommendationVbox;

    @FXML
    private HBox mainDetailPane;
    @FXML
    private ImageView memberImageView;
    @FXML
    private ImageView bookImageView;
    @FXML
    private ChoiceBox<BookIssueStatus> borrowCondtion;
    @FXML
    private Label borrowBookIDLabel;

    @FXML
    private ListView<HBox> recomendationList;


    private Member member;
    private BookItem bookItem;
    private SuggestionTable recommendationTable;

    private boolean isMemberBeingSet = false;
    private boolean isBookBeingSet = false;

    protected static final ExecutorService executorrr = Executors.newFixedThreadPool(4);

    @Override
    protected void fetchBookItemDetails() {
        if (!getTitlePageStack().peek().equals(item.getIssueID() + "")) {
            getTitlePageStack().push(item.getIssueID() + "");
        }
        member = item.getMember();
        setMember(member);

        bookItem = item.getBookItem();
        setBookItem(bookItem);

        setDateeee();
    }

    @Override
    protected void updateAddModeUIUX() {
        edittttButton.setVisible(!addMode);
        addButton.setVisible(addMode);

        scanBookButton.setVisible(addMode);
        scanMemberButton.setVisible(addMode);

        memberIDText.setEditable(addMode);
        memberNameText.setEditable(addMode);
        barcodeTextField.setEditable(addMode);
        bookNameTextField.setEditable(addMode);

        borrowBookDateText.setEditable(addMode);
        returnDateText.setEditable(addMode);
        borrowCondtion.setMouseTransparent(!addMode);

        scanBookButton.setMouseTransparent(!addMode);
        scanMemberButton.setMouseTransparent(!addMode);

        borrowBookIDLabel.setText(null);

        if (addMode) {
            item = new BookIssue(null, null, null, null);
            saveButton.setVisible(!addMode);
            saveContainer.setVisible(!addMode);

            memberIDText.setText(null);
            setMemberTextFieldNull();
            memberImageView.setImage(null);

            barcodeTextField.setText(null);
            setBookTextFielNull();
            bookImageView.setImage(null);

            borrowCondtion.setValue(BookIssueStatus.BORROWED);
            //Xử lý ngày tháng mượn
            LocalDate borrowDate = LocalDate.now();
            LocalDate returnDate = borrowDate.plusWeeks(2); // Thêm 2 tuần

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            borrowBookDateText.setText(borrowDate.format(formatter));
            returnDateText.setText(returnDate.format(formatter));
        }
    }

    @Override
    protected void updateEditModeUIUX() {
        edittttButton.setVisible(!editMode);
        saveButton.setVisible(editMode);
        deleteeeButton.setVisible(editMode);
        saveContainer.setVisible(editMode);

        memberIDText.setEditable(editMode);
        barcodeTextField.setEditable(editMode);

        borrowBookDateText.setEditable(editMode);
        returnDateText.setEditable(editMode);
        borrowCondtion.setMouseTransparent(!editMode);

        scanBookButton.setMouseTransparent(editMode);
        scanMemberButton.setMouseTransparent(editMode);
    }

    @Override
    protected boolean validateItemBookInput() {

        return true;
    }

    @Override
    protected boolean getNewBookItemInformation() throws Exception {
        if (member == null) {
            CustomerAlter.showMessage("Hãy điền bạn đọc vào!");
            return false;
        }
        if (bookItem == null) {
            CustomerAlter.showMessage("Hãy điền thông tin sách mượn vào!");
            return false;
        }
        if (borrowBookDateText == null) {
            CustomerAlter.showMessage("Không được để trống ngày mượn!");
            return false;
        }
        String reformattedDate = reformatDate(borrowBookDateText.getText());
        String reformattedReturnDate = reformatDate(returnDateText.getText());
        item.setBookItem(bookItem);
        item.setMember(member);
        item.setCreatedDate(reformattedDate);
        item.setDueDate(reformattedReturnDate);
        //item = new BookIssue(member, bookItem, reformattedDate, reformattedReturnDate);
        item.setStatus(borrowCondtion.getValue());
        if (borrowBookIDLabel != null && borrowBookIDLabel.getText() != null) {
            item.setIssueID(Integer.valueOf(borrowBookIDLabel.getText()));
        }
        return true;
    }

    @Override
    public String getItemType() {
        return "đơn mượn sách";
    }


    @FXML
    public void initialize() {
        borrowCondtion.getItems().addAll(BookIssueStatus.values());

        recommendationTable = new SuggestionTable(this.suggestionPane, this.recommendationVbox, this.recomendationList);
        // Đăng ký listener để xử lý sự kiện click
        recommendationTable.setRowClickListener(new SuggestionRowClickListener() {
            @Override
            public void onRowClick(Object o) {
                if (o instanceof Member) {
                    isMemberBeingSet = true;
                    setMember((Member) o);
                    recommendationVbox.getChildren().clear();
                    suggestionPane.setLayoutX(30);
                    suggestionPane.setLayoutY(30);
                    suggestionPane.setVisible(false);
                } else if (o instanceof BookItem) {
                    isBookBeingSet = true;
                    setBookItem((BookItem) o);
                    recommendationVbox.getChildren().clear();
                    suggestionPane.setLayoutX(30);
                    suggestionPane.setLayoutY(30);
                    suggestionPane.setVisible(false);
                }
            }
        });

        suggestionPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                // Scene đã được tạo, thêm event filter
                newScene.getRoot().addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                    if (suggestionPane.isVisible()) {
                        // Lấy tọa độ của điểm click trong không gian của suggestionPane
                        Point2D point = suggestionPane.sceneToLocal(event.getSceneX(), event.getSceneY());

                        // Kiểm tra xem click có nằm ngoài suggestionPane không
                        if (!suggestionPane.contains(point)) {
                            suggestionPane.setLayoutX(30);
                            suggestionPane.setLayoutY(30);
                            suggestionPane.setVisible(false);
                            recommendationVbox.getChildren().clear();
                        }
                    }
                });
            }
        });

        bookNameTextField.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                Stage stage = (Stage) bookNameTextField.getScene().getWindow();

                stage.widthProperty().addListener((obs, oldWidth, newWidth) -> {
                    if (Math.abs(newWidth.doubleValue() - oldWidth.doubleValue()) > 100) { // Kiểm tra chênh lệch
                        suggestionPane.setLayoutX(30);
                        suggestionPane.setLayoutY(30);
                        suggestionPane.setVisible(false); // Không hiển thị recommendationTable
                    } else {
                        Platform.runLater(() -> recommendationTable.updateSuggestionPaneForActiveField());
                    }
                });

                stage.heightProperty().addListener((obs, oldHeight, newHeight) -> {
                    if (Math.abs(newHeight.doubleValue() - oldHeight.doubleValue()) > 100) { // Kiểm tra chênh lệch
                        suggestionPane.setLayoutX(30);
                        suggestionPane.setLayoutY(30);
                        suggestionPane.setVisible(false); // Không hiển thị recommendationTable
                    } else {
                        Platform.runLater(() -> recommendationTable.updateSuggestionPaneForActiveField());
                    }
                });
            }
        });

        memberNameText.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!isMemberBeingSet && addMode) {
                    if (newValue != null && !newValue.isEmpty()) {
                        recommendationTable.loadFindData("memberName", newValue);
                        recommendationTable.updateSuggestionPanePosition(memberNameText);
                    } else {
                        suggestionPane.setLayoutX(30);
                        suggestionPane.setLayoutY(30);
                        suggestionPane.setVisible(false);
                    }
                }
            }
        });

        // Lắng nghe sự thay đổi trong TextField tìm kiếm
        memberIDText.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!isMemberBeingSet && addMode) {
                    if (newValue != null && !newValue.isEmpty()) {
                        recommendationTable.loadFindData("memberID", newValue);
                        recommendationTable.updateSuggestionPanePosition(memberIDText);
                    } else {
                        suggestionPane.setLayoutX(0);
                        suggestionPane.setLayoutY(0);
                        suggestionPane.setVisible(false);
                    }
                }
                isMemberBeingSet = false;
            }
        });

        bookNameTextField.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!isBookBeingSet && addMode) {
                    if (newValue != null && !newValue.isEmpty()) {
                        if (memberIDText.getText() != null) {
                            System.out.println("Cos mmeber ID r");
                            recommendationTable.loadFindData("bookItemName", newValue, memberIDText.getText());
                        } else {
                            recommendationTable.loadFindData("bookItemName", newValue);
                        }
                        recommendationTable.updateSuggestionPanePosition(bookNameTextField);
                    } else {
                        suggestionPane.setLayoutX(0);
                        suggestionPane.setLayoutY(0);
                        suggestionPane.setVisible(false);
                    }
                }
            }
        });

        barcodeTextField.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!isBookBeingSet && addMode) {
                    if (newValue != null && !newValue.isEmpty()) {
                        if (memberIDText.getText() != null) {
                            System.out.println("Cos mmeber ID r");
                            recommendationTable.loadFindData("bookBarCode", newValue, memberIDText.getText());
                        } else {
                            recommendationTable.loadFindData("bookBarCode", newValue);
                        }
                        recommendationTable.updateSuggestionPanePosition(barcodeTextField);
                    } else {
                        suggestionPane.setLayoutX(0);
                        suggestionPane.setLayoutY(0);
                        suggestionPane.setVisible(false);
                    }
                }
                isBookBeingSet = false;
            }
        });


    }

    @FXML
    void handleSaveButton(ActionEvent event) {
        saveChanges();
    }

    @FXML
    void handleAddButton(ActionEvent event) {
        saveChanges();
    }

    @FXML
    void handleDeleteButton(ActionEvent event) {
        deleteChanges();
    }

    @FXML
    void handleEditButton(ActionEvent event) {
        getTitlePageStack().push("Edit");
        setEditMode(true);
    }

    @FXML
    void onScanBookButtonAction(ActionEvent event) {
        bookItem = new BookItem();
        Command scanCommand = new AdminCommand("scan", new BookItem());
        commandInvoker.setCommand(scanCommand);
        if (commandInvoker.executeCommand()) {
            bookItem = ((AdminCommand) scanCommand).getBookItemResult();
            setBookItem(bookItem);
        }
    }

    @FXML
    void onScanMemberButtonAction(ActionEvent event) {
        member = new Member(null);
        Command scanCommand = new AdminCommand("scan", new Member(null));
        commandInvoker.setCommand(scanCommand);
        if (commandInvoker.executeCommand()) {
            member = ((AdminCommand) scanCommand).getMemberResult();
            setMember(member);
        }
    }

    public void loadInitalStatus() {
        setAddMode(false);
        setEditMode(false);
    }


    private void setMemberTextFieldNull() {
        memberNameText.setText(null);
        phoneNumberText.setText(null);
        UserEmailText.setText(null);
        userGenderText.setText(null);
        totalOFBorrowBookText.setText(null);
        totalOfLostBookText.setText(null);
    }

    private void setBookTextFielNull() {
        bookNameTextField.setText(null);
        genreText.setText(null);
        authorTextField.setText(null);

    }

    public void setMember(Member member) {
        this.member = member;
        memberNameText.setText(member.getPerson().getLastName() + " " + member.getPerson().getFirstName());
        memberIDText.setText(String.valueOf(member.getPerson().getId()));
        phoneNumberText.setText(member.getPerson().getPhone());
        UserEmailText.setText(member.getPerson().getEmail());
        userGenderText.setText(member.getPerson().getGender().toString());
        try {
            Map<String, Object> findCriteriaa = new HashMap<>();
            findCriteriaa.put("BookIssueStatus", BookIssueStatus.BORROWED);
            findCriteriaa.put("member_ID", member.getPerson().getId());
            int lostBook = BookIssueDAO.getInstance().searchByCriteria(findCriteriaa).size();
            totalOFBorrowBookText.setText(lostBook + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //totalOFBorrowBookText.updateDisplayText(String.valueOf(member.getTotalBooksCheckOut()));
        try {
            File file = new File(member.getPerson().getImagePath());
            if (ImageCache.getImageLRUCache().get(file.toURI().toString()) != null) {
                memberImageView.setImage(ImageCache.getImageLRUCache().get(file.toURI().toString()));
            } else {
                Image newImage = new Image(file.toURI().toString());
                ImageCache.getImageLRUCache().put(file.toURI().toString(), newImage);
                memberImageView.setImage(newImage);
            }
        } catch (Exception e) {
            memberImageView.setImage(new Image(getClass().getResourceAsStream("/image/avatar/default.png")));
        }
        try {
            Map<String, Object> findCriteriaa = new HashMap<>();
            findCriteriaa.put("BookIssueStatus", BookIssueStatus.LOST);
            findCriteriaa.put("member_ID", member.getPerson().getId());
            int lostBook = BookIssueDAO.getInstance().searchByCriteria(findCriteriaa).size();
            totalOfLostBookText.setText(lostBook + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setBookItem(BookItem bookItem) {
        this.bookItem = bookItem;
        bookNameTextField.setText(bookItem.getTitle());
        barcodeTextField.setText(String.valueOf(bookItem.getBarcode()));
        genreText.setText(getCategories(bookItem.getCategories()));
        authorTextField.setText(getAuthors(bookItem.getAuthors()));
        bookImageView.setImage(new Image(bookItem.getImagePath()));
        // Tải ảnh bất đồng bộ
        Task<Image> loadImageTask = new Task<>() {
            @Override
            protected Image call() throws Exception {
                try {
                    Image image = ImageCache.getImageLRUCache().get(bookItem.getImagePath());
                    if (image != null) {
                        System.out.println("tai anh trong cache");
                        return image;
                    } else {
                        Image image1 = new Image(bookItem.getImagePath(), true);
                        ImageCache.getImageLRUCache().put(bookItem.getImagePath(), image1);
                        return new Image(image1.getUrl());
                    }
                } catch (Exception e) {
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

        executorrr.submit(loadImageTask);
    }

    private void setDateeee() {
        borrowBookIDLabel.setText(String.valueOf(item.getIssueID()));
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // hoặc định dạng phù hợp với dữ liệu của bạn
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            // Chuyển đổi và định dạng cho borrowDate
            if (item.getCreatedDate() != null) {
                LocalDate createdDate = LocalDate.parse(item.getCreatedDate(), inputFormatter);
                borrowBookDateText.setText(createdDate.format(outputFormatter));
            } else {
                borrowBookDateText.setText(""); // Hoặc giá trị mặc định khác
            }

            // Chuyển đổi và định dạng cho returnDate
            if (item.getDueDate() != null) {
                LocalDate dueDate = LocalDate.parse(item.getDueDate(), inputFormatter);
                returnDateText.setText(dueDate.format(outputFormatter));
            } else {
                returnDateText.setText(""); // Hoặc giá trị mặc định khác
            }
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            // Xử lý lỗi nếu cần (ví dụ: hiển thị thông báo lỗi cho người dùng)
            borrowBookDateText.setText(""); // Hoặc giá trị mặc định khác
            returnDateText.setText(""); // Hoặc giá trị mặc định khác
        }
        borrowCondtion.setValue(item.getStatus());
    }

}
