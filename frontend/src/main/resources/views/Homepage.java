package views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

public class Homepage implements Initializable {

    @FXML
    private AnchorPane page1Container;

    @FXML
    private AnchorPane page2Container;

    @FXML
    private Button leftNavButton;

    @FXML
    private Button rightNavButton;

    @FXML
    private MenuButton criteriaButton;

    @FXML
    private FontIcon criteriaIcon;

    @FXML
    private MenuButton criteriaButton1;

    @FXML
    private FontIcon criteriaIcon1;

    @FXML
    private Rectangle book1Rect;

    @FXML
    private Rectangle book2Rect;

    @FXML
    private Rectangle book3Rect;

    @FXML
    private Rectangle book4Rect;

    @FXML
    private Rectangle book5Rect;

    @FXML
    private Rectangle book6Rect;

    private int currentPage = 1;
    private final int TOTAL_PAGES = 2;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        page1Container.setVisible(true);
        page2Container.setVisible(false);

        updateNavigationButtons();

        setupBookClickHandlers();
    }

    private void setupBookClickHandlers() {
        book1Rect.setOnMouseClicked(event -> handleBookClick(1));
        book2Rect.setOnMouseClicked(event -> handleBookClick(2));
        book3Rect.setOnMouseClicked(event -> handleBookClick(3));
        book4Rect.setOnMouseClicked(event -> handleBookClick(4));
        book5Rect.setOnMouseClicked(event -> handleBookClick(5));
        book6Rect.setOnMouseClicked(event -> handleBookClick(6));
    }

    private void handleBookClick(int bookId) {
        System.out.println("Book " + bookId + " clicked");

    }

    @FXML
    private void nextBooks(ActionEvent event) {
        if (currentPage < TOTAL_PAGES) {
            currentPage++;
            updatePageVisibility();
            updateNavigationButtons();
        }
    }

    @FXML
    private void previousBooks(ActionEvent event) {
        if (currentPage > 1) {
            currentPage--;
            updatePageVisibility();
            updateNavigationButtons();
        }
    }

    private void updatePageVisibility() {
        page1Container.setVisible(currentPage == 1);
        page2Container.setVisible(currentPage == 2);
    }

    private void updateNavigationButtons() {
        leftNavButton.setDisable(currentPage == 1);
        rightNavButton.setDisable(currentPage == TOTAL_PAGES);
    }

    @FXML
    public void changeToCriteriaStar(ActionEvent event) {
        criteriaIcon.setIconLiteral("fth-star");
        criteriaButton.setText("");
    }

    @FXML
    public void changeToCriteriaHeart(ActionEvent event) {
        criteriaIcon.setIconLiteral("fth-heart");
        criteriaButton.setText("");
    }

    @FXML
    public void changeToCriteriaBookmark(ActionEvent event) {
        criteriaIcon.setIconLiteral("fth-bookmark");
        criteriaButton.setText("");
    }

    @FXML
    public void changeToCriteriatriangle(ActionEvent event) {
        criteriaIcon1.setIconLiteral("fth-alert-triangle");
        criteriaButton1.setText("");
    }

    public void handleSearch() {
        System.out.println("Search initiated");
    }

    public void openUserProfile() {
        System.out.println("Opening user profile");
    }

    public void openNotifications() {
        System.out.println("Opening notifications");
    }

    public void navigateToForum() {
        System.out.println("Navigating to forum");
    }

    public void viewAuthor() {
        System.out.println("Viewing author details");
    }

    public void sortByTime() {
        System.out.println("Sorting by post time");
    }

    public void viewRanking() {
        System.out.println("Viewing rankings");
    }

    public void reportContent() {
        System.out.println("Reporting content");
    }

    public void copyDirectLink() {
        System.out.println("Copying direct link");
    }

    public void saveContent() {
        System.out.println("Saving content");
    }
}
