package com.library.anishelf.controller;

import com.library.anishelf.model.Book;
import com.library.anishelf.model.enums.BookStatus;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class BookTableRowController extends BaseRowController<Book, BookPageController> {


    @FXML
    private Label ISBNLabel;

    @FXML
    private Label authorNameLabel;

    @FXML
    private Label bookNameLabel;

    @FXML
    private Label categoryLabel;

    @FXML
    private Label locationLabel;

    @FXML
    private Label numberOfBookLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private HBox mainRowHbox;

    @Override
    protected void updateRowDisplay() {
        ISBNLabel.setText(String.valueOf(item.getIsbn()));
        authorNameLabel.setText(getAuthors(item.getAuthors()));
        bookNameLabel.setText(item.getTitle());
        categoryLabel.setText(getCategories(item.getCategories()));
        locationLabel.setText(item.getLocation());
        numberOfBookLabel.setText((String.valueOf(item.getQuantity())));
        if(item.getstatus() == null) {
            System.out.println(item.getIsbn() +" is null status");
        }
        statusLabel.setText(item.getstatus()+"");
        if(item.getstatus().equals(BookStatus.AVAILABLE)) {
            statusLabel.setStyle(("-fx-text-fill: green;"));
        }
        else {
            statusLabel.setStyle(("-fx-text-fill: red;"));
        }
    }


}
