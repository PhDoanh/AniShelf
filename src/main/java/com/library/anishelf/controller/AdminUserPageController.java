package com.library.anishelf.controller;

import com.library.anishelf.model.Member;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class AdminUserPageController extends BasePageController<Member, AdminUserAppDetailController, AdminUserAppTableController> {

    private static final String DETAIL_FXML_VIEW = "/view/AdminUserDetail.fxml";
    private static final String TABLE_FXML_VIEW = "/view/AdminUserTable.fxml";

    @FXML
    private AnchorPane LocationContainer;

    @FXML
    private AnchorPane userDetailLocationPane;

    @FXML
    private AnchorPane userTableLocationPane;
    @FXML
    protected Label titleOfPage;

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
        userTableLocationPane.getChildren().add(tableBookPane);
        userDetailLocationPane.getChildren().add(detailPane);
        LocationContainer.getChildren().add(messagePane);
    }
    @Override
    public void switchPage() {

    }
    @Override
    public  void initalPage() {

    }
    @Override
    public void setTitleOfPage() {
        titleOfPage.setText("Quản lý độc giả");
    }
}
