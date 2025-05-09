package com.library.anishelf.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;

public abstract class BasePageController<T, D extends BaseDetailController<T>,
        TB extends BaseTableController<T, ? extends BasePageController, ? extends BaseRowController>>
        extends BasicController {

    @FXML
    protected Label titlePage;

    protected FXMLLoader detailLoader;
    protected FXMLLoader tableLoader;
    protected Node detailPane;
    protected Node tablePane;

    protected D detailController;
    protected TB tableController;


    protected abstract String getDetailFXMLPath();

    protected abstract String getTableFXMLPath();

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
     * @param item
     */
    public void loadDetail(T item) {
        if (detailController.hasUnsavedChanges()) {
            boolean confirmYes = CustomerAlter.showAlter("Thông tin bạn đang thêm/sửa sẽ bị mất");
            if (confirmYes) {
                loadDetailItem(item);
            }
        } else {
            loadDetailItem(item);
        }
    }

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
            boolean confirmYes = CustomerAlter.showAlter("Thông tin bạn đang thêm/sửa sẽ bị mất");
            if (confirmYes) {
                loadAddNewItem();
            }
        } else {
            loadAddNewItem();
        }
    }

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
        setTitlePage();
        tableController.loadData();
    }

    /**
     * Set tiêu đề cho Page.
     */
    protected void setTitlePage() {
        titlePage.setText(String.join(" / ", getTitlePageStack()));
    }

    public abstract void alterPage();

    public abstract void startPage();

    protected abstract void setupControllers();

    protected abstract void setupViews();

}
