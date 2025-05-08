package com.library.anishelf.controller;

import com.library.anishelf.model.BookIssue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class AdminBorrowBookPageController extends BasePageController<BookIssue, AdminBorrowBookDetailController, AdminBorrowBookTableController> {

    private static final String TABLE_FXML_VIEW = "/view/AdminBorrowTable.fxml";
    private static final String DETAIL_FXML_VIEW = "/view/AdminBorrowDetail.fxml";

    @FXML
    private AnchorPane detailContainerPane;

    @FXML
    private AnchorPane detailView;

    @FXML
    private AnchorPane tableView;

    @FXML
    private Button backButton;

    @FXML
    private Label titleOfPage;

    @Override
    protected String getItemDetailFXMLPath() {
        return DETAIL_FXML_VIEW;
    }

    @Override
    protected String getItemTableFXMLPath() {
        return TABLE_FXML_VIEW;
    }

    @Override
    protected void initializeControllers() {

    }
    @Override
    protected void initializeViews() {
        this.tableView.getChildren().add(super.tableBookPane);
        this.detailContainerPane.getChildren().add(super.detailPane);
    }

    @FXML
    void onBackButtonAction(ActionEvent event) {
        while (getTitlePageStack().peek() != "Quản lý mượn sách") {
            getTitlePageStack().pop();
        }
        loadData();
        switchPage();
    }
    @Override
    public void switchPage() {
        detailView.setVisible(!detailView.isVisible());
        tableView.setVisible(!tableView.isVisible());
        if(detailView.isVisible()) {
            page1 = false;
        } else {
            page1 = true;
        }
    }
    @Override
    public void initalPage() {
        page1 = true;
        setTitlePage();
        detailView.setVisible(false);
        tableView.setVisible(true);
        loadData();
    }



}
