package com.library.anishelf.controller;

import com.library.anishelf.util.ImageCache;
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
    private TextField ISBNTextField;

    @FXML
    private Button addBookButton;

    @FXML
    private AnchorPane addBookButtonPane;

    @FXML
    private TextField writerNameText;

    @FXML
    private TextArea bookDescriptionText;

    @FXML
    private ImageView bookImageView;

    @FXML
    private TextField bookNameTextField;

    @FXML
    private TextField genreText;

    @FXML
    private Button selectImageButton;

    @FXML
    private AnchorPane bookCopiesContainer;

    @FXML
    private VBox bookCopiesListContainer;

    @FXML
    private Button deleteBookButton;

    @FXML
    private Button editBookButton;

    @FXML
    private TextField locationTextField;

    @FXML
    private HBox mainPane;

    @FXML
    private TextField totalCopiesTextField;

    @FXML
    private TextField borrowedCopiesTextField;

    @FXML
    private TextField lostCopiesTextField;

    @FXML
    private Button bookDetailPageButton;

    @FXML
    private Button bookCopiesPageButton;

    @FXML
    private Button saveBookButton;

    @FXML
    private Button scanBookButton;

    @FXML
    private ScrollPane scrollPaneContainer;

    @FXML
    private ScrollPane recommandationPane;
    @FXML
    private VBox recommendationVbox;

    @FXML
    private ListView<HBox> recommendationList;

    private SuggestionTable recommendationTable;

    private PauseTransition pauseTransition = new PauseTransition(Duration.seconds(0.5));
    private boolean isBookDetailPageActive = true;
    private boolean isLoadingBookDetails = false;
    private boolean isProcessingCopies = false;
    protected static final ExecutorService executor = Executors.newFixedThreadPool(4);

    @Override
    protected void loadBookItemDetails() {
        if (!getTitlePageStack().peek().equals(item.getISBN() + "")) {
            getTitlePageStack().push(item.getISBN() + "");
        }
        isLoadingBookDetails = true;
        isProcessingCopies = true;

        bookNameTextField.setText(item.getTitle());
        ISBNTextField.setText(String.valueOf(item.getISBN()));
        writerNameText.setText(getAuthors(item.getAuthors()));
        genreText.setText(getCategories(item.getCategories()));
        totalCopiesTextField.setText(item.getQuantity() + "");
        borrowedCopiesTextField.setText(String.valueOf(item.getNumberOfLoanedBooks()));
        lostCopiesTextField.setText(String.valueOf(item.getNumberOfLostBooks()));
        locationTextField.setText(item.getPlaceAt());
        bookDescriptionText.setText(item.getDescription());

        // Tải ảnh bất đồng bộ
        Task<Image> loadImageTask = new Task<>() {
            @Override
            protected Image call() throws Exception {
                try {
                    Image image = ImageCache.getImageLRUCache().get(item.getImagePath());
                    if (image != null) {
                        System.out.println("tai anh trong cache");
                        return image;
                    } else {
                        System.out.println("Khong co anh trong cache");
                        Image image1 = new Image(item.getImagePath(), true);
                        ImageCache.getImageLRUCache().put(item.getImagePath(), image1);
                        return new Image(image1.getUrl());
                    }
                } catch (Exception e) {
                    System.out.println("Length: " + item.getImagePath().length());

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

        executor.submit(loadImageTask);
        loadBookCopies();
    }

    @Override
    protected void updateAddModeUIUX() {
        //Xử lý các nút bấm
        editBookButton.setVisible(!addMode);
        addBookButtonPane.setVisible(addMode);
        addBookButton.setVisible(addMode);
        selectImageButton.setVisible(addMode);
        scanBookButton.setVisible(addMode);

        //Cho phép các trường chỉnh sửa
        ISBNTextField.setEditable(addMode);
        bookNameTextField.setEditable(addMode);
        locationTextField.setEditable(addMode);
        writerNameText.setEditable(addMode);
        genreText.setEditable(addMode);
        totalCopiesTextField.setEditable(addMode);

        //Nếu như mà là mở addMode thì các trường thông tin set về rỗng
        if (addMode) {
            item = new Book();
            bookCopiesListContainer.getChildren().clear();
            deleteBookButton.setVisible(!addMode);
            saveBookButton.setVisible(!addMode);
            ISBNTextField.setText(null);
            bookNameTextField.setText(null);
            locationTextField.setText(null);
            writerNameText.setText(null);
            genreText.setText(null);
            totalCopiesTextField.setText(null);
            borrowedCopiesTextField.setText(null);
            lostCopiesTextField.setText(null);
            bookDescriptionText.setText(null);
            bookImageView.setImage(null);
        }
    }

    @Override
    protected void updateEditModeUIUX() {
        //Xử lý ẩn hiện các nút bấm
        editBookButton.setVisible(!editMode);
        addBookButtonPane.setVisible(!editMode);
        deleteBookButton.setVisible(editMode);
        saveBookButton.setVisible(editMode);
        selectImageButton.setVisible(editMode);

        //cho phép các trường có thể sửa đổi
        ISBNTextField.setEditable(editMode);
        bookNameTextField.setEditable(editMode);
        locationTextField.setEditable(editMode);
        writerNameText.setEditable(editMode);
        bookDescriptionText.setEditable(editMode);
        genreText.setEditable(editMode);
        totalCopiesTextField.setEditable(editMode);
    }

    @Override
    protected boolean validateIBookInput() {
        // Validate ISBN
        if (ISBNTextField.getText().trim().isEmpty()) {
            CustomerAlter.showMessage("ISBN không được để trống");
            return false;
        }

        // Validate title
        if (bookNameTextField.getText().trim().isEmpty()) {
            CustomerAlter.showMessage("Tên sách không được để trống");
            return false;
        }

        // Validate location
        if (locationTextField.getText().trim().isEmpty()) {
            CustomerAlter.showMessage("Vị trí không được để trống");
            return false;
        }

        // Validate quantity
        try {
            int quantity = Integer.parseInt(totalCopiesTextField.getText().trim());
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
    protected boolean getNewBookItemInformation() throws Exception {
        item.setISBN(Long.parseLong(ISBNTextField.getText().trim()));
        item.setTitle(bookNameTextField.getText().trim());
        item.setPlaceAt(locationTextField.getText().trim());
        item.setQuantity(Integer.parseInt(totalCopiesTextField.getText().trim()));
        item.setDescription(bookDescriptionText.getText());
        //Xử lý authors thành List
        String authorList = writerNameText.getText();
        List<Author> authors = Arrays.stream(authorList.split("\\s*,\\s*"))
                .map(name -> new Author(name)) // Tạo đối tượng Author
                .collect(Collectors.toList());
        item.setAuthors(authors);
        //Xử lý cat
        String catList = genreText.getText();
        List<Category> cats = Arrays.stream(catList.split("\\s*,\\s*"))
                .map(name -> new Category(name)) // Tạo đối tượng Author
                .collect(Collectors.toList());
        item.setCategories(cats);
        return true;
    }

    @Override
    protected String getItemType() {
        return "sách";
    }

    @FXML
    private void initialize() {
        recommendationTable = new SuggestionTable(this.recommandationPane, this.recommendationVbox, this.recommendationList);

        recommandationPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                // Scene đã được tạo, thêm event filter
                newScene.getRoot().addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                    if (recommandationPane.isVisible()) {
                        // Lấy tọa độ của điểm click trong không gian của recommandationPane
                        Point2D point = recommandationPane.sceneToLocal(event.getSceneX(), event.getSceneY());

                        // Kiểm tra xem click có nằm ngoài recommandationPane không
                        if (!recommandationPane.contains(point)) {
                            recommandationPane.setVisible(false);
                            recommendationVbox.getChildren().clear();
                        }
                    }
                });
            }
        });

        recommendationTable.setRowClickListener(new SuggestionRowClickListener() {
            @Override
            public void onRowClick(Object o) {
                if (o instanceof Book) {
                    setItem((Book) o);
                    recommendationVbox.getChildren().clear();
                    recommandationPane.setVisible(false);
                }
            }
        });

        childFitWidthParent(bookCopiesListContainer, scrollPaneContainer);

        // Listener cho ISBN TextField
        ISBNTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isLoadingBookDetails && addMode) {
                if (newValue != null && !newValue.isEmpty()) {
                    recommendationTable.updateSuggestionPanePosition(ISBNTextField);
                    pauseTransition.playFromStart();
                    String isbnNumbers = newValue.replaceAll("[^0-9]", "");
                    pauseTransition.setOnFinished(event -> {
                        recommendationTable.loadFindData("bookISBNAPI", isbnNumbers);
                    });
                }
            }
            isLoadingBookDetails = false;
        });

        bookNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isProcessingCopies && addMode) {
                if (newValue != null && !newValue.isEmpty()) {
                    recommendationTable.updateSuggestionPanePosition(bookNameTextField);
                    // Reset và restart pause transition mỗi khi có thay đổi text
                    pauseTransition.playFromStart();

                    //recommendationTable.loadFindData("bookNameAPI", newValue);
                    pauseTransition.setOnFinished(event -> {
                        recommendationTable.loadFindData("bookNameAPI", bookNameTextField.getText());
                    });
                }
            }
            isProcessingCopies = false;
        });
    }

    @FXML
    void handleAddButton(ActionEvent event) {
        saveChanges();
    }

    @FXML
    public void onSelectImageAction(ActionEvent event) {
        if (ISBNTextField.getText().isEmpty() || ISBNTextField.getText().equals("") || ISBNTextField.getText() == null) {
            CustomerAlter.showMessage("Vui lòng nhập ISBN cho quyển sách trước");
        } else {
            item.setISBN(Long.parseLong(ISBNTextField.getText().trim()));
            item.setImagePath(getImagePath(item));
            //Nếu như có chọn ảnh thì set ảnh cho bookImageView
            if (item.getImagePath() != null) {
                Image image = new Image(item.getImagePath());
                ImageCache.getImageLRUCache().put(item.getImagePath(), image);
                bookImageView.setImage(image);
            }
        }
    }

    @FXML
    void handleDeleteButton(ActionEvent event) {
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
    void handleEditButton(ActionEvent event) {
        getTitlePageStack().push("Edit");
        if (item != null) {
            setEditMode(true);
        }
    }

    @FXML
    void onBookDetailPageAction(ActionEvent event) {
        isBookDetailPageActive = true;
        updatePageButtonsState();

        //Chuyển sang Page1
        bookCopiesContainer.setVisible(!isBookDetailPageActive);
    }

    @FXML
    void onBookCopiesPageAction(ActionEvent event) {
        isBookDetailPageActive = false;
        updatePageButtonsState();

        //Chuyển sang Page2
        bookCopiesContainer.setVisible(!isBookDetailPageActive);
    }

    @FXML
    void handleSaveButton(ActionEvent event) {
        saveChanges();

    }

    @FXML
    void handleScanButton(ActionEvent event) {
        Command scanCommand = new AdminCommand("scan", new Book());
        commandInvoker.setCommand(scanCommand);
        if (commandInvoker.executeCommand()) {
            item = ((AdminCommand) scanCommand).getBookResult();
            System.out.println(item.getISBN());
            loadBookItemDetails();
        } else {
            System.out.println("chiuuuu");
        }
    }

    private void updatePageButtonsState() {
        if (isBookDetailPageActive) {
            //choice button color darker
            bookDetailPageButton.setStyle("-fx-background-color: #DDDCDC;");
            bookCopiesPageButton.setStyle("-fx-background-color: #FFF;");

            //choice button bring to front
            if (bookDetailPageButton.getParent().getChildrenUnmodifiable().indexOf(bookDetailPageButton) <
                    bookDetailPageButton.getParent().getChildrenUnmodifiable().size() - 1) {
                bookCopiesPageButton.toBack();
            }
        } else {
            //choice button color darker
            bookCopiesPageButton.setStyle("-fx-background-color: #DDDCDC;");
            bookDetailPageButton.setStyle("-fx-background-color: #FFF;");

            //choice button bring to front
            if (bookCopiesPageButton.getParent().getChildrenUnmodifiable().indexOf(bookCopiesPageButton) <
                    bookCopiesPageButton.getParent().getChildrenUnmodifiable().indexOf(bookDetailPageButton)) {
                bookDetailPageButton.toBack();
            }
        }
    }

    private void loadBookCopies() {
        bookCopiesListContainer.getChildren().clear();
        Map<String, Object> findCriteria2 = new HashMap<>();
        findCriteria2.put("ISBN", this.item.getISBN());
        try {
            List<BookItem> bookItemList = BookItemDAO.getInstance().searchByCriteria(findCriteria2);
            for (BookItem item : bookItemList) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminBookCopyRow.fxml"));
                    HBox row = loader.load();

                    AdminBookItemRowController rowController = loader.getController();
                    rowController.setBookInstance(item);

                    childFitWidthParent(row, scrollPaneContainer);
                    bookCopiesListContainer.getChildren().add(row);
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
