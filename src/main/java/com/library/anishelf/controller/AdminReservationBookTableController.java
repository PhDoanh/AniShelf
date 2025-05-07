package com.library.anishelf.controller;

import com.library.anishelf.dao.BookReservationDAO;
import com.library.anishelf.model.BookReservation;
import com.library.anishelf.model.enums.BookReservationStatus;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AdminReservationBookTableController extends BaseTableController<BookReservation, AdminReservationBookPageController, AdminReservationTableRowController> {

    private static final String ROW_FXML_VIEW = "/view/AdminReservationTableRow.fxml";

    @FXML
    private Button addButton;

    @FXML
    private TextField barcodeSearchText;

    @FXML
    private TextField bookNameSearchText;

    @FXML
    private TextField borrowDateSearchText;

    @FXML
    private TextField borrowerFindText;

    @FXML
    private TextField memeberIDSearchText;

    @FXML
    private ScrollPane scrollPaneContainer;

    @FXML
    private ChoiceBox<String> statusSearchBox;

    @FXML
    private Button searchReservationButton;

    @FXML
    private VBox tableBookPane;

    @FXML
    protected void initialize() {
        statusSearchBox.getItems().add("None");
        statusSearchBox.getItems().addAll(BookReservationStatus.CANCELED.toString(), BookReservationStatus.WAITING.toString(), BookReservationStatus.COMPLETED.toString());
    }

    @Override
    protected String getItemRowFXML() {
        return ROW_FXML_VIEW;
    }

    @Override
    protected void fetchDataFromSource() throws SQLException {
        itemsList.addAll(BookReservationDAO.getInstance().selectAll());
    }

    @Override
    protected void getItemCriteria() {
        findCriteria.clear();
        if (!barcodeSearchText.getText().isEmpty()) {
            findCriteria.put("barcode", barcodeSearchText.getText());
        }
        if(!bookNameSearchText.getText().isEmpty()){
            findCriteria.put("title", bookNameSearchText.getText());
        }
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // hoặc định dạng phù hợp với dữ liệu của bạn
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        // Chuyển đổi và định dạng cho borrowDate
        if (borrowDateSearchText.getText() != null && !borrowDateSearchText.getText().isEmpty()) {
            LocalDate createdDate = LocalDate.parse(borrowDateSearchText.getText(), inputFormatter);
            createdDate.format(outputFormatter);
            findCriteria.put("creation_date", createdDate.toString());
        }

        if (!borrowerFindText.getText().isEmpty()) {
            findCriteria.put("fullname", borrowerFindText.getText());
        }
        if (!memeberIDSearchText.getText().isEmpty()) {
            findCriteria.put("member_ID", memeberIDSearchText.getText());
        }
        if (!statusSearchBox.getItems().isEmpty() || statusSearchBox.getValue() != "None" || statusSearchBox.getValue() != null) {
            findCriteria.put("BookReservationStatus", statusSearchBox.getValue());
        }

    }

    @FXML
    void handleAddButtonClick(ActionEvent event) {
        mainController.loadAddPane();
    }

    @FXML
    protected void searchItemCriteria() {
        getItemCriteria();
        try {
            itemsList.clear();
            itemsList.addAll(BookReservationDAO.getInstance().searchByCriteria(findCriteria));
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadRows();
    }

    @FXML
    void handleSearchButtonClick(ActionEvent event) {
        searchItemCriteria();
    }

}
