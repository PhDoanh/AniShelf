package com.library.anishelf.dao;

import com.library.anishelf.util.RuntimeDebugUtil;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.sql.*;

/**
 * The type Database connection.
 */
public class DatabaseConnection {
    private static DatabaseConnection databaseConnection;
    private static Connection dbConnection;

    private static final String DB_URL = "jdbc:postgresql://217.142.224.197:5432/aniself?TimeZone=Asia/Ho_Chi_Minh";
    private static final String DB_USER = "master";
    private static final String DB_PASSWORD = "doanhanma";

    // Sử dụng RuntimeDebugUtil để gỡ lỗi
    private static final RuntimeDebugUtil logger = RuntimeDebugUtil.getInstance();

    // Hằng số cho các câu SQL thông báo lỗi
    private static final String ERROR_PREPARE_STATEMENT = "Exception in getPrepareStatement: %s";
    private static final String ERROR_SCROLLABLE_RESULT = "Exception in getScrollableResultSet: %s";
    private static final String ERROR_EXECUTE_QUERY = "Exception in Query: %s";

    private DatabaseConnection() {
    }

    /**
     * Lấy instance của DatabaseConnection (Singleton pattern)
     *
     * @return Instance duy nhất của DatabaseConnection
     */
    public static synchronized DatabaseConnection getInstance() {
        if (databaseConnection == null) {
            logger.debug("DatabaseConnection", "Tạo instance mới của DatabaseConnection");
            databaseConnection = new DatabaseConnection();
            databaseConnection.initializeConnection();
        }
        return databaseConnection;
    }

    /**
     * Khởi tạo kết nối đến database
     */
    public void initializeConnection() {
        try {
            logger.debug("DatabaseConnection", "Đang khởi tạo kết nối đến database: " + DB_URL);
            Class.forName("org.postgresql.Driver");
            dbConnection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            logger.info("DatabaseConnection", "Kết nối database thành công");
        } catch (SQLException e) {
            logger.error("DatabaseConnection", "Lỗi SQL khi kết nối database", e);
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.showAndWait();
            System.exit(1);
        } catch (ClassNotFoundException e) {
            logger.error("DatabaseConnection", "Không tìm thấy PostgreSQL JDBC Driver", e);
            Alert alert = new Alert(Alert.AlertType.ERROR, "PostgreSQL JDBC Driver not found: " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
            System.exit(1);
        }
    }

    /**
     * Thực thi câu lệnh SQL không trả về kết quả
     *
     * @param query Câu lệnh SQL cần thực thi
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean execute(String query) {
        logger.debug("DatabaseConnection", "Thực thi câu lệnh: " + query);

        try (Statement statement = dbConnection.createStatement()) {
            statement.execute(query);
            logger.info("DatabaseConnection", "Thực thi câu lệnh thành công");
            return true;
        } catch (SQLException ex) {
            logger.error("DatabaseConnection", String.format(ERROR_EXECUTE_QUERY, query), ex);
            return false;
        }
    }

    /**
     * Thực thi câu lệnh SQL có trả về kết quả
     *
     * @param query Câu lệnh SQL cần thực thi
     * @return ResultSet chứa kết quả truy vấn, hoặc null nếu có lỗi
     */
    public ResultSet runQuery(String query) {
        logger.debug("DatabaseConnection", "Thực thi truy vấn: " + query);

        try {
            Statement statement = dbConnection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            logger.info("DatabaseConnection", "Truy vấn thực thi thành công");
            return resultSet;
        } catch (SQLException ex) {
            logger.error("DatabaseConnection", String.format(ERROR_EXECUTE_QUERY, query), ex);
            return null;
        }
    }

    /**
     * Tạo prepared statement từ câu lệnh SQL
     *
     * @param query Câu lệnh SQL
     * @return PreparedStatement hoặc null nếu có lỗi
     */
    public PreparedStatement prepareStatement(String query) {
        logger.debug("DatabaseConnection", "Tạo PreparedStatement: " + query);

        try {
            PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
            return preparedStatement;
        } catch (SQLException ex) {
            logger.error("DatabaseConnection", String.format(ERROR_PREPARE_STATEMENT, query), ex);
            return null;
        }
    }

    /**
     * Lấy ResultSet có thể cuộn
     *
     * @param query Câu lệnh SQL
     * @return ResultSet có thể cuộn hoặc null nếu có lỗi
     */
    public ResultSet fetchScrollableResultSet(String query) {
        logger.debug("DatabaseConnection", "Lấy scrollable ResultSet: " + query);

        try {
            Statement statement = dbConnection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = statement.executeQuery(query);
            return resultSet;
        } catch (SQLException ex) {
            logger.error("DatabaseConnection", String.format(ERROR_SCROLLABLE_RESULT, query), ex);
            return null;
        }
    }

    /**
     * Lấy đối tượng Connection
     *
     * @return Đối tượng Connection
     */
    public Connection getConnection() {
        return dbConnection;
    }

    /**
     * Đóng tất cả kết nối và giải phóng tài nguyên
     */
    public void closeConnection() {
        logger.debug("DatabaseConnection", "Đang đóng kết nối database");
        try {
            if (dbConnection != null && !dbConnection.isClosed()) {
                dbConnection.close();
                logger.info("DatabaseConnection", "Kết nối database đã đóng thành công");
            }
        } catch (SQLException e) {
            logger.error("DatabaseConnection", "Lỗi khi đóng kết nối database", e);
        }
    }
}
