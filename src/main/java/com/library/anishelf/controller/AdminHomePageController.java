package com.library.anishelf.controller;

import com.library.anishelf.dao.BookDAO;
import com.library.anishelf.dao.BookIssueDAO;
import com.library.anishelf.model.Book;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AdminHomePageController extends BasicController {

    @FXML
    private ChoiceBox<String> dateReportBox;
    @FXML
    private ChoiceBox<Integer> dateReportBox2;

    @FXML
    private LineChart<String, Number> lineChart;

    @FXML
    private HBox topBookHbox;

    @FXML
    private Label totalBookLabel;

    @FXML
    private Label totalBorrowLabel;

    @FXML
    private Label totalIssueLabel;

    @FXML
    private Label totalReaderLabel;

    @FXML
    private ScrollPane scrollPane;

    private BookPageController bookPageController;
    private static AdminNavBarController adminNavBarController;

    @FXML
    public void initialize() {
        dateReportBox.getItems().addAll("Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
                "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12");
        dateReportBox2.getItems().addAll(2024);
        dateReportBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(dateReportBox2.getValue() != null) {
                loadLineChartDataForMonth(); // Gọi lại để tải dữ liệu mới
            }
        });
        dateReportBox2.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(dateReportBox.getValue() != null) {
                loadLineChartDataForMonth(); // Gọi lại để tải dữ liệu mới
            }
        });
    }

    public void setAdminBookPageController(BookPageController bookPageController) {
        this.bookPageController = bookPageController;
    }

    public void setAdminMenuController(AdminNavBarController adminNavBarController) {
        this.adminNavBarController = adminNavBarController;
        loadTopBookHbox();
    }

    public void setTotalBookLabel(String totalBookLabel) {
        this.totalBookLabel.setText(totalBookLabel);
    }

    public void setTotalBorrowLabel(String totalBorrowLabel) {
        this.totalBorrowLabel.setText(totalBorrowLabel);
    }

    public void setTotalIssueLabel(String totalIssueLabel) {
        this.totalIssueLabel.setText(totalIssueLabel);
    }

    public void setTotalReaderLabel(String totalReaderLabel) {
        this.totalReaderLabel.setText(totalReaderLabel);
    }

    private void loadTopBookHbox() {
        topBookHbox.getChildren().clear();
        try {
            List<Book> highRankBooks = BookDAO.getInstance().findAll();

            sortBooks(highRankBooks, (book1, book2) -> Integer.compare(book2.getRate(), book1.getRate()));
            int count = 0;
            for (Book book : highRankBooks) {
                count++;
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TopBookCard.fxml"));
                VBox row = loader.load();

                AdminDashboardBookCardController rowController = loader.getController();
                rowController.setAdminMenuController(adminNavBarController);
                rowController.setMainController(bookPageController);
                rowController.setItem(book);

                topBookHbox.getChildren().add(row);
                if (count == 11) {
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sortBooks(List<Book> books, Comparator<Book> comparator) {
        Collections.sort(books, comparator);
    }

    private void loadLineChartDataForMonth() {
        lineChart.getData().clear(); // Xoá dữ liệu cũ
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Số lượng theo thời gian của tháng");

        // Lấy tháng được chọn
        String selectedMonth = dateReportBox.getSelectionModel().getSelectedItem();
        if (selectedMonth != null) {
            int monthIndex = dateReportBox.getSelectionModel().getSelectedIndex() + 1; // Tháng từ 1 đến 12
            int currentYear = dateReportBox2.getValue();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


            // Tính toán số lượng sách mượn cho từng ngày trong tháng đã chọn
            try {
                Map<String, Object> findCriteria = new HashMap<>();
                // Lấy số ngày tối đa trong tháng đã chọn
                LocalDate firstDayOfMonth = LocalDate.of(currentYear, monthIndex, 1);
                int maxDaysInMonth = firstDayOfMonth.lengthOfMonth(); // Lấy số ngày trong tháng

                for (int day = 1; day <= maxDaysInMonth; day++) {
                    LocalDate date = LocalDate.of(currentYear, monthIndex, day);
                    String formattedDate = date.format(formatter);

                    // Đưa vào criteria tìm kiếm
                    findCriteria.put("creation_date", formattedDate);
                    int numberBorrowedToday = BookIssueDAO.getInstance().findByCriteria(findCriteria).size();
                    series1.getData().add(new XYChart.Data<>(String.valueOf(day), numberBorrowedToday));
                    findCriteria.clear();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            lineChart.getData().add(series1); // Thêm chuỗi dữ liệu vào biểu đồ
            lineChart.setTitle("Số lượng sách mượn trong tháng " + selectedMonth);
            lineChart.getXAxis().setLabel("Ngày trong tháng");
            lineChart.getYAxis().setLabel("Số lượng sách mượn");
        }
    }

}
