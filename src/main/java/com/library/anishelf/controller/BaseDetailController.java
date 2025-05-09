package com.library.anishelf.controller;

import com.library.anishelf.service.command.AdminCommand;
import com.library.anishelf.service.command.Command;

public abstract class BaseDetailController<T> extends BasicController {
    protected T Data;
    protected boolean beingEditMode;
    protected boolean beingAddMode;
    protected boolean hasUnsavedChanges;

    protected BasePageAppController mainController;

    /**
     * Set the main controller
     */
    public void connectToParentController(BasePageAppController controller) {
        this.mainController = controller;
    }

    public void setData(T data) {
        this.Data = data;
        fetchBookItemDetails();
        mainController.setTitleOfPage();
    }

    protected abstract void fetchBookItemDetails();

    /**
     * Set AddMode cho Detail.
     *
     * @param isAdd
     */
    public void setBeingAddMode(boolean isAdd) {
        this.beingAddMode = isAdd;
        updateAddModeUIUX();
        mainController.setTitleOfPage();
    }

    protected abstract void updateAddModeUIUX();

    /**
     * Set EditMode cho Detail.
     *
     * @param enable
     */
    public void setBeingEditMode(boolean enable) {
        this.beingEditMode = enable;
        updateEditModeUIUX();
        mainController.setTitleOfPage();
    }

    protected abstract void updateEditModeUIUX();

    /**
     * Vinh cc.
     */
    protected void saveDetailChanges() {
        try {
            if (beingAddMode) {
                if (validateDataItemInput() && getNewDataItemInformation()) {
                    boolean confirmYes = CustomerAlter.showAlter("Thêm " + getItemType() + " mới?");
                    if (confirmYes) {
                        Command addCommand = new AdminCommand("add", this.Data);
                        commandInvoker.setCommand(addCommand);
                        if (commandInvoker.executeCommand()) {
                            getTitlePageStack().pop();
                            mainController.refreshPageData();
                            setBeingAddMode(false);
                            System.out.println("Đã lưu thay đổi");
                        }
                    }
                }
            } else {
                if (validateDataItemInput() && getNewDataItemInformation()) {
                    boolean confirmYes = CustomerAlter.showAlter("Bạn có muốn lưu thay đổi " + getItemType() + " này hay không?");
                    if (confirmYes) {
                        // sửa sách trong CSDL
                        Command editCommand = new AdminCommand("edit", this.Data);
                        commandInvoker.setCommand(editCommand);
                        if (commandInvoker.executeCommand()) {
                            getTitlePageStack().pop();
                            mainController.refreshPageData();
                            fetchBookItemDetails();
                            setBeingEditMode(false);
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
     * Khi người dùng bấm nút delete thì gọi command xóa Data.
     */
    protected void deleteDetailChanges() {
        try {
            boolean confirmYes = CustomerAlter.showAlter("Bạn có chắc muốn xóa " + getItemType() + " này chứ?");
            if (confirmYes) {
                Command deleteCommand = new AdminCommand("delete", this.Data);
                commandInvoker.setCommand(deleteCommand);
                if (commandInvoker.executeCommand()) {
                    getTitlePageStack().pop();
                    getTitlePageStack().pop();
                    mainController.refreshPageData();
                    mainController.switchPage();
                    setBeingEditMode(false);
                    System.out.println("Đã lưu thay đổi");
                }
            }
        } catch (Exception e) {
            CustomerAlter.showMessage(e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean hasUnsavedDetailsChanges() {
        return hasUnsavedChanges;
    }

    /**
     * load về trạng thái mặc định cho detail.
     */
    public void loadInitalStatus() {
        Data = null;
        setBeingEditMode(false);
        setBeingAddMode(false);
    }

    /**
     * Kiểm tra xem các thông tin người dùng nhập hợp lệ hay ko.
     *
     * @return
     */
    protected abstract boolean validateDataItemInput();

    /**
     * Tạo newItem từ các trường text.
     *
     * @return
     * @throws Exception
     */
    protected abstract boolean getNewDataItemInformation() throws Exception;

    /**
     * Lấy thể loại của trang nay là gì (Đơn muon sách, Đơn đặt sách,...)
     *
     * @return
     */
    protected abstract String getItemType();


}
