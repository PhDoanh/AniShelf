package com.library.anishelf.controller;

import com.library.anishelf.service.UserService;
import com.library.anishelf.dao.BookDAO;
import com.library.anishelf.model.Book;
import com.library.anishelf.model.Category;
import com.library.anishelf.util.SceneManagerUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class AdvancedSearchController {
    @FXML
    private Pagination pagination;
    @FXML
    private TextField searchText;
    @FXML
    private ChoiceBox<String> categoryChoiceBox;
    @FXML
    HBox categoryBox;

    private ScrollPane scrollPane1 = new ScrollPane(), scrollPane2 = new ScrollPane();
    private Map<String, Object> criteria = new HashMap<>();

    private static final String DASHBOARD_FXML = "/view/UserHomePage.fxml";

    private SceneManagerUtil sceneManagerUtil = SceneManagerUtil.getInstance();
    private List<Book> findBook;
    private VBox test = new VBox();
    private HBox row1Box = new HBox(), row2Box = new HBox();

    /**
     * hàm khởi tạo cho AdvancedSearch
     */
    public void initialize() {
        categoryChoiceBox.getItems().addAll("title", "category_name", "author_name");
        categoryChoiceBox.setValue("title");
        scrollPane1.getStyleClass().add("real-transparent-scrollpane");
        scrollPane2.getStyleClass().add("real-transparent-scrollpane");

        scrollPane1.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane1.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        scrollPane2.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane2.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        try {
            List<Category> categoryList = BookDAO.getInstance().selectAllCategory();
            for (int i = 0; i < categoryList.size(); i++) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/view/Category.fxml"));
                    AnchorPane anchorPane = fxmlLoader.load();
                    CategoryController categoryController = fxmlLoader.getController();
                    categoryController.setData(categoryList.get(i).getCatagoryName(),this);
                    categoryBox.getChildren().add(anchorPane);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // Thay thế AnimationUtil.showMessage bằng CustomerAlter.showMessage
        CustomerAlter.showMessage("Bạn yêu ơi, vì 1 sách có 1 thể thoi nên nó sẽ tìm thể loại cuối bạn chọn nhé! Mình lười code chỉ được chọn 1 cho bạn yêu quá");
    }

    /**
     * từ thanh tìm kiếm truyền vào tìm kiếm cụ thể.
     * @param keyword từ khoá truyền vào
     */
    public void setSearchText(String keyword) {
        searchText.setText(keyword);
        onSearchButtonAction(new ActionEvent());
    }

    /**
     * thêm điều kiện.
     * @param category điều kiện
     */
    public void addCategoryCriteria(String category) {
        criteria.put("category_name",category);
    }

    /**
     * xoá điều kiện.
     * @param category điều kiện
     */
    public void deleteCategoryCriteria(String category) {
        Iterator<Map.Entry<String, Object>> iterator = criteria.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            if (entry.getValue() == category) {
                iterator.remove();
                break;
            }
        }
    }

    /**
     * quay lại dashboard
     *
     * @param event ấn vào để quay dashboard
     */
    public void onBackButtonAction(ActionEvent event) {
        VBox content = (VBox) sceneManagerUtil.loadScene(DASHBOARD_FXML);
        if (content != null) {
            sceneManagerUtil.updateSceneContainer(content);
        }
    }

    /**
     * tìm sách theo điều kiện
     *
     * @param event tìm sách theo điều kiện
     */
    public void onSearchButtonAction(ActionEvent event) {
        String keyword = searchText.getText();
        String category = categoryChoiceBox.getValue();

        criteria.put(category, keyword);
        UserService searchService = new UserService("searchBookByCategory", criteria);

        if (searchService.processOperation()) {
            findBook = (List<Book>) searchService.getPayload();
            System.out.println("Tìm thấy " + findBook.size() + " sách");
        } else {
            System.out.println("Không thấy sách");
        }
        deleteCategoryCriteria(searchText.getText());
        showFindedBook();
    }

    /**
     * tìm xong thì hiển thị ra
     */
    private void showFindedBook() {
        int numberOfPage = (findBook.size() - 1) / 12 + 1;
        pagination.setPageCount(numberOfPage);
        pagination.setPageFactory(pageIndex -> loadBook(pageIndex * 12, Math.min((pageIndex + 1) * 12, findBook.size())));
    }

    /**
     * load lên các HBox cho đẹp hàng.
     *
     * @param start index bắt đầu
     * @param end   index kết thúc
     * @return vbox đã load sách sau khi tìm
     */
    private VBox loadBook(int start, int end) {
        row1Box.getChildren().clear();
        row2Box.getChildren().clear();
        test.getChildren().clear();

        for (int i = start; i < Math.min(start + 6, end); i++) {
            loadBookCard(i, row1Box);
        }
        for (int i = start + 6; i < Math.min(start + 12, end); i++) {
            loadBookCard(i, row2Box);
        }
        scrollPane1.setContent(row1Box);
        scrollPane2.setContent(row2Box);
        test.getChildren().addAll(scrollPane1, scrollPane2);
        test.setAlignment(Pos.CENTER);
        return test;
    }

    /**
     * tải lên các sách tìm được.
     * @param index thứ tự của sách
     * @param rowBox hàng chứa sách
     */
    private void loadBookCard(int index, HBox rowBox) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/VerticalTypeBookCard.fxml"));
            VBox cardBox = fxmlLoader.load();
            VerticalTypeBookCardController cardController = fxmlLoader.getController();
            cardController.setData(findBook.get(index));
            rowBox.getChildren().add(cardBox);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}