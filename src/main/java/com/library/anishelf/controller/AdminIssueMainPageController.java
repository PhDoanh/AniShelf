package com.library.anishelf.controller;

import com.library.anishelf.model.Report;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class AdminIssueMainPageController extends BasePageController<Report, AdminIssueOfBookDetailController, AdminIssueOfBookTableController> {
    private static final String TABLE_FXML_VIEW = "/view/AdminIssueTable.fxml";
    private static final String DETAIL_FXML_VIEW = "/view/AdminIssueDetail.fxml";
    @FXML
    private AnchorPane DetailPaneContainer;

    @FXML
    private HBox detailIssuePage;

    @FXML
    private AnchorPane messageIssuePane;

    @FXML
    private Button returnIssueButton;

    private AdminDashboardController adminMainDashboardController;

    @FXML
    private AnchorPane tableIssuePage;

    @Override
    protected String getItemDetailFXMLPath() {
        return DETAIL_FXML_VIEW;
    }

    @Override
    protected String getItemTableFXMLPath() {
        System.out.println("Issuel");
        return TABLE_FXML_VIEW;
    }

    @Override
    protected void initializeControllers() {
        adminMainDashboardController = dashboardLoader.getController();
        adminMainDashboardController.setAdminIssuePageController(this);
    }

    @Override
    protected void initializeViews() {
        this.tableIssuePage.getChildren().add(super.tableBookPane);
        this.DetailPaneContainer.getChildren().add(super.detailPane);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(MESSAGE_FXML));
            Node mess = loader.load();
            AnchorPane.setTopAnchor((AnchorPane) mess, 0.0);
            AnchorPane.setBottomAnchor((AnchorPane) mess, 0.0);
            AnchorPane.setLeftAnchor((AnchorPane) mess, 0.0);
            AnchorPane.setRightAnchor((AnchorPane) mess, 0.0);
            this.messageIssuePane.getChildren().add(mess);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void switchPage() {
        detailIssuePage.setVisible(!detailIssuePage.isVisible());
        tableIssuePage.setVisible(!tableIssuePage.isVisible());
        if(detailIssuePage.isVisible()) {
            page1 = false;
        } else {
            page1 = true;
        }
    }

    @Override
    public void initalPage() {
        page1 = true;
        setTitlePage();
        detailIssuePage.setVisible(false);
        tableIssuePage.setVisible(true);
        loadData();
    }

    @FXML
    void handleReturnButton(ActionEvent event) {
        while (getTitlePageStack().peek() != "Report") {
            getTitlePageStack().pop();
        }
        loadData();
        switchPage();
    }

}