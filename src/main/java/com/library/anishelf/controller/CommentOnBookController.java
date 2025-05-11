package com.library.anishelf.controller;

import com.library.anishelf.model.Comment;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.io.File;

public class CommentOnBookController {

    private Comment commentOfBook;
    @FXML
    private Label nameOfUserLabel;

    @FXML
    private ImageView starImageView;

    @FXML
    private Text commentBookText;

    @FXML
    private Circle avatarUserImage;
    @FXML
    VBox commentBoxContainer;

    private String [] colorsComment = {"FFFFFF"};

    public void setData() {
        nameOfUserLabel.setText("book name");
    }

    public void setData(Comment comment) {
        this.commentOfBook = comment;
        Image image = new Image(new File(comment.getMember().getPerson().getImagePath()).toURI().toString());
        avatarUserImage.setFill(new ImagePattern((image)));
        nameOfUserLabel.setText(comment.getMember().getUsername());
        starImageView.setImage(setUpStarImage(comment.getRate()));
        commentBookText.setText(comment.getContent());
    }

    private Image setUpStarImage(int numOfStar) {
        String imagePath = "/image/book/" + numOfStar + "Star.png";
        if (getClass().getResourceAsStream(imagePath) == null) {
            System.out.println("Image not found: " + imagePath);
            return new Image(getClass().getResourceAsStream("/image/book/1Star.png"));
        }
        return new Image(getClass().getResourceAsStream(imagePath));
    }
}
