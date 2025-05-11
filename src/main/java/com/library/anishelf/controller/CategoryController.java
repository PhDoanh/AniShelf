package com.library.anishelf.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;


public class CategoryController {
    @FXML
    Label categoryNameLabel;
    @FXML
    Button XButtontt;

    private AdvancedSearchController advancedSearchBookController;

    /**
     * thiết lập ữ liệu.
     * @param name tên thể loại
     * @param advancedSearchController controller
     */
    public void setDataItem(String name, AdvancedSearchController advancedSearchController) {
        this.advancedSearchBookController = advancedSearchController;
        categoryNameLabel.setText(name);
        categoryNameLabel.getStyleClass().add("label-4");
    }

    /**
     * khi ấn vào thể loại
     * @param mouseEvent khi ấn
     */
    public void handleCategoryMouseClicked(MouseEvent mouseEvent) {
        categoryNameLabel.getStyleClass().clear();
        categoryNameLabel.getStyleClass().add("label-5");
        XButtontt.setVisible(true);
        advancedSearchBookController.addCategoryCriteria(categoryNameLabel.getText());
    }

    /**
     * xoá thể loại
     * @param actionEvent khi ấn
     */
    public void handleXbuttonClicked(ActionEvent actionEvent) {
        categoryNameLabel.getStyleClass().clear();
        categoryNameLabel.getStyleClass().add("label-4");
        XButtontt.setVisible(false);
        advancedSearchBookController.deleteCategoryCriteria(categoryNameLabel.getText());
    }
}
