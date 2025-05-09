package com.library.anishelf.controller;

import com.library.anishelf.util.AnimationUtil;
import com.library.anishelf.service.BookAPIService;
import com.library.anishelf.dao.MemberDAO;
import com.library.anishelf.model.Book;
import com.library.anishelf.model.Member;
import com.library.anishelf.util.SceneManagerUtil;
import com.library.anishelf.util.config.UsrInfo;
import com.library.anishelf.util.config.UsrConfigUtil;
import com.library.anishelf.util.ThemeManagerUtil;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.util.Duration;
import javafx.concurrent.Task;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;


public class NavigationBarController {
    @FXML
    private VBox contentBox;

    @FXML
    private StackPane stackPane;

    @FXML
    private VBox topContainer;
    
    @FXML
    private HBox navigationBar;
    
    @FXML
    private ScrollPane contentScrollPane;

    @FXML
    ImageView avatarImage;
    
    @FXML
    TextField searchText;

    @FXML
    AnchorPane suggestionContainer;

    @FXML
    Button dashboardButton, bookmarkButton, bookRankingButton;

    @FXML
    private ListView<HBox> suggestionList;
    
    @FXML
    private HBox avatarContainer;

    private SceneManagerUtil sceneManagerUtil;
    private Button[] buttons;
    private ObservableList<HBox> filteredSuggestions;
    private boolean isAtTop = true;
    private static final double SCROLL_THRESHOLD = 10.0;

    private static UsrInfo usrInfo;
    private int memberID;
    private static Member member;
    
    // Avatar dropdown menu
    private Popup avatarDropdownPopup;
    private VBox avatarDropdownMenu;
    private AvatarDropdownMenuController avatarDropdownMenuController;
    private PauseTransition hidePopupTimer;

    private static final String DASHBOARD_FXML = "/view/UserHomePage.fxml";
    private static final String ADVANCED_SEARCH_FXML = "/view/AdvancedSearchPage.fxml";
    private static final String BOOKMARK_FXML = "/view/Bookmark.fxml";
    private static final String USER_LOGIN_FXML = "/view/UserLoginPage.fxml";
    private static final String BOOK_RANKING_FXML = "/view/BookRanking.fxml";
    private static final String SUGGEST_CARD_FXML = "/view/SuggestedBookCard.fxml";
    private static final String AVATAR_DROPDOWN_MENU_FXML = "/view/AvatarDropdownMenu.fxml";

    public void setInfo() {
        setupScrollHandler();
        initializeAvatarDropdownMenu();
        // Không gọi configureAvatarClip() vì đã xử lý trong FXML

        usrInfo = UsrConfigUtil.getInstance().findUserById(Integer.toString(memberID));
        if (usrInfo == null) {
            usrInfo = new UsrInfo(Integer.toString(memberID), "default");
            UsrConfigUtil.getInstance().writeUserInfoToFile(Integer.toString(memberID), "default");
        }
        ThemeManagerUtil.getInstance().changeTheme(usrInfo.getColor());

        sceneManagerUtil = SceneManagerUtil.getInstance(contentBox);
        sceneManagerUtil.addUserMenuController(this);
        VBox content = (VBox) sceneManagerUtil.loadScene(DASHBOARD_FXML);
        if (content != null) {
            sceneManagerUtil.updateSceneContainer(content);
            contentBox.getChildren().clear();
            contentBox.getChildren().add(content);
        }

        ThemeManagerUtil.getInstance().addPane(stackPane);

        searchBookSuggestion();

        // Cập nhật mảng buttons - bỏ đi các nút đã xóa trong FXML
        buttons = new Button[]{dashboardButton, bookmarkButton, bookRankingButton};
        ThemeManagerUtil.getInstance().changeMenuBarButtonColor(buttons, dashboardButton);
        
        // Thiết lập xử lý sự kiện cho avatar
        setupAvatarEvents();
    }
    
    /**
     * Khởi tạo menu thả xuống avatar
     */
    private void initializeAvatarDropdownMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AVATAR_DROPDOWN_MENU_FXML));
            avatarDropdownMenu = loader.load();
            avatarDropdownMenuController = loader.getController();
            avatarDropdownMenuController.setUserMenuController(this);
            
            avatarDropdownPopup = new Popup();
            avatarDropdownPopup.getContent().add(avatarDropdownMenu);
            avatarDropdownPopup.setAutoHide(true);
            
            hidePopupTimer = new PauseTransition(Duration.millis(300));
            hidePopupTimer.setOnFinished(e -> hideAvatarDropdownMenu());
        } catch (IOException e) {
            System.err.println("Không thể tải avatar dropdown menu: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Thiết lập xử lý sự kiện cho avatar
     */
    private void setupAvatarEvents() {
        if (avatarContainer != null) {
            avatarContainer.setOnMouseEntered(this::showAvatarDropdownMenu);
            avatarContainer.setOnMouseExited(e -> hidePopupTimer.playFromStart());
            
            avatarDropdownPopup.setOnShown(event -> {
                avatarDropdownMenu.setOnMouseEntered(e -> hidePopupTimer.stop());
                avatarDropdownMenu.setOnMouseExited(e -> hidePopupTimer.playFromStart());
            });
        }
        
        if (avatarImage != null) {
            avatarImage.setOnMouseEntered(this::showAvatarDropdownMenu);
            avatarImage.setOnMouseExited(e -> hidePopupTimer.playFromStart());
            // Vô hiệu hóa việc mở Setting view khi bấm vào avatar
            avatarImage.setOnMouseClicked(null);
        }
    }
    
    /**
     * Hiển thị menu thả xuống avatar
     */
    private void showAvatarDropdownMenu(MouseEvent event) {
        if (avatarDropdownPopup != null && !avatarDropdownPopup.isShowing()) {
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            
            // Tính toán vị trí hiển thị chính xác phía dưới bên phải của avatar
            double x = avatarImage.localToScreen(avatarImage.getBoundsInLocal()).getMinX() 
                    + avatarImage.getFitWidth() - avatarDropdownMenu.getPrefWidth() + 10;
            double y = avatarImage.localToScreen(avatarImage.getBoundsInLocal()).getMaxY() + 5;
            
            avatarDropdownPopup.show(stage, x, y);
            hidePopupTimer.stop();
        }
    }
    
    /**
     * Ẩn menu thả xuống avatar
     */
    public void hideAvatarDropdownMenu() {
        if (avatarDropdownPopup != null && avatarDropdownPopup.isShowing()) {
            avatarDropdownPopup.hide();
        }
    }
    
    /**
     * Cập nhật màu nút được chọn trên thanh menu
     */
    public void updateMenuButtonHighlight(Button selectedButton) {
        ThemeManagerUtil.getInstance().changeMenuBarButtonColor(buttons, selectedButton);
    }

    /**
     * Thiết lập xử lý sự kiện cuộn để ẩn/hiện thanh điều hướng
     */
    private void setupScrollHandler() {
        contentScrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            double scrollPosition = newValue.doubleValue();
            
            // Xác định khi nào là ở đầu trang
            if (scrollPosition <= 0.05) {
                if (!isAtTop) {
                    AnimationUtil.getInstance().showNavigationBar(navigationBar);
                    isAtTop = true;
                }
            } else {
                isAtTop = false;
                // Chỉ kích hoạt animation khi đã cuộn đủ xa
                if (Math.abs(oldValue.doubleValue() - scrollPosition) > 0.01) {
                    AnimationUtil.getInstance().toggleNavigationBarVisibility(navigationBar, scrollPosition);
                }
            }
        });
    }

    public void onDashboardButtonAction(ActionEvent event) {
        VBox content = (VBox) sceneManagerUtil.loadScene(DASHBOARD_FXML);
        if (content != null) {
            sceneManagerUtil.updateSceneContainer(content);
            ThemeManagerUtil.getInstance().changeMenuBarButtonColor(buttons, dashboardButton);
        }
    }

    public void onAdvancedSearchButtonAction(ActionEvent event) {
        VBox content = (VBox) sceneManagerUtil.loadScene(ADVANCED_SEARCH_FXML);
        if (content != null) {
            sceneManagerUtil.updateSceneContainer(content);
            try {
                AdvancedSearchController advancedSearchController = SceneManagerUtil.getInstance().getController(ADVANCED_SEARCH_FXML);
                advancedSearchController.setSearchText(searchText.getText());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            clearSuggestions();
        }
    }

    public void onBookmarkButtonAction(ActionEvent event) {
        VBox content = (VBox) sceneManagerUtil.loadScene(BOOKMARK_FXML);
        if (content != null) {
            sceneManagerUtil.updateSceneContainer(content);
            ThemeManagerUtil.getInstance().changeMenuBarButtonColor(buttons, bookmarkButton);
        }
    }

    public void onBookRankingButtonAction(ActionEvent actionEvent) {
        VBox content = (VBox) sceneManagerUtil.loadScene(BOOK_RANKING_FXML);
        if (content != null) {
            sceneManagerUtil.updateSceneContainer(content);
            ThemeManagerUtil.getInstance().changeMenuBarButtonColor(buttons, bookRankingButton);
        }
    }

    /**
     * Xử lý đăng xuất - được gọi từ menu thả xuống
     */
    public void onLogoutButtonAction(ActionEvent event) {
        boolean confirmYes = CustomerAlter.showAlter("Anh bỏ em à");
        if (confirmYes) {
            try {
                sceneManagerUtil.clearAllCaches();
                Stage stage = (Stage) stackPane.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource(USER_LOGIN_FXML));
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Hiển thị thông tin người dùng bao gồm ảnh đại diện
     */
    public void showInfo() {
        String imagePath = member.getPerson().getImagePath();
        
        // Nếu đường dẫn chứa "_temp_", đây là ảnh tạm thời chưa được lưu
        // Chỉ hiển thị ảnh tạm trong Information-view, không hiển thị trong UserMenu
        if (imagePath != null && imagePath.contains("_temp_")) {
            // Ảnh tạm thời - hiển thị ảnh mặc định hoặc ảnh cũ
            avatarImage.setImage(new Image(getClass().getResourceAsStream("/image/default/avatar.png")));
            return;
        }

        try {
            if (imagePath.startsWith("/")) {
                // Đường dẫn tài nguyên
                try {
                    Image image = new Image(getClass().getResourceAsStream(imagePath), 
                                          0, 0, true, false); 
                    
                    if (image != null && !image.isError()) {
                        avatarImage.setImage(image);
                    } else {
                        Path targetPath = Paths.get("target/classes" + imagePath);
                        if (Files.exists(targetPath)) {
                            avatarImage.setImage(new Image(targetPath.toUri().toString(), 
                                                         0, 0, true, false));
                        } else {
                            avatarImage.setImage(new Image(getClass().getResourceAsStream("/image/default/avatar.png")));
                        }
                    }
                } catch (Exception e) {
                    avatarImage.setImage(new Image(getClass().getResourceAsStream("/image/default/avatar.png")));
                }
            } else {
                // Đường dẫn tệp cục bộ
                File file = new File(imagePath);
                if (file.exists()) {
                    avatarImage.setImage(new Image(file.toURI().toString(), 
                                                 0, 0, true, false));
                } else {
                    avatarImage.setImage(new Image(getClass().getResourceAsStream("/image/default/avatar.png")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            avatarImage.setImage(new Image(getClass().getResourceAsStream("/image/default/avatar.png")));
        }
    }

    public void searchBookSuggestion() {
        filteredSuggestions = FXCollections.observableArrayList();
        suggestionList.setItems(filteredSuggestions);

        searchText.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredSuggestions.clear();

            fetchBooksFromApi(newValue, filteredSuggestions);

            suggestionList.setOnMouseClicked(event -> clearSuggestions());
            suggestionContainer.setOnMouseClicked(event -> clearSuggestions());
        });
    }

    /**
     * tìm sách ở api.
     *
     * @param keyword
     * @param filteredSuggestions
     */
    private void fetchBooksFromApi(String keyword, ObservableList<HBox> filteredSuggestions) {
        if (keyword.isEmpty()) {
            return;
        }
        Task<List<Book>> fetchBooksTask = new Task<>() {
            @Override
            protected List<Book> call() throws Exception {
                return BookAPIService.searchBooksByKeyword(keyword);
            }
        };

        fetchBooksTask.setOnSucceeded(event -> {
            List<Book> bookList = fetchBooksTask.getValue();
            updateSuggestions(bookList, filteredSuggestions);
        });

        new Thread(fetchBooksTask).start();
    }

    private void updateSuggestions(List<Book> bookList, ObservableList<HBox> filteredSuggestions) {
        for (int i = 0; i < bookList.size(); i++) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource(SUGGEST_CARD_FXML));
                HBox cardBox = fxmlLoader.load();
                SuggestedBookCardController cardController = fxmlLoader.getController();
                cardController.setData(bookList.get(i));
                filteredSuggestions.add(cardBox);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (i == 6) break;
        }

        adjustSuggestionListHeight(filteredSuggestions);
    }

    private void adjustSuggestionListHeight(ObservableList<HBox> filteredSuggestions) {
        suggestionList.setPrefHeight(filteredSuggestions.size() * 60);
        if (suggestionList.getPrefHeight() > 360) {
            suggestionList.setPrefHeight(360);
        }
        suggestionContainer.setVisible(!filteredSuggestions.isEmpty());
        suggestionList.setVisible(!filteredSuggestions.isEmpty());
    }

    private void clearSuggestions() {
        suggestionContainer.setVisible(false);
        filteredSuggestions.clear();
        searchText.clear();
    }

    public void onDashBoardMouseClicked(javafx.scene.input.MouseEvent mouseEvent) {
        VBox content = (VBox) sceneManagerUtil.loadScene(DASHBOARD_FXML);
        if (content != null) {
            sceneManagerUtil.updateSceneContainer(content);
            ThemeManagerUtil.getInstance().changeMenuBarButtonColor(buttons, dashboardButton);
        }
    }

    private void findMember() {
        try {
            member = MemberDAO.getInstance().findById(memberID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (member != null) {
            showInfo();
        } else {
            System.err.println("khong tim thay member");
        }
    }

    public void setMemberID(int memberID) {
        this.memberID = memberID;
        System.out.println("MemberID được thiết lập: " + memberID);
        findMember();
        setInfo();
    }

    public static Member getMember() {
        return member;
    }

    public void changeColorButtonBack() {
        ThemeManagerUtil.getInstance().changeMenuBarButtonColor(buttons, dashboardButton);
    }

    public static UsrInfo getUserInfo() {
        return usrInfo;
    }
}
