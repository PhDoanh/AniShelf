package com.library.anishelf.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class AdminSideMenuController extends BasicController {
    @FXML
    private Button addABookButton;

    @FXML
    private Button addANewBookButton;

    @FXML
    private Button addNewBorrowBookButtonAction;

    @FXML
    private Button addANewMemberButton;

    @FXML
    private Button addANewReservationBookButton;

    @FXML
    private AnchorPane addTableContainer;

    @FXML
    private Button bookItemManagementButton;

    @FXML
    private ImageView bookManagementLogoView;

    @FXML
    private BorderPane borderContainer;

    @FXML
    private Button borrowBookButton;

    @FXML
    private ImageView borrowLogoView;

    @FXML
    private Button dashboardButton;

    @FXML
    private ImageView dashboardLogoView;

    @FXML
    private HBox hBoxMainContainer;

    @FXML
    private ImageView issueLogoView;

    @FXML
    private Button issuesBookButton;

    @FXML
    private Button logoutAppButton;

    @FXML
    private ImageView logoutLogoView;

    @FXML
    private ImageView logoView;

    @FXML
    private AnchorPane mainContainer;

    @FXML
    private VBox menuBarForAdmin;

    @FXML
    private ImageView openMenuIconView;

    @FXML
    private Button userManagementButtonForAdmin;

    @FXML
    private ImageView userManagementLogoView;

    @FXML
    private Button reservationManagementButton;

    @FXML
    private Button settingAppButton;

    @FXML
    private ImageView settingLogoView;

    @FXML
    private AnchorPane overlayForMenu;


    private boolean isSideMenuExpanded = false;
    private static Image collapseMenuIcon = new Image(BasicController.class.getResource("/image/icon/minimize.png").toExternalForm());;
    private static Image expandMenuIcon = new Image(BasicController.class.getResource("/image/icon/maximize.png").toExternalForm());

    private AdminMainDashboardController adminMainDashboardController;
    private AdminBookItemPageController adminBookItemPageController;
    private AdminUserPageController adminUserPageController;
    private AdminBorrowPageController adminBorrowPageController;
    private AdminReservationPageController adminReservationPageController;
    private AdminIssueMainPageController adminIssueMainPageController;

    private int currentAdminId;

    public void setCurrentAdminId(int AdminID) {
        this.currentAdminId = AdminID;
    }

    private Button activeMenuButton = null;

    public void initialize() throws IOException {
       // titlePageStack.push("Dashboard");
        setTitleLabel();
        adminBookItemPageController = bookPagePaneLoader.getController();
        adminUserPageController = userPagePaneLoader.getController();
        adminBorrowPageController = borrowPagePaneLoader.getController();
        adminReservationPageController = reservationPagePaneLoader.getController();
        adminIssueMainPageController = issuePagePaneLoader.getController();
        adminMainDashboardController = dashboardLoader.getController();
        adminMainDashboardController.setAdminMainMenuController(this);

        switchPage(dashboardPane);
        hideButtonTextsView();
        openMenuIconView.setOnMouseClicked(event -> toggleMenuList());
        hanleAddTablePaneClose();
        logoView.setOnMouseClicked(event -> {
            handleDashboardButtonClick(new ActionEvent());});
    }

    private void hanleAddTablePaneClose() {
        // Lắng nghe sự thay đổi của Scene
        borderContainer.sceneProperty().addListener((observable, oldScene, newScene) -> {
            newScene.setOnMouseClicked(event -> {
                if(addTableContainer.isVisible()) {
                    javafx.geometry.Bounds bounds = addTableContainer.localToScene(addTableContainer.getBoundsInLocal());

                    if (!bounds.contains(event.getSceneX(), event.getSceneY())) {
                        // Ẩn addTableContainer nếu click ngoài
                        addTableContainer.setVisible(false);
                    }
                }
            });
        });
    }

    @FXML
    void handleAppLogoClick(MouseEvent event) {
    }


    @FXML
    void handleAddNewBookButtonClick(ActionEvent event) {
        while (!getTitlePageStack().isEmpty()) getTitlePageStack().pop();
        getTitlePageStack().push("Quản lý sách");
        switchPage(bookPagePane);
        setActiveButtonView(bookItemManagementButton);
        adminBookItemPageController.loadAddPane();
        addTableContainer.setVisible(false);
    }

    @FXML
    void handleAddNewBorrowButtonClick(ActionEvent event) {
        while (!getTitlePageStack().isEmpty()) getTitlePageStack().pop();
        getTitlePageStack().push("Quản lý mượn sách");
        switchPage(borrowPagePane);
        setActiveButtonView(borrowBookButton);
        adminBorrowPageController.loadAddPane();
        addTableContainer.setVisible(false);
    }

    @FXML
    void handleAddNewMemberButtonClick(ActionEvent event) {
        while (!getTitlePageStack().isEmpty()) getTitlePageStack().pop();
        getTitlePageStack().push("Quản lý độc giả");
        switchPage(userPagePane);
        setActiveButtonView(userManagementButtonForAdmin);
        adminUserPageController.loadAddPane();
        addTableContainer.setVisible(false);
    }
    @FXML
    void handleAddNewReservationButtonClick(ActionEvent event) {
        while (!getTitlePageStack().isEmpty()) getTitlePageStack().pop();
        getTitlePageStack().push("Quản lý đặt trước sách");
        switchPage(reservationPagePane);
        setActiveButtonView(reservationManagementButton);
        adminReservationPageController.loadAddPane();
        addTableContainer.setVisible(false);
    }

    @FXML
     void handleDashboardButtonClick(ActionEvent event) {
        while (!getTitlePageStack().isEmpty()) getTitlePageStack().pop();
        getTitlePageStack().push("Dashboard");
        if(addTableContainer.isVisible()) {
            addTableContainer.setVisible(false);
        }
        setActiveButtonView(dashboardButton);

        adminMainDashboardController.fetchRecentIssuel();
        switchPage(dashboardPane);
    }

    @FXML
    void handleBorrowButtonClick(ActionEvent event) {
        while (!getTitlePageStack().isEmpty()) getTitlePageStack().pop();
        getTitlePageStack().push("Quản lý mượn sách");
        if(addTableContainer.isVisible()) {
            addTableContainer.setVisible(false);
        }
        setActiveButtonView(borrowBookButton);
        adminBorrowPageController.initalPage();
        switchPage(borrowPagePane);
    }
    @FXML
    void handleReservationButtonClick(ActionEvent event) {
        while (!getTitlePageStack().isEmpty()) getTitlePageStack().pop();
        getTitlePageStack().push("Quản lý đặt trước sách");
        if(addTableContainer.isVisible()) {
            addTableContainer.setVisible(false);
        }
        setActiveButtonView(reservationManagementButton);
        adminReservationPageController.initalPage();
        switchPage(reservationPagePane);
    }

    @FXML
    void handleBookManagmentButtonClick(ActionEvent event) {
        while (!getTitlePageStack().isEmpty()) getTitlePageStack().pop();
        getTitlePageStack().push("Quản lý sách");
        if(addTableContainer.isVisible()) {
            addTableContainer.setVisible(false);
        }
        setActiveButtonView(bookItemManagementButton);
        adminBookItemPageController.initalPage();
        switchPage(bookPagePane);
    }

    @FXML
    void handleReaderManagementButtonClick(ActionEvent event) {
        while (!getTitlePageStack().isEmpty()) getTitlePageStack().pop();
        getTitlePageStack().push("Quản lý độc giả");
        if(addTableContainer.isVisible()) {
            addTableContainer.setVisible(false);
        }
        setActiveButtonView(userManagementButtonForAdmin);
        adminUserPageController.loadData();
        switchPage(userPagePane);
    }

    @FXML
    void handleIssuesButtonClick(ActionEvent event) {
        while (!getTitlePageStack().isEmpty()) getTitlePageStack().pop();
        getTitlePageStack().push("Report");
        if(addTableContainer.isVisible()) {
            addTableContainer.setVisible(false);
        }
        setActiveButtonView(issuesBookButton);
        adminIssueMainPageController.initalPage();
        switchPage(issuePagePane);
    }

    @FXML
    void handleLogoutButtonClick(ActionEvent event) {
        boolean confirmYes = CustomerAlter.showAlter("Bạn muốn thoát à?");
        if (confirmYes) {
            try {
                Stage stage = (Stage) mainContainer.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("/view/UserLogin.fxml"));
                stage.setWidth(stage.getWidth());
                stage.setHeight(stage.getHeight());
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @FXML
    void handleSettingButtonClick(ActionEvent event) {
        if(addTableContainer.isVisible()) {
            addTableContainer.setVisible(false);
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(SETTING_FXML));
            Parent root = loader.load();
            AdminSettingController controller = loader.getController();
            controller.reset();
            controller.setAdminID(this.currentAdminId);
            Stage stage = new Stage();
            stage.setTitle("AppInfo");
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            overlayForMenu.setVisible(true);
            stage.showAndWait();
            overlayForMenu.setVisible(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleAddButtonClick(ActionEvent event) {
        addTableContainer.setVisible(!addTableContainer.isVisible());
    }

    private void toggleMenuList() {
        if (!isSideMenuExpanded) {
            menuBarForAdmin.setMinWidth(238);
            menuBarForAdmin.setMaxWidth(238);
            addABookButton.setMaxWidth(100);
            addABookButton.setMinWidth(100);
            openMenuIconView.setImage(collapseMenuIcon);
            showButtonTextsView();
        } else {
            menuBarForAdmin.setMinWidth(62);
            menuBarForAdmin.setMaxWidth(62);
            addABookButton.setMinWidth(54);
            addABookButton.setMaxWidth(54);

            openMenuIconView.setImage(expandMenuIcon);
            hideButtonTextsView();
        }
        isSideMenuExpanded = !isSideMenuExpanded;
    }

    private void showButtonTextsView() {
        addABookButton.setText("New");
        dashboardButton.setText("Dashboard");
        bookItemManagementButton.setText("Quản lý sách");
        reservationManagementButton.setText("Đặt sách");
        borrowBookButton.setText("Mượn trả");
        userManagementButtonForAdmin.setText("Quản lý độc giả");
        issuesBookButton.setText("Sự cố");
        settingAppButton.setText("Cài đặt");
        logoutAppButton.setText("Đăng xuất");
    }

    private void hideButtonTextsView() {
        addABookButton.setText("");
        dashboardButton.setText("");
        bookItemManagementButton.setText("");
        borrowBookButton.setText("");
        userManagementButtonForAdmin.setText("");
        reservationManagementButton.setText("");
        issuesBookButton.setText("");
        settingAppButton.setText("");
        logoutAppButton.setText("");
    }

    private void switchPage(Node e) {
        mainContainer.getChildren().clear();
        mainContainer.getChildren().add(e);
    }

    public void setTitleLabel() {
      //  titleLabel.updateDisplayText(getAllTitles());
    }

    private void setActiveButtonView(Button button) {
        if (activeMenuButton != null) {
            // Reset lại kiểu dáng của nút trước đó
            activeMenuButton.setStyle("");
        }

        // Gán nút hiện tại vào trạng thái đang hoạt động
        activeMenuButton = button;

        // Đặt kiểu dáng in đậm màu cho nút đang được chọn
        activeMenuButton.setStyle("-fx-font-weight: bold; -fx-background-color: #E0E0E0;"); // Kiểu CSS
    }


}
