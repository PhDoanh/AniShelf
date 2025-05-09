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

public abstract class BaseAppTableController<T, P extends BasePageAppController, R extends BaseAppRowController<T, P>> extends BasicController {
    @FXML
    protected ScrollPane scrollPaneContainer;
    @FXML
    protected VBox tableBookPane;

    protected ObservableSet<T> dataItems = FXCollections.observableSet(new HashSet<>());
    protected BasePageAppController mainController;
    protected Map<String, Object> searchFilters = new HashMap<>();

    private static final int SCROLL_LOAD_TRIGGER_THRESHOL = 10;  // Số hàng trước khi đến cuối bảng để kích hoạt tải thêm
    private int currentItemCount = 0;  // Theo dõi số hàng hiện tại đã được tải
    private static final int ITEMS_PER_LOAD_DATA = 20; // Số hàng sẽ được tải mỗi lần

    protected abstract String getItemRowFXML();

    protected abstract void fetchDataFromSource() throws SQLException;

    /**
     * Load lại Data cho Table.
     */
    public void refreshTableData() {
        try {
            dataItems.clear();
            fetchDataFromSource();
            currentItemCount = 0;
            tableBookPane.getChildren().clear();
            loadAdditionalItems();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load lại các row.
     */
    protected void renderTableRows() {
        tableBookPane.getChildren().clear();

        for (T item : dataItems) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(getItemRowFXML()));
                HBox row = loader.load();

                BaseAppRowController rowController = loader.getController();
                rowController.connectToParentController(this.getMainnnnController());
                rowController.setData(item);

                childFitWidthParent(row, scrollPaneContainer);
                tableBookPane.getChildren().add(row);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Làm cho Vbox nằm trong scrollPaneContainer resize theo scrollPaneContainer
     */
    protected void setContainerFitWithScrollPane() {
        childFitWidthParent(tableBookPane, scrollPaneContainer);
        childFitHeightParent(tableBookPane, scrollPaneContainer);
    }

    /**
     * Sau khi lấy set parentController (PageController) thì mới tiến hành load các hàng vào bảng.
     *
     * @param mainController
     */
    public void setMainnnController(BasePageAppController mainController) {
        this.mainController = mainController;
        initializeScrollPagination();
        refreshTableData();
        setContainerFitWithScrollPane();
    }

    /**
     * Lấy parentController (PageController) của fxml này.
     *
     * @return
     */
    public BasePageAppController getMainnnnController() {
        return mainController;
    }

    /**
     * Thêm lắng nghe scrollPaneContainer, nếu lướt thì tải thêm (loadAdditionalItems).
     */
    private void initializeScrollPagination() {
        scrollPaneContainer.addEventFilter(ScrollEvent.SCROLL, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                // Kiểm tra xem người dùng có gần đến cuối bảng không
                if (scrollPaneContainer.getContent().getBoundsInLocal().getMaxY() - scrollPaneContainer.getVvalue() * scrollPaneContainer.getContent().getBoundsInLocal().getHeight() <= SCROLL_LOAD_TRIGGER_THRESHOL) {
                    // Nếu gần đến cuối, tải thêm hàng
                    loadAdditionalItems();
                }
            }
        });
    }

    protected void loadAdditionalItems() {
        if (currentItemCount < dataItems.size()) {
            int remainingItems = dataItems.size() - currentItemCount;
            int itemsToLoad = Math.min(ITEMS_PER_LOAD_DATA, remainingItems);

            // Tải thêm nhóm hàng tiếp theo
            for (int i = 0; i < itemsToLoad; i++) {
                if (currentItemCount < dataItems.size()) {
                    T item = (T) dataItems.toArray()[currentItemCount];  // Lấy mục tiếp theo từ danh sách
                    addItemToTable(item);
                    currentItemCount++;
                }
            }
        }
    }

    /**
     * Tải các hàng vào Table.
     *
     * @param item
     */
    protected void addItemToTable(T item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(getItemRowFXML()));
            HBox row = loader.load();
            BaseAppRowController rowController = loader.getController();
            rowController.connectToParentController(this.getMainnnnController());
            rowController.setData(item);
            childFitWidthParent(row, scrollPaneContainer);
            tableBookPane.getChildren().add(row);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Lấy các tiêu chí tìm kiếm.
     */
    protected abstract void getItemCriteria();

    /**
     * Sau khi lấy tiêu chí thì đặt vào DAO phù hợp gọi renderTableRows để trả về các kết quả tìm kiếm dc vào bảng.
     */
    protected abstract void searchItemCriteria();
}

