package com.library.anishelf.dao;

import com.github.benmanes.caffeine.cache.Cache;
import com.library.anishelf.util.CacheManagerUtil;
import com.library.anishelf.util.EmailUtil;
import com.library.anishelf.model.Member;
import com.library.anishelf.model.Person;
import com.library.anishelf.model.enums.AccountStatus;
import com.library.anishelf.model.enums.Gender;
import com.library.anishelf.util.RuntimeDebugUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MemberDAO implements GenericDAO<Member> {
    private static MemberDAO memberDAO;

    private static DatabaseConnection databaseConnection;
    private static Cache<Integer, Member> cache;
    private static final RuntimeDebugUtil logger = RuntimeDebugUtil.getInstance();
    
    // Hằng số cho các câu lệnh SQL
    private static final String INSERT_MEMBER =
            "INSERT INTO \"Members\" (\"first_name\", \"last_name\", \"birth_date\", \"gender\", \"email\", \"phone\", \"image_path\") "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";
    
    private static final String INSERT_USER = 
            "INSERT INTO \"Users\" (\"username\", \"password\", \"member_ID\") VALUES (?, ?, ?)";

    private static final String UPDATE_MEMBER = 
            "UPDATE \"Members\" " +
            "SET \"first_name\" = ?, \"last_name\" = ?, \"birth_date\" = ?, \"gender\" = ?, \"email\" = ?, \"phone\" = ?, \"image_path\" = ?" +
            " WHERE \"member_ID\" = ?";

    private static final String UPDATE_ACCOUNT = 
            "UPDATE \"Users\" SET \"AccountStatus\" = ?::account_status WHERE \"user_ID\" = ?";

    private static final String DELETE_MEMBER = 
            "DELETE FROM \"Members\" WHERE \"member_ID\" = ?";
            
    private static final String DELETE_USER = 
            "DELETE FROM \"Users\" WHERE \"member_ID\" = ?";

    private static final String FIND_MEMBER = 
            "SELECT * FROM \"Members\" m JOIN \"Users\" u ON m.\"member_ID\" = u.\"member_ID\" WHERE m.\"member_ID\" = ?";

    private static final String SELECT_ALL_MEMBERS = 
            "SELECT * FROM \"Members\" m JOIN \"Users\" u ON m.\"member_ID\" = u.\"member_ID\"";

    /**
     * Constructor riêng để thực hiện Singleton pattern
     */
    private MemberDAO() {
        databaseConnection = DatabaseConnection.getInstance();
        cache = CacheManagerUtil.buildCache(100);
        logger.debug("MemberDAO", "Khởi tạo instance MemberDAO với cache có kích thước 100");
    }

    /**
     * Lấy instance duy nhất của MemberDAO (Singleton pattern)
     * 
     * @return Instance của MemberDAO
     */
    public static synchronized MemberDAO getInstance() {
        if (memberDAO == null) {
            memberDAO = new MemberDAO();
        }
        return memberDAO;
    }

    /**
     * Thêm thông tin tài khoản người dùng
     * 
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     * @param memberID ID của thành viên
     * @return true nếu thêm thành công, false nếu thất bại
     * @throws SQLException nếu có lỗi xảy ra khi thao tác với database
     */
    private boolean addUser(String username, String password, int memberID) throws SQLException {
        logger.debug("MemberDAO", "Thêm tài khoản cho thành viên ID: " + memberID);
        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(INSERT_USER)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setInt(3, memberID);

            int rowsAffected = preparedStatement.executeUpdate();
            boolean success = rowsAffected == 1;
            
            if (success) {
                logger.info("MemberDAO", "Thêm tài khoản thành công cho thành viên ID: " + memberID);
                return true;
            } else {
                logger.error("MemberDAO", "Không thể thêm tài khoản cho thành viên ID: " + memberID);
                throw new SQLException("Failed to insert member");
            }
        } catch (SQLException e) {
            logger.error("MemberDAO", "Lỗi SQL khi thêm tài khoản: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Tạo mật khẩu ngẫu nhiên 6 chữ số
     * 
     * @return Chuỗi mật khẩu ngẫu nhiên
     */
    private String createRandomPassword() {
        Random random = new Random();
        int password = 100000 + random.nextInt(900000);
        logger.debug("MemberDAO", "Tạo mật khẩu ngẫu nhiên");
        return String.valueOf(password);
    }

    /**
     * Xử lý chuỗi ngày tháng và chuyển đổi thành đối tượng Date SQL
     * 
     * @param birthDateStr Chuỗi ngày tháng
     * @return Date SQL hoặc null nếu chuỗi không hợp lệ
     */
    private Date parseBirthDate(String birthDateStr) {
        if (birthDateStr == null || birthDateStr.isEmpty()) {
            logger.debug("MemberDAO", "Ngày sinh rỗng hoặc null");
            return null;
        }
        
        try {
            // Loại bỏ múi giờ nếu có
            if (birthDateStr.contains("+")) {
                birthDateStr = birthDateStr.substring(0, birthDateStr.indexOf("+")).trim();
            }
            
            Date sqlDate = Date.valueOf(birthDateStr);
            return sqlDate;
        } catch (IllegalArgumentException e) {
            logger.error("MemberDAO", "Lỗi định dạng ngày tháng: " + birthDateStr, e);
            return null;
        }
    }

    /**
     * Thiết lập tham số cho PreparedStatement về ngày sinh
     * 
     * @param preparedStatement PreparedStatement cần thiết lập
     * @param parameterIndex Vị trí tham số
     * @param birthDateStr Chuỗi ngày sinh
     * @throws SQLException nếu có lỗi xảy ra khi thiết lập tham số
     */
    private void setBirthDateParameter(PreparedStatement preparedStatement, int parameterIndex, String birthDateStr) throws SQLException {
        Date sqlDate = parseBirthDate(birthDateStr);
        if (sqlDate != null) {
            preparedStatement.setDate(parameterIndex, sqlDate);
        } else {
            preparedStatement.setNull(parameterIndex, Types.DATE);
        }
    }

    @Override
    public void insert(Member member) throws SQLException {
        logger.debug("MemberDAO", "Bắt đầu thêm thành viên mới: " + member.getPerson().getFirstName() + " " + member.getPerson().getLastName());
        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(INSERT_MEMBER, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, member.getPerson().getFirstName());
            preparedStatement.setString(2, member.getPerson().getLastName());
            
            // Thiết lập ngày sinh
            setBirthDateParameter(preparedStatement, 3, member.getPerson().getBirthdate());
            
            // Thiết lập giới tính
            preparedStatement.setObject(4, member.getPerson().getGender().toString(), Types.OTHER);
            
            preparedStatement.setString(5, member.getPerson().getEmail());
            preparedStatement.setString(6, member.getPerson().getPhone());
            preparedStatement.setString(7, member.getPerson().getImagePath());

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted == 0) {
                logger.error("MemberDAO", "Không thể thêm thông tin thành viên vào database");
                throw new SQLException("Failed to insert member, no rows affected");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int memberId = generatedKeys.getInt(1);
                    logger.info("MemberDAO", "Đã thêm thành viên với ID: " + memberId);
                    
                    String password = createRandomPassword();
                    if (addUser(member.getPerson().getPhone(), password, memberId)) {
                        logger.info("MemberDAO", "Gửi email thông tin tài khoản đến: " + member.getPerson().getEmail());
                        EmailUtil.sendEmailAsync(member.getPerson().getEmail(),
                                "TÀI KHOẢN", "Tài khoản: " + member.getPerson().getPhone()
                                        + ", Mật khẩu: " + password);
                    } else {
                        logger.error("MemberDAO", "Không thể thêm tài khoản cho thành viên ID: " + memberId);
                        throw new SQLException("Failed to insert member");
                    }
                } else {
                    logger.error("MemberDAO", "Không thể lấy ID của thành viên vừa thêm");
                    throw new SQLException("Failed to insert member, could not obtain ID");
                }
            }
        } catch (SQLException e) {
            logger.error("MemberDAO", "Lỗi SQL khi thêm thành viên: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Cập nhật trạng thái tài khoản
     * 
     * @param status Trạng thái mới
     * @param memberID ID của thành viên
     * @return true nếu cập nhật thành công, false nếu thất bại
     * @throws SQLException nếu có lỗi xảy ra khi thao tác với database
     */
    private boolean updateAccount(AccountStatus status, int memberID) throws SQLException {
        logger.debug("MemberDAO", "Cập nhật trạng thái tài khoản thành " + status + " cho thành viên ID: " + memberID);
        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(UPDATE_ACCOUNT)) {
            preparedStatement.setString(1, status.name());
            preparedStatement.setInt(2, memberID);

            int rowsAffected = preparedStatement.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                logger.info("MemberDAO", "Cập nhật trạng thái tài khoản thành công cho thành viên ID: " + memberID);
            } else {
                logger.warning("MemberDAO", "Không thể cập nhật trạng thái tài khoản cho thành viên ID: " + memberID);
            }
            
            return success;
        } catch (SQLException e) {
            logger.error("MemberDAO", "Lỗi SQL khi cập nhật trạng thái tài khoản: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean updateEntity(Member member) throws SQLException {
        logger.debug("MemberDAO", "Bắt đầu cập nhật thông tin thành viên ID: " + member.getPerson().getId());
        Connection connection = databaseConnection.getConnection();
        boolean originalAutoCommit = connection.getAutoCommit();
        
        try {
            connection.setAutoCommit(false);

            // Cập nhật thông tin cá nhân
            try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_MEMBER)) {
                preparedStatement.setString(1, member.getPerson().getFirstName());
                preparedStatement.setString(2, member.getPerson().getLastName());
                
                // Thiết lập ngày sinh
                setBirthDateParameter(preparedStatement, 3, member.getPerson().getBirthdate());
                
                // Thiết lập giới tính
                preparedStatement.setObject(4, member.getPerson().getGender().toString(), Types.OTHER);
                
                preparedStatement.setString(5, member.getPerson().getEmail());
                preparedStatement.setString(6, member.getPerson().getPhone());
                preparedStatement.setString(7, member.getPerson().getImagePath());
                preparedStatement.setInt(8, member.getPerson().getId());

                int rowsUpdated = preparedStatement.executeUpdate();
                if (rowsUpdated == 0) {
                    logger.warning("MemberDAO", "Không có dòng nào được cập nhật cho thành viên ID: " + member.getPerson().getId());
                    connection.rollback();
                    return false;
                }
                
                logger.info("MemberDAO", "Đã cập nhật thông tin cá nhân cho thành viên ID: " + member.getPerson().getId());
            }

            // Cập nhật thông tin tài khoản
            try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ACCOUNT)) {
                preparedStatement.setObject(1, member.getStatus().name(), Types.OTHER);
                preparedStatement.setInt(2, member.getAccId());

                int rowsUpdated = preparedStatement.executeUpdate();
                if (rowsUpdated == 0) {
                    logger.warning("MemberDAO", "Không có dòng nào được cập nhật cho tài khoản ID: " + member.getAccId());
                    connection.rollback();
                    return false;
                }
                
                logger.info("MemberDAO", "Đã cập nhật thông tin tài khoản ID: " + member.getAccId());
            }

            connection.commit();
            // Cập nhật cache
            cache.put(member.getPerson().getId(), member);
            logger.info("MemberDAO", "Cập nhật thành công và lưu vào cache cho thành viên ID: " + member.getPerson().getId());
            return true;
        } catch (SQLException e) {
            connection.rollback();
            logger.error("MemberDAO", "Lỗi SQL khi cập nhật thành viên ID " + member.getPerson().getId() + ": " + e.getMessage(), e);
            throw e;
        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
    }

    @Override
    public boolean deleteEntity(Member member) throws SQLException {
        if (member == null || member.getPerson() == null) {
            logger.warning("MemberDAO", "Không thể xóa thành viên: tham số null");
            return false;
        }
        
        int memberId = member.getPerson().getId();
        logger.debug("MemberDAO", "Bắt đầu xóa thành viên ID: " + memberId);
        
        Connection connection = databaseConnection.getConnection();
        boolean originalAutoCommit = connection.getAutoCommit();
        
        try {
            connection.setAutoCommit(false);

            // Xóa thông tin tài khoản trước
            int userRowsDeleted = 0;
            try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER)) {
                preparedStatement.setInt(1, memberId);
                userRowsDeleted = preparedStatement.executeUpdate();
                
                if (userRowsDeleted > 0) {
                    logger.info("MemberDAO", "Đã xóa thông tin tài khoản cho thành viên ID: " + memberId);
                } else {
                    logger.warning("MemberDAO", "Không tìm thấy thông tin tài khoản cho thành viên ID: " + memberId);
                }
            }

            // Sau đó xóa thông tin thành viên
            try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_MEMBER)) {
                preparedStatement.setInt(1, memberId);
                int memberRowsDeleted = preparedStatement.executeUpdate();
                
                if (memberRowsDeleted > 0) {
                    logger.info("MemberDAO", "Đã xóa thông tin thành viên ID: " + memberId);
                } else {
                    logger.warning("MemberDAO", "Không tìm thấy thông tin thành viên ID: " + memberId);
                    connection.rollback();
                    return false;
                }
            }

            connection.commit();
            // Xóa khỏi cache
            cache.invalidate(memberId);
            logger.info("MemberDAO", "Xóa thành công thành viên ID: " + memberId);
            return true;
        } catch (SQLException e) {
            connection.rollback();
            logger.error("MemberDAO", "Lỗi SQL khi xóa thành viên ID " + memberId + ": " + e.getMessage(), e);
            throw e;
        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
    }

    @Override
    public Member findById(Number keywords) throws SQLException {
        int memberId = keywords.intValue();
        logger.debug("MemberDAO", "Tìm kiếm thành viên với ID: " + memberId);
        
        // Kiểm tra trong cache trước
        Member cachedMember = cache.getIfPresent(memberId);
        if (cachedMember != null) {
            logger.info("MemberDAO", "Tìm thấy thành viên ID " + memberId + " trong cache");
            return cachedMember;
        }
        
        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(FIND_MEMBER)) {
            preparedStatement.setInt(1, memberId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Person person = new Person(
                            resultSet.getInt("member_id"),
                            resultSet.getString("first_name"),
                            resultSet.getString("last_name"),
                            resultSet.getString("image_path"),
                            Gender.valueOf(resultSet.getString("gender")),
                            resultSet.getString("birth_date"),
                            resultSet.getString("email"),
                            resultSet.getString("phone")
                    );
                    
                    Member member = new Member(
                            resultSet.getInt("user_ID"),
                            resultSet.getString("username"),
                            resultSet.getString("password"),
                            AccountStatus.valueOf(resultSet.getString("AccountStatus")),
                            resultSet.getString("added_at_timestamp"), 
                            person
                    );
                    
                    // Lưu vào cache
                    cache.put(memberId, member);
                    logger.info("MemberDAO", "Tìm thấy và cache thành viên ID: " + memberId);
                    return member;
                } else {
                    logger.info("MemberDAO", "Không tìm thấy thành viên ID: " + memberId);
                }
            }
        } catch (SQLException e) {
            logger.error("MemberDAO", "Lỗi SQL khi tìm kiếm thành viên ID " + memberId + ": " + e.getMessage(), e);
            throw e;
        }

        return null;
    }

    @Override
    public List<Member> findByCriteria(Map<String, Object> criteria) throws SQLException {
        if (criteria == null || criteria.isEmpty()) {
            logger.warning("MemberDAO", "Tìm kiếm với tiêu chí rỗng, trả về danh sách rỗng");
            return Collections.emptyList();
        }
        
        logger.debug("MemberDAO", "Tìm kiếm thành viên theo tiêu chí: " + criteria);
        StringBuilder findMemberByCriteria = new StringBuilder("SELECT * FROM \"Members\" m JOIN \"Users\" u ON m.\"member_ID\" = u.\"member_ID\" WHERE ");

        for (String key : criteria.keySet()) {
            if (key.equals("member_id")) {
                findMemberByCriteria.append("CAST(m.\"member_id\" AS TEXT) LIKE ? OR ");
            } else if (key.equals("fullname")) {
                findMemberByCriteria.append("CONCAT(m.\"last_name\", ' ', m.\"first_name\") LIKE ? OR ");
            } else if (!key.startsWith("\"")) {
                findMemberByCriteria.append("\"").append(key).append("\"").append(" LIKE ? OR ");
            } else {
                findMemberByCriteria.append(key).append(" LIKE ? OR ");
            }
        }

        // Xóa "OR " cuối cùng
        findMemberByCriteria.setLength(findMemberByCriteria.length() - 4);

        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(findMemberByCriteria.toString())) {
            int index = 1;
            for (Object value : criteria.values()) {
                String searchPattern = "%" + value.toString() + "%";
                preparedStatement.setString(index++, searchPattern);
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Member> members = new ArrayList<>();
                while (resultSet.next()) {
                    int memberId = resultSet.getInt("member_id");
                    Member member = findById(memberId);
                    if (member != null) {
                        members.add(member);
                    }
                }
                
                logger.info("MemberDAO", "Tìm thấy " + members.size() + " thành viên theo tiêu chí");
                return members;
            }
        } catch (SQLException e) {
            logger.error("MemberDAO", "Lỗi SQL khi tìm kiếm theo tiêu chí: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<Member> findAll() throws SQLException {
        logger.debug("MemberDAO", "Lấy danh sách tất cả thành viên");
        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(SELECT_ALL_MEMBERS)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Member> members = new ArrayList<>();
                while (resultSet.next()) {
                    int memberId = resultSet.getInt("member_id");
                    Member member = findById(memberId);
                    if (member != null) {
                        members.add(member);
                    }
                }
                
                logger.info("MemberDAO", "Đã lấy " + members.size() + " thành viên từ database");
                return members;
            }
        } catch (SQLException e) {
            logger.error("MemberDAO", "Lỗi SQL khi lấy tất cả thành viên: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Xóa thành viên khỏi bộ nhớ cache
     * 
     * @param memberID ID của thành viên cần xóa khỏi cache
     */
    public void fetchCache(int memberID) {
        logger.debug("MemberDAO", "Xóa thành viên ID " + memberID + " khỏi cache");
        cache.invalidate(memberID);
    }
}
