package com.library.anishelf.controller;

import com.library.anishelf.dao.BookDAO;
import com.library.anishelf.dao.BookItemDAO;
import com.library.anishelf.dao.BookReservationDAO;
import com.library.anishelf.model.Book;
import com.library.anishelf.model.Category;
import com.library.anishelf.model.enums.BookItemStatus;
import com.library.anishelf.model.enums.BookReservationStatus;
import com.library.anishelf.model.enums.BookStatus;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.*;

public class BookTableController extends BaseTableController<Book, BookPageController, BookTableRowController> {

    @FXML
    private TextField ISBNFindText;

    @FXML
    private Button addButton;

    @FXML
    private TextField authorFindText;

    @FXML
    private TextField bookNameFindTExt;

    @FXML
    private VBox tableVbox;

    @FXML
    private ChoiceBox<String> categoryChoiceBox;

    @FXML
    private VBox categoryList;

    @FXML
    private AnchorPane categoryTable;

    @FXML
    private Button findButton;

    @FXML
    private HBox mainPane;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private ChoiceBox<String> statusFindBox;

    @FXML
    private AnchorPane tableBookPane;

    @FXML
    private Label totalNumberBookLabel;

    @FXML
    private Label totalNumberBorrowLabel;

    @FXML
    private Label totalNumberIssueLabel;

    @FXML
    private Label totalNumberLostLabel;


    private static final String ROW_FXML = "/view/BookTableRow.fxml";
    private AdminHomePageController adminHomePageController;

    private int totalNumberBook;
    private int totalNumberLost;

    @FXML
    protected void initialize() {
        setCategoryFindList();
        statusFindBox.getItems().add("None");
        statusFindBox.getItems().addAll(BookStatus.AVAILABLE.toString(), BookStatus.UNAVAILABLE.toString().toString());
        bookNameFindTExt.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                Stage stage = (Stage) bookNameFindTExt.getScene().getWindow();

                stage.widthProperty().addListener((obs, oldWidth, newWidth) ->
                        Platform.runLater(() -> categoryTable.setVisible(false)));

                stage.heightProperty().addListener((obs, oldHeight, newHeight) ->
                        Platform.runLater(() -> categoryTable.setVisible(false)));
            }
        });
        adminHomePageController = dashboardLoader.getController();
    }

    @Override
    protected String getRowFXML() {
        return ROW_FXML;
    }

    @Override
    protected void loadDataFromSource() throws SQLException {
        itemsList.addAll(BookDAO.getInstance().findAll());
        setText();
    }

    @Override
    protected void getCriteria() {
        findCriteria.clear();

        // Kiểm tra và thêm tiêu chí tìm kiếm theo ISBN
        if (!ISBNFindText.getText().isEmpty()) {
            findCriteria.put("ISBN", ISBNFindText.getText());
        }

        // Kiểm tra và thêm tiêu chí tìm kiếm theo tên sách
        if (!bookNameFindTExt.getText().isEmpty()) {
            findCriteria.put("title", bookNameFindTExt.getText());
        }
        // Kiểm tra và thêm tiêu chí tìm kiếm theo tác giả
        if (!authorFindText.getText().isEmpty()) {
            findCriteria.put("author_name", authorFindText.getText());
        }

        if (!categoryChoiceBox.getItems().isEmpty() && categoryChoiceBox.getValue() != "None" && categoryChoiceBox.getValue() != null) {
            findCriteria.put("category_name", categoryChoiceBox.getValue());
        }

        if (!statusFindBox.getItems().isEmpty() && statusFindBox.getValue() != "None" && statusFindBox.getValue() != null) {
            findCriteria.put("BookStatus", statusFindBox.getValue());
        }

    }

    @Override
    protected void searchCriteria() {
        getCriteria();
        if (findCriteria.isEmpty()) {
            loadData();
            return;
        }
        try {
            itemsList.clear();
            itemsList.addAll(BookDAO.getInstance().findByCriteria(findCriteria));
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadRows();
    }

    @FXML
    void onFindButtonAction(ActionEvent event) {
        searchCriteria();
    }

    @FXML
    void onAddButtonAction(ActionEvent event) {
        mainController.loadAddPane();
    }

    protected void setText() {
        try {
            totalNumberBook = 0;
            totalNumberLost = 0;
            findCriteria.put("BookItemStatus", BookItemStatus.AVAILABLE.toString());
            List<Book> bookList = BookDAO.getInstance().findAll();
            for (Book book : bookList) {
                totalNumberBook += book.getQuantity();
                totalNumberLost += book.getLostBooksCount();
            }
            totalNumberBookLabel.setText(String.valueOf(this.totalNumberBook-this.totalNumberLost));
            totalNumberLostLabel.setText(String.valueOf(this.totalNumberLost));
        } catch (Exception e) {
            e.printStackTrace();
        }
        adminHomePageController.setTotalBookLabel(totalNumberBookLabel.getText());
        try {
            findCriteria.put("BookItemStatus", BookItemStatus.LOANED.toString());
            totalNumberBorrowLabel.setText(BookItemDAO.getInstance().findByCriteria(findCriteria).size() + "");
            findCriteria.clear();
            findCriteria.put("BookReservationStatus", BookReservationStatus.WAITING.toString());
            totalNumberIssueLabel.setText(BookReservationDAO.getInstance().findByCriteria(findCriteria).size() + "");
            findCriteria.clear();
            findCriteria.put("BookItemStatus", BookItemStatus.LOST.toString());
            totalNumberLostLabel.setText(BookItemDAO.getInstance().findByCriteria(findCriteria).size() + "");
            findCriteria.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void setCategoryFindList() {
        try {
            categoryChoiceBox.getItems().add("None");
            List<Category> categories = BookDAO.getInstance().selectAllCategory();
            for (Category category : categories) {
                categoryChoiceBox.getItems().add(category.toString());
            }
        } catch (Exception e) {
            System.out.println("Lỗi setCategoryFindList:" + e.getMessage());
        }

    }


}
