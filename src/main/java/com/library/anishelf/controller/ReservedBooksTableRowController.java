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

/**
 * The type Reserved books table row controller.
 */
public class ReservedBooksTableRowController extends BaseRowController<BookReservation, ReservedBooksPageController> {

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
            if (item.getReservationDate() != null) {
                LocalDate createdDate = LocalDate.parse(item.getReservationDate(), inputFormatter);
                borrowDateLabel.setText(createdDate.format(outputFormatter));
            } else {
                borrowDateLabel.setText(""); // Hoặc giá trị mặc định khác
            }

        } catch (DateTimeParseException e) {
            e.printStackTrace();
            // Xử lý lỗi nếu cần (ví dụ: hiển thị thông báo lỗi cho người dùng)
            borrowDateLabel.setText(""); // Hoặc giá trị mặc định khác
        }
        statusLabel.setText(item.getReservationStatus().toString());
        if (item.getReservationStatus().equals(BookReservationStatus.CANCELED)) {
            statusLabel.setStyle("-fx-text-fill: red;");
        } else if (item.getReservationStatus().equals(BookReservationStatus.WAITING)) {
            statusLabel.setStyle("-fx-text-fill: blue");
        } else if (item.getReservationStatus().equals(BookReservationStatus.COMPLETED)) {
            statusLabel.setStyle("-fx-text-fill:green;");
        }
    }

}
