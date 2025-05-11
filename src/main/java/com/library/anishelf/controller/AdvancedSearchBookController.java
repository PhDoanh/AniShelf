package com.library.anishelf.controller;

import com.library.anishelf.service.command.UserCommand;
import com.library.anishelf.util.Animation;
import com.library.anishelf.dao.BookDAO;
import com.library.anishelf.model.Book;
import com.library.anishelf.model.Category;
import com.library.anishelf.util.fxmlLoader;
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

public class AdvancedSearchBookController {
    @FXML
    private Pagination paginationToNavigate;
    @FXML
    private TextField searchTextField;
    @FXML
    private ChoiceBox<String> categorySelectBox;
    @FXML
    HBox categoryBoxContainer;

    private ScrollPane scrollPaneContainer1 = new ScrollPane(), scrollPaneContainer2 = new ScrollPane();
    private Map<String, Object> criteriaToSearch = new HashMap<>();

    private static final String DASHBOARD_FXML_VIEW = "/view/DashBoard-view.fxml";

    private fxmlLoader fxmlLoader1 = fxmlLoader1.getInstance();
    private List<Book> searchBook;
    private VBox testContainer = new VBox();
    private HBox row1BoxContainer = new HBox(), row2BoxContainer = new HBox();

    /**
     * hàm khởi tạo cho AdvancedSearch
     */
    public void initialize() {
        categorySelectBox.getItems().addAll("title", "category_name", "author_name");
        categorySelectBox.setValue("title");
        scrollPaneContainer1.getStyleClass().add("real-transparent-scrollpane");
        scrollPaneContainer2.getStyleClass().add("real-transparent-scrollpane");

        scrollPaneContainer1.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPaneContainer1.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        scrollPaneContainer2.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPaneContainer2.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        try {
            List<Category> categoryList = BookDAO.getInstance().selectAllCategory();
            for (int i = 0; i < categoryList.size(); i++) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/view/Category-view.fxml"));
                    AnchorPane anchorPane = fxmlLoader.load();
                    CategoryController categoryController = fxmlLoader.getController();
                    categoryController.setDataItem(categoryList.get(i).getCatagoryName(),this);
                    categoryBoxContainer.getChildren().add(anchorPane);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // Không phát âm thanh
        Animation.getInstance().showMessage("Bạn yêu ơi, vì 1 sách có 1 thể thoi nên nó sẽ tìm thể loại cuối bạn chọn nhé! Mình lười code chỉ được chọn 1 cho bạn yêu quá");
    }

    /**
     * từ thanh tìm kiếm truyền vào tìm kiếm cụ thể.
     * @param keyword từ khoá truyền vào
     */
    public void setSearchTextField(String keyword) {
        searchTextField.setText(keyword);
        handleSearchButtonClicked(new ActionEvent());
    }

    /**
     * thêm điều kiện.
     * @param category điều kiện
     */
    public void addCategoryCriteria(String category) {
        criteriaToSearch.put("category_name",category);
    }

    /**
     * xoá điều kiện.
     * @param category điều kiện
     */
    public void deleteCategoryCriteria(String category) {
        Iterator<Map.Entry<String, Object>> iterator = criteriaToSearch.entrySet().iterator();
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
    public void handleBackButtonClicked(ActionEvent event) {
        VBox content = (VBox) fxmlLoader1.loadFXML(DASHBOARD_FXML_VIEW);
        if (content != null) {
            fxmlLoader1.updateContentBox(content);
        }
    }

    /**
     * tìm sách theo điều kiện
     *
     * @param event tìm sách theo điều kiện
     */
    public void handleSearchButtonClicked(ActionEvent event) {
        String keyword = searchTextField.getText();
        String category = categorySelectBox.getValue();

        criteriaToSearch.put(category, keyword);
        UserCommand searchCommand = new UserCommand("searchBookByCategory", criteriaToSearch);

        if (searchCommand.execute()) {
            searchBook = (List<Book>) searchCommand.getObject();
            System.out.println("Tìm thấy " + searchBook.size() + " sách");
        } else {
            System.out.println("Không thấy sách");
        }
        deleteCategoryCriteria(searchTextField.getText());
        showSearchedBook();
    }

    /**
     * tìm xong thì hiển thị ra
     */
    private void showSearchedBook() {
        int numberOfPage = (searchBook.size() - 1) / 12 + 1;
        paginationToNavigate.setPageCount(numberOfPage);
        paginationToNavigate.setPageFactory(pageIndex -> loadMoreBook(pageIndex * 12, Math.min((pageIndex + 1) * 12, searchBook.size())));
    }

    /**
     * load lên các HBox cho đẹp hàng.
     *
     * @param start index bắt đầu
     * @param end   index kết thúc
     * @return vbox đã load sách sau khi tìm
     */
    private VBox loadMoreBook(int start, int end) {
        row1BoxContainer.getChildren().clear();
        row2BoxContainer.getChildren().clear();
        testContainer.getChildren().clear();

        for (int i = start; i < Math.min(start + 6, end); i++) {
            loadMoreBookCard(i, row1BoxContainer);
        }
        for (int i = start + 6; i < Math.min(start + 12, end); i++) {
            loadMoreBookCard(i, row2BoxContainer);
        }
        scrollPaneContainer1.setContent(row1BoxContainer);
        scrollPaneContainer2.setContent(row2BoxContainer);
        testContainer.getChildren().addAll(scrollPaneContainer1, scrollPaneContainer2);
        testContainer.setAlignment(Pos.CENTER);
        return testContainer;
    }

    /**
     * tải lên các sách tìm được.
     * @param index thứ tự của sách
     * @param rowBox hàng chứa sách
     */
    private void loadMoreBookCard(int index, HBox rowBox) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/BookCard2-view.fxml"));
            VBox cardBox = fxmlLoader.load();
            BookCard2Controller cardController = fxmlLoader.getController();
            cardController.setDataBook(searchBook.get(index));
            rowBox.getChildren().add(cardBox);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}