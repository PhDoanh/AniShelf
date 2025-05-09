package com.library.anishelf.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;

public abstract class BasePageAppController<T, D extends BaseDetailController<T>,
        TB extends BaseAppTableController<T, ? extends BasePageAppController, ? extends BaseAppRowController>>
        extends BasicController {

    @FXML
    protected Label titleOfPage;

    protected FXMLLoader detailViewLoader;
    protected FXMLLoader tableViewLoader;
    protected Node detailContentPane;
    protected Node tableContentPane;

    protected D detailViewController;
    protected TB tableViewController;


    protected abstract String getItemDetailFXMLPath();

    protected abstract String getItemTableFXMLPath();

    protected boolean isTableViewActive = true;

    /**
     * Hàm khởi tạo.
     * Đặt detailContentPane vào vị trí detail, Table vào vị trí Table.
     * Sau đó refreshTableData cho Table.
     */
    @FXML
    public void initialize() {
        try {
            detailViewLoader = loadFXML(getItemDetailFXMLPath(), BaseDetailController.class);
            detailContentPane = loadPane(detailViewLoader, BaseDetailController.class);
            detailViewController = detailViewLoader.getController();
            detailViewController.connectToParentController(this);
            if (detailViewLoader == null) {
                System.out.println("detailViewLoader is null");
            }

            tableViewLoader = loadFXML(getItemTableFXMLPath(), BaseAppTableController.class);
            tableContentPane = loadPane(tableViewLoader, BaseAppTableController.class);
            tableViewController = tableViewLoader.getController();
            tableViewController.setMainnnController(this);

            initializeViews();
            initalPage();
            refreshPageData();
            initializeControllers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Load Detail cho Data.
     *
     * @param item
     */
    public void loadDetail(T item) {
        if (detailViewController.hasUnsavedDetailsChanges()) {
            boolean confirmYes = CustomerAlter.showAlter("Thông tin bạn đang thêm/sửa sẽ bị mất");
            if (confirmYes) {
                renderDetailView(item);
            }
        } else {
            renderDetailView(item);
        }
    }

    protected void renderDetailView(T item) {
        detailViewController.loadInitalStatus();
        detailViewController.setData(item);
        if (isTableViewActive) {
            switchPage();
        }
    }

    /**
     * Dùng để tạo một form để thêm Item
     */
    public void openCreateItemForm() {
        getTitlePageStack().push("Add");
        if (detailViewController.hasUnsavedDetailsChanges()) {
            boolean confirmYes = CustomerAlter.showAlter("Thông tin bạn đang thêm/sửa sẽ bị mất");
            if (confirmYes) {
                prepareNewItemForm();
            }
        } else {
            prepareNewItemForm();
        }
    }

    protected void prepareNewItemForm() {
        detailViewController.loadInitalStatus();
        detailViewController.setBeingAddMode(true);
        if (isTableViewActive) {
            switchPage();
        }
    }

    /**
     * Load lại Data trong Table và đặt lại tiêu đề cho trang.
     */
    public void refreshPageData() {
        setTitleOfPage();
        tableViewController.refreshTableData();
    }

    /**
     * Set tiêu đề cho Page.
     */
    protected void setTitleOfPage() {
        titleOfPage.setText(String.join(" / ", getTitlePageStack()));
    }

    public abstract void switchPage();

    public abstract void initalPage();

    protected abstract void initializeControllers();

    protected abstract void initializeViews();

}
