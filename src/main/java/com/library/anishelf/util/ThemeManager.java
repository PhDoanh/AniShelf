package com.library.anishelf.util;

import com.library.anishelf.controller.UserMenuController;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Class quản lý theme của ứng dụng
 */
public class ThemeManager {
    private static final String CUPERTINO_DARK_CSS = "/style/cupertino-dark.css";
    private static final String CUPERTINO_LIGHT_CSS = "/style/cupertino-light.css";
    private static final String NORD_DARK_CSS = "/style/nord-dark.css";
    private static final String NORD_LIGHT_CSS = "/style/nord-light.css";
    private static final String PRIMER_DARK_CSS = "/style/primer-dark.css";
    private static final String PRIMER_LIGHT_CSS = "/style/primer-light.css";
    private static final String DRACULA_CSS = "/style/dracula.css";
    private static final String CUSTOM_CSS = "/style/custom.css";
    private static final String DEFAULT = "/style/primer-light.css";
    
    // Sửa các String CSS thành class selector
    private static final String BUTTON_ACTIVE_STYLE_CLASS = "active-nav-button";
    private static final String BUTTON_INACTIVE_STYLE_CLASS = "nav-button";

    private static ThemeManager instance;
    private final List<Pane> panes;
    private String currentTheme;

    private ThemeManager() {
        panes = new ArrayList<>();
        currentTheme = DEFAULT;
    }

    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    /**
     * Thêm pane để có thể quản lý theme.
     *
     * @param pane Pane cần thêm
     */
    public void addPane(Pane pane) {
        if (!panes.contains(pane)) {
            panes.add(pane);
            applyThemeToPane(pane);
        }
    }

    /**
     * Áp dụng theme hiện tại cho một pane cụ thể.
     * 
     * @param pane Pane cần áp dụng theme
     */
    public void applyTheme(Pane pane) {
        applyThemeToPane(pane);
    }

    /**
     * Thay đổi theme của ứng dụng.
     *
     * @param theme Tên theme
     */
    public void changeTheme(String theme) {
        String newTheme = getThemePath(theme);
        if (!Objects.equals(currentTheme, newTheme)) {
            currentTheme = newTheme;
            applyThemeToAllPanes();
        }
    }

    /**
     * Áp dụng theme cho tất cả các pane.
     */
    private void applyThemeToAllPanes() {
        for (Pane pane : panes) {
            applyThemeToPane(pane);
        }
    }

    /**
     * Áp dụng theme cho một pane cụ thể.
     *
     * @param pane Pane cần áp dụng
     */
    private void applyThemeToPane(Pane pane) {
        pane.getStylesheets().clear();
        pane.getStylesheets().add(getClass().getResource(currentTheme).toExternalForm());
        pane.getStylesheets().add(getClass().getResource(CUSTOM_CSS).toExternalForm());
    }

    /**
     * Thay đổi màu nút trên thanh điều hướng bằng cách thay đổi CSS class thay vì inline style.
     *
     * @param buttons Mảng các nút
     * @param activeButton Nút đang active
     */
    public void changeMenuBarButtonColor(Button[] buttons, Button activeButton) {
        for (Button button : buttons) {
            button.getStyleClass().remove(BUTTON_ACTIVE_STYLE_CLASS);
            if (button.equals(activeButton)) {
                // Thêm class active cho button được chọn
                if (!button.getStyleClass().contains(BUTTON_ACTIVE_STYLE_CLASS)) {
                    button.getStyleClass().add(BUTTON_ACTIVE_STYLE_CLASS);
                }
            } else {
                // Đảm bảo button có class inactive
                if (!button.getStyleClass().contains(BUTTON_INACTIVE_STYLE_CLASS)) {
                    button.getStyleClass().add(BUTTON_INACTIVE_STYLE_CLASS);
                }
            }
        }
    }

    /**
     * Lấy đường dẫn của theme dựa theo tên.
     *
     * @param themeName Tên theme
     * @return Đường dẫn đến file CSS của theme
     */
    private String getThemePath(String themeName) {
        return switch (themeName) {
            case "cupertino-dark" -> CUPERTINO_DARK_CSS;
            case "cupertino-light" -> CUPERTINO_LIGHT_CSS;
            case "nord-dark" -> NORD_DARK_CSS;
            case "nord-light" -> NORD_LIGHT_CSS;
            case "primer-dark" -> PRIMER_DARK_CSS;
            case "primer-light" -> PRIMER_LIGHT_CSS;
            case "dracula" -> DRACULA_CSS;
            default -> DEFAULT;
        };
    }

    /**
     * Lấy tên theme hiện tại.
     *
     * @return Tên theme hiện tại
     */
    public String getCurrentThemeName() {
        return switch (currentTheme) {
            case CUPERTINO_DARK_CSS -> "cupertino-dark";
            case CUPERTINO_LIGHT_CSS -> "cupertino-light";
            case NORD_DARK_CSS -> "nord-dark";
            case NORD_LIGHT_CSS -> "nord-light";
            case PRIMER_DARK_CSS -> "primer-dark";
            case PRIMER_LIGHT_CSS -> "primer-light";
            case DRACULA_CSS -> "dracula";
            default -> "default";
        };
    }
}
