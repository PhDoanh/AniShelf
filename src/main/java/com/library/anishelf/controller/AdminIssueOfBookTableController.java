package com.library.anishelf.controller;

import com.library.anishelf.dao.ReportDAO;
import com.library.anishelf.model.Report;
import com.library.anishelf.model.enums.ReportStatus;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.sql.SQLException;

public class AdminIssueOfBookTableController extends BaseTableController<Report, AdminIssueMainPageController, AdminIssueTableRowOfIssueController> {
    protected static final String ROW_FXMLg_VIEW = "/view/AdminIssueTableRow.fxml";

    @FXML
    private TextField emailFindText;

    @FXML
    private Button findIssueButton;

    @FXML
    private AnchorPane mainContainer;

    @FXML
    private TextField memberNameFindText;

    @FXML
    private TextField memeberIDFindText;

    @FXML
    private ScrollPane scrollPaneContainer;

    @FXML
    private ChoiceBox<String> statusSearchBox;

    @FXML
    private VBox tableBookPane;
    private AdminDashboardController adminMainDashboardController;

    public void initialize() {
        statusSearchBox.getItems().add("None");
        statusSearchBox.getItems().addAll(ReportStatus.PENDING.toString(),ReportStatus.RESOLVED.toString());
        adminMainDashboardController = dashboardLoader.getController();
    }

    @Override
    protected String getItemRowFXML() {
        return ROW_FXML_VIEW;
    }

    @Override
    protected void fetchDataFromSource() throws SQLException {
        itemsList.addAll(ReportDAO.getInstance().selectAll());

        //Xử lý set total cho Dashboard
        findCriteria.clear();
        findCriteria.put("ReportStatus",ReportStatus.PENDING.toString());
        int totalIssuel = ReportDAO.getInstance().searchByCriteria(findCriteria).size();
        adminMainDashboardController.setTotalIssueLabel(totalIssuel+"");
        findCriteria.clear();
    }

    @FXML
    void handleFindButtonAction(ActionEvent event) {
        searchItemCriteria();
    }

    @Override
    protected void getItemCriteria(){
        findCriteria.clear();
        if(!memeberIDFindText.getText().isEmpty()){
            findCriteria.put("member_ID",memeberIDFindText.getText());
        }
        if (!statusSearchBox.getItems().isEmpty() && statusSearchBox.getValue() != "None" && statusSearchBox.getValue() != null) {
            findCriteria.put("ReportStatus", statusSearchBox.getValue());
        }

    }
    @FXML
    protected void searchItemCriteria() {
        getItemCriteria();
        try {
            itemsList.clear();
            itemsList.addAll(ReportDAO.getInstance().searchByCriteria(findCriteria));
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadRows();
    }


}
