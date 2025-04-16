package com.library.backend.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:postgresql://217.142.224.197:5432/aniself?TimeZone=Asia/Ho_Chi_Minh";
    private static final String USER = "master";
    private static final String PASSWORD = "doanhanma";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
