package com.library.anishelf.controller;

import com.library.anishelf.service.UserService;
import com.library.anishelf.model.Book;
import com.library.anishelf.util.SceneManagerUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * The type User home page controller.
 */
public class UserHomePageController implements Initializable {
    @FXML
    private HBox popularHBox;
    @FXML
    private HBox highRankHBox;

    private SceneManagerUtil sceneManagerUtil = SceneManagerUtil.getInstance();

    private List<Book> popularBooks;
    private List<Book> highRankBooks;

    private static final String MORE_BOOK_FXML = "/view/MoreBookPage.fxml";
    private static final int MAX_DISPLAYED_BOOKS = 6; // Limit to 6 books per row

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Sử dụng UserService để lấy truyện phổ biến
        UserService popularBooksService = new UserService("getPopularBooks", null);
        if (popularBooksService.processOperation()) {
            popularBooks = (List<Book>) popularBooksService.getPayload();
        } else {
            System.out.println("Lỗi lấy truyện phổ biến.");
            popularBooks = new ArrayList<>(); // Initialize empty list to avoid NPE
        }

        // Đảm bảo ScrollPane có kích thước phù hợp
        popularHBox.setSpacing(15);
        popularHBox.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));

        // Thêm truyện phổ biến vào HBox
        int popularCount = Math.min(popularBooks.size(), MAX_DISPLAYED_BOOKS);
        for (int i = 0; i < popularCount; i++) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/view/HorizontalTypeBookCard.fxml"));
                HBox cardBox = fxmlLoader.load();
                HorizontalTypeBookCardController cardController = fxmlLoader.getController();
                cardController.setData(popularBooks.get(i));
                popularHBox.getChildren().add(cardBox);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Sử dụng UserService để lấy truyện xếp hạng cao
        UserService highRankBooksService = new UserService("getHighRankBooks", null);
        if (highRankBooksService.processOperation()) {
            highRankBooks = (List<Book>) highRankBooksService.getPayload();
        } else {
            System.out.println("Lỗi lấy truyện xếp hạng cao.");
            highRankBooks = new ArrayList<>(); // Initialize empty list to avoid NPE
        }

        // Đảm bảo ScrollPane có kích thước phù hợp
        highRankHBox.setSpacing(15);
        highRankHBox.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));

        // Thêm truyện xếp hạng cao vào HBox
        int highRankCount = Math.min(highRankBooks.size(), MAX_DISPLAYED_BOOKS);
        for (int i = 0; i < highRankCount; i++) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/view/VerticalTypeBookCard.fxml"));
                VBox cardBox = fxmlLoader.load();
                VerticalTypeBookCardController cardController = fxmlLoader.getController();
                cardController.setData(highRankBooks.get(i));

                // Set minimum width to ensure proper layout
                cardBox.setMinWidth(123);
                cardBox.setPrefWidth(123);

                highRankHBox.getChildren().add(cardBox);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * sang MoreBook
     *
     * @param event khi án
     */
    public void onMoreButtonAction(ActionEvent event) {
        VBox content = (VBox) sceneManagerUtil.loadScene(MORE_BOOK_FXML);
        if (content != null) {
            sceneManagerUtil.updateSceneContainer(content);
        }
    }
}
