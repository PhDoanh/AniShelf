package com.library.anishelf.controller;

import com.library.anishelf.model.BookIssue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

/**
 * The type Borrowed book page controller.
 */
public class BorrowedBookPageController extends BasePageController<BookIssue, BorrowBookInfoController, BorrowedBooksTableController> {

    private static final String TABLE_FXML = "/view/BorrowedBooksTable.fxml";
    private static final String DETAIL_FXML = "/view/BorrowedBookInfo.fxml";

    @FXML
    private AnchorPane detailLocation;

    @FXML
    private AnchorPane detailPage;

    @FXML
    private AnchorPane tablePage;

    @FXML
    private Button returnButton;

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

    }

    @Override
    protected void setupViews() {
        this.tablePage.getChildren().add(super.tablePane);
        this.detailLocation.getChildren().add(super.detailPane);
    }

    /**
     * On return button action.
     *
     * @param event the event
     */
    @FXML
    void onReturnButtonAction(ActionEvent event) {
        while (getTitlePageStack().peek() != "Quản lý mượn truyện") {
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
