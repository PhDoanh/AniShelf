package com.library.anishelf.controller;

import com.library.anishelf.util.NotificationManagerUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;

/**
 * The type Base page controller.
 *
 * @param <T>  the type parameter
 * @param <D>  the type parameter
 * @param <TB> the type parameter
 */
public abstract class BasePageController<T, D extends BaseDetailController<T>,
        TB extends BaseTableController<T, ? extends BasePageController, ? extends BaseRowController>>
        extends BasicController {

    /**
     * The Detail loader.
     */
    protected FXMLLoader detailLoader;
    /**
     * The Table loader.
     */
    protected FXMLLoader tableLoader;
    /**
     * The Detail pane.
     */
    protected Node detailPane;
    /**
     * The Table pane.
     */
    protected Node tablePane;

    /**
     * The Detail controller.
     */
    protected D detailController;
    /**
     * The Table controller.
     */
    protected TB tableController;


    /**
     * Gets detail fxml path.
     *
     * @return the detail fxml path
     */
    protected abstract String getDetailFXMLPath();

    /**
     * Gets table fxml path.
     *
     * @return the table fxml path
     */
    protected abstract String getTableFXMLPath();

    /**
     * The Page 1.
     */
    protected boolean page1 = true;

    /**
     * Hàm khởi tạo.
     * Đặt detailPane vào vị trí detail, Table vào vị trí Table.
     * Sau đó loadData cho Table.
     */
    @FXML
    public void initialize() {
        try {
            detailLoader = loadFXML(getDetailFXMLPath(), BaseDetailController.class);
            detailPane = loadPane(detailLoader, BaseDetailController.class);
            detailController = detailLoader.getController();
            detailController.setMainController(this);
            if (detailLoader == null) {
                System.out.println("detailLoader is null");
            }

            tableLoader = loadFXML(getTableFXMLPath(), BaseTableController.class);
            tablePane = loadPane(tableLoader, BaseTableController.class);
            tableController = tableLoader.getController();
            tableController.setMainController(this);

            setupViews();
            startPage();
            loadData();
            setupControllers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Load Detail cho item.
     *
     * @param item the item
     */
    public void loadDetail(T item) {
        if (detailController.hasUnsavedChanges()) {
            NotificationManagerUtil.showConfirmation("Thông tin đang thêm/sửa sẽ bị mất", confirmed -> {
                if (confirmed) {
                    loadDetailItem(item);
                }
            });
        } else {
            loadDetailItem(item);
        }
    }

    /**
     * Load detail item.
     *
     * @param item the item
     */
    protected void loadDetailItem(T item) {
        detailController.loadStartStatus();
        detailController.setItem(item);
        if (page1) {
            alterPage();
        }
    }

    /**
     * Dùng để tạo một form để thêm Item
     */
    public void loadAddPane() {
        getTitlePageStack().push("Add");
        if (detailController.hasUnsavedChanges()) {
            NotificationManagerUtil.showConfirmation("Thông tin đang thêm/sửa sẽ bị mất", confirmed -> {
                if (confirmed) {
                    loadAddNewItem();
                }
            });
        } else {
            loadAddNewItem();
        }
    }

    /**
     * Load add new item.
     */
    protected void loadAddNewItem() {
        detailController.loadStartStatus();
        detailController.setAddMode(true);
        if (page1) {
            alterPage();
        }
    }

    /**
     * Load lại Data trong Table và đặt lại tiêu đề cho trang.
     */
    public void loadData() {
        tableController.loadData();
    }

    /**
     * Alter page.
     */
    public abstract void alterPage();

    /**
     * Start page.
     */
    public abstract void startPage();

    /**
     * Sets controllers.
     */
    protected abstract void setupControllers();

    /**
     * Sets views.
     */
    protected abstract void setupViews();

}
