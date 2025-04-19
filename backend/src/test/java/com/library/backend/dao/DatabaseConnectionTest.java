package com.library.backend.dao;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;

class DatabaseConnectionTest {
    @Test
    void testGetConnection() {
        DatabaseConnection instance = DatabaseConnection.getInstance();
        try {
            Connection conn = instance.getConnection();
            assertNotNull(conn);
            assertFalse(conn.isClosed());
        } catch (SQLException e) {
            fail("Should not throw exception: " + e.getMessage());
        }
    }

    @Test
    void testSingletonInstance() {
        DatabaseConnection instance1 = DatabaseConnection.getInstance();
        DatabaseConnection instance2 = DatabaseConnection.getInstance();
        assertSame(instance1, instance2, "Should return the same instance");
    }

    @Test
    void testCloseConnection() {
        DatabaseConnection instance = DatabaseConnection.getInstance();
        try {
            Connection conn = instance.getConnection();
            instance.closeConnection();
            assertTrue(conn.isClosed(), "Connection should be closed");
        } catch (SQLException e) {
            fail("Should not throw exception: " + e.getMessage());
        }
    }
}