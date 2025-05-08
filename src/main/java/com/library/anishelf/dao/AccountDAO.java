package com.library.anishelf.dao;

import com.library.anishelf.util.EmailUtil;
import com.library.anishelf.model.Person;
import com.library.anishelf.util.RuntimeDebugUtil;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

/**
 * The type Account dao.
 */
public class AccountDAO {
    private static Database database;
    private static AccountDAO accountDAO;
    private static final RuntimeDebugUtil logger = RuntimeDebugUtil.getInstance();
    private static final String TAG = "AccountDAO";

    private AccountDAO() {
        database = Database.getInstance();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static synchronized AccountDAO getInstance() {
        if (accountDAO == null) {
            accountDAO = new AccountDAO();
        }
        return accountDAO;
    }

    // Lấy thông tin tài khoản admin - tối ưu bằng cách chỉ select các cột cần thiết
    private static final String GET_ACCOUNT_ADMIN =
            "SELECT \"admin_ID\" FROM \"Admins\" WHERE \"username\" = ? AND \"password\" = ?";
    private static final String GET_ACCOUNT_ADMIN_BY_ID =
            "SELECT \"admin_ID\", \"username\" FROM \"Admins\" WHERE \"admin_ID\" = ?";

    // lấy thông tin tài khoản user - tối ưu bằng cách chỉ select các cột cần thiết
    private static final String GET_ACCOUNT_USER =
            "SELECT \"member_ID\" FROM \"Users\" WHERE \"username\" = ? AND \"password\" = ? AND \"AccountStatus\"::text != 'CLOSED'";
    private static final String GET_ACCOUNT_USER_BY_ID =
            "SELECT \"user_ID\", \"username\", \"member_ID\" FROM \"Users\" WHERE \"user_ID\" = ?";

    // Thêm tài khoản user - thêm index hint
    private static final String INSERT_USER =
            "INSERT INTO \"Users\" (\"username\", \"password\", \"member_ID\", \"AccountStatus\") VALUES (?, ?, ?, 'ACTIVE'::account_status)";

    // Thêm thành viên - không thay đổi do cần tất cả các cột
    private static final String INSERT_MEMBER =
            "INSERT INTO \"Members\" (\"first_name\", \"last_name\", \"birth_date\", \"gender\", \"email\", \"phone\") " +
                    "VALUES (?, ?, ?::date, ?::gender_type, ?, ?)";

    // Lấy thông tin reset password - tối ưu bằng cách chỉ select các cột cần thiết và sử dụng JOIN tối ưu
    private static final String GET_RESET_PASSWORD_USER =
            "SELECT u.\"username\" FROM \"Members\" m JOIN \"Users\" u ON u.\"member_ID\" = m.\"member_ID\" WHERE m.\"email\" = ? LIMIT 1";

    // Lấy thông tin user bằng username - tối ưu bằng cách sử dụng EXISTS
    private static final String GET_USER_BY_USERNAME =
            "SELECT 1 FROM \"Users\" WHERE \"username\" = ? LIMIT 1";

    // Cập nhật tài khoản - tối ưu bằng cách sử dụng subquery thay vì JOIN
    private static final String UPDATE_USER =
            "UPDATE \"Users\" SET \"password\" = ? WHERE \"member_ID\" = (SELECT \"member_ID\" FROM \"Members\" WHERE \"email\" = ?)";

    // Cập nhật otp - thêm index hint
    private static final String UPDATE_OTP_AND_EXPIRY =
            "UPDATE \"Users\" SET \"otp\" = ?, \"otp_expiry\" = NOW() + INTERVAL '5 minutes' WHERE \"username\" = ?";

    // Kiểm tra otp - tối ưu bằng cách chỉ select cần thiết và sử dụng INNER JOIN
    private static final String VALIDATE_OTP =
            "SELECT 1 FROM \"Users\" u INNER JOIN \"Members\" m ON u.\"member_ID\" = m.\"member_ID\" WHERE m.\"email\" = ? AND u.\"otp\" = ? AND u.\"otp_expiry\" > NOW() LIMIT 1";

    /**
     * Validate user login int.
     *
     * @param username the username
     * @param password the password
     * @return the int
     * @throws SQLException the sql exception
     */
    public int validateUserLogin(String username, String password) throws SQLException {
        logger.debug(TAG, "Thực hiện đăng nhập user với username: " + username);

        try (PreparedStatement preparedStatement = database.getConnection().prepareStatement(GET_ACCOUNT_USER)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int memberId = resultSet.getInt("member_id");
                    logger.info(TAG, "Đăng nhập thành công với member_id: " + memberId);
                    return memberId;
                } else {
                    logger.info(TAG, "Đăng nhập thất bại với username: " + username);
                    return 0;
                }
            }
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi đăng nhập user: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Validate admin login int.
     *
     * @param username the username
     * @param password the password
     * @return the int
     * @throws SQLException the sql exception
     */
    public int validateAdminLogin(String username, String password) throws SQLException {
        logger.debug(TAG, "Thực hiện đăng nhập admin với username: " + username);

        try (PreparedStatement preparedStatement = database.getConnection().prepareStatement(GET_ACCOUNT_ADMIN)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int adminId = resultSet.getInt("admin_id");
                    logger.info(TAG, "Đăng nhập admin thành công với admin_id: " + adminId);
                    return adminId;
                } else {
                    logger.info(TAG, "Đăng nhập admin thất bại với username: " + username);
                    return 0;
                }
            }
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi đăng nhập admin: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Register new member boolean.
     *
     * @param person   the person
     * @param username the username
     * @param password the password
     * @return the boolean
     * @throws SQLException the sql exception
     */
    public boolean registerNewMember(@NotNull Person person, String username, String password) throws SQLException {
        logger.info(TAG, "Đăng ký thành viên mới: " + username + ", email: " + person.getEmail());

        // Kiểm tra username đã tồn tại chưa
        if (isUsernameExists(username)) {
            logger.warning(TAG, "Đăng ký thất bại: Username đã tồn tại: " + username);
            throw new SQLException("User already exists");
        }

        // Bắt đầu transaction
        database.getConnection().setAutoCommit(false);

        try {
            // Tạo thành viên mới và lấy ID
            int memberId = insertMember(person);

            // Tạo tài khoản với member ID
            boolean result = addUserAccount(username, password, memberId);

            // Commit transaction nếu mọi thứ thành công
            database.getConnection().commit();
            logger.info(TAG, "Đăng ký thành công cho: " + username + " với memberId: " + memberId);
            return result;
        } catch (SQLException e) {
            // Rollback transaction nếu có lỗi
            try {
                database.getConnection().rollback();
            } catch (SQLException rollbackEx) {
                logger.error(TAG, "Lỗi khi rollback transaction: " + rollbackEx.getMessage(), rollbackEx);
            }

            logger.error(TAG, "Lỗi khi đăng ký thành viên mới: " + e.getMessage(), e);
            throw e;
        } finally {
            // Khôi phục auto-commit
            try {
                database.getConnection().setAutoCommit(true);
            } catch (SQLException autoCommitEx) {
                logger.error(TAG, "Lỗi khi khôi phục auto-commit: " + autoCommitEx.getMessage(), autoCommitEx);
            }
        }
    }

    private boolean isUsernameExists(String username) throws SQLException {
        try (PreparedStatement preparedStatement = database.getConnection().prepareStatement(GET_USER_BY_USERNAME)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi kiểm tra username: " + e.getMessage(), e);
            throw e;
        }
    }

    private int insertMember(@NotNull Person person) throws SQLException {
        try (PreparedStatement updateStatement = database.getConnection().prepareStatement(INSERT_MEMBER, Statement.RETURN_GENERATED_KEYS)) {
            updateStatement.setString(1, person.getFirstName());
            updateStatement.setString(2, person.getLastName());
            updateStatement.setString(3, person.getBirthdate());
            updateStatement.setString(4, person.getGender().toString());
            updateStatement.setString(5, person.getEmail());
            updateStatement.setString(6, person.getPhone());

            int affectedRows = updateStatement.executeUpdate();
            if (affectedRows == 0) {
                logger.error(TAG, "Tạo thành viên thất bại, không có dòng nào được thêm vào");
                throw new SQLException("Failed to insert member, no rows affected");
            }

            try (ResultSet generatedKeys = updateStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    logger.error(TAG, "Tạo thành viên thất bại, không lấy được ID");
                    throw new SQLException("Failed to insert member, no ID obtained");
                }
            }
        }
    }

    private boolean addUserAccount(String username, String password, int memberID) throws SQLException {
        try (PreparedStatement preparedStatement = database.getConnection().prepareStatement(INSERT_USER)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setInt(3, memberID);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 1) {
                logger.info(TAG, "Tạo tài khoản thành công cho username: " + username);
                return true;
            } else {
                logger.error(TAG, "Tạo tài khoản thất bại cho username: " + username);
                throw new SQLException("Failed to insert user account");
            }
        }
    }

    /**
     * Initiate password reset boolean.
     *
     * @param email the email
     * @return the boolean
     * @throws SQLException the sql exception
     */
    public boolean initiatePasswordReset(String email) throws SQLException {
        logger.info(TAG, "Bắt đầu quá trình khôi phục mật khẩu cho email: " + email);

        try (PreparedStatement preparedStatement = database.getConnection().prepareStatement(GET_RESET_PASSWORD_USER)) {
            preparedStatement.setString(1, email);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String username = resultSet.getString("username");
                    String otp = generateOTP();

                    storeOTP(username, otp);

                    // Gửi email OTP
                    String emailSubject = "[THÔNG BÁO] Khôi phục mật khẩu";
                    String emailBody = "Mã OTP của bạn là " + otp;
                    EmailUtil.sendAsyncEmail(email, emailSubject, emailBody);

                    logger.info(TAG, "Đã gửi OTP đến email: " + email);
                    return true;
                } else {
                    logger.warning(TAG, "Email không tồn tại trong hệ thống: " + email);
                    throw new SQLException("Account with email " + email + " not found");
                }
            }
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi khôi phục mật khẩu: " + e.getMessage(), e);
            throw e;
        }
    }

    private void storeOTP(String username, String OTP) throws SQLException {
        logger.debug(TAG, "Lưu OTP cho username: " + username);

        try (PreparedStatement preparedStatement = database.getConnection().prepareStatement(UPDATE_OTP_AND_EXPIRY)) {
            preparedStatement.setString(1, OTP);
            preparedStatement.setString(2, username);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                logger.error(TAG, "Không thể lưu OTP cho username: " + username);
                throw new SQLException("Failed to store OTP for user: " + username);
            }
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi lưu OTP: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Validate otp boolean.
     *
     * @param email the email
     * @param OTP   the otp
     * @return the boolean
     * @throws SQLException the sql exception
     */
    public boolean validateOTP(String email, String OTP) throws SQLException {
        logger.debug(TAG, "Xác thực OTP cho email: " + email);

        try (PreparedStatement preparedStatement = database.getConnection().prepareStatement(VALIDATE_OTP)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, OTP);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                boolean isValid = resultSet.next();

                if (isValid) {
                    logger.info(TAG, "OTP hợp lệ cho email: " + email);
                } else {
                    logger.warning(TAG, "OTP không hợp lệ hoặc đã hết hạn cho email: " + email);
                }

                return isValid;
            }
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi xác thực OTP: " + e.getMessage(), e);
            throw e;
        }
    }

    @NotNull
    private String generateOTP() {
        Random random = new Random();
        int password = 100000 + random.nextInt(900000);
        String otp = String.valueOf(password);
        logger.debug(TAG, "Đã tạo OTP mới");
        return otp;
    }

    /**
     * Change password boolean.
     *
     * @param email       the email
     * @param oldPassword the old password
     * @param newPassword the new password
     * @return the boolean
     * @throws SQLException the sql exception
     */
    public boolean changePassword(String email, String oldPassword, String newPassword) throws SQLException {
        logger.info(TAG, "Đổi mật khẩu cho email: " + email);

        try {
            // Xác thực mật khẩu cũ
            int userId = validateUserLogin(email, oldPassword);
            if (userId == 0) {
                logger.warning(TAG, "Đổi mật khẩu thất bại: Mật khẩu cũ không đúng");
                return false;
            }

            // Cập nhật mật khẩu mới
            return updatePassword(email, newPassword);
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi đổi mật khẩu: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Change password boolean.
     *
     * @param email       the email
     * @param newPassword the new password
     * @return the boolean
     * @throws SQLException the sql exception
     */
    public boolean changePassword(String email, String newPassword) throws SQLException {
        logger.info(TAG, "Đặt lại mật khẩu cho email: " + email);
        return updatePassword(email, newPassword);
    }

    private boolean updatePassword(String email, String newPassword) throws SQLException {
        try (PreparedStatement preparedStatement = database.getConnection().prepareStatement(UPDATE_USER)) {
            preparedStatement.setString(1, newPassword);
            preparedStatement.setString(2, email);

            int affectedRows = preparedStatement.executeUpdate();
            boolean success = affectedRows > 0;

            if (success) {
                logger.info(TAG, "Cập nhật mật khẩu thành công cho email: " + email);
            } else {
                logger.warning(TAG, "Cập nhật mật khẩu thất bại: Email không tồn tại: " + email);
            }

            return success;
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi cập nhật mật khẩu: " + e.getMessage(), e);
            throw e;
        }
    }
}
