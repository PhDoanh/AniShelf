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

public class CommentController {

    private Comment comment;
    @FXML
    private Label nameLabel;

    @FXML
    private ImageView starImage;

    @FXML
    private Text commentText;

    @FXML
    private Circle avatarImage;
    @FXML
    VBox commentBox;

    private String [] colors = {"FFFFFF"};

    public void setData() {
        nameLabel.setText("book name");
    }

    public void setData(Comment comment) {
        this.comment = comment;
        Image image = new Image(new File(comment.getMember().getPerson().getImagePath()).toURI().toString());
        avatarImage.setFill(new ImagePattern((image)));
        nameLabel.setText(comment.getMember().getUsername());
        starImage.setImage(starImage(comment.getRate()));
        commentText.setText(comment.getContent());
    }

    private Image starImage(int numOfStar) {
        String imagePath = "/image/general/" + numOfStar + "star.png";
        if (getClass().getResourceAsStream(imagePath) == null) {
            System.out.println("Image not found: " + imagePath);
            return new Image(getClass().getResourceAsStream("/image/general/1star.png"));
        }
        return new Image(getClass().getResourceAsStream(imagePath));
    }
}
