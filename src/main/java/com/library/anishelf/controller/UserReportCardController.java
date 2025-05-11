package com.library.anishelf.controller;

import com.library.anishelf.model.Report;
import com.library.anishelf.util.fxmlLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.io.IOException;


public class UserReportCardController {
    @FXML
    private Label reportTitleTextLabel, conditionText;

    @FXML
    HBox reportCardBoxContainer;

    private Report reportFromUser;

    private static final String USER_REPORT_FXML_VIEW = "/view/UserReport-view.fxml";

    public void initializeData(Report report) {
        this.reportFromUser =report;
        reportTitleTextLabel.setText(report.getTitle());
        conditionText.setText(report.getStatus().toString());
        if(conditionText.getText() == "PENDING") {
            reportCardBoxContainer.setStyle("-fx-background-color: #FF7878;-fx-background-radius: 20;");
        } else {
            reportCardBoxContainer.setStyle("-fx-background-color: #AFFF84;-fx-background-radius: 20;");
        }
    }

    public void editUserReport(Report report) {
        reportTitleTextLabel.setText(report.getTitle());
        conditionText.setText(report.getStatus().toString());
    }

    public void handleShowReportMouseClicked(MouseEvent mouseEvent) {
        try {
            UserAppReportController userAppReportController = fxmlLoader.getInstance().getController(USER_REPORT_FXML_VIEW);
            userAppReportController.showUserssueContent(reportFromUser,this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
