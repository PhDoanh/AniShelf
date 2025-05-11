package com.library.anishelf.controller;

import com.library.anishelf.util.NotificationManagerUtil;
import com.library.anishelf.util.CacheManagerUtil;
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

public class ReservedBookInfoController extends BaseDetailController<BookReservation> {

    @FXML
    private Button addButotn;

    @FXML
    private TextField authorNameText;

    @FXML
    private TextField barCodeText;

    @FXML
    private TextField bookNameText;

    @FXML
    private TextField borowDateText;

    @FXML
    private TextField categoryText;

    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;

    @FXML
    private TextField emailText;

    @FXML
    private TextField genderText;

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
    private AnchorPane savePane;

    @FXML
    private TextField statusText;

    @FXML
    private ScrollPane suggestionPane;

    @FXML
    private TextField totalOFBorrowText;

    @FXML
    private TextField totalOfLostText;

    @FXML
    private VBox suggestionVbox;

    @FXML
    private HBox mainDetailPane;
    @FXML
    private ImageView memberImage;
    @FXML
    private ImageView bookImage;
    @FXML
    private ChoiceBox<BookReservationStatus> borrowStatus;
    @FXML
    private Label borrowIDLabel;
    @FXML
    private ListView<HBox> sugestionList;

    private Member member;
    private BookItem bookItem;
    private SuggestionTable suggestionTable;

    private boolean isSettingMember = false;
    private boolean isSettingBook = false;

    protected static final ExecutorService executor = Executors.newFixedThreadPool(4);

    @Override
    protected void loadItemDetails() {
        if(!getTitlePageStack().peek().equals(item.getId()+"")) {
            getTitlePageStack().push(item.getId() + "");
        }
        member = item.getMember();
        setMember(member);

        bookItem = item.getBookItem();
        setBookItem(bookItem);

        setDateIssue();
    }

    @Override
    protected void updateAddModeUI() {
        editButton.setVisible(!addMode);
        addButotn.setVisible(addMode);

        memberIDText.setEditable(addMode);
        memberNameText.setEditable(addMode);
        barCodeText.setEditable(addMode);
        bookNameText.setEditable(addMode);

        borowDateText.setEditable(addMode);
        returnDateText.setEditable(addMode);
        borrowStatus.setMouseTransparent(!addMode);

        if (addMode) {
            saveButton.setVisible(!addMode);
            savePane.setVisible(!addMode);

            memberIDText.setText(null);
            setMemberTextFieldNull();
            memberImage.setImage(null);

            barCodeText.setText(null);
            setBookTextFielNull();
            bookImage.setImage(null);

            borrowIDLabel.setText(null);

            borrowStatus.setValue(BookReservationStatus.WAITING);
            //Xử lý ngày tháng mượn
            LocalDate borrowDate = LocalDate.now();
            LocalDate returnDate = borrowDate.plusWeeks(2); // Thêm 2 tuần

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            borowDateText.setText(borrowDate.format(formatter));
            returnDateText.setText(returnDate.format(formatter));
        }
    }

    @Override
    protected void updateEditModeUI() {
        editButton.setVisible(!editMode);
        saveButton.setVisible(editMode);
        deleteButton.setVisible(editMode);
        savePane.setVisible(editMode);

        memberIDText.setEditable(editMode);
        barCodeText.setEditable(editMode);

        borowDateText.setEditable(editMode);
        returnDateText.setEditable(editMode);
        borrowStatus.setMouseTransparent(!editMode);
    }

    @Override
    protected boolean validateInput() {

        return true;
    }

    @Override
    protected boolean getNewItemInformation() throws Exception {
        if (member == null) {
            NotificationManagerUtil.showInfo("Vui lòng điền thông tin thành viên!");
            return false;
        }
        if (bookItem == null) {
            NotificationManagerUtil.showInfo("Vui lòng điền thông tin truyện!");
            return false;
        }
        if (borowDateText == null) {
            NotificationManagerUtil.showInfo("Vui lòng điền ngày mượn!");
            return false;
        }
        String reformattedDate = reformatDate(borowDateText.getText());
        String reformattedReturnDate = reformatDate(returnDateText.getText());
        item = new BookReservation(member, bookItem, reformattedDate, reformattedReturnDate);
        item.setReservationStatus(borrowStatus.getValue());
        if (borrowIDLabel != null && borrowIDLabel.getText() != null) {
            item.setId(Integer.valueOf(borrowIDLabel.getText()));
        }
        return true;
    }
    @Override
    public String getType() {
        return "đơn đặt truyện";
    }

    @FXML
    public void initialize() {
        borrowStatus.getItems().addAll(BookReservationStatus.values());

        suggestionTable = new SuggestionTable(this.suggestionPane, this.suggestionVbox, this.sugestionList);
        suggestionPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                // Scene đã được tạo, thêm event filter
                newScene.getRoot().addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                    if (suggestionPane.isVisible()) {
                        // Lấy tọa độ của điểm click trong không gian của suggestionPane
                        Point2D point = suggestionPane.sceneToLocal(event.getSceneX(), event.getSceneY());

                        // Kiểm tra xem click có nằm ngoài suggestionPane không
                        if (!suggestionPane.contains(point)) {
                            suggestionPane.setVisible(false);
                            suggestionVbox.getChildren().clear();
                        }
                    }
                });
            }
        });
        // Đăng ký listener để xử lý sự kiện click
        suggestionTable.setRowClickListener(new SuggestionRowClickListener() {
            @Override
            public void onRowClick(Object o) {
                if (o instanceof Member) {
                    isSettingMember = true;
                    setMember((Member) o);
                    suggestionVbox.getChildren().clear();
                    suggestionPane.setVisible(false);
                } else if (o instanceof BookItem) {
                    isSettingBook = true;
                    setBookItem((BookItem) o);
                    suggestionVbox.getChildren().clear();
                    suggestionPane.setVisible(false);
                }
            }
        });

        bookNameText.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                Stage stage = (Stage) bookNameText.getScene().getWindow();

                stage.widthProperty().addListener((obs, oldWidth, newWidth) -> {
                    if (Math.abs(newWidth.doubleValue() - oldWidth.doubleValue()) > 100) { // Kiểm tra chênh lệch
                        suggestionPane.setLayoutX(0);
                        suggestionPane.setLayoutY(0);
                        suggestionPane.setVisible(false); // Không hiển thị suggestionTable
                    } else {
                        Platform.runLater(() -> suggestionTable.updateSuggestionPaneForActiveField());
                    }
                });

                stage.heightProperty().addListener((obs, oldHeight, newHeight) -> {
                    if (Math.abs(newHeight.doubleValue() - oldHeight.doubleValue()) > 100) { // Kiểm tra chênh lệch
                        suggestionPane.setLayoutX(0);
                        suggestionPane.setLayoutY(0);
                        suggestionPane.setVisible(false); // Không hiển thị suggestionTable
                    } else {
                        Platform.runLater(() -> suggestionTable.updateSuggestionPaneForActiveField());
                    }
                });
            }
        });

        memberNameText.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!isSettingMember && addMode) {
                    if (newValue != null && !newValue.isEmpty()) {
                        suggestionTable.loadFindData("memberName", newValue);
                        suggestionTable.updateSuggestionPanePosition(memberNameText);
                    } else {
                        suggestionPane.setLayoutX(50);
                        suggestionPane.setLayoutY(50);
                        suggestionPane.setVisible(false);
                    }
                }
            }
        });

        // Lắng nghe sự thay đổi trong TextField tìm kiếm
        memberIDText.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!isSettingMember && addMode) {
                    if (newValue != null && !newValue.isEmpty()) {
                        suggestionTable.loadFindData("memberID", newValue);
                        suggestionTable.updateSuggestionPanePosition(memberIDText);
                    } else {
                        suggestionPane.setLayoutX(50);
                        suggestionPane.setLayoutY(50);
                        suggestionPane.setVisible(false);
                    }
                }
                isSettingMember = false;
            }
        });

        bookNameText.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!isSettingBook && addMode) {
                    if (newValue != null && !newValue.isEmpty()) {
                        suggestionTable.loadFindData("bookItemName", newValue);
                        suggestionTable.updateSuggestionPanePosition(bookNameText);
                    } else {
                        suggestionPane.setLayoutX(50);
                        suggestionPane.setLayoutY(50);
                        suggestionPane.setVisible(false);
                    }
                }
            }
        });

        barCodeText.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!isSettingBook && addMode) {
                    if (newValue != null && !newValue.isEmpty()) {
                        suggestionTable.loadFindData("bookBarCode", newValue);
                        suggestionTable.updateSuggestionPanePosition(barCodeText);
                    } else {
                        suggestionPane.setLayoutX(50);
                        suggestionPane.setLayoutY(50 );
                        suggestionPane.setVisible(false);
                    }
                }
                isSettingBook = false;
            }
        });


    }


    @FXML
    void oSaveButtonAction(ActionEvent event) {
        saveChanges();
    }

    @FXML
    void onAddButtonAction(ActionEvent event) {
        saveChanges();
    }

    @FXML
    void onDeleteButton(ActionEvent event) {
        deleteChanges();
    }

    @FXML
    void onEditButtonAction(ActionEvent event) {
        getTitlePageStack().push("Edit");
        setEditMode(true);
    }

    public void loadStartStatus() {
        setAddMode(false);
        setEditMode(false);
    }


    private void setMemberTextFieldNull() {
        memberNameText.setText(null);
        phoneNumberText.setText(null);
        emailText.setText(null);
        genderText.setText(null);
        totalOFBorrowText.setText(null);
        totalOfLostText.setText(null);
    }

    private void setBookTextFielNull() {
        bookNameText.setText(null);
        categoryText.setText(null);
        authorNameText.setText(null);

    }

    public void setMember(Member member) {
        this.member = member;
        memberNameText.setText(member.getPerson().getLastName() + " " + member.getPerson().getFirstName());
        memberIDText.setText(String.valueOf(member.getPerson().getId()));
        phoneNumberText.setText(member.getPerson().getPhone());
        emailText.setText(member.getPerson().getEmail());
        genderText.setText(member.getPerson().getGender().toString());
        totalOFBorrowText.setText(String.valueOf(member.getCheckedOutBooksCount()));
        try {
            File file = new File(member.getPerson().getImagePath());
            memberImage.setImage(new Image(file.toURI().toString()));
        } catch (Exception e) {
            memberImage.setImage(new Image(getClass().getResourceAsStream("/image/default/avatar.png")));
        }
        try {
            Map<String, Object> findCriteriaa = new HashMap<>();
            findCriteriaa.put("BookIssueStatus", BookIssueStatus.LOST);
            findCriteriaa.put("member_ID", member.getPerson().getId());
            int lostBook = BookIssueDAO.getInstance().findByCriteria(findCriteriaa).size();
            totalOfLostText.setText(lostBook + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setBookItem(BookItem bookItem) {
        this.bookItem = bookItem;
        bookNameText.setText(bookItem.getTitle());
        barCodeText.setText(String.valueOf(bookItem.getBookBarcode()));
        categoryText.setText(getCategories(bookItem.getCategories()));
        authorNameText.setText(getAuthors(bookItem.getAuthors()));
        // Tải ảnh bất đồng bộ
        Task<Image> loadImageTask = new Task<>() {
            @Override
            protected Image call() throws Exception {
                try {
                    Image image = CacheManagerUtil.getImageFromCache(bookItem.getImagePath());
                    if(image != null) {
                        System.out.println("tai anh trong cache");
                        return image;
                    } else {
                        Image image1 = new Image(bookItem.getImagePath(), true);
                        CacheManagerUtil.putImageToCache(bookItem.getImagePath(), image1);
                        return new Image(image1.getUrl());
                    }
                } catch (Exception e) {
                    System.out.println("Khong co anh trong cache");
                    System.out.println("Length: " + bookItem.getImagePath().length());

                    File file = new File("bookImage/default.png");
                    return new Image(file.toURI().toString());
                }
            }
        };

        loadImageTask.setOnSucceeded(event -> {
            if (bookImage.getScene() != null) {
                bookImage.setImage(loadImageTask.getValue());
            }
        });

        executor.submit(loadImageTask);
    }

    private void setDateIssue() {
        borrowIDLabel.setText(String.valueOf(item.getId()));

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // hoặc định dạng phù hợp với dữ liệu của bạn
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            // Chuyển đổi và định dạng cho borrowDate
            if (item.getReservationDate() != null) {
                LocalDate createdDate = LocalDate.parse(item.getReservationDate(), inputFormatter);
                borowDateText.setText(createdDate.format(outputFormatter));
            } else {
                borowDateText.setText(""); // Hoặc giá trị mặc định khác
            }

            // Chuyển đổi và định dạng cho returnDate
            if (item.getExpectedReturnDate() != null) {
                LocalDate dueDate = LocalDate.parse(item.getExpectedReturnDate(), inputFormatter);
                returnDateText.setText(dueDate.format(outputFormatter));
            } else {
                returnDateText.setText(""); // Hoặc giá trị mặc định khác
            }
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            // Xử lý lỗi nếu cần (ví dụ: hiển thị thông báo lỗi cho người dùng)
            borowDateText.setText(""); // Hoặc giá trị mặc định khác
            returnDateText.setText(""); // Hoặc giá trị mặc định khác
        }
        borrowStatus.setValue(item.getReservationStatus());
    }

}
