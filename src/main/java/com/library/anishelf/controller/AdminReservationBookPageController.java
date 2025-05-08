package com.library.anishelf.controller;

import com.library.anishelf.model.BookReservation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class AdminReservationBookPageController extends BasePageController<BookReservation, AdminReservationBookDetailController, AdminReservationBookTableController> {

    private static final String TABLE_FXML_VIEW = "/view/AdminReservationTable.fxml";
    private static final String DETAIL_FXML_VIEW = "/view/AdminReservationDetail.fxml";

    @FXML
    private AnchorPane detailLocationContainer;

    @FXML
    private AnchorPane detailPageContainer;

    @FXML
    private AnchorPane tablePageContainer;

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
        this.tablePageContainer.getChildren().add(super.tableBookPane);
        this.detailLocationContainer.getChildren().add(super.detailPane);
    }

    @FXML
    void onBackButtonAction(ActionEvent event) {
        while (getTitlePageStack().peek() != "Quản lý đặt trước sách") {
            getTitlePageStack().pop();
        }
        loadData();
        switchPage();
    }

    @Override
    public void switchPage() {
        detailPageContainer.setVisible(!detailPageContainer.isVisible());
        tablePageContainer.setVisible(!tablePageContainer.isVisible());
        if (detailPageContainer.isVisible()) {
            page1 = false;
        } else {
            page1 = true;
        }
    }

    @Override
    public void initalPage() {
        page1 = true;
        setTitlePage();
        detailPageContainer.setVisible(false);
        tablePageContainer.setVisible(true);
        loadData();
    }


}
