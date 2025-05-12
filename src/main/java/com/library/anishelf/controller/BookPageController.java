package com.library.anishelf.controller;

import com.library.anishelf.model.Book;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

/**
 * The type Book page controller.
 */
public class BookPageController extends BasePageController<Book, BookInfoController, BookTableController> {

    private static final String TABLE_FXML = "/view/BookTable.fxml";
    private static final String DETAIL_FXML = "/view/BookInfo.fxml";

    @FXML
    private AnchorPane detailLocation;

    @FXML
    private AnchorPane detailPage;

    @FXML
    private Button returnButton;

    @FXML
    private AnchorPane tablePage;

    private AdminHomePageController adminHomePageController;

    @Override
    protected String getDetailFXMLPath() {
        return DETAIL_FXML;
    }

    @Override
    protected String getTableFXMLPath() {
        return TABLE_FXML;
    }

    @Override
    protected void setupControllers() {
        adminHomePageController = dashboardLoader.getController();
        adminHomePageController.setAdminBookPageController(this);

    }

    @Override
    protected void setupViews() {
        tablePage.getChildren().add(tablePane);
        detailLocation.getChildren().add(detailPane);
    }

    /**
     * Khi mà bấm quay lại (page1 -> page2) thì sẽ load lại Data của bảng Table và các Summary Total
     *
     * @param event the event
     */
    @FXML
    void onReturnButton(ActionEvent event) {
        while (getTitlePageStack().peek() != "Quản lý truyện") {
            getTitlePageStack().pop();
        }
        loadData();
        alterPage();
    }

    @Override
    public void alterPage() {
        detailPage.setVisible(!detailPage.isVisible());
        tablePage.setVisible(!tablePage.isVisible());
        if (detailPage.isVisible()) {
            page1 = false;
        } else {
            page1 = true;
        }
    }

    @Override
    public void startPage() {
        page1 = true;
        detailPage.setVisible(false);
        tablePage.setVisible(true);
        loadData();
    }

}
