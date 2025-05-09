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

public class BorrowedBooksTableController extends BaseTableController<BookIssue, BorrowedBookPageController, BorrowedBooksTableRowController> {

    private static final String ROW_FXML = "/view/BorrowedBooksTableRow.fxml";

    @FXML
    private Button addButton;

    @FXML
    private TextField bookNameFindText;

    @FXML
    private TextField borrowDateFindText;

    @FXML
    private TextField borrowerFindText;

    @FXML
    private TextField memeberIDFindText;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextField statusFindText;

    @FXML
    private Button findButton;

    @FXML
    private ChoiceBox<String> statusFindBox;

    @FXML
    private VBox tableVbox;

    private AdminHomePageController adminHomePageController;

    public void initialize() {
        statusFindBox.getItems().add("None");
        statusFindBox.getItems().addAll(BookIssueStatus.BORROWED.toString(), BookIssueStatus.RETURNED.toString(), BookIssueStatus.LOST.toString());
        adminHomePageController = dashboardLoader.getController();
    }

    @Override
    protected String getRowFXML() {
        return ROW_FXML;
    }

    @Override
    protected void loadDataFromSource() throws SQLException {
        itemsList.addAll(BookIssueDAO.getInstance().findAll());
        //Xu ly set total cho Dashboard
        findCriteria.clear();
        findCriteria.put("BookItemStatus", BookItemStatus.LOANED.toString());
        int totalBorrow = BookItemDAO.getInstance().findByCriteria(findCriteria).size();
        adminHomePageController.setTotalBorrowLabel(totalBorrow+"");
        findCriteria.clear();
    }
    @Override
    protected void getCriteria(){
        findCriteria.clear();
        if(!bookNameFindText.getText().isEmpty()){
            findCriteria.put("title",bookNameFindText.getText());
        }
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // hoặc định dạng phù hợp với dữ liệu của bạn
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        // Chuyển đổi và định dạng cho borrowDate
        if (borrowDateFindText.getText() != null && !borrowDateFindText.getText().isEmpty()) {
            LocalDate createdDate = LocalDate.parse(borrowDateFindText.getText(), inputFormatter);
            createdDate.format(outputFormatter);
            findCriteria.put("creation_date", createdDate.toString());
        }
        if(!borrowerFindText.getText().isEmpty()){
            findCriteria.put("fullname",borrowerFindText.getText());
        }

        if(!memeberIDFindText.getText().isEmpty()){
            findCriteria.put("member_ID",memeberIDFindText.getText());
        }
        if(statusFindBox.getValue() != "None" && statusFindBox.getValue() != null){
            findCriteria.put("BookIssueStatus",statusFindBox.getValue());
        }

    }

    @FXML
    void onAddButtonAction(ActionEvent event) {
        mainController.loadAddPane();
    }

    @Override
    protected void searchCriteria() {
        getCriteria();
        if(findCriteria.isEmpty()) {
            loadData();
            return;
        }
        try {
            itemsList.clear();
            itemsList.addAll(BookIssueDAO.getInstance().findByCriteria(findCriteria));
        }catch (Exception e) {
            e.printStackTrace();
        }
        loadRows();
    }

    @FXML
    void onFindButtonAction(ActionEvent event) {
        searchCriteria();
    }

}
