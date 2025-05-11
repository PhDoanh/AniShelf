package com.library.anishelf.controller;

import com.library.anishelf.dao.BookDAO;
import com.library.anishelf.model.Book;
import com.library.anishelf.util.fxmlLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Pagination;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MoreBookToReadController {

    @FXML
    private VBox moreBookBoxContainer;

    private VBox testContainer = new VBox();

    private HBox row1BoxContainer = new HBox(), row2BoxContainer =new HBox();

    private ScrollPane scrollPaneCotainer1 =new ScrollPane(), scrollPaneContainer2 =new ScrollPane();
    @FXML
    private Pagination paginationToNavigate;

    private static final String DASHBOARD_FXML_VIEW = "/view/DashBoard-view.fxml";

    private fxmlLoader fxmlLoader1 = fxmlLoader1.getInstance();

    private List<Book> listOfBook = new ArrayList<>();

    public void initialize() {
        try {
            listOfBook = BookDAO.getInstance().selectAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println(listOfBook.size());

        scrollPaneCotainer1.getStyleClass().add("real-transparent-scrollpane");
        scrollPaneContainer2.getStyleClass().add("real-transparent-scrollpane");

        scrollPaneCotainer1.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPaneCotainer1.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        scrollPaneContainer2.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPaneContainer2.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        int numberOfPage = (listOfBook.size() - 1) / 12 + 1;
        paginationToNavigate.setPageCount((int) Math.ceil((double) listOfBook.size() / 12));
        paginationToNavigate.setPageFactory(pageIndex -> loadMoreBook(pageIndex * 12, Math.min((pageIndex + 1) * 12, listOfBook.size())));
    }

    /**
     * load các sách
     * @param start chỉ số sách đầu
     * @param end chỉ số sách cuối
     * @return VBox chứa tất cả sách
     */
    public VBox loadMoreBook(int start, int end) {
        row1BoxContainer.getChildren().clear();
        row2BoxContainer.getChildren().clear();
        testContainer.getChildren().clear();

        for (int i = start; i < Math.min(start + 6, end); i++) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/view/BookCard2-view.fxml"));
                VBox cardBox = fxmlLoader.load();
                BookCard2Controller cardController = fxmlLoader.getController();
                cardController.setDataBook(listOfBook.get(i));
                row1BoxContainer.getChildren().add(cardBox);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (int i = start + 6; i < Math.min(start + 12, end); i++) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/view/BookCard2-view.fxml"));
                VBox cardBox = fxmlLoader.load();
                BookCard2Controller cardController = fxmlLoader.getController();
                cardController.setDataBook(listOfBook.get(i));
                row2BoxContainer.getChildren().add(cardBox);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        scrollPaneCotainer1.setContent(row1BoxContainer);
        scrollPaneContainer2.setContent(row2BoxContainer);
        testContainer.getChildren().addAll(scrollPaneCotainer1, scrollPaneContainer2);
        testContainer.setAlignment(Pos.CENTER);
        return testContainer;
    }

    /**
     * về dashboard.
     * @param event khi ấn
     */
    public void handleBackButtonClicked(ActionEvent event) {
        VBox content = (VBox) fxmlLoader1.loadFXML(DASHBOARD_FXML_VIEW);
        if (content != null) {
            if (moreBookBoxContainer.getChildren().contains(content)) {
                moreBookBoxContainer.getChildren().remove(content);
            }
            fxmlLoader1.updateContentBox(content);
            fxmlLoader1.getInstance().changeColorWhenBack();
        }
    }

}
