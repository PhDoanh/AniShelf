package com.library.anishelf.controller;

import com.library.anishelf.model.Report;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class AdminIssueTableRowOfIssueController extends BaseRowController<Report, AdminIssueMainPageController> {

    @FXML
    private Label detailIssueLabel;

    @FXML
    private Label emailOfMemberLabel;

    @FXML
    private Label IDOfMemberLabel;

    @FXML
    private Label memberNameLabel;

    @FXML
    private Label statusIssueLabel;

    @FXML
    private Label titleOfIssueLabel;

    @FXML
    private HBox mainRowHbox;

    // This method is called to update the display of the row with the current item's data.
    @Override
    protected void updateRowIssueDisplay() {
        IDOfMemberLabel.setText(item.getMember().getPerson().getId() + "");
        memberNameLabel.setText(item.getMember().getPerson().getLastName() + " " + item.getMember().getPerson().getFirstName());
        titleOfIssueLabel.setText(item.getTitle());
        detailIssueLabel.setText(item.getContent());
        emailOfMemberLabel.setText(item.getMember().getPerson().getEmail());
        statusIssueLabel.setText(item.getStatus().toString());
        if(statusIssueLabel.getText().equals("RESOLVED")){
            statusIssueLabel.setStyle("-fx-text-fill: green;");
        } else if(statusIssueLabel.getText().equals("PENDING")){
            statusIssueLabel.setStyle("-fx-text-fill: red;");
        }
    }


}
