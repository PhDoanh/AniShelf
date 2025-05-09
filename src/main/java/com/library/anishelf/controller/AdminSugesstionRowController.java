package com.library.anishelf.controller;

import com.library.anishelf.util.CacheManagerUtil;
import com.library.anishelf.model.Book;
import com.library.anishelf.model.BookItem;
import com.library.anishelf.model.Member;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminSugesstionRowController {
    @FXML
    private ImageView imageView;

    @FXML
    private Label textLabel;
    private Object object;
    private SuggestionTable suggestionTable;
    private String text;
    private String ImagePath;
    protected static final ExecutorService executor = Executors.newFixedThreadPool(4);

    public void setMainController(SuggestionTable suggest) {
        suggestionTable = suggest;
    }

    public void setSuggestion(Object o) {
        this.object = o;
        if (object instanceof Member) {
            Member member = (Member) object;
            textLabel.setText(member.getPerson().getLastName() + " " + member.getPerson().getFirstName() + " - " + member.getPerson().getId());
            // Tải ảnh bất đồng bộ
            Task<Image> loadImageTask = new Task<>() {
                @Override
                protected Image call() throws Exception {
                    // Nếu như ảnh của member mà không có hoặc đường dẫn ảnh lỗi thì set mặc định
                    try {
                        File file = new File(member.getPerson().getImagePath());
                        return new Image(file.toURI().toString());
                    } catch (Exception e) {
                        return new Image(getClass().getResourceAsStream("/image/default/avatar.png"));
                    }
                }
            };

            loadImageTask.setOnSucceeded(event -> {
                if (imageView.getScene() != null) {
                    imageView.setImage(loadImageTask.getValue());
                }
            });

            executor.submit(loadImageTask);


        } else if (object instanceof BookItem) {
            BookItem bookItem = (BookItem) object;
            textLabel.setText(bookItem.getTitle() + " - " + bookItem.getBookBarcode());
            // Tải ảnh bất đồng bộ
            Task<Image> loadImageTask = new Task<>() {
                @Override
                protected Image call() throws Exception {
                    try {
                        Image image = CacheManagerUtil.getImageFromCache(bookItem.getImagePath());
                        if(image != null) {
                            System.out.println("tai anh trong cache");
                            return image;
                        } else {
                            System.out.println("Khong co anh trong cache");
                            Image image1 = new Image(bookItem.getImagePath(), true);
                            CacheManagerUtil.putImageToCache(bookItem.getImagePath(), image1);
                            return new Image(image1.getUrl());
                        }
                    } catch (Exception e) {
                        System.out.println("Length: " + bookItem.getImagePath().length());

                        // Sử dụng đường dẫn resource thay vì đường dẫn tệp tuyệt đối
                        return new Image(getClass().getResourceAsStream("/image/default/book.png"));
                    }
                }
            };

            loadImageTask.setOnSucceeded(event -> {
                if (imageView.getScene() != null) {
                    imageView.setImage(loadImageTask.getValue());
                }
            });

            executor.submit(loadImageTask);
        } else if (object instanceof Book) {
            Book book = (Book) object;
            textLabel.setText(book.getTitle() + " - " + book.getIsbn());

            // Tải ảnh bất đồng bộ
            Task<Image> loadImageTask = new Task<>() {
                @Override
                protected Image call() throws Exception {
                    try {
                        Image image = CacheManagerUtil.getImageFromCache(book.getImagePath());
                        if(image != null) {
                            System.out.println("tai anh trong cache");
                            return image;
                        } else {
                            System.out.println("Khong co anh trong cache");
                            Image image1 = new Image(book.getImagePath(), true);
                            CacheManagerUtil.putImageToCache(book.getImagePath(), image1);
                            return new Image(image1.getUrl());
                        }
                    } catch (Exception e) {
                        System.out.println("Length: " + book.getImagePath().length());

                        // Sử dụng đường dẫn resource thay vì đường dẫn tệp tuyệt đối
                        return new Image(getClass().getResourceAsStream("/image/default/book.png"));
                    }
                }
            };

            loadImageTask.setOnSucceeded(event -> {
                if (imageView.getScene() != null) {
                    imageView.setImage(loadImageTask.getValue());
                }
            });

            executor.submit(loadImageTask);
        }

    }
}
