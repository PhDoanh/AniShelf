package com.library.frontend.controllers;

import com.library.frontend.models.ForumPost;
import com.library.frontend.models.PostListCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;

public class HomeController {
    @FXML
    private ComboBox<String> criteriaComboBox;
    @FXML private ScrollPane scrollPane;
    @FXML private HBox booksHBox;
    @FXML private ListView<ForumPost> forumListView;
    @FXML
    public void initialize() {
        // Thêm 6 cuốn sách mẫu
        for (int i = 1; i <= 6; i++) {
            VBox bookItem = new VBox(5);

            // Thêm hình ảnh (thay đổi đường dẫn phù hợp)
            ImageView imageView = new ImageView(new Image("book_cover.png"));
            imageView.setFitWidth(120);
            imageView.setFitHeight(160);

            // Thêm label
            Label bookLabel = new Label("Book " + i);

            bookItem.getChildren().addAll(imageView, bookLabel);
            booksHBox.getChildren().add(bookItem);
        }
        // Tạo dữ liệu mẫu
        ObservableList<ForumPost> posts = FXCollections.observableArrayList(
                new ForumPost(
                        "Cách đọc sách hiệu quả",
                        "Chia sẻ phương pháp đọc sách khoa học...",
                        "Nguyễn Văn A",
                        LocalDateTime.now()
                ),
                new ForumPost(
                        "Review sách hay năm 2023",
                        "Những cuốn sách đáng đọc nhất năm nay...",
                        "Trần Thị B",
                        LocalDateTime.now().minusDays(2)
                )
        );

        // Thiết lập ListView
        forumListView.setItems(posts);
        forumListView.setCellFactory(param -> new PostListCell());
    }
    // xử lí nút mũi tên trái
    @FXML
    public void handleRightArrow() {
        double delta = 0.2;
        scrollPane.setVvalue(scrollPane.getVvalue() + delta);
    }
    // xử lí nút mũi tên phải
    @FXML
    public void handleLeftArrow() {
        double delta = 0.2;
        scrollPane.setVvalue(scrollPane.getVvalue() - delta);
    }
}
