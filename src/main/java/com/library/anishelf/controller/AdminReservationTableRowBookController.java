package com.library.anishelf.controller;

import com.library.anishelf.model.BookReservation;
import com.library.anishelf.model.enums.BookReservationStatus;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AdminReservationTableRowBookController extends BaseRowController<BookReservation, AdminReservationBookPageController> {

    @FXML
    private Label ISBNLabel;

    @FXML
    private Label bookBorrowNameLabel;

    @FXML
    private Label borrowDateBookLabel;

    @FXML
    private Button editReservationButton;

    @FXML
    private Label memberIDReservationLabel;

    @FXML
    private Label memberNameReservationLabel;

    @FXML
    private Label conditionLabel;

    @FXML
    private HBox mainRowContainer;

    @Override
    protected void updateRowOfTableDisplay() {
        memberIDReservationLabel.setText(String.valueOf(item.getMember().getPerson().getId()));
        memberNameReservationLabel.setText(item.getMember().getPerson().getLastName() + " " + item.getMember().getPerson().getFirstName());
        bookBorrowNameLabel.setText(item.getBookItem().getTitle());
        ISBNLabel.setText(item.getBookItem().getBarcode() + "");
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // hoặc định dạng phù hợp với dữ liệu của bạn
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            // Chuyển đổi và định dạng cho borrowDate
            if (item.getCreatedDate() != null) {
                LocalDate createdDate = LocalDate.parse(item.getCreatedDate(), inputFormatter);
                borrowDateBookLabel.setText(createdDate.format(outputFormatter));
            } else {
                borrowDateBookLabel.setText(""); // Hoặc giá trị mặc định khác
            }

        } catch (DateTimeParseException e) {
            e.printStackTrace();
            // Xử lý lỗi nếu cần (ví dụ: hiển thị thông báo lỗi cho người dùng)
            borrowDateBookLabel.setText(""); // Hoặc giá trị mặc định khác
        }
        conditionLabel.setText(item.getStatus().toString());
        if(item.getStatus().equals(BookReservationStatus.CANCELED)) {
            conditionLabel.setStyle("-fx-text-fill: red;");
        } else if(item.getStatus().equals(BookReservationStatus.WAITING)) {
            conditionLabel.setStyle("-fx-text-fill: blue");
        } else if (item.getStatus().equals(BookReservationStatus.COMPLETED)) {
            conditionLabel.setStyle("-fx-text-fill:green;");
        }
    }

}
