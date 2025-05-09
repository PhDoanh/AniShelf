package com.library.anishelf.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

public abstract class BaseAppRowController<T, P extends BasePageController> extends BasicController {
    @FXML
    protected HBox mainRowContainer;
    protected T Data;
    protected P parentController;

    protected static BaseAppRowController selectedRowww = null;

    /**
     * Set parentController (PageController).
     *
     * @param controller
     */
    public void connectToParentController(P controller) {
        this.parentController = controller;
    }

    @FXML
    public void initialize() {
        initializeRowClickHandler();
    }

    /**
     * Cài đặt cho hàng nếu được chuột click vào tiến hành gọi hàm handleRowItemClick().
     */
    private void initializeRowClickHandler() {
        mainRowContainer.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                handleRowClick();
            }
        });
    }

    protected void handleRowClick() {
        updateRowSelection();
        loadDetailView();
    }

    /**
     * Set lại selectedRowww ( hàng đang được người dùng chọn).
     * Nếu người dùng chọn hàng nào thì sẽ cho hàng đấy đậm màu hơn.
     */
    protected void updateRowSelection() {
        if (selectedRowww != null) {
            selectedRowww.resetStyleOfRow();
        }
        selectedRowww = this;
        setSelectedRowStyle();
    }

    /**
     * Đậm màu hơn.
     */
    protected void setSelectedRowStyle() {
        mainRowContainer.setStyle("-fx-background-color: #DDDCDC;");
    }

    /**
     * Trả về bình thường.
     */
    public void resetStyleOfRow() {
        mainRowContainer.setStyle("");
    }

    /**
     * Set Data cho hàng.
     * Gọi updateRowOfTableDisplay để cập nhật các trường text trong row.
     *
     * @param data
     */
    public void setData(T data) {
        this.Data = data;
        updateRowOfTableDisplay();
    }

    /**
     * Khi người dùng bấm vào hàng thì sẽ load Detail của Data này
     * thông qua parentController.
     */
    protected void loadDetailView() {
        parentController.loadDetail(Data);
    }

    /**
     * Cập nhật các trường text trong row.
     */
    protected abstract void updateRowOfTableDisplay();

}

