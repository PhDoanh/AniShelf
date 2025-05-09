package com.library.anishelf.controller;

import com.library.anishelf.util.ThemeManagerUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SettingPageController {
    @FXML
    VBox settingBox;
    @FXML
    HBox themesContainer; // Container for theme options

    public void initialize() {
        // Highlight current theme
        highlightCurrentTheme();
    }

    /**
     * Highlight the current theme selection
     */
    private void highlightCurrentTheme() {
        String currentTheme = ThemeManagerUtil.getInstance().getCurrentThemeName();
        
        // Reset all theme options
        if (themesContainer != null) {
            for (int i = 0; i < themesContainer.getChildren().size(); i++) {
                if (themesContainer.getChildren().get(i) instanceof Label) {
                    Label themeLabel = (Label) themesContainer.getChildren().get(i);
                    themeLabel.getStyleClass().remove("selected-theme");
                }
            }
            
            // Find and highlight the current theme
            for (int i = 0; i < themesContainer.getChildren().size(); i++) {
                if (themesContainer.getChildren().get(i) instanceof Label) {
                    Label themeLabel = (Label) themesContainer.getChildren().get(i);
                    String themeName = themeLabel.getUserData() != null ? 
                        themeLabel.getUserData().toString() : "";
                    
                    if (themeName.equals(currentTheme)) {
                        themeLabel.getStyleClass().add("selected-theme");
                        break;
                    }
                }
            }
        }
    }

    /**
     * Thay đổi giao diện sang Nord Light theme
     * @param mouseEvent sự kiện chuột
     */
    public void onNordLightMouseClicked(MouseEvent mouseEvent) {
        ThemeManagerUtil.getInstance().changeTheme("nord-light");
        ThemeManagerUtil.getInstance().applyTheme(settingBox);
        highlightCurrentTheme();
    }

    /**
     * Thay đổi giao diện sang Nord Dark theme
     * @param mouseEvent sự kiện chuột
     */
    public void onNordDarkMouseClicked(MouseEvent mouseEvent) {
        ThemeManagerUtil.getInstance().changeTheme("nord-dark");
        ThemeManagerUtil.getInstance().applyTheme(settingBox);
        highlightCurrentTheme();
    }

    /**
     * Thay đổi giao diện sang Cupertino Dark theme
     * @param mouseEvent sự kiện chuột
     */
    public void onCupertinoDarkMouseClicked(MouseEvent mouseEvent) {
        ThemeManagerUtil.getInstance().changeTheme("cupertino-dark");
        ThemeManagerUtil.getInstance().applyTheme(settingBox);
        highlightCurrentTheme();
    }

    /**
     * Thay đổi giao diện sang Cupertino Light theme
     * @param mouseEvent sự kiện chuột
     */
    public void onCupertinoLightMouseClicked(MouseEvent mouseEvent) {
        ThemeManagerUtil.getInstance().changeTheme("cupertino-light");
        ThemeManagerUtil.getInstance().applyTheme(settingBox);
        highlightCurrentTheme();
    }

    /**
     * Thay đổi giao diện sang Dracula theme
     * @param mouseEvent sự kiện chuột
     */
    public void onDraculaMouseClicked(MouseEvent mouseEvent) {
        ThemeManagerUtil.getInstance().changeTheme("dracula");
        ThemeManagerUtil.getInstance().applyTheme(settingBox);
        highlightCurrentTheme();
    }

    /**
     * Thay đổi giao diện sang Primer Dark theme
     * @param mouseEvent sự kiện chuột
     */
    public void onPrimerDarkMouseClicked(MouseEvent mouseEvent) {
        ThemeManagerUtil.getInstance().changeTheme("primer-dark");
        ThemeManagerUtil.getInstance().applyTheme(settingBox);
        highlightCurrentTheme();
    }

    /**
     * Thay đổi giao diện sang Primer Light theme
     * @param mouseEvent sự kiện chuột
     */
    public void onPrimerLightMouseClicked(MouseEvent mouseEvent) {
        ThemeManagerUtil.getInstance().changeTheme("primer-light");
        ThemeManagerUtil.getInstance().applyTheme(settingBox);
        highlightCurrentTheme();
    }
}
