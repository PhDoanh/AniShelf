package com.library.anishelf.service;

import com.library.anishelf.util.NotificationManagerUtil;
import com.library.anishelf.controller.AdminNavBarController;
import com.library.anishelf.controller.BasicController;
import com.library.anishelf.dao.AccountDAO;
import com.library.anishelf.model.enums.Role;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import com.library.anishelf.controller.NavigationBarController;
public class AuthenticationService extends BasicController implements ServiceHandler {
    private Role role;
    private String password;
    private String username;
    private Stage stage;
    private int memberId;
    private int adminId;

    public AuthenticationService(Stage stage, Role role, String username, String password) {
        this.role = role;
        this.username = username;
        this.password = password;
        this.stage = stage;
    }

    @Override
    public boolean handleRequest() {
        try {
            if (username.isEmpty() || password.isEmpty()) {
                System.out.println("Username or password is empty");
            }
            if (validateCredentials()) {
                navigateToMenu();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    private boolean validateCredentials() throws SQLException {
        if (role.equals(Role.NONE)) {
            if(AccountDAO.getInstance().validateUserLogin(username, password) != 0) {
                this.memberId = AccountDAO.getInstance().validateUserLogin(username,password);
                NotificationManagerUtil.showInfo("Đăng nhập thành công với tư cách người dùng");
                return true;
            }
        } else if (role.equals(Role.ADMIN)) {
            if (AccountDAO.getInstance().validateAdminLogin(username, password)!=0) {
                this.adminId = AccountDAO.getInstance().validateAdminLogin(username,password);
                NotificationManagerUtil.showInfo("Đăng nhập thành công với tư cách quản trị viên");
                return true;
            }
        }
        NotificationManagerUtil.showInfo("Thông tin đăng nhập bị sai");
        return false;
    }

    private void navigateToMenu() {
        if (role.equals(Role.ADMIN)) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/AdminNavBar.fxml"));
                Parent root = fxmlLoader.load();

                AdminNavBarController controller = fxmlLoader.getController();
                controller.setAdminID(adminId);

                Scene scene = new Scene(root);
                this.stage.setResizable(true);
                stage.setWidth(stage.getWidth());
                stage.setHeight(stage.getHeight());
                this.stage.setScene(scene);
                this.stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (role.equals(Role.NONE)) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/NavigationBar.fxml"));
                Parent root = fxmlLoader.load();

                NavigationBarController userMenu = fxmlLoader.getController();

                userMenu.setMemberID(memberId);
                stage.setResizable(true);
                stage.setWidth(stage.getWidth());
                stage.setHeight(stage.getHeight());
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
