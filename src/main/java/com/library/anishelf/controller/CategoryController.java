package com.library.anishelf.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;


/**
 * The type Category controller.
 */
public class CategoryController {
    /**
     * The Category name.
     */
    @FXML
    Label categoryName;
    /**
     * The X button.
     */
    @FXML
    Button XButton;

    private AdvancedSearchController advancedSearchController;

    /**
     * thiết lập ữ liệu.
     *
     * @param name                     tên thể loại
     * @param advancedSearchController controller
     */
    public void setData(String name, AdvancedSearchController advancedSearchController) {
        this.advancedSearchController = advancedSearchController;
        categoryName.setText(name);
        categoryName.getStyleClass().add("label-4");
    }

    /**
     * khi ấn vào thể loại
     *
     * @param mouseEvent khi ấn
     */
    public void onCategoryMouseClicked(MouseEvent mouseEvent) {
        categoryName.getStyleClass().clear();
        categoryName.getStyleClass().add("label-5");
        XButton.setVisible(true);
        advancedSearchController.addCategoryCriteria(categoryName.getText());
    }

    /**
     * xoá thể loại
     *
     * @param actionEvent khi ấn
     */
    public void onXbuttonAction(ActionEvent actionEvent) {
        categoryName.getStyleClass().clear();
        categoryName.getStyleClass().add("label-4");
        XButton.setVisible(false);
        advancedSearchController.deleteCategoryCriteria(categoryName.getText());
    }
}
