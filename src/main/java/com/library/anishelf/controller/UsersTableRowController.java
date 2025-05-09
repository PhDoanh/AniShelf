package com.library.anishelf.controller;

import com.library.anishelf.model.Member;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;


public class UsersTableRowController extends BaseRowController<Member, UsersPageController> {

    @FXML
    private HBox mainRowHbox;
    @FXML
    private Label memberIDlabel;

    @FXML
    private Label memberNameLabel;

    @FXML
    private Label phoneNumberLabel;

    @Override
    protected void updateRowDisplay() {
        memberIDlabel.setText(String.valueOf(item.getPerson().getId()));
        memberNameLabel.setText(item.getPerson().getLastName() + " " + item.getPerson().getFirstName());
        phoneNumberLabel.setText(item.getPerson().getPhone());
    }

}