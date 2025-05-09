package com.library.anishelf.controller;

import com.library.anishelf.model.BookIssue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class BorrowedBooksTableRowController extends BaseRowController<BookIssue, BorrowedBookPageController> {

    @FXML
    private Label barCodeLabel;

    @FXML
    private Label bookNameLabel;

    @FXML
    private Label borrowDateLabel;

    @FXML
    private Button editButton;

    @FXML
    private Label memberIDLabel;

    @FXML
    private Label memberNameLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private HBox mainRowHbox;

    @Override
    protected void updateRowDisplay() {
        memberIDLabel.setText(String.valueOf(item.getMember().getPerson().getId()));
        memberNameLabel.setText(item.getMember().getPerson().getLastName() + " " + item.getMember().getPerson().getFirstName());
        bookNameLabel.setText(item.getBookItem().getTitle());
        barCodeLabel.setText(item.getBookItem().getBookBarcode() + "");
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // hoặc định dạng phù hợp với dữ liệu của bạn
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            // Chuyển đổi và định dạng cho borrowDate
            if (item.getIssueDate() != null) {
                LocalDate createdDate = LocalDate.parse(item.getIssueDate(), inputFormatter);
                borrowDateLabel.setText(createdDate.format(outputFormatter));
            } else {
                borrowDateLabel.setText(""); // Hoặc giá trị mặc định khác
            }

        } catch (DateTimeParseException e) {
            e.printStackTrace();
            // Xử lý lỗi nếu cần (ví dụ: hiển thị thông báo lỗi cho người dùng)
            borrowDateLabel.setText(""); // Hoặc giá trị mặc định khác
        }
        statusLabel.setText(item.getStatus().toString());
        if(statusLabel.getText().equals("LOST")) {
            statusLabel.setStyle("-fx-text-fill: red;");
        } else if (statusLabel.getText().equals("BORROWED")) {
            statusLabel.setStyle("-fx-text-fill: blue;");
        } else if (statusLabel.getText().equals("RETURNED")) {
            statusLabel.setStyle("-fx-text-fill: green;");
        }
    }

}
