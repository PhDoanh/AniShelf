package com.library.anishelf.controller;

import com.library.anishelf.util.SceneManagerUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/**
 * Controller cho menu thả xuống của avatar
 */
public class AvatarDropdownMenuController {
    @FXML
    private VBox avatarDropdownMenu;

    @FXML
    private Button personalInfoButton;

    @FXML
    private Button historyButton;

    @FXML
    private Button settingButton;

    @FXML
    private Button logoutMenuButton;

    private static final String INFORMATION_FXML = "/view/ProfilePage.fxml";
    private static final String HISTORY_FXML = "/view/ReservedBorrowedHistoryPage.fxml";
    private static final String SETTING_FXML = "/view/SettingPage.fxml";

    private NavigationBarController navigationBarController;

    /**
     * Thiết lập tham chiếu đến NavigationBarController
     */
    public void setUserMenuController(NavigationBarController controller) {
        this.navigationBarController = controller;
    }

    /**
     * Xử lý sự kiện khi nhấp vào nút Hồ sơ cá nhân
     */
    @FXML
    public void onPersonalInfoButtonAction(ActionEvent event) {
        VBox content = (VBox) SceneManagerUtil.getInstance().loadScene(INFORMATION_FXML);
        if (content != null) {
            SceneManagerUtil.getInstance().updateSceneContainer(content);
        } else {
            System.err.println("Failed to load ProfilePage.fxml");
        }
        hideDropdownMenu();
    }

    /**
     * Xử lý sự kiện khi nhấp vào nút Lịch sử mượn/đặt
     */
    @FXML
    public void onHistoryButtonAction(ActionEvent event) {
        VBox content = (VBox) SceneManagerUtil.getInstance().loadScene(HISTORY_FXML);
        if (content != null) {
            SceneManagerUtil.getInstance().updateSceneContainer(content);
        } else {
            System.err.println("Failed to load ReservedBorrowedHistoryPage.fxml");
        }
        hideDropdownMenu();
    }

    /**
     * Xử lý sự kiện khi nhấp vào nút Cài đặt
     */
    @FXML
    public void onSettingButtonAction(ActionEvent actionEvent) {
        VBox content = (VBox) SceneManagerUtil.getInstance().loadScene(SETTING_FXML);
        if (content != null) {
            SceneManagerUtil.getInstance().updateSceneContainer(content);
        } else {
            System.err.println("Failed to load SettingPage.fxml");
        }
        hideDropdownMenu();
    }

    /**
     * Xử lý sự kiện khi nhấp vào nút Đăng xuất
     */
    @FXML
    public void onLogoutMenuButtonAction(ActionEvent event) {
        if (navigationBarController != null) {
            navigationBarController.onLogoutButtonAction(event);
        }
        hideDropdownMenu();
    }

    private void hideDropdownMenu() {
        if (navigationBarController != null) {
            navigationBarController.hideAvatarDropdownMenu();
        }
    }
}