package com.library.anishelf.controller;

import com.library.anishelf.model.Report;
import com.library.anishelf.model.enums.ReportStatus;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class AdminIssueOfBookDetailController extends BaseDetailController<Report> {

    @FXML
    private Label IDOfMemberLabel;

    @FXML
    private Label IDOfReportLabel;

    @FXML
    private TextArea detailOfIssueText;

    @FXML
    private Button editIssueButton;

    @FXML
    private Label emailMemberLabel;

    @FXML
    private Label nameOfMemberLabel;

    @FXML
    private Label phoneNumberMemberLabel;

    @FXML
    private Button saveIssueButton;

    @FXML
    private ChoiceBox<ReportStatus> statusBookBox;

    @FXML
    private Label titelIssueLabel;

    public void initialize() {
        statusBookBox.getItems().addAll(ReportStatus.values());
    }

    @Override
    protected void fetchBookItemDetails() {
        statusBookBox.setValue(item.getStatus());

        if(!getTitlePageStack().peek().equals(item.getReportID()+"")) {
            getTitlePageStack().push(item.getReportID() + "");
        }
        IDOfReportLabel.setText(String.valueOf(item.getReportID()));
        nameOfMemberLabel.setText(item.getMember().getPerson().getLastName() + " " + item.getMember().getPerson().getFirstName());
        IDOfMemberLabel.setText(String.valueOf(item.getMember().getPerson().getId()));
        emailMemberLabel.setText(item.getMember().getPerson().getEmail());
        titelIssueLabel.setText(item.getTitle());
        detailOfIssueText.setText(item.getContent());
        phoneNumberMemberLabel.setText(item.getMember().getPerson().getPhone());
    }

    @Override
    protected void updateAddModeUIUX() {

    }

    @Override
    protected void updateEditModeUIUX() {
        editIssueButton.setVisible(!editMode);
        saveIssueButton.setVisible(editMode);

        statusBookBox.setMouseTransparent(!editMode);
    }

    @Override
    protected boolean validateItemBookInput() {
        return true;
    }

    @Override
    protected boolean getNewBookItemInformation() throws Exception {
        item.setStatus(statusBookBox.getValue());
        return true;
    }

    @Override
    protected String getItemType() {
        return "report";
    }

    @FXML
    private void handleEditButton(ActionEvent event) {
        getTitlePageStack().push("Edit");
        setEditMode(true);
    }

    @FXML
    private void handleSaveButton(ActionEvent event) {
        saveChanges();
    }


}
