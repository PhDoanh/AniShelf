package com.library.anishelf.controller;

import com.library.anishelf.model.BookItem;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class AdminBookItemRowController {

    @FXML
    private TextField bookIdText;

    @FXML
    private TextField noteTextField;

    @FXML
    private TextField conditionText;

    private AdminBookPageController mainContainer;
    private BookItem bookInstance;

    public void setMainContainer(AdminBookPageController mainContainer) {
        this.mainContainer = mainContainer;
    }

    /**
     * Hàm để set các thông tin các trường và bookInstance của row này.
     * @param bookInstance
     */
    public void setBookInstance(BookItem bookInstance) {
        this.bookInstance = bookInstance;
        bookIdText.setText(String.valueOf(bookInstance.getBarcode()));
        if(bookInstance.getNote() != null) {
            noteTextField.setText(String.valueOf(bookInstance));
        } else {
            noteTextField.setText("-");
        }
        conditionText.setText(String.valueOf(bookInstance.getStatus().getDisplayName()));
    }
}
