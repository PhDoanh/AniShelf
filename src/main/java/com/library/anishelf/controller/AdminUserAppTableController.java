package com.library.anishelf.controller;

import com.library.anishelf.dao.MemberDAO;
import com.library.anishelf.model.Member;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.sql.SQLException;

public class AdminUserAppTableController extends BaseTableController<Member, AdminUserPageController, AdminUserTableRowController> {
    protected static final String ROW_FXML_VIEW = "/view/AdminUserTableRow.fxml";
    @FXML
    private Button addButton;

    @FXML
    private ScrollPane scrollPaneContainer;

    @FXML
    private TextField searchTextData;

    @FXML
    private VBox tableBookPane; /* đổi tên thành tableBookPane*/

    private String findValueItem;

    private AdminMainDashboardController adminMainDashboardController;

    @Override
    protected String getItemRowFXML() {
        return ROW_FXML_VIEW;
    }

    @Override
    protected void fetchDataFromSource() throws SQLException {
        itemsList.addAll(MemberDAO.getInstance().selectAll());
        adminMainDashboardController.setTotalOfReaderLabel(itemsList.size()+"");
    }

    @FXML
    public void initialize() {
        adminMainDashboardController = dashboardLoader.getController();
        // Lắng nghe sự thay đổi trong TextField tìm kiếm
        searchTextData.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != null && !newValue.trim().isEmpty()) {
                    findValueItem = newValue;
                    searchItemCriteria();
                } else {
                    // Nếu trường tìm kiếm rỗng, tải lại toàn bộ dữ liệu
                    loadData();
                }
            }
        });
    }

    @FXML
    void handleAddButton(ActionEvent event) {
        mainController.loadAddPane();
    }

    @Override
    protected void getItemCriteria() {
    }
    @Override
    protected void searchItemCriteria() {
        itemsList.clear();
        try {
            findCriteria.clear();
            findCriteria.put("phone", findValueItem);
            itemsList.addAll(MemberDAO.getInstance().searchByCriteria(findCriteria));

            findCriteria.clear();
            findCriteria.put("member_id", findValueItem);
            itemsList.addAll(MemberDAO.getInstance().searchByCriteria(findCriteria));

            findCriteria.clear();
            findCriteria.put("fullname", findValueItem);
            itemsList.addAll(MemberDAO.getInstance().searchByCriteria(findCriteria));


        } catch (SQLException e) {
            e.printStackTrace();
        }
        loadRows();
    }


}
