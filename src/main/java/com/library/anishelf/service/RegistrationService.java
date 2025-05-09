package com.library.anishelf.service;

import com.library.anishelf.controller.CustomerAlter;
import com.library.anishelf.dao.AccountDAO;
import com.library.anishelf.model.Person;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * The type Registration service.
 */
public class RegistrationService implements ServiceHandler {
    private Stage stage;
    private Person person;
    private String username;
    private String password;

    /**
     * Instantiates a new Registration service.
     *
     * @param stage    the stage
     * @param person   the person
     * @param username the username
     * @param password the password
     */
    public RegistrationService(Stage stage, Person person, String username, String password) {
        this.stage = stage;
        this.person = person;
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean handleRequest() {
        try {
            if (AccountDAO.getInstance().registerNewMember(person, username, password)) {
                CustomerAlter.showMessage("Đăng ký tài khoản thành công.");
                return true;
            }
        } catch (SQLException e) {
            CustomerAlter.showMessage(e.getMessage());
        }
        return false;
    }

}
