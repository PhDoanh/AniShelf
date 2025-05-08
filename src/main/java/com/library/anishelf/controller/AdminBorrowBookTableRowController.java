package com.library.anishelf.controller;

import com.library.anishelf.model.BookIssue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AdminBorrowBookTableRowController extends BaseRowController<BookIssue, AdminBorrowBookPageController> {

    @FXML
    private Label barCodeBookLabel;

    @FXML
    private Label nameOfBookLabel;

    @FXML
    private Label borrowBookDateLabel;

    @FXML
    private Button edittttButton;

    @FXML
    private Label memberIDLabel;

    @FXML
    private Label nameOfMemberLabel;

    @FXML
    private Label ConditionLabel;

    @FXML
    private HBox mainRowContainer;

    @Override
    protected void updateRowOfTableDisplay() {
        memberIDLabel.setText(String.valueOf(item.getMember().getPerson().getId()));
        nameOfMemberLabel.setText(item.getMember().getPerson().getLastName() + " " + item.getMember().getPerson().getFirstName());
        nameOfBookLabel.setText(item.getBookItem().getTitle());
        barCodeBookLabel.setText(item.getBookItem().getBarcode() + "");
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // hoặc định dạng phù hợp với dữ liệu của bạn
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            // Chuyển đổi và định dạng cho borrowDate
            if (item.getCreatedDate() != null) {
                LocalDate createdDate = LocalDate.parse(item.getCreatedDate(), inputFormatter);
                borrowBookDateLabel.setText(createdDate.format(outputFormatter));
            } else {
                borrowBookDateLabel.setText(""); // Hoặc giá trị mặc định khác
            }

        } catch (DateTimeParseException e) {
            e.printStackTrace();
            // Xử lý lỗi nếu cần (ví dụ: hiển thị thông báo lỗi cho người dùng)
            borrowBookDateLabel.setText(""); // Hoặc giá trị mặc định khác
        }
        ConditionLabel.setText(item.getStatus().toString());
        if(ConditionLabel.getText().equals("LOST")) {
            ConditionLabel.setStyle("-fx-text-fill: red;");
        } else if (ConditionLabel.getText().equals("BORROWED")) {
            ConditionLabel.setStyle("-fx-text-fill: blue;");
        } else if (ConditionLabel.getText().equals("RETURNED")) {
            ConditionLabel.setStyle("-fx-text-fill: green;");
        }
    }

}
