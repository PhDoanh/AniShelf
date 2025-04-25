package com.library.frontend.models;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;

public class PostListCell extends ListCell<ForumPost> {
    private final Label titleLabel = new Label();
    private final Label summaryLabel = new Label();
    private final Label authorLabel = new Label();
    private final Label dateLabel = new Label();
    private final VBox container = new VBox(titleLabel, summaryLabel, authorLabel, dateLabel);

    public PostListCell() {
        // Định dạng các thành phần
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        summaryLabel.setWrapText(true);
        dateLabel.setStyle("-fx-text-fill: #666;");
        container.setSpacing(5);
        container.setPadding(new Insets(10));
    }

    @Override
    protected void updateItem(ForumPost post, boolean empty) {
        super.updateItem(post, empty);
        if (empty || post == null) {
            setGraphic(null);
        } else {
            titleLabel.setText(post.getTitle());
            summaryLabel.setText(post.getSummary());
            authorLabel.setText("Đăng bởi: " + post.getAuthor());
            dateLabel.setText("Ngày đăng: " + post.getPostDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            setGraphic(container);
        }
    }
}