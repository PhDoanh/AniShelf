package com.library.anishelf.controller;

import com.library.anishelf.util.fxmlLoader;
import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;

public class UserAppIssuesController {
    private static final String SETTING_FXML_VIEW = "/view/Setting-view.fxml";

    private fxmlLoader fxmlLoader1 = fxmlLoader1.getInstance();

    public void handleBackButtonClicked(ActionEvent actionEvent) {
        VBox content = (VBox) fxmlLoader1.loadFXML(SETTING_FXML_VIEW);
        if (content != null) {
            fxmlLoader1.updateContentBox(content);
        }
    }

    public void handleAddIssueButtonClicked(ActionEvent actionEvent) {
    }
}
