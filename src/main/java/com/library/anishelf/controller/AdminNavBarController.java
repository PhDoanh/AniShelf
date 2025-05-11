package com.library.anishelf.controller;

import com.library.anishelf.util.NotificationManagerUtil;
import com.library.anishelf.util.ThemeManagerUtil;
import com.library.anishelf.util.NavHistoryManagerUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;

public class AdminNavBarController extends BasicController {
    // Regular navigation buttons
    @FXML
    private Button dashboardButton;
    
    @FXML
    private Button bookManagementButton;
    
    @FXML
    private Button readerManagementButton;
    
    @FXML
    private Button borrowButton;
    
    @FXML
    private Button reservationButton;
    
    @FXML
    private Button settingButton;
    
    @FXML
    private Button logoutButton;
    
    // Back và Next buttons
    @FXML
    private Button backButton;
    
    @FXML
    private Button nextButton;
    
    // Add dropdown menu button and its menu items
    @FXML
    private MenuButton addButton;
    
    @FXML
    private MenuItem addNewBookMenuItem;
    
    @FXML
    private MenuItem addNewMemberMenuItem;
    
    @FXML
    private MenuItem addNewBorrowMenuItem;
    
    @FXML
    private MenuItem addNewReservationMenuItem;
    
    // Icons
    @FXML
    private FontIcon settingIcon;
    
    @FXML
    private FontIcon logoutIcon;

    // Other UI components
    @FXML
    private Label titleLabel;
    
    @FXML
    private ImageView logo;
    
    @FXML
    private BorderPane borderPane;
    
    @FXML
    private AnchorPane mainPane;
    
    @FXML
    private HBox navigationBar;
    
    @FXML
    private AnchorPane overlay;

    // Controllers for different pages
    private AdminHomePageController adminHomePageController;
    private BookPageController bookPageController;
    private UsersPageController usersPageController;
    private BorrowedBookPageController borrowedBookPageController;
    private ReservedBooksPageController reservedBooksPageController;

    private int AdminID;
    
    // Array of navigation buttons for highlighting active section
    private Button[] navButtons;
    
    // Manager lịch sử điều hướng
    private NavHistoryManagerUtil historyManager = NavHistoryManagerUtil.getInstance();
    
    // Các đường dẫn FXML
    private static final String DASHBOARD_FXML = "/view/AdminHomePage.fxml";
    private static final String BOOK_MANAGEMENT_FXML = "/view/BookPage.fxml";
    private static final String USER_MANAGEMENT_FXML = "/view/UsersPage.fxml";
    private static final String BORROW_MANAGEMENT_FXML = "/view/BorrowedBookPage.fxml";
    private static final String RESERVATION_MANAGEMENT_FXML = "/view/ReservedBooksPage.fxml";
    
    public void setAdminID(int AdminID) {
        this.AdminID = AdminID;
    }

    public void initialize() throws IOException {
        // Load page controllers
        bookPageController = bookPagePaneLoader.getController();
        usersPageController = userPagePaneLoader.getController();
        borrowedBookPageController = borrowPagePaneLoader.getController();
        reservedBooksPageController = reservationPagePaneLoader.getController();
        adminHomePageController = dashboardLoader.getController();
        adminHomePageController.setAdminMenuController(this);
        
        // Initialize navigation buttons array for active button highlighting
        navButtons = new Button[]{dashboardButton, bookManagementButton, readerManagementButton, 
                                 borrowButton, reservationButton};
        
        // Set title and open dashboard by default
        while (!getTitlePageStack().isEmpty()) getTitlePageStack().pop();
        getTitlePageStack().push("Dashboard");
        setTitleLabel();
        openPage(dashboardPane);
        setActiveButton(dashboardButton);
        
        // Set logo click handler
        logo.setOnMouseClicked(event -> {onDashboardButtonAction(new ActionEvent());});
        
        // Apply theme to navigation buttons
        ThemeManagerUtil.getInstance().addPane(borderPane);
        
        // Thêm trang dashboard vào lịch sử điều hướng
        historyManager.addToHistory(DASHBOARD_FXML);
        
        // Cập nhật trạng thái ban đầu cho nút back và next
        updateBackNextButtonState();
    }

    /**
     * Cập nhật trạng thái của nút back và next dựa trên lịch sử
     */
    private void updateBackNextButtonState() {
        backButton.setDisable(!historyManager.canGoBack());
        nextButton.setDisable(!historyManager.canGoForward());
    }
    
    /**
     * Xử lý sự kiện khi nhấn nút Back (Quay lại trang trước)
     */
    @FXML
    void onBackButtonAction(ActionEvent event) {
        if (historyManager.canGoBack()) {
            String previousPage = historyManager.goBack();
            navigateToPage(previousPage);
            updateBackNextButtonState();
        }
    }
    
    /**
     * Xử lý sự kiện khi nhấn nút Next (Đi đến trang kế tiếp)
     */
    @FXML
    void onNextButtonAction(ActionEvent event) {
        if (historyManager.canGoForward()) {
            String nextPage = historyManager.goForward();
            navigateToPage(nextPage);
            updateBackNextButtonState();
        }
    }
    
    /**
     * Điều hướng đến một trang cụ thể
     * @param pagePath Đường dẫn đến trang
     */
    private void navigateToPage(String pagePath) {
        if (pagePath.equals(DASHBOARD_FXML)) {
            openPage(dashboardPane);
            setActiveButton(dashboardButton);
        } else if (pagePath.equals(BOOK_MANAGEMENT_FXML)) {
            bookPageController.startPage();
            openPage(bookPagePane);
            setActiveButton(bookManagementButton);
        } else if (pagePath.equals(USER_MANAGEMENT_FXML)) {
            usersPageController.loadData();
            openPage(userPagePane);
            setActiveButton(readerManagementButton);
        } else if (pagePath.equals(BORROW_MANAGEMENT_FXML)) {
            borrowedBookPageController.startPage();
            openPage(borrowPagePane);
            setActiveButton(borrowButton);
        } else if (pagePath.equals(RESERVATION_MANAGEMENT_FXML)) {
            reservedBooksPageController.startPage();
            openPage(reservationPagePane);
            setActiveButton(reservationButton);
        }
    }

    @FXML
    void onLogoAction(MouseEvent event) {
        onDashboardButtonAction(new ActionEvent());
    }

    @FXML
    void onAddNewBookButtonAction(ActionEvent event) {
        while (!getTitlePageStack().isEmpty()) getTitlePageStack().pop();
        getTitlePageStack().push("Quản lý truyện");
        openPage(bookPagePane);
        setActiveButton(bookManagementButton);
        bookPageController.loadAddPane();
        
        // Thêm vào lịch sử điều hướng
        historyManager.addToHistory(BOOK_MANAGEMENT_FXML);
        updateBackNextButtonState();
    }

    @FXML
    void onAddNewBorrowButtonAction(ActionEvent event) {
        while (!getTitlePageStack().isEmpty()) getTitlePageStack().pop();
        getTitlePageStack().push("Quản lý mượn truyện");
        openPage(borrowPagePane);
        setActiveButton(borrowButton);
        borrowedBookPageController.loadAddPane();
        
        // Thêm vào lịch sử điều hướng
        historyManager.addToHistory(BORROW_MANAGEMENT_FXML);
        updateBackNextButtonState();
    }

    @FXML
    void onAddNewMemberButtonAction(ActionEvent event) {
        while (!getTitlePageStack().isEmpty()) getTitlePageStack().pop();
        getTitlePageStack().push("Quản lý độc giả");
        openPage(userPagePane);
        setActiveButton(readerManagementButton);
        usersPageController.loadAddPane();
        
        // Thêm vào lịch sử điều hướng
        historyManager.addToHistory(USER_MANAGEMENT_FXML);
        updateBackNextButtonState();
    }
    
    @FXML
    void onAddNewReservationButtonAction(ActionEvent event) {
        while (!getTitlePageStack().isEmpty()) getTitlePageStack().pop();
        getTitlePageStack().push("Quản lý đặt trước truyện");
        openPage(reservationPagePane);
        setActiveButton(reservationButton);
        reservedBooksPageController.loadAddPane();
        
        // Thêm vào lịch sử điều hướng
        historyManager.addToHistory(RESERVATION_MANAGEMENT_FXML);
        updateBackNextButtonState();
    }

    @FXML
    void onDashboardButtonAction(ActionEvent event) {
        while (!getTitlePageStack().isEmpty()) getTitlePageStack().pop();
        getTitlePageStack().push("Dashboard");
        setActiveButton(dashboardButton);
        openPage(dashboardPane);
        
        // Thêm vào lịch sử điều hướng
        historyManager.addToHistory(DASHBOARD_FXML);
        updateBackNextButtonState();
    }

    @FXML
    void onBorrowButtonAction(ActionEvent event) {
        while (!getTitlePageStack().isEmpty()) getTitlePageStack().pop();
        getTitlePageStack().push("Quản lý mượn truyện");
        setActiveButton(borrowButton);
        borrowedBookPageController.startPage();
        openPage(borrowPagePane);
        
        // Thêm vào lịch sử điều hướng
        historyManager.addToHistory(BORROW_MANAGEMENT_FXML);
        updateBackNextButtonState();
    }
    
    @FXML
    void onReservationButtonAction(ActionEvent event) {
        while (!getTitlePageStack().isEmpty()) getTitlePageStack().pop();
        getTitlePageStack().push("Quản lý đặt trước truyện");
        setActiveButton(reservationButton);
        reservedBooksPageController.startPage();
        openPage(reservationPagePane);
        
        // Thêm vào lịch sử điều hướng
        historyManager.addToHistory(RESERVATION_MANAGEMENT_FXML);
        updateBackNextButtonState();
    }

    @FXML
    void onBookManagmentButtonAction(ActionEvent event) {
        while (!getTitlePageStack().isEmpty()) getTitlePageStack().pop();
        getTitlePageStack().push("Quản lý truyện");
        setActiveButton(bookManagementButton);
        bookPageController.startPage();
        openPage(bookPagePane);
        
        // Thêm vào lịch sử điều hướng
        historyManager.addToHistory(BOOK_MANAGEMENT_FXML);
        updateBackNextButtonState();
    }

    @FXML
    void onReaderManagementButtonAction(ActionEvent event) {
        while (!getTitlePageStack().isEmpty()) getTitlePageStack().pop();
        getTitlePageStack().push("Quản lý độc giả");
        setActiveButton(readerManagementButton);
        usersPageController.loadData();
        openPage(userPagePane);
        
        // Thêm vào lịch sử điều hướng
        historyManager.addToHistory(USER_MANAGEMENT_FXML);
        updateBackNextButtonState();
    }

    @FXML
    void onLogoutButtonAction(ActionEvent event) {
        NotificationManagerUtil.showConfirmation("Bạn có chắc muốn đăng xuất?", confirmed -> {
            if (confirmed) {
                try {
                    // Xóa lịch sử điều hướng khi đăng xuất
                    historyManager.clearHistory();
                    
                    Stage stage = (Stage) mainPane.getScene().getWindow();
                    Parent root = FXMLLoader.load(getClass().getResource("/view/UserLoginPage.fxml"));
                    stage.setWidth(stage.getWidth());
                    stage.setHeight(stage.getHeight());
                    stage.setScene(new Scene(root));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    void onSettingButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(SETTING_FXML));
            Parent root = loader.load();
            AdminSettingPageController controller = loader.getController();
            controller.reset();
            controller.setAdminID(this.AdminID);
            Stage stage = new Stage();
            stage.setTitle("Cài đặt");
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            overlay.setVisible(true);
            stage.showAndWait();
            overlay.setVisible(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openPage(Node e) {
        mainPane.getChildren().clear();
        mainPane.getChildren().add(e);
    }

    public void setTitleLabel() {
        if (!getTitlePageStack().isEmpty()) {
            titleLabel.setText(getTitlePageStack().peek());
        } else {
            titleLabel.setText("Dashboard");
        }
    }

    /**
     * Thiết lập nút điều hướng đang active
     * @param button Nút sẽ được đánh dấu là active
     */
    private void setActiveButton(Button button) {
        // Reset tất cả các nút về trạng thái bình thường
        for (Button navButton : navButtons) {
            navButton.getStyleClass().remove("active-nav-button");
            navButton.getStyleClass().add("nav-button");
        }
        
        // Đánh dấu nút được chọn
        button.getStyleClass().remove("nav-button");
        button.getStyleClass().add("active-nav-button");
        
        // Cập nhật tiêu đề tương ứng
        setTitleLabel();
    }
    
    /**
     * Cho AdminDashboardBookCardController và các controller khác gọi để thêm trang vào lịch sử
     * @param pagePath Đường dẫn đến trang
     */
    public void addPageToHistory(String pagePath) {
        historyManager.addToHistory(pagePath);
        updateBackNextButtonState();
    }
}
