package com.library.anishelf.controller;

import com.library.anishelf.model.BookItem;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class BookCopyRowController {

    @FXML
    private TextField barcodeField;

    @FXML
    private TextField noteField;

    @FXML
    private TextField statusField;

    private BookPageController mainController;
    private BookItem bookItem;

    public void setMainController(BookPageController mainController) {
        this.mainController = mainController;
    }

    /**
     * Hàm để set các thông tin các trường và bookItem của row này.
     * @param bookItem
     */
    public void setBookItem(BookItem bookItem) {
        this.bookItem = bookItem;
        barcodeField.setText(String.valueOf(bookItem.getBookBarcode()));
        if(bookItem.getRemarks() != null) {
            noteField.setText(String.valueOf(bookItem));
        } else {
            noteField.setText("-");
        }
        statusField.setText(String.valueOf(bookItem.getBookItemStatus().getDisplayName()));
    }
}
