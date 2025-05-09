package com.library.anishelf.controller;

import com.library.anishelf.service.AdminService;
import com.library.anishelf.service.ServiceHandler;

public abstract class BaseDetailController<T> extends BasicController {
    protected T item;
    protected boolean editMode;
    protected boolean addMode;
    protected boolean hasUnsavedChanges;

    protected BasePageController mainController;

    /**
     * Set the main controller
     */
    public void setMainController(BasePageController controller) {
        this.mainController = controller;
    }

    public void setItem(T item) {
        this.item = item;
        loadItemDetails();
        mainController.setTitlePage();
    }

    protected abstract void loadItemDetails();

    /**
     * Set AddMode cho Detail.
     *
     * @param isAdd
     */
    public void setAddMode(boolean isAdd) {
        this.addMode = isAdd;
        updateAddModeUI();
        mainController.setTitlePage();
    }

    protected abstract void updateAddModeUI();

    /**
     * Set EditMode cho Detail.
     *
     * @param enable
     */
    public void setEditMode(boolean enable) {
        this.editMode = enable;
        updateEditModeUI();
        mainController.setTitlePage();
    }

    protected abstract void updateEditModeUI();

    /**
     * Vinh cc.
     */
    protected void saveChanges() {
        try {
            if (addMode) {
                if (validateInput() && getNewItemInformation()) {
                    boolean confirmYes = CustomerAlter.showAlter("Thêm " + getType() + " mới?");
                    if (confirmYes) {
                        ServiceHandler addServiceHandler = new AdminService("add", this.item);
                        serviceInvoker.setServiceHandler(addServiceHandler);
                        if (serviceInvoker.invokeService()) {
                            getTitlePageStack().pop();
                            mainController.loadData();
                            setAddMode(false);
                            System.out.println("Đã lưu thay đổi");
                        }
                    }
                }
            } else {
                if (validateInput() && getNewItemInformation()) {
                    boolean confirmYes = CustomerAlter.showAlter("Bạn có muốn lưu thay đổi " + getType() + " này hay không?");
                    if (confirmYes) {
                        // sửa sách trong CSDL
                        ServiceHandler editServiceHandler = new AdminService("edit", this.item);
                        serviceInvoker.setServiceHandler(editServiceHandler);
                        if (serviceInvoker.invokeService()) {
                            getTitlePageStack().pop();
                            mainController.loadData();
                            loadItemDetails();
                            setEditMode(false);
                            System.out.println("Đã lưu thay đổi");
                        }

                    }
                } else {
                    System.out.println("Tiếp tục edit");
                }
            }
        } catch (Exception e) {
            CustomerAlter.showMessage(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Khi người dùng bấm nút delete thì gọi Service xóa item.
     */
    protected void deleteChanges() {
        try {
            boolean confirmYes = CustomerAlter.showAlter("Bạn có chắc muốn xóa " + getType() + " này chứ?");
            if (confirmYes) {
                ServiceHandler deleteServiceHandler = new AdminService("delete", this.item);
                serviceInvoker.setServiceHandler(deleteServiceHandler);
                if (serviceInvoker.invokeService()) {
                    getTitlePageStack().pop();
                    getTitlePageStack().pop();
                    mainController.loadData();
                    mainController.alterPage();
                    setEditMode(false);
                    System.out.println("Đã lưu thay đổi");
                }
            }
        } catch (Exception e) {
            CustomerAlter.showMessage(e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean hasUnsavedChanges() {
        return hasUnsavedChanges;
    }

    /**
     * load về trạng thái mặc định cho detail.
     */
    public void loadStartStatus() {
        item = null;
        setEditMode(false);
        setAddMode(false);
    }

    /**
     * Kiểm tra xem các thông tin người dùng nhập hợp lệ hay ko.
     *
     * @return
     */
    protected abstract boolean validateInput();

    /**
     * Tạo newItem từ các trường text.
     *
     * @return
     * @throws Exception
     */
    protected abstract boolean getNewItemInformation() throws Exception;

    /**
     * Lấy thể loại của trang nay là gì (Đơn muon sách, Đơn đặt sách,...)
     *
     * @return
     */
    protected abstract String getType();


}
