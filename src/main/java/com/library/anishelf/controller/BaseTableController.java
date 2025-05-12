package com.library.anishelf.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.*;

/**
 * The type Base table controller.
 *
 * @param <T> the type parameter
 * @param <P> the type parameter
 * @param <R> the type parameter
 */
public abstract class BaseTableController<T, P extends BasePageController, R extends BaseRowController<T, P>> extends BasicController {
    /**
     * The Scroll pane.
     */
    @FXML
    protected ScrollPane scrollPane;
    /**
     * The Table vbox.
     */
    @FXML
    protected VBox tableVbox;

    /**
     * The Items list.
     */
    protected ObservableSet<T> itemsList = FXCollections.observableSet(new HashSet<>());
    /**
     * The Main controller.
     */
    protected BasePageController mainController;
    /**
     * The Find criteria.
     */
    protected Map<String, Object> findCriteria = new HashMap<>();

    private static final int LOAD_THRESHOLD = 10;  // Số hàng trước khi đến cuối bảng để kích hoạt tải thêm
    private int loadedItemsCount = 0;  // Theo dõi số hàng hiện tại đã được tải
    private static final int ITEMS_PER_LOAD = 20; // Số hàng sẽ được tải mỗi lần

    /**
     * Gets row fxml.
     *
     * @return the row fxml
     */
    protected abstract String getRowFXML();

    /**
     * Load data from source.
     *
     * @throws SQLException the sql exception
     */
    protected abstract void loadDataFromSource() throws SQLException;

    /**
     * Load lại Data cho Table.
     */
    public void loadData() {
        try {
            itemsList.clear();
            loadDataFromSource();
            loadedItemsCount = 0;
            tableVbox.getChildren().clear();
            loadMoreRows();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load lại các row.
     */
    protected void loadRows() {
        tableVbox.getChildren().clear();

        for (T item : itemsList) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(getRowFXML()));
                HBox row = loader.load();

                BaseRowController rowController = loader.getController();
                rowController.setMainController(this.getMainController());
                rowController.setItem(item);

                childFitWidthParent(row, scrollPane);
                tableVbox.getChildren().add(row);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Làm cho Vbox nằm trong scrollPane resize theo scrollPane
     */
    protected void setVboxFitWithScrollPane() {
        childFitWidthParent(tableVbox, scrollPane);
        childFitHeightParent(tableVbox, scrollPane);
    }

    /**
     * Sau khi lấy set mainController (PageController) thì mới tiến hành load các hàng vào bảng.
     *
     * @param mainController the main controller
     */
    public void setMainController(BasePageController mainController) {
        this.mainController = mainController;
        setScrollListener();
        loadData();
        setVboxFitWithScrollPane();
    }

    /**
     * Lấy mainController (PageController) của fxml này.
     *
     * @return main controller
     */
    public BasePageController getMainController() {
        return mainController;
    }

    /**
     * Thêm lắng nghe scrollPane, nếu lướt thì tải thêm (loadMoreRows).
     */
    private void setScrollListener() {
        scrollPane.addEventFilter(ScrollEvent.SCROLL, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                // Kiểm tra xem người dùng có gần đến cuối bảng không
                if (scrollPane.getContent().getBoundsInLocal().getMaxY() - scrollPane.getVvalue() * scrollPane.getContent().getBoundsInLocal().getHeight() <= LOAD_THRESHOLD) {
                    // Nếu gần đến cuối, tải thêm hàng
                    loadMoreRows();
                }
            }
        });
    }

    /**
     * Load more rows.
     */
    protected void loadMoreRows() {
        if (loadedItemsCount < itemsList.size()) {
            int remainingItems = itemsList.size() - loadedItemsCount;
            int itemsToLoad = Math.min(ITEMS_PER_LOAD, remainingItems);

            // Tải thêm nhóm hàng tiếp theo
            for (int i = 0; i < itemsToLoad; i++) {
                if (loadedItemsCount < itemsList.size()) {
                    T item = (T) itemsList.toArray()[loadedItemsCount];  // Lấy mục tiếp theo từ danh sách
                    loadRow(item);
                    loadedItemsCount++;
                }
            }
        }
    }

    /**
     * Tải các hàng vào Table.
     *
     * @param item the item
     */
    protected void loadRow(T item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(getRowFXML()));
            HBox row = loader.load();
            BaseRowController rowController = loader.getController();
            rowController.setMainController(this.getMainController());
            rowController.setItem(item);
            childFitWidthParent(row, scrollPane);
            tableVbox.getChildren().add(row);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Lấy các tiêu chí tìm kiếm.
     */
    protected abstract void getCriteria();

    /**
     * Sau khi lấy tiêu chí thì đặt vào DAO phù hợp gọi loadRows để trả về các kết quả tìm kiếm dc vào bảng.
     */
    protected abstract void searchCriteria();
}

