package com.library.anishelf.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;

public class AdminNavBarController extends BasicController {
    @FXML
    private Button addButton;

    @FXML
    private Button addNewBookButton;

    @FXML
    private Button addNewBorrowButtonAction;

    @FXML
    private Button addNewMemberButton;

    @FXML
    private Button addNewReservationButton;

    @FXML
    private AnchorPane addTablePane;

    @FXML
    private Button bookManagementButton;

    @FXML
    private ImageView bookManagementLogo;

    @FXML
    private BorderPane borderPane;

    @FXML
    private Button borrowButton;

    @FXML
    private ImageView borrowLogo;

    @FXML
    private Button dashboardButton;

    @FXML
    private ImageView dashboardLogo;

    @FXML
    private HBox hBoxMain;

    @FXML
    private Button logoutButton;
    
    @FXML
    private FontIcon logoutIcon;

    @FXML
    private ImageView logo;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private VBox menuBar;

    @FXML
    private FontIcon openMenuIcon;

    @FXML
    private Button readerManagementButton;

    @FXML
    private ImageView readerManagementlogo;

    @FXML
    private Button reservationButton;

    @FXML
    private Button settingButton;
    
    @FXML
    private FontIcon settingIcon;

    @FXML
    private AnchorPane overlay;


    private boolean isMenuExpanded = false;

    private AdminHomePageController adminHomePageController;
    private BookPageController bookPageController;
    private UsersPageController usersPageController;
    private BorrowedBookPageController borrowedBookPageController;
    private ReservedBooksPageController reservedBooksPageController;

    private int AdminID;

    public void setAdminID(int AdminID) {
        this.AdminID = AdminID;
    }

    private Button currentActiveButton = null;

    public void initialize() throws IOException {
       // titlePageStack.push("Dashboard");
        setTitleLabel();
        bookPageController = bookPagePaneLoader.getController();
        usersPageController = userPagePaneLoader.getController();
        borrowedBookPageController = borrowPagePaneLoader.getController();
        reservedBooksPageController = reservationPagePaneLoader.getController();
        adminHomePageController = dashboardLoader.getController();
        adminHomePageController.setAdminMenuController(this);

        openPage(dashboardPane);
        hideButtonTexts();
        openMenuIcon.setOnMouseClicked(event -> toggleMenu());
        hanleAddTablePaneClose();
        logo.setOnMouseClicked(event -> {onDashboardButtonAction(new ActionEvent());});
    }

    private void hanleAddTablePaneClose() {
        // Lắng nghe sự thay đổi của Scene
        borderPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            newScene.setOnMouseClicked(event -> {
                if(addTablePane.isVisible()) {
                    javafx.geometry.Bounds bounds = addTablePane.localToScene(addTablePane.getBoundsInLocal());

                    if (!bounds.contains(event.getSceneX(), event.getSceneY())) {
                        // Ẩn addTablePane nếu click ngoài
                        addTablePane.setVisible(false);
                    }
                }
            });
        });
    }

    @FXML
    void onLogoAction(MouseEvent event) {
    }


    @FXML
    void onAddNewBookButtonAction(ActionEvent event) {
        while (!getTitlePageStack().isEmpty()) getTitlePageStack().pop();
        getTitlePageStack().push("Quản lý sách");
        openPage(bookPagePane);
        setActiveButton(bookManagementButton);
        bookPageController.loadAddPane();
        addTablePane.setVisible(false);
    }

    @FXML
    void onAddNewBorrowButtonAction(ActionEvent event) {
        while (!getTitlePageStack().isEmpty()) getTitlePageStack().pop();
        getTitlePageStack().push("Quản lý mượn sách");
        openPage(borrowPagePane);
        setActiveButton(borrowButton);
        borrowedBookPageController.loadAddPane();
        addTablePane.setVisible(false);
    }

    @FXML
    void onAddNewMemberButtonAction(ActionEvent event) {
        while (!getTitlePageStack().isEmpty()) getTitlePageStack().pop();
        getTitlePageStack().push("Quản lý độc giả");
        openPage(userPagePane);
        setActiveButton(readerManagementButton);
        usersPageController.loadAddPane();
        addTablePane.setVisible(false);
    }
    @FXML
    void onAddNewReservationButtonAction(ActionEvent event) {
        while (!getTitlePageStack().isEmpty()) getTitlePageStack().pop();
        getTitlePageStack().push("Quản lý đặt trước sách");
        openPage(reservationPagePane);
        setActiveButton(reservationButton);
        reservedBooksPageController.loadAddPane();
        addTablePane.setVisible(false);
    }

    @FXML
     void onDashboardButtonAction(ActionEvent event) {
        while (!getTitlePageStack().isEmpty()) getTitlePageStack().pop();
        getTitlePageStack().push("Dashboard");
        if(addTablePane.isVisible()) {
            addTablePane.setVisible(false);
        }
        setActiveButton(dashboardButton);

        openPage(dashboardPane);
    }

    @FXML
    void onBorrowButtonAction(ActionEvent event) {
        while (!getTitlePageStack().isEmpty()) getTitlePageStack().pop();
        getTitlePageStack().push("Quản lý mượn sách");
        if(addTablePane.isVisible()) {
            addTablePane.setVisible(false);
        }
        setActiveButton(borrowButton);
        borrowedBookPageController.startPage();
        openPage(borrowPagePane);
    }
    @FXML
    void onReservationButtonAction(ActionEvent event) {
        while (!getTitlePageStack().isEmpty()) getTitlePageStack().pop();
        getTitlePageStack().push("Quản lý đặt trước sách");
        if(addTablePane.isVisible()) {
            addTablePane.setVisible(false);
        }
        setActiveButton(reservationButton);
        reservedBooksPageController.startPage();
        openPage(reservationPagePane);
    }

    @FXML
    void onBookManagmentButtonAction(ActionEvent event) {
        while (!getTitlePageStack().isEmpty()) getTitlePageStack().pop();
        getTitlePageStack().push("Quản lý sách");
        if(addTablePane.isVisible()) {
            addTablePane.setVisible(false);
        }
        setActiveButton(bookManagementButton);
        bookPageController.startPage();
        openPage(bookPagePane);
    }

    @FXML
    void onReaderManagementButtonAction(ActionEvent event) {
        while (!getTitlePageStack().isEmpty()) getTitlePageStack().pop();
        getTitlePageStack().push("Quản lý độc giả");
        if(addTablePane.isVisible()) {
            addTablePane.setVisible(false);
        }
        setActiveButton(readerManagementButton);
        usersPageController.loadData();
        openPage(userPagePane);
    }

    @FXML
    void onLogoutButtonAction(ActionEvent event) {
        boolean confirmYes = CustomerAlter.showAlter("Bạn muốn thoát à?");
        if (confirmYes) {
            try {
                Stage stage = (Stage) mainPane.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("/view/UserLoginPage.fxml"));
                stage.setWidth(stage.getWidth());
                stage.setHeight(stage.getHeight());
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @FXML
    void onSettingButtonAction(ActionEvent event) {
        if(addTablePane.isVisible()) {
            addTablePane.setVisible(false);
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(SETTING_FXML));
            Parent root = loader.load();
            AdminSettingPageController controller = loader.getController();
            controller.reset();
            controller.setAdminID(this.AdminID);
            Stage stage = new Stage();
            stage.setTitle("AppInfo");
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

    @FXML
    void onAddButtonAction(ActionEvent event) {
        addTablePane.setVisible(!addTablePane.isVisible());
    }

    private void toggleMenu() {
        if (!isMenuExpanded) {
            menuBar.setMinWidth(238);
            menuBar.setMaxWidth(238);
            addButton.setMaxWidth(100);
            addButton.setMinWidth(100);
            
            // Thay đổi icon thành minimize
            openMenuIcon.setIconLiteral("fth-minimize");
            
            showButtonTexts();
        } else {
            menuBar.setMinWidth(62);
            menuBar.setMaxWidth(62);
            addButton.setMinWidth(54);
            addButton.setMaxWidth(54);

            // Thay đổi icon thành maximize
            openMenuIcon.setIconLiteral("fth-maximize");
            
            hideButtonTexts();
        }
        isMenuExpanded = !isMenuExpanded;
    }

    private void showButtonTexts() {
        addButton.setText("New");
        dashboardButton.setText("Dashboard");
        bookManagementButton.setText("Quản lý sách");
        reservationButton.setText("Đặt sách");
        borrowButton.setText("Mượn trả");
        readerManagementButton.setText("Quản lý độc giả");
        settingButton.setText("Cài đặt");
        logoutButton.setText("Đăng xuất");
    }

    private void hideButtonTexts() {
        addButton.setText("");
        dashboardButton.setText("");
        bookManagementButton.setText("");
        borrowButton.setText("");
        readerManagementButton.setText("");
        reservationButton.setText("");
        settingButton.setText("");
        logoutButton.setText("");
    }

    private void openPage(Node e) {
        mainPane.getChildren().clear();
        mainPane.getChildren().add(e);
    }

    public void setTitleLabel() {
      //  titleLabel.setText(getAllTitles());
    }

    private void setActiveButton(Button button) {
        if (currentActiveButton != null) {
            // Reset lại kiểu dáng của nút trước đó
            currentActiveButton.setStyle("");
        }

        // Gán nút hiện tại vào trạng thái đang hoạt động
        currentActiveButton = button;

        // Đặt kiểu dáng in đậm màu cho nút đang được chọn
        currentActiveButton.setStyle("-fx-font-weight: bold; -fx-background-color: #E0E0E0;"); // Kiểu CSS
    }


}
