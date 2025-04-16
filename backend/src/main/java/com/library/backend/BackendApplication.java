package com.library.backend;

import com.library.backend.dao.UserDAO;
import com.library.backend.services.UserService;

import java.util.TimeZone;

public class BackendApplication {
    public static void main(String[] args) {
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

        System.out.println("Starting Backend Application...");

        // Initialize the UserService and UserDAO
        UserDAO userDAO = new UserDAO();
        UserService userService = new UserService();

        // Example usage: Fetch the first user from the database
        String firstUserName = userDAO.getFirstUserName();
        if (firstUserName != null) {
            System.out.println("First user in the database: " + firstUserName);
        } else {
            System.out.println("No users found in the database.");
        }

        // Example usage: Greet a user
        String greeting = userService.greetUser("John");
        System.out.println(greeting);
    }
}