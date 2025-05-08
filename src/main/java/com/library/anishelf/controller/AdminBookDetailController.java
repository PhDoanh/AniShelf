package com.library.anishelf.controller;

import com.library.anishelf.util.CacheManagerUtil;
import com.library.anishelf.service.command.AdminCommand;
import com.library.anishelf.service.command.Command;
import com.library.anishelf.dao.BookItemDAO;
import com.library.anishelf.model.Author;
import com.library.anishelf.model.Book;
import com.library.anishelf.model.BookItem;
import com.library.anishelf.model.Category;
import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class AdminBookDetailController extends BaseDetailController<Book> {

    @FXML
    private TextField ISBNText;

    @FXML
    private Button addButton;

    @FXML
    private AnchorPane addButtonPane;

    @FXML
    private TextField authorNameText;

    @FXML
    private TextArea bookContentText;

    @FXML
    private ImageView bookImage;

    @FXML
    private TextField bookNameText;

    @FXML
    private TextField categoryText;

    @FXML
    private Button choiceImageButton;

    @FXML
    private AnchorPane copyBookPane;

    @FXML
    private VBox copyBookTableVbox;

    @FXML
    private Button deleteButton;

    @FXML
    private Button ediButton;

    @FXML
    private TextField locationText;

    @FXML
    private HBox mainPane;

    @FXML
    private TextField numberOfBookText;

    @FXML
    private TextField numberOfBorrowText;

    @FXML
    private TextField numberOfLostText;

    @FXML
    private Button page1Button;

    @FXML
    private Button page2Button;

    @FXML
    private Button saveButton;

    @FXML
    private Button scanButton;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private ScrollPane suggestionPane;
    @FXML
    private VBox suggestionVbox;

    @FXML
    private ListView<HBox> sugestionList;

    private SuggestionTable suggestionTable;

    private PauseTransition pauseTransition = new PauseTransition(Duration.seconds(0.5));
    private boolean isPage1 = true;
    private boolean isSettingItem = false;
    private boolean isSettingItem2 = false;
    protected static final ExecutorService executor = Executors.newFixedThreadPool(4);

    @Override
    protected void loadItemDetails() {
        if (!getTitlePageStack().peek().equals(item.getIsbn() + "")) {
            getTitlePageStack().push(item.getIsbn() + "");
        }
        isSettingItem = true;
        isSettingItem2 = true;

        bookNameText.setText(item.getTitle());
        ISBNText.setText(String.valueOf(item.getIsbn()));
        authorNameText.setText(getAuthors(item.getAuthors()));
        categoryText.setText(getCategories(item.getCategories()));
        numberOfBookText.setText(item.getQuantity() + "");
        numberOfBorrowText.setText(String.valueOf(item.getLoanedBooksCount()));
        numberOfLostText.setText(String.valueOf(item.getLostBooksCount()));
        locationText.setText(item.getLocation());
        bookContentText.setText(item.getSummary());

        // Tải ảnh bất đồng bộ
        Task<Image> loadImageTask = new Task<>() {
            @Override
            protected Image call() throws Exception {
                try {
                    Image image = CacheManagerUtil.getImageFromCache(item.getImagePath());
                    if (image != null) {
                        System.out.println("tai anh trong cache");
                        return image;
                    } else {
                        System.out.println("Khong co anh trong cache");
                        Image image1 = new Image(item.getImagePath(), true);
                        CacheManagerUtil.putImageToCache(item.getImagePath(), image1);
                        return new Image(image1.getUrl());
                    }
                } catch (Exception e) {
                    System.out.println("Length: " + item.getImagePath().length());

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
        loadBookItem();
    }

    @Override
    protected void updateAddModeUI() {
        //Xử lý các nút bấm
        ediButton.setVisible(!addMode);
        addButtonPane.setVisible(addMode);
        addButton.setVisible(addMode);
        choiceImageButton.setVisible(addMode);
        scanButton.setVisible(addMode);

        //Cho phép các trường chỉnh sửa
        ISBNText.setEditable(addMode);
        bookNameText.setEditable(addMode);
        locationText.setEditable(addMode);
        authorNameText.setEditable(addMode);
        categoryText.setEditable(addMode);
        numberOfBookText.setEditable(addMode);

        //Nếu như mà là mở addMode thì các trường thông tin set về rỗng
        if (addMode) {
            item = new Book();
            copyBookTableVbox.getChildren().clear();
            deleteButton.setVisible(!addMode);
            saveButton.setVisible(!addMode);
            ISBNText.setText(null);
            bookNameText.setText(null);
            locationText.setText(null);
            authorNameText.setText(null);
            categoryText.setText(null);
            numberOfBookText.setText(null);
            numberOfBorrowText.setText(null);
            numberOfLostText.setText(null);
            bookContentText.setText(null);
            bookImage.setImage(null);
        }
    }

    @Override
    protected void updateEditModeUI() {
        //Xử lý ẩn hiện các nút bấm
        ediButton.setVisible(!editMode);
        addButtonPane.setVisible(!editMode);
        deleteButton.setVisible(editMode);
        saveButton.setVisible(editMode);
        choiceImageButton.setVisible(editMode);

        //cho phép các trường có thể sửa đổi
        ISBNText.setEditable(editMode);
        bookNameText.setEditable(editMode);
        locationText.setEditable(editMode);
        authorNameText.setEditable(editMode);
        bookContentText.setEditable(editMode);
        categoryText.setEditable(editMode);
        numberOfBookText.setEditable(editMode);
    }

    @Override
    protected boolean validateInput() {
        // Validate ISBN
        if (ISBNText.getText().trim().isEmpty()) {
            CustomerAlter.showMessage("ISBN không được để trống");
            return false;
        }

        // Validate title
        if (bookNameText.getText().trim().isEmpty()) {
            CustomerAlter.showMessage("Tên sách không được để trống");
            return false;
        }

        // Validate location
        if (locationText.getText().trim().isEmpty()) {
            CustomerAlter.showMessage("Vị trí không được để trống");
            return false;
        }

        // Validate quantity
        try {
            int quantity = Integer.parseInt(numberOfBookText.getText().trim());
            if (quantity < 0) {
                CustomerAlter.showMessage("Số lượng không hợp lệ");
                return false;
            }
        } catch (NumberFormatException e) {
            CustomerAlter.showMessage("Số lượng phải là số");
            return false;
        }

        return true;
    }

    @Override
    protected boolean getNewItemInformation() throws Exception {
        item.setIsbn(Long.parseLong(ISBNText.getText().trim()));
        item.setTitle(bookNameText.getText().trim());
        item.setLocation(locationText.getText().trim());
        item.setQuantity(Integer.parseInt(numberOfBookText.getText().trim()));
        item.setSummary(bookContentText.getText());
        //Xử lý authors thành List
        String authorList = authorNameText.getText();
        List<Author> authors = Arrays.stream(authorList.split("\\s*,\\s*"))
                .map(name -> new Author(name)) // Tạo đối tượng Author
                .collect(Collectors.toList());
        item.setAuthors(authors);
        //Xử lý cat
        String catList = categoryText.getText();
        List<Category> cats = Arrays.stream(catList.split("\\s*,\\s*"))
                .map(name -> new Category(name)) // Tạo đối tượng Author
                .collect(Collectors.toList());
        item.setCategories(cats);
        return true;
    }

    @Override
    protected String getType() {
        return "sách";
    }

    @FXML
    private void initialize() {
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

        suggestionTable.setRowClickListener(new SuggestionRowClickListener() {
            @Override
            public void onRowClick(Object o) {
                if (o instanceof Book) {
                    setItem((Book) o);
                    suggestionVbox.getChildren().clear();
                    suggestionPane.setVisible(false);
                }
            }
        });

        childFitWidthParent(copyBookTableVbox, scrollPane);

        // Listener cho ISBN TextField
        ISBNText.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isSettingItem && addMode) {
                if (newValue != null && !newValue.isEmpty()) {
                    suggestionTable.updateSuggestionPanePosition(ISBNText);
                    pauseTransition.playFromStart();
                    String isbnNumbers = newValue.replaceAll("[^0-9]", "");
                    pauseTransition.setOnFinished(event -> {
                        suggestionTable.loadFindData("bookISBNAPI", isbnNumbers);
                    });
                }
            }
            isSettingItem = false;
        });

        bookNameText.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isSettingItem2 && addMode) {
                if (newValue != null && !newValue.isEmpty()) {
                    suggestionTable.updateSuggestionPanePosition(bookNameText);
                    // Reset và restart pause transition mỗi khi có thay đổi text
                    pauseTransition.playFromStart();

                    //suggestionTable.loadFindData("bookNameAPI", newValue);
                    pauseTransition.setOnFinished(event -> {
                        suggestionTable.loadFindData("bookNameAPI", bookNameText.getText());
                    });
                }
            }
            isSettingItem2 = false;
        });
    }

    @FXML
    void onAddButtonAction(ActionEvent event) {
        saveChanges();
    }

    @FXML
    public void onChoiceImageButtonAction(ActionEvent event) {
        if (ISBNText.getText().isEmpty() || ISBNText.getText().equals("") || ISBNText.getText() == null) {
            CustomerAlter.showMessage("Vui lòng nhập ISBN cho quyển sách trước");
        } else {
            item.setIsbn(Long.parseLong(ISBNText.getText().trim()));
            item.setImagePath(getImagePath(item));
            //Nếu như có chọn ảnh thì set ảnh cho bookImage
            if (item.getImagePath() != null) {
                Image image = new Image(item.getImagePath());
                CacheManagerUtil.putImageToCache(item.getImagePath(), image);
                bookImage.setImage(image);
            }
        }
    }

    @FXML
    void onDeleteButtonAction(ActionEvent event) {
        boolean confrimYes = CustomerAlter.showAlter("Bạn muốn xóa quyển sách này?");
        if (confrimYes) {
            //Xóa sách trong CSDL
            Command deleteCommand = new AdminCommand("delete", this.item);
            commandInvoker.setCommand(deleteCommand);
            if (commandInvoker.executeCommand()) {
                mainController.loadData();
                mainController.alterPage();
                loadStartStatus();
                System.out.println("Đã xóa sách");
            } else {
                CustomerAlter.showMessage("Xóa sách thất bại");
            }
        } else {
            System.out.println("Tiếp tục edit");
        }

    }

    @FXML
    void onEditButtonAction(ActionEvent event) {
        getTitlePageStack().push("Edit");
        if (item != null) {
            setEditMode(true);
        }
    }

    @FXML
    void onPage1ButtonAction(ActionEvent event) {
        isPage1 = true;
        setButtonPageAnimation();

        //Chuyển sang Page1
        copyBookPane.setVisible(!isPage1);
    }

    @FXML
    void onPage2ButtonAction(ActionEvent event) {
        isPage1 = false;
        setButtonPageAnimation();

        //Chuyển sang Page2
        copyBookPane.setVisible(!isPage1);
    }

    @FXML
    void onSaveButtonAction(ActionEvent event) {
        saveChanges();

    }

    @FXML
    void onScanButtonAction(ActionEvent event) {
        Command scanCommand = new AdminCommand("scan", new Book());
        commandInvoker.setCommand(scanCommand);
        if (commandInvoker.executeCommand()) {
            item = ((AdminCommand) scanCommand).getBookResult();
            System.out.println(item.getIsbn());
            loadItemDetails();
        } else {
            System.out.println("chiuuuu");
        }
    }

    private void setButtonPageAnimation() {
        if (isPage1) {
            //choice button color darker
            page1Button.setStyle("-fx-background-color: #DDDCDC;");
            page2Button.setStyle("-fx-background-color: #FFF;");

            //choice button bring to front
            if (page1Button.getParent().getChildrenUnmodifiable().indexOf(page1Button) <
                    page1Button.getParent().getChildrenUnmodifiable().size() - 1) {
                page2Button.toBack();
            }
        } else {
            //choice button color darker
            page2Button.setStyle("-fx-background-color: #DDDCDC;");
            page1Button.setStyle("-fx-background-color: #FFF;");

            //choice button bring to front
            if (page2Button.getParent().getChildrenUnmodifiable().indexOf(page2Button) <
                    page2Button.getParent().getChildrenUnmodifiable().indexOf(page1Button)) {
                page1Button.toBack();
            }
        }
    }

    private void loadBookItem() {
        copyBookTableVbox.getChildren().clear();
        Map<String, Object> findCriteria2 = new HashMap<>();
        findCriteria2.put("ISBN", this.item.getIsbn());
        try {
            List<BookItem> bookItemList = BookItemDAO.getInstance().findByCriteria(findCriteria2);
            for (BookItem item : bookItemList) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminBookCopyRow.fxml"));
                    HBox row = loader.load();

                    AdminBookCopyRowController rowController = loader.getController();
                    rowController.setBookItem(item);

                    childFitWidthParent(row, scrollPane);
                    copyBookTableVbox.getChildren().add(row);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
