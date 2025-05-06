package com.library.anishelf.controller;

import com.library.anishelf.model.Book;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class AdminBookItemPageController extends BasePageController<Book, AdminBookDetailController, AdminBookItemTableController> {

    private static final String TABLE_VIEW_FXML = "/view/AdminBookTable.fxml";
    private static final String DETAIL_VIEW_FXML = "/view/AdminBookDetail.fxml";

    @FXML
    private AnchorPane contentDetailLocation;

    @FXML
    private AnchorPane detailBookPage;

    @FXML
    private Button backButton;

    @FXML
    private AnchorPane tableBookPage;

    @FXML
    private Label titleOfPage;

    private AdminDashboardController adminMainDashboardContainer;

    @Override
    protected String getItemDetailFXMLPath() {
        return DETAIL_VIEW_FXML;
    }

    @Override
    protected String getItemTableFXMLPath() {
        return TABLE_VIEW_FXML;
    }

    @Override
    protected void initializeControllers() {
        adminMainDashboardContainer = dashboardLoader.getController();
        adminMainDashboardContainer.setAdminBookPageController(this);

    }

    @Override
    protected void initializeViews() {
        tableBookPage.getChildren().add(tableBookPane);
        contentDetailLocation.getChildren().add(detailPane);
    }

    /**
     * When you press back (page 1 -> page 2) it will reload the Table Data and Summary Totals
     *
     * @param event
     */
    @FXML
    void handleBackButton(ActionEvent event) {
        while (getTitlePageStack().peek() != "Quản lý sách") {
            getTitlePageStack().pop();
        }
        loadData();
        switchPage();
    }

    @Override
    public void switchPage() {
        detailBookPage.setVisible(!detailBookPage.isVisible());
        tableBookPage.setVisible(!tableBookPage.isVisible());
        if(detailBookPage.isVisible()) {
            page1 = false;
        } else {
            page1 = true;
        }
    }
    @Override
    public void initalPage() {
        page1 = true;
        setTitlePage();
        detailBookPage.setVisible(false);
        tableBookPage.setVisible(true);
        loadData();
    }

}
