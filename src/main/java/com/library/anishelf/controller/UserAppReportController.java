package com.library.anishelf.controller;

import com.library.anishelf.dao.ReportDAO;
import com.library.anishelf.model.Report;
import com.library.anishelf.model.enums.ReportStatus;
import com.library.anishelf.util.fxmlLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserAppReportController {
    private static final String SETTING_FXML_VIEW = "/view/Setting-view.fxml";
    private static final String REPORT_CARD_FXML_VIEW ="/view/UserReportCard-view.fxml";

    private fxmlLoader fxmlLoader1 = fxmlLoader1.getInstance();

    @FXML
    private VBox reportsBoxContainer, vboxContainer;
    @FXML
    private TextField reportTitleTextField;
    @FXML
    private TextArea reportContentTextArea;

    private List<Report> reportList = new ArrayList<>();
    private Report currentUserReport;
    private UserReportCardController userCurrentReportCardController;

    public void initialize() {
        try {
            // Instead of using the generic searchByCriteria, let's directly query for reportList
            // owned by the current member using a SQL query that doesn't use LIKE on integer columns
            int memberId = UserMenuController.getMember().getPerson().getId();
            
            // Create a custom SQL query string
            String sqlQuery = "SELECT * FROM \"Reports\" WHERE \"member_ID\" = " + memberId;
            reportList = getUserReportsByQuery(sqlQuery);
            
            for (int i = 0; i < reportList.size(); i++) {
                loadUserReports(reportList.get(i));
            }
            
            CustomerAlterApp.showAlertMessage("Report những vấn đề của bạn");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Helper method to get reportList using a custom SQL query
     */
    private List<Report> getUserReportsByQuery(String sql) throws SQLException {
        List<Report> results = new ArrayList<>();
        List<Report> allReports = ReportDAO.getInstance().selectAll();
        
        // Filter reportList to only include those from the current member
        int memberId = UserMenuController.getMember().getPerson().getId();
        for (Report report : allReports) {
            if (report.getMember().getPerson().getId() == memberId) {
                results.add(report);
            }
        }
        
        return results;
    }

    public void handleBackButtonClicked(ActionEvent actionEvent) {
        VBox content = (VBox) fxmlLoader1.loadFXML(SETTING_FXML_VIEW);
        if (content != null) {
            fxmlLoader1.updateContentBox(content);
        }
    }

    public void handleAddReportButtonClicked(ActionEvent actionEvent) {
        if(currentUserReport !=null && (currentUserReport.getContent().isEmpty()
                || currentUserReport.getTitle().isEmpty())) {
            CustomerAlterApp.showAlertMessage("Viết xong rì pọt kia đi thì cho thêm");
            return;
        }
        currentUserReport = new Report(UserMenuController.getMember(),"","");
        currentUserReport.setStatus(ReportStatus.PENDING);
        try {
            //load new report
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(REPORT_CARD_FXML_VIEW));
            HBox cardBox = fxmlLoader.load();
            userCurrentReportCardController = fxmlLoader.getController();
            userCurrentReportCardController.setData(currentUserReport);
            reportsBoxContainer.getChildren().add(cardBox);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //set data and css
        reportContentTextArea.setText("");
        reportTitleTextField.setText("");
        vboxContainer.setStyle("-fx-background-color: #FF7878;-fx-background-radius: 20;");
    }

    public void showUserssueContent(Report report, UserReportCardController userReportCardController) {
        this.userCurrentReportCardController =userReportCardController;
        this.currentUserReport = report;
        reportContentTextArea.setText(report.getContent());
        reportTitleTextField.setText(report.getTitle());
        String statusText = report.getStatus().toString();
        if(statusText.equals("PENDING")) {
            vboxContainer.setStyle("-fx-background-color: #FF7878;-fx-background-radius: 20;");
        } else {
            vboxContainer.setStyle("-fx-background-color: #AFFF84;-fx-background-radius: 20;");
        }
    }

    public void handleSaveButtonClicked(ActionEvent actionEvent) {
        if (currentUserReport != null) {
            if(reportContentTextArea.getText().isEmpty()
            || reportTitleTextField.getText().isEmpty()) {
                CustomerAlterApp.showAlertMessage("Hãy điền đủ report!");
                return;
            }
            if(reportList.contains(currentUserReport)) {
                updateReports();
            } else {
                addMoreReport();
            }
        } else {
            CustomerAlterApp.showAlertMessage("Hãy chọn report để sửa hoặc tạo mới");
        }
    }

    private void loadUserReports(Report report) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(REPORT_CARD_FXML_VIEW));
            HBox cardBox = fxmlLoader.load();
            UserReportCardController cardController = fxmlLoader.getController();
            cardController.setData(report);
            reportsBoxContainer.getChildren().add(cardBox);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleReportTitleTextMouseClicked(MouseEvent mouseEvent) {
        if(currentUserReport == null) {
            CustomerAlterApp.showAlertMessage("hãy chọn một report hoặc tạo report để sửa");
        }
    }

    public void handleReportContentTextMouseClicked(MouseEvent mouseEvent) {
        if(currentUserReport == null) {
            CustomerAlterApp.showAlertMessage("hãy chọn một report hoặc tạo report để sửa");
        }
    }

    private void updateReports() {
        try {
            currentUserReport.setContent(reportContentTextArea.getText());
            currentUserReport.setTitle(reportTitleTextField.getText());
            userCurrentReportCardController.editReport(currentUserReport);
            ReportDAO.getInstance().update(currentUserReport);
            CustomerAlterApp.showAlertMessage("Mình lưu cho bạn rùi nhá :)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        reportContentTextArea.setText("");
        reportTitleTextField.setText("");
        vboxContainer.setStyle("-fx-background-color: #ffffff;-fx-background-radius: 20;");
        currentUserReport = null;
    }

    private void addMoreReport() {
        try {
            currentUserReport.setContent(reportContentTextArea.getText());
            currentUserReport.setTitle(reportTitleTextField.getText());
            userCurrentReportCardController.editReport(currentUserReport);
            ReportDAO.getInstance().add(currentUserReport);
            CustomerAlterApp.showAlertMessage("Mình lưu cho bạn rùi nhá :)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        reportContentTextArea.setText("");
        reportTitleTextField.setText("");
        vboxContainer.setStyle("-fx-background-color: #ffffff;-fx-background-radius: 20;");
        currentUserReport = null;
    }

}
