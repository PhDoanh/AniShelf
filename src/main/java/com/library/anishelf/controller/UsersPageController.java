package com.library.anishelf.controller;

import com.library.anishelf.model.Member;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class UsersPageController extends BasePageController<Member, UserInfoPageController, UsersTableController> {

    private static final String DETAIL_FXML = "/view/UserInfoPage.fxml";
    private static final String TABLE_FXML = "/view/UsersTable.fxml";

    @FXML
    private AnchorPane messageLocationPane;

    @FXML
    private AnchorPane userDetailLocationPane;

    @FXML
    private AnchorPane userTableLocationPane;
    @FXML
    protected Label titlePage;

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
        userTableLocationPane.getChildren().add(tablePane);
        userDetailLocationPane.getChildren().add(detailPane);
        messageLocationPane.getChildren().add(messagePane);
    }
    @Override
    public void alterPage() {

    }
    @Override
    public  void startPage() {

    }
    @Override
    public void setTitlePage() {
        titlePage.setText("Quản lý độc giả");
    }
}
