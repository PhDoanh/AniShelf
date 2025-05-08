package com.library.anishelf.controller;

import com.library.anishelf.util.ImageCache;
import com.library.anishelf.model.Author;
import com.library.anishelf.model.Book;
import com.library.anishelf.service.BookService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.List;

import static com.library.anishelf.controller.BookSuggestionCardController.executor;

public class AdminDashboardBookItemCardController {

    @FXML
    private Label writerNameLabel;

    @FXML
    private ImageView bookImageView;

    @FXML
    private Label nameOfBookLabel;

    @FXML
    private VBox vboxMainContainer;

    @FXML
    private ImageView starImageView;

    private AdminBookItemPageController mainPageController;
    private AdminMenuController adminMenuBookController;

    private Book currentBook;
    // This function is used to set the book item in the card
    public void setBookItem(Book book) {
        this.currentBook = book;
        setupRowClickHandler();
        nameOfBookLabel.setText(book.getTitle());
        String author = "";
        List<Author> authorList = book.getAuthors();
        for(int i = 0;i<authorList.size();i++) {
            author += authorList.get(i).getAuthorName() + ",";
        }
        writerNameLabel.setText(author);
        if(book.getRate() == 0) {
            book.setRate(BookService.getInstance().isContainInAllBooks(book).getRate());
        }
        starImageView.setImage(setStarImage(book.getRate()));

        // Tải ảnh bất đồng bộ
        Task<Image> loadImageTask = new Task<>() {
            @Override
            protected Image call() throws Exception {
                try {
                    Image image = ImageCache.getImageLRUCache().get(book.getImagePath());
                    if(image != null) {
                        return image;
                    } else {
                        Image image1 = new Image(book.getImagePath(), true);
                        ImageCache.getImageLRUCache().put(book.getImagePath(), image1);
                        return new Image(image1.getUrl());
                    }
                } catch (Exception e) {
                    System.out.println("Length: " + book.getImagePath().length());

                    File file = new File("bookImageView/default.png");
                    return new Image(file.toURI().toString());
                }
            }
        };

        loadImageTask.setOnSucceeded(event -> {
            if (bookImageView.getScene() != null) {
                bookImageView.setImage(loadImageTask.getValue());
            }
        });

        executor.submit(loadImageTask);


    }
    // this function is used to set the image of the star
    private Image setStarImage(int numOfStar) {
        String imagePath = "/image/book/" + numOfStar + "Star.png";
        if (getClass().getResourceAsStream(imagePath) == null) {
            System.out.println("Image not found: " + imagePath);
            return new Image(getClass().getResourceAsStream("/image/book/1Star.png"));
        }
        return new Image(getClass().getResourceAsStream(imagePath));
    }

    private void setupRowClickHandler() {
        vboxMainContainer.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                handleRowItemClick();
            }
        });
    }
    // this function is used to handle the click event of the row
    protected void handleRowItemClick() {
        this.adminMenuBookController.onBookManagmentButtonAction(new ActionEvent());
        mainPageController.loadDetail(this.currentBook);
    }

    public void setMainPageController(AdminBookItemPageController mainPageController) {
        this.mainPageController = mainPageController;
    }

    public void setAdminMenuBookController(AdminMenuController adminMenuBookController) {
        this.adminMenuBookController = adminMenuBookController;
    }

}
