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

public class UsersTableController extends BaseTableController<Member, UsersPageController, UsersTableRowController> {
    protected static final String ROW_FXML = "/view/UsersTableRow.fxml";
    @FXML
    private Button addButton;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextField searchText;

    @FXML
    private VBox tableVbox; /* đổi tên thành TableVbox*/

    private String findValue;

    private AdminHomePageController adminHomePageController;

    @Override
    protected String getRowFXML() {
        return ROW_FXML;
    }

    @Override
    protected void loadDataFromSource() throws SQLException {
        itemsList.addAll(MemberDAO.getInstance().findAll());
        adminHomePageController.setTotalReaderLabel(itemsList.size()+"");
    }

    @FXML
    public void initialize() {
        adminHomePageController = dashboardLoader.getController();
        // Lắng nghe sự thay đổi trong TextField tìm kiếm
        searchText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != null && !newValue.trim().isEmpty()) {
                    findValue = newValue;
                    searchCriteria();
                } else {
                    // Nếu trường tìm kiếm rỗng, tải lại toàn bộ dữ liệu
                    loadData();
                }
            }
        });
    }

    @FXML
    void onAddButtonAction(ActionEvent event) {
        mainController.loadAddPane();
    }

    @Override
    protected void getCriteria() {
    }
    @Override
    protected void searchCriteria() {
        itemsList.clear();
        try {
            findCriteria.clear();
            findCriteria.put("phone", findValue);
            itemsList.addAll(MemberDAO.getInstance().findByCriteria(findCriteria));

            findCriteria.clear();
            findCriteria.put("member_id", findValue);
            itemsList.addAll(MemberDAO.getInstance().findByCriteria(findCriteria));

            findCriteria.clear();
            findCriteria.put("fullname", findValue);
            itemsList.addAll(MemberDAO.getInstance().findByCriteria(findCriteria));


        } catch (SQLException e) {
            e.printStackTrace();
        }
        loadRows();
    }


}
