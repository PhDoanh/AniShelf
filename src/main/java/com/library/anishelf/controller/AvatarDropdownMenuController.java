package com.library.anishelf.controller;

import com.library.anishelf.util.SceneManagerUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

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

    public void setUserMenuController(NavigationBarController controller) {
        this.navigationBarController = controller;
    }

    public void onPersonalInfoButtonAction(ActionEvent event) {
        VBox content = (VBox) SceneManagerUtil.getInstance().loadScene(INFORMATION_FXML);
        if (content != null) {
            SceneManagerUtil.getInstance().updateSceneContainer(content);
        } else {
            System.err.println("Failed to load ProfilePage.fxml");
        }
        hideDropdownMenu();
    }

    public void onHistoryButtonAction(ActionEvent event) {
        VBox content = (VBox) SceneManagerUtil.getInstance().loadScene(HISTORY_FXML);
        if (content != null) {
            SceneManagerUtil.getInstance().updateSceneContainer(content);
        } else {
            System.err.println("Failed to load ReservedBorrowedHistoryPage.fxml");
        }
        hideDropdownMenu();
    }

    public void onSettingButtonAction(ActionEvent actionEvent) {
        VBox content = (VBox) SceneManagerUtil.getInstance().loadScene(SETTING_FXML);
        if (content != null) {
            SceneManagerUtil.getInstance().updateSceneContainer(content);
        } else {
            System.err.println("Failed to load SettingPage.fxml");
        }
        hideDropdownMenu();
    }

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