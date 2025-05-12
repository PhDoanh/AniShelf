package com.library.anishelf.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

/**
 * The type Base row controller.
 *
 * @param <T> the type parameter
 * @param <P> the type parameter
 */
public abstract class BaseRowController<T, P extends BasePageController> extends BasicController {
    /**
     * The Main row hbox.
     */
    @FXML
    protected HBox mainRowHbox;
    /**
     * The Item.
     */
    protected T item;
    /**
     * The Main controller.
     */
    protected P mainController;

    /**
     * The constant selectedRow.
     */
    protected static BaseRowController selectedRow = null;

    /**
     * Set mainController (PageController).
     *
     * @param controller the controller
     */
    public void setMainController(P controller) {
        this.mainController = controller;
    }

    /**
     * Initialize.
     */
    @FXML
    public void initialize() {
        setupRowClickHandler();
    }

    /**
     * Cài đặt cho hàng nếu được chuột click vào tiến hành gọi hàm handleRowClick().
     */
    private void setupRowClickHandler() {
        mainRowHbox.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                handleRowClick();
            }
        });
    }

    /**
     * Handle row click.
     */
    protected void handleRowClick() {
        updateRowSelection();
        loadDetailView();
    }

    /**
     * Set lại selectedRow ( hàng đang được người dùng chọn).
     * Nếu người dùng chọn hàng nào thì sẽ cho hàng đấy đậm màu hơn.
     */
    protected void updateRowSelection() {
        if (selectedRow != null) {
            selectedRow.resetRowStyle();
        }
        selectedRow = this;
        setSelectedRowStyle();
    }

    /**
     * Đậm màu hơn.
     */
    protected void setSelectedRowStyle() {
        mainRowHbox.setStyle("-fx-background-color: #DDDCDC;");
    }

    /**
     * Trả về bình thường.
     */
    public void resetRowStyle() {
        mainRowHbox.setStyle("");
    }

    /**
     * Set item cho hàng.
     * Gọi updateRowDisplay để cập nhật các trường text trong row.
     *
     * @param item the item
     */
    public void setItem(T item) {
        this.item = item;
        updateRowDisplay();
    }

    /**
     * Khi người dùng bấm vào hàng thì sẽ load Detail của item này
     * thông qua mainController.
     */
    protected void loadDetailView() {
        mainController.loadDetail(item);
    }

    /**
     * Cập nhật các trường text trong row.
     */
    protected abstract void updateRowDisplay();

}

