package com.library.anishelf.controller;

import com.library.anishelf.model.Member;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;


public class AdminUserAppTableRowController extends BaseRowController<Member, AdminUserPageController> {

    @FXML
    private HBox mainRowHbox;
    @FXML
    private Label memberIdlabel;

    @FXML
    private Label nameOfMemberLabel;

    @FXML
    private Label phoneNumberMemberLabel;

    @Override
    protected void updateRowOfTableDisplay() {
        memberIdlabel.setText(String.valueOf(item.getPerson().getId()));
        nameOfMemberLabel.setText(item.getPerson().getLastName() + " " + item.getPerson().getFirstName());
        phoneNumberMemberLabel.setText(item.getPerson().getPhone());
    }

}