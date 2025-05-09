package com.library.anishelf.controller;

import com.library.anishelf.util.CacheManagerUtil;
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

public class BorrowBookInfoController extends BaseDetailController<BookIssue> {

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

    // Loại bỏ các biến scanBookButton và scanMemberButton

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
    private ChoiceBox<BookIssueStatus> borrowStatus;
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
        if (!getTitlePageStack().peek().equals(item.getIssueID() + "")) {
            getTitlePageStack().push(item.getIssueID() + "");
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

        // Loại bỏ các biến scanBookButton và scanMemberButton

        memberIDText.setEditable(addMode);
        memberNameText.setEditable(addMode);
        barCodeText.setEditable(addMode);
        bookNameText.setEditable(addMode);

        borowDateText.setEditable(addMode);
        returnDateText.setEditable(addMode);
        borrowStatus.setMouseTransparent(!addMode);

        borrowIDLabel.setText(null);

        if (addMode) {
            item = new BookIssue(null, null, null, null);
            saveButton.setVisible(!addMode);
            savePane.setVisible(!addMode);

            memberIDText.setText(null);
            setMemberTextFieldNull();
            memberImage.setImage(null);

            barCodeText.setText(null);
            setBookTextFielNull();
            bookImage.setImage(null);

            borrowStatus.setValue(BookIssueStatus.BORROWED);
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
            CustomerAlter.showMessage("Vui lòng chọn thành viên");
            return false;
        }
        if (bookItem == null) {
            CustomerAlter.showMessage("Vui lòng chọn sách");
            return false;
        }
        if (borowDateText == null) {
            CustomerAlter.showMessage("Vui lòng nhập ngày mượn");
            return false;
        }
        String reformattedDate = reformatDate(borowDateText.getText());
        String reformattedReturnDate = reformatDate(returnDateText.getText());
        item.setBookItem(bookItem);
        item.setMember(member);
        item.setIssueDate(reformattedDate);
        item.setDueDate(reformattedReturnDate);
        //item = new BookIssue(member, bookItem, reformattedDate, reformattedReturnDate);
        item.setStatus(borrowStatus.getValue());
        if (borrowIDLabel != null && borrowIDLabel.getText() != null) {
            item.setIssueID(Integer.valueOf(borrowIDLabel.getText()));
        }
        return true;
    }

    @Override
    public String getType() {
        return "đơn mượn sách";
    }


    @FXML
    public void initialize() {
        borrowStatus.getItems().addAll(BookIssueStatus.values());

        suggestionTable = new SuggestionTable(this.suggestionPane, this.suggestionVbox, this.sugestionList);
        // Đăng ký listener để xử lý sự kiện click
        suggestionTable.setRowClickListener(new SuggestionRowClickListener() {
            @Override
            public void onRowClick(Object o) {
                if (o instanceof Member) {
                    setMember((Member) o);
                    suggestionPane.setVisible(false);
                    isSettingMember = true;
                    memberIDText.setText(String.valueOf(member.getPerson().getId()));
                } else if (o instanceof BookItem) {
                    setBookItem((BookItem) o);
                    suggestionPane.setVisible(false);
                    isSettingBook = true;
                    barCodeText.setText(String.valueOf(bookItem.getBookBarcode()));
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
                            suggestionVbox.getChildren().clear();
                        }
                    }
                });
            }
        });

        bookNameText.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                Stage stage = (Stage) bookNameText.getScene().getWindow();

                stage.widthProperty().addListener((obs, oldWidth, newWidth) -> {
                    if (Math.abs(newWidth.doubleValue() - oldWidth.doubleValue()) > 100) { // Kiểm tra chênh lệch
                        suggestionPane.setLayoutX(30);
                        suggestionPane.setLayoutY(30);
                        suggestionPane.setVisible(false); // Không hiển thị suggestionTable
                    } else {
                        Platform.runLater(() -> suggestionTable.updateSuggestionPaneForActiveField());
                    }
                });

                stage.heightProperty().addListener((obs, oldHeight, newHeight) -> {
                    if (Math.abs(newHeight.doubleValue() - oldHeight.doubleValue()) > 100) { // Kiểm tra chênh lệch
                        suggestionPane.setLayoutX(30);
                        suggestionPane.setLayoutY(30);
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
                if (!isSettingMember && addMode) {
                    if (newValue != null && !newValue.isEmpty()) {
                        suggestionTable.loadFindData("memberID", newValue);
                        suggestionTable.updateSuggestionPanePosition(memberIDText);
                    } else {
                        suggestionPane.setLayoutX(0);
                        suggestionPane.setLayoutY(0);
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
                        if (memberIDText.getText() != null) {
                            System.out.println("Cos mmeber ID r");
                            suggestionTable.loadFindData("bookItemName", newValue, memberIDText.getText());
                        } else {
                            suggestionTable.loadFindData("bookItemName", newValue);
                        }
                        suggestionTable.updateSuggestionPanePosition(bookNameText);
                    } else {
                        suggestionPane.setLayoutX(0);
                        suggestionPane.setLayoutY(0);
                        suggestionPane.setVisible(false);
                    }
                }
            }
        });

        barCodeText.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!isSettingBook && addMode) {
                    if (newValue != null && !newValue.isEmpty()) {
                        if (memberIDText.getText() != null) {
                            System.out.println("Cos mmeber ID r");
                            suggestionTable.loadFindData("bookBarCode", newValue, memberIDText.getText());
                        } else {
                            suggestionTable.loadFindData("bookBarCode", newValue);
                        }
                        suggestionTable.updateSuggestionPanePosition(barCodeText);
                    } else {
                        suggestionPane.setLayoutX(0);
                        suggestionPane.setLayoutY(0);
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
        try {
            Map<String, Object> findCriteriaa = new HashMap<>();
            findCriteriaa.put("BookIssueStatus", BookIssueStatus.BORROWED);
            findCriteriaa.put("member_ID", member.getPerson().getId());
            int lostBook = BookIssueDAO.getInstance().findByCriteria(findCriteriaa).size();
            totalOFBorrowText.setText(lostBook + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //totalOFBorrowText.setText(String.valueOf(member.getTotalBooksCheckOut()));
        try {
            File file = new File(member.getPerson().getImagePath());
            Image cachedImage = CacheManagerUtil.getImageFromCache(file.toURI().toString());
            if (cachedImage != null) {
                memberImage.setImage(cachedImage);
            } else {
                Image newImage = new Image(file.toURI().toString());
                CacheManagerUtil.putImageToCache(file.toURI().toString(), newImage);
                memberImage.setImage(newImage);
            }
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
                    // Kiểm tra đường dẫn ảnh có hợp lệ không
                    if (bookItem.getImagePath() == null || bookItem.getImagePath().trim().isEmpty()) {
                        throw new Exception("Đường dẫn ảnh trống hoặc null");
                    }
                    
                    Image image = CacheManagerUtil.getImageFromCache(bookItem.getImagePath());
                    if (image != null) {
                        return image;
                    } else {
                        Image image1 = new Image(bookItem.getImagePath(), true);
                        CacheManagerUtil.putImageToCache(bookItem.getImagePath(), image1);
                        return new Image(image1.getUrl());
                    }
                } catch (Exception e) {
                    // Ghi log lỗi và trả về ảnh mặc định
                    System.out.println("Không thể tải ảnh: " + e.getMessage());
                    if (bookItem.getImagePath() != null) {
                        System.out.println("Đường dẫn ảnh: " + bookItem.getImagePath());
                    }
                    return new Image(getClass().getResourceAsStream("/image/default/book.png"));
                }
            }
        };

        loadImageTask.setOnSucceeded(event -> {
            if (bookImage.getScene() != null) {
                bookImage.setImage(loadImageTask.getValue());
            }
        });

        // Đặt ảnh mặc định ngay lập tức trong khi đợi tải ảnh thật
        try {
            bookImage.setImage(new Image(getClass().getResourceAsStream("/image/default/book.png")));
        } catch (Exception e) {
            System.out.println("Không thể tải ảnh mặc định: " + e.getMessage());
        }

        executor.submit(loadImageTask);
    }

    private void setDateIssue() {
        borrowIDLabel.setText(String.valueOf(item.getIssueID()));
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // hoặc định dạng phù hợp với dữ liệu của bạn
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            // Chuyển đổi và định dạng cho borrowDate
            if (item.getIssueDate() != null) {
                LocalDate createdDate = LocalDate.parse(item.getIssueDate(), inputFormatter);
                borowDateText.setText(createdDate.format(outputFormatter));
            } else {
                borowDateText.setText(""); // Hoặc giá trị mặc định khác
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
            borowDateText.setText(""); // Hoặc giá trị mặc định khác
            returnDateText.setText(""); // Hoặc giá trị mặc định khác
        }
        borrowStatus.setValue(item.getStatus());
    }

}
