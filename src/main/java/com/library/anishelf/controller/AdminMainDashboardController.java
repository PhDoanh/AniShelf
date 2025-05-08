package com.library.anishelf.controller;

import com.library.anishelf.dao.BookDAO;
import com.library.anishelf.dao.BookIssueDAO;
import com.library.anishelf.dao.ReportDAO;
import com.library.anishelf.model.Book;
import com.library.anishelf.model.Report;
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

public class AdminMainDashboardController extends BasicController {

    @FXML
    private ChoiceBox<String> dateReportBox1;
    @FXML
    private ChoiceBox<Integer> dateReportBox2;

    @FXML
    private LineChart<String, Number> lineChartTime;

    @FXML
    private VBox recentIssueBookContainer;

    @FXML
    private HBox topBookContainer;

    @FXML
    private Label totalOfBookLabel;

    @FXML
    private Label totalOfBorrowBookLabel;

    @FXML
    private Label totalOfIssueLabel;

    @FXML
    private Label totalOfReaderLabel;

    @FXML
    private ScrollPane scrollPaneContainer;

    private AdminBookItemPageController adminBookItemPageController;
    private static AdminIssueMainPageController adminIssueMainPageController;
    private static AdminMenuController adminMainMenuController;

    @FXML
    public void initialize() {
        dateReportBox1.getItems().addAll("Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
                "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12");
        dateReportBox2.getItems().addAll(2024);
        dateReportBox1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(dateReportBox2.getValue() != null) {
                fetchLineChartDataForMonth(); // Gọi lại để tải dữ liệu mới
            }
        });
        dateReportBox2.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(dateReportBox1.getValue() != null) {
                fetchLineChartDataForMonth(); // Gọi lại để tải dữ liệu mới
            }
        });
    }

    public void setAdminBookPageController(AdminBookItemPageController adminBookItemPageController) {
        this.adminBookItemPageController = adminBookItemPageController;
    }

    public void setAdminIssuePageController(AdminIssueMainPageController adminIssueMainPageController) {
        this.adminIssueMainPageController = adminIssueMainPageController;
    }

    public void setAdminMainMenuController(AdminMenuController adminMenuController) {
        this.adminMainMenuController = adminMenuController;
        fetchRecentIssuel();
        fetchTopBookHbox();
    }

    public void setTotalOfBookLabel(String totalOfBookLabel) {
        this.totalOfBookLabel.setText(totalOfBookLabel);
    }

    public void setTotalOfBorrowBookLabel(String totalOfBorrowBookLabel) {
        this.totalOfBorrowBookLabel.setText(totalOfBorrowBookLabel);
    }

    public void setTotalOfIssueLabel(String totalOfIssueLabel) {
        this.totalOfIssueLabel.setText(totalOfIssueLabel);
    }

    public void setTotalOfReaderLabel(String totalOfReaderLabel) {
        this.totalOfReaderLabel.setText(totalOfReaderLabel);
    }

    public void fetchRecentIssuel() {
        recentIssueBookContainer.getChildren().clear();
        Map<String, Object> findCriteria = new HashMap<>();
        findCriteria.put("ReportStatus", "PENDING");
        try {
            List<Report> itemsList = ReportDAO.getInstance().searchByCriteria(findCriteria);
            for (Report item : itemsList) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminRecentIssuelTableRow.fxml"));
                    HBox row = loader.load();

                    AdminRecentIssueController rowController = loader.getController();
                    if (adminIssueMainPageController == null) {
                        System.out.println("adminIssueMainPageController is null");
                    }
                    rowController.setMenuController(this.adminMainMenuController);
                    rowController.setMainController(adminIssueMainPageController);
                    rowController.setItem(item);

                    childFitWidthParent(row, scrollPaneContainer);
                    recentIssueBookContainer.getChildren().add(row);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchTopBookHbox() {
        topBookContainer.getChildren().clear();
        try {
            List<Book> highRankBooks = BookDAO.getInstance().selectAll();

            sortForBooks(highRankBooks, (book1, book2) -> Integer.compare(book2.getRate(), book1.getRate()));
            int count = 0;
            for (Book book : highRankBooks) {
                count++;
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TopBookCard.fxml"));
                VBox row = loader.load();

                AdminDashboardBookItemCardController rowController = loader.getController();
                rowController.setAdminMenuBookController(adminMainMenuController);
                rowController.setMainPageController(adminBookItemPageController);
                rowController.setBookItem(book);

                topBookContainer.getChildren().add(row);
                if (count == 11) {
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sortForBooks(List<Book> books, Comparator<Book> comparator) {
        Collections.sort(books, comparator);
    }

    private void fetchLineChartDataForMonth() {
        lineChartTime.getData().clear(); // Xoá dữ liệu cũ
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Số lượng theo thời gian của tháng");

        // Lấy tháng được chọn
        String selectedMonth = dateReportBox1.getSelectionModel().getSelectedItem();
        if (selectedMonth != null) {
            int monthIndex = dateReportBox1.getSelectionModel().getSelectedIndex() + 1; // Tháng từ 1 đến 12
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
                    int numberBorrowedToday = BookIssueDAO.getInstance().searchByCriteria(findCriteria).size();
                    series1.getData().add(new XYChart.Data<>(String.valueOf(day), numberBorrowedToday));
                    findCriteria.clear();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            lineChartTime.getData().add(series1); // Thêm chuỗi dữ liệu vào biểu đồ
            lineChartTime.setTitle("Số lượng sách mượn trong tháng " + selectedMonth);
            lineChartTime.getXAxis().setLabel("Ngày trong tháng");
            lineChartTime.getYAxis().setLabel("Số lượng sách mượn");
        }
    }

}
