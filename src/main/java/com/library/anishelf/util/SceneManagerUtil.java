package com.library.anishelf.util;

import com.library.anishelf.controller.NavigationBarController;
import com.library.anishelf.controller.ProfilePageController;
import com.library.anishelf.service.BookService;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Scene manager util.
 */
public class SceneManagerUtil {

    private static final String USER_MENU_FXML = "/view/NavigationBar.fxml";
    private static final String BOOKMARK_FXML = "/view/Bookmark.fxml";
    private static final String DASHBOARD_FXML = "/view/UserHomePage.fxml";
    private static final String RANKING_FXML = "/view/BookRanking.fxml";
    private static final String HISTORY_FXML = "/view/ReservedBorrowedHistoryPage.fxml";
    private static final String MORE_BOOK_FXML = "/view/MoreBookPage.fxml";
    private static final String BOOK_FXML = "/view/Book.fxml";
    private static final String ADVANCED_SEARCH_FXML = "/view/AdvancedSearchPage.fxml";


    private static SceneManagerUtil instance = null;
    private Map<String, Pane> fxmlCache = new HashMap<>();
    private Map<String, Object> controllerCache = new HashMap<>();
    private VBox container;

    private SceneManagerUtil() {
    }

    private SceneManagerUtil(VBox container) {
        this.container = container;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static SceneManagerUtil getInstance() {
        if (instance == null) {
            instance = new SceneManagerUtil();
        }
        return instance;
    }

    /**
     * Gets instance.
     *
     * @param container the container
     * @return the instance
     */
    public static SceneManagerUtil getInstance(VBox container) {
        if (instance == null) {
            instance = new SceneManagerUtil(container);
        } else {
            instance.container = container;
        }
        return instance;
    }


    /**
     * Load and get controller t.
     *
     * @param <T>      the type parameter
     * @param fxmlPath the fxml path
     * @return the t
     * @throws IOException the io exception
     */
    public static <T> T loadAndGetController(String fxmlPath) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL resource = SceneManagerUtil.class.getResource(fxmlPath);
        fxmlLoader.setLocation(resource);
        Pane loaded = fxmlLoader.load();
        return fxmlLoader.getController();
    }


    /**
     * Load scene pane.
     *
     * @param fxmlPath the fxml path
     * @return the pane
     */
    public Pane loadScene(String fxmlPath) {
        try {
            if (fxmlCache.containsKey(fxmlPath)) {
                return fxmlCache.get(fxmlPath);
            }
            FXMLLoader fxmlLoader = new FXMLLoader();
            URL resource = SceneManagerUtil.class.getResource(fxmlPath);
            fxmlLoader.setLocation(resource);
            Pane newContent = fxmlLoader.load();
            controllerCache.put(fxmlPath, fxmlLoader.getController());
            fxmlCache.put(fxmlPath, newContent);
            ThemeManagerUtil.getInstance().addPane(newContent);

            // Thêm trang vào lịch sử điều hướng nếu cần
            try {
                NavigationBarController navigationBarController = getController(USER_MENU_FXML);
                navigationBarController.addPageToHistory(fxmlPath);
            } catch (IOException e) {
                // Bỏ qua nếu không thể lấy controller
            }

            return newContent;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Gets container.
     *
     * @return the container
     */
    public VBox getContainer() {
        return container;
    }

    /**
     * Update scene container.
     *
     * @param content the content
     */
    public void updateSceneContainer(VBox content) {

        try {

            Object infoController = controllerCache.get("/view/ProfilePage.fxml");
            if (infoController != null && infoController instanceof ProfilePageController) {
                ProfilePageController controller =
                        (ProfilePageController) infoController;


                controller.restoreOriginalImageIfNeeded();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        container.setPadding(Insets.EMPTY);
        container.getChildren().clear();
        VBox.setVgrow(content, Priority.ALWAYS);
        container.getChildren().add(content);
    }

    /**
     * Gets controller.
     *
     * @param <T>      the type parameter
     * @param fxmlPath the fxml path
     * @return the controller
     * @throws IOException the io exception
     */
    public <T> T getController(String fxmlPath) throws IOException {
        return (T) controllerCache.get(fxmlPath);
    }

    /**
     * Add user menu controller.
     *
     * @param navigationBarController the navigation bar controller
     */
    public void addUserMenuController(NavigationBarController navigationBarController) {
        controllerCache.put(USER_MENU_FXML, navigationBarController);
    }

    /**
     * Clear all caches.
     */
    public void clearAllCaches() {
        controllerCache.clear();
        fxmlCache.clear();
        System.out.println(fxmlCache.size());
        BookService.getInstance().clearCache();
    }

    /**
     * Refresh book scenes.
     */
    public void refreshBookScenes() {
        fxmlCache.remove(DASHBOARD_FXML);
        fxmlCache.remove(BOOKMARK_FXML);
        fxmlCache.remove(HISTORY_FXML);
        fxmlCache.remove(RANKING_FXML);
        fxmlCache.remove(MORE_BOOK_FXML);
        fxmlCache.remove(BOOK_FXML);
        fxmlCache.remove(ADVANCED_SEARCH_FXML);
    }

    /**
     * Highlight back button.
     */
    public void highlightBackButton() {
        try {
            NavigationBarController navigationBarController = getController(USER_MENU_FXML);
            navigationBarController.changeColorButtonBack();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Điều hướng đến một trang cụ thể và lưu vào lịch sử
     *
     * @param fxmlPath Đường dẫn đến trang
     */
    public void navigateToPage(String fxmlPath) {
        VBox content = (VBox) loadScene(fxmlPath);
        if (content != null) {
            updateSceneContainer(content);
            try {
                NavigationBarController navigationBarController = getController(USER_MENU_FXML);
                navigationBarController.addPageToHistory(fxmlPath);

                // Cập nhật highlight cho nút tương ứng
                if (fxmlPath.equals(DASHBOARD_FXML)) {
                    navigationBarController.updateMenuButtonHighlight(navigationBarController.getDashboardButton());
                } else if (fxmlPath.equals(BOOKMARK_FXML)) {
                    navigationBarController.updateMenuButtonHighlight(navigationBarController.getBookmarkButton());
                } else if (fxmlPath.equals(RANKING_FXML)) {
                    navigationBarController.updateMenuButtonHighlight(navigationBarController.getBookRankingButton());
                }
            } catch (IOException e) {
                // Bỏ qua nếu không thể lấy controller
            }
        }
    }
}
