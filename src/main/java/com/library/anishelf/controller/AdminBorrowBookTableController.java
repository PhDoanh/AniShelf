package com.library.anishelf.controller;

import com.library.anishelf.dao.BookIssueDAO;
import com.library.anishelf.dao.BookItemDAO;
import com.library.anishelf.model.BookIssue;
import com.library.anishelf.model.enums.BookIssueStatus;
import com.library.anishelf.model.enums.BookItemStatus;
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

public class AdminBorrowBookTableController extends BaseTableController<BookIssue, AdminBorrowBookPageController,AdminBorrowTableRowController> {

    private static final String ROW_FXML_VIEW = "/view/AdminBorrowTableRow.fxml";

    @FXML
    private Button addButton;

    @FXML
    private TextField barCodeTextField;

    @FXML
    private TextField bookNameTextField;

    @FXML
    private TextField borrowDateTextField;

    @FXML
    private TextField borrowerTextField;

    @FXML
    private TextField memeberIDTextField;

    @FXML
    private ScrollPane scrollPaneContainer;

    @FXML
    private TextField condtionTextField;

    @FXML
    private Button findButton;

    @FXML
    private ChoiceBox<String> statusFindBox;

    @FXML
    private VBox tableBookPane;

    private AdminMainDashboardController adminMainDashboardController;

    public void initialize() {
        statusFindBox.getItems().add("None");
        statusFindBox.getItems().addAll(BookIssueStatus.BORROWED.toString(), BookIssueStatus.RETURNED.toString(), BookIssueStatus.LOST.toString());
        adminMainDashboardController = dashboardLoader.getController();
    }

    @Override
    protected String getItemRowFXML() {
        return ROW_FXML_VIEW;
    }

    @Override
    protected void fetchDataFromSource() throws SQLException {
        itemsList.addAll(BookIssueDAO.getInstance().selectAll());
        //Xu ly set total cho Dashboard
        findCriteria.clear();
        findCriteria.put("BookItemStatus", BookItemStatus.LOANED.toString());
        int totalBorrow = BookItemDAO.getInstance().searchByCriteria(findCriteria).size();
        adminMainDashboardController.setTotalOfBorrowBookLabel(totalBorrow+"");
        findCriteria.clear();
    }
    @Override
    protected void getItemCriteria(){
        findCriteria.clear();
        if(!barCodeTextField.getText().isEmpty()){
            findCriteria.put("barcode", barCodeTextField.getText());
        }
        if(!bookNameTextField.getText().isEmpty()){
            findCriteria.put("title", bookNameTextField.getText());
        }
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // hoặc định dạng phù hợp với dữ liệu của bạn
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        // Chuyển đổi và định dạng cho borrowDate
        if (borrowDateTextField.getText() != null && !borrowDateTextField.getText().isEmpty()) {
            LocalDate createdDate = LocalDate.parse(borrowDateTextField.getText(), inputFormatter);
            createdDate.format(outputFormatter);
            findCriteria.put("creation_date", createdDate.toString());
        }
        if(!borrowerTextField.getText().isEmpty()){
            findCriteria.put("fullname", borrowerTextField.getText());
        }

        if(!memeberIDTextField.getText().isEmpty()){
            findCriteria.put("member_ID", memeberIDTextField.getText());
        }
        if(statusFindBox.getValue() != "None" && statusFindBox.getValue() != null){
            findCriteria.put("BookIssueStatus",statusFindBox.getValue());
        }

    }

    @FXML
    void handleAddButton(ActionEvent event) {
        mainController.loadAddPane();
    }

    @Override
    protected void searchItemCriteria() {
        getItemCriteria();
        if(findCriteria.isEmpty()) {
            loadData();
            return;
        }
        try {
            itemsList.clear();
            itemsList.addAll(BookIssueDAO.getInstance().searchByCriteria(findCriteria));
        }catch (Exception e) {
            e.printStackTrace();
        }
        loadRows();
    }

    @FXML
    void handleFindButton(ActionEvent event) {
        searchItemCriteria();
    }

}
