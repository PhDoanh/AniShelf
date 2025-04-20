package com.library.backend.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public String getFirstUserName() {
        String sql = "SELECT name FROM Users LIMIT 1";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) { // ...changed code...
            e.printStackTrace();
        }
        return null;
    }
}
