package com.library.anishelf.controller;

import com.library.anishelf.util.CacheManagerUtil;
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

import static com.library.anishelf.controller.SuggestedBookCardController.executor;

public class AdminDashboardBookCardController {

    @FXML
    private Label authorNameLabel;

    @FXML
    private ImageView bookImage;

    @FXML
    private Label bookNameLabel;

    @FXML
    private VBox vboxMain;

    @FXML
    private ImageView starImage;

    private BookPageController mainController;
    private AdminNavBarController adminNavBarController;

    private Book book;

    public void setItem(Book book) {
        this.book = book;
        setupRowClickHandler();
        bookNameLabel.setText(book.getTitle());
        String author = "";
        List<Author> authorList = book.getAuthors();
        for(int i = 0;i<authorList.size();i++) {
            author += authorList.get(i).getName() + ",";
        }
        authorNameLabel.setText(author);
        if(book.getRate() == 0) {
            book.setRate(BookService.getInstance().findBookInAllBooks(book).getRate());
        }
        starImage.setImage(starImage(book.getRate()));

        // Tải ảnh bất đồng bộ
        Task<Image> loadImageTask = new Task<>() {
            @Override
            protected Image call() throws Exception {
                try {
                    Image image = CacheManagerUtil.getImageFromCache(book.getImagePath());
                    if(image != null) {
                        return image;
                    } else {
                        Image image1 = new Image(book.getImagePath(), true);
                        CacheManagerUtil.putImageToCache(book.getImagePath(), image1);
                        return new Image(image1.getUrl());
                    }
                } catch (Exception e) {
                    System.out.println("Length: " + book.getImagePath().length());

                    File file = new File("bookImage/default.png");
                    return new Image(file.toURI().toString());
                }
            }
        };

        loadImageTask.setOnSucceeded(event -> {
            if (bookImage.getScene() != null) {
                bookImage.setImage(loadImageTask.getValue());
            }
        });

        executor.submit(loadImageTask);


    }
    private Image starImage(int numOfStar) {
        String imagePath = "/image/general/" + numOfStar + "star.png";
        if (getClass().getResourceAsStream(imagePath) == null) {
            System.out.println("Image not found: " + imagePath);
            return new Image(getClass().getResourceAsStream("/image/general/1star.png"));
        }
        return new Image(getClass().getResourceAsStream(imagePath));
    }

    private void setupRowClickHandler() {
        vboxMain.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                handleRowClick();
            }
        });
    }

    protected void handleRowClick() {
        this.adminNavBarController.onBookManagmentButtonAction(new ActionEvent());
        mainController.loadDetail(this.book);
    }

    public void setMainController(BookPageController mainController) {
        this.mainController = mainController;
    }

    public void setAdminMenuController(AdminNavBarController adminNavBarController) {
        this.adminNavBarController = adminNavBarController;
    }

}
