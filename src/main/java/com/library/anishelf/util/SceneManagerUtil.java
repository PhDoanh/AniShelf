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

public class SceneManagerUtil {

    private static final String USER_MENU_FXML = "/view/NavigationBar.fxml";
    private static final String BOOKMARK_FXML = "/view/Bookmark.fxml";
    private static final String DASHBOARD_FXML = "/view/UserHomePage.fxml";
    private static final String RANKING_FXML = "/view/BookRanking.fxml";
    private static final String HISTORY_FXML = "/view/ReservedBorrowedHistoryPage.fxml";
    private static final String MORE_BOOK_FXML = "/view/MoreBookPage.fxml";
    

    private static SceneManagerUtil instance = null;
    private Map<String, Pane> fxmlCache = new HashMap<>();
    private Map<String, Object> controllerCache = new HashMap<>();
    private VBox container;

    private SceneManagerUtil() {
    }

    private SceneManagerUtil(VBox container) {
        this.container = container;
    }

    public static SceneManagerUtil getInstance() {
        if(instance == null) {
            instance = new SceneManagerUtil();
        }
        return instance;
    }

    public static SceneManagerUtil getInstance(VBox container) {
        if (instance == null) {
            instance = new SceneManagerUtil(container);
        } else {
            instance.container = container;  
        }
        return instance;
    }


    public static <T> T loadAndGetController(String fxmlPath) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL resource = SceneManagerUtil.class.getResource(fxmlPath);
        fxmlLoader.setLocation(resource);
        Pane loaded = fxmlLoader.load();
        return fxmlLoader.getController();
    }

    
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
            return newContent;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public VBox getContainer() {
        return container;
    }

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

    public <T> T getController(String fxmlPath) throws IOException {
        return (T) controllerCache.get(fxmlPath); 
    }

    public void addUserMenuController(NavigationBarController navigationBarController) {
        controllerCache.put(USER_MENU_FXML, navigationBarController);
    }

    public void clearAllCaches() {
        controllerCache.clear();
        fxmlCache.clear();
        System.out.println(fxmlCache.size());
        BookService.getInstance().clearCache();
    }

    public void refreshBookScenes() {
        fxmlCache.remove(DASHBOARD_FXML);
        fxmlCache.remove(BOOKMARK_FXML);
        fxmlCache.remove(HISTORY_FXML);
        fxmlCache.remove(RANKING_FXML);
        fxmlCache.remove(MORE_BOOK_FXML);
    }

    public void highlightBackButton() {
        try {
            NavigationBarController navigationBarController = getController(USER_MENU_FXML);
            navigationBarController.changeColorButtonBack();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
