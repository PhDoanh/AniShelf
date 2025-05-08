package com.library.anishelf.controller;

import com.library.anishelf.model.BookIssue;
import com.library.anishelf.model.enums.BookIssueStatus;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AdminUserAppHistoryRowController {

    @FXML
    private Label itemBarcodeLabel;

    @FXML
    private Label bookTitleLabel;

    @FXML
    private Label borrowBookDateLabel;

    @FXML
    private Label returnBookDateLabel;

    @FXML
    private Label conditionLabel;

    public void setDataItem(BookIssue bookIssue) {
        itemBarcodeLabel.setText(bookIssue.getBookItem().getBarcode()+"");
        bookTitleLabel.setText(bookIssue.getBookItem().getTitle());
        //Xử lý ngày
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // hoặc định dạng phù hợp với dữ liệu của bạn
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            // Chuyển đổi và định dạng cho borrowDate
            if (bookIssue.getDueDate()!= null) {
                LocalDate createdDate = LocalDate.parse(bookIssue.getDueDate(), inputFormatter);
                borrowBookDateLabel.setText(createdDate.format(outputFormatter));
            } else {
                borrowBookDateLabel.setText(""); // Hoặc giá trị mặc định khác
            }

        } catch (DateTimeParseException e) {
            e.printStackTrace();
            // Xử lý lỗi nếu cần (ví dụ: hiển thị thông báo lỗi cho người dùng)
            borrowBookDateLabel.setText(""); // Hoặc giá trị mặc định khác
        }

        try {
            // Chuyển đổi và định dạng cho borrowDate
            if (bookIssue.getDueDate()!= null) {
                LocalDate createdDate = LocalDate.parse(bookIssue.getDueDate(), inputFormatter);
                returnBookDateLabel.setText(createdDate.format(outputFormatter));
            } else {
                returnBookDateLabel.setText(""); // Hoặc giá trị mặc định khác
            }

        } catch (DateTimeParseException e) {
            e.printStackTrace();
            // Xử lý lỗi nếu cần (ví dụ: hiển thị thông báo lỗi cho người dùng)
            returnBookDateLabel.setText(""); // Hoặc giá trị mặc định khác
        }

        conditionLabel.setText(bookIssue.getStatus()+"");
        if(bookIssue.getStatus().equals(BookIssueStatus.LOST)) {
            conditionLabel.setStyle(("-fx-text-fill: red;"));
        }
        else if (bookIssue.getStatus().equals(BookIssueStatus.BORROWED)) {
            conditionLabel.setStyle(("-fx-text-fill: blue;"));
        } else {
            conditionLabel.setStyle(("-fx-text-fill: green;"));
        }


    }

}
