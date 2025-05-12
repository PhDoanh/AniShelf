package com.library.anishelf.dao;

import com.library.anishelf.model.Member;
import com.library.anishelf.model.BookItem;
import com.library.anishelf.model.enums.BookItemStatus;
import com.library.anishelf.model.BookIssue;
import com.library.anishelf.model.enums.BookIssueStatus;
import com.library.anishelf.util.RuntimeDebugUtil;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lớp quản lý việc mượn truyện
 * Triển khai giao diện GenericDAO cho thao tác CRUD với BookIssue
 */
public class BookIssueDAO implements GenericDAO<BookIssue> {
    private static BookIssueDAO bookIssueDAO;
    private static DatabaseConnection databaseConnection;
    private static BookItemDAO bookItemDAO;
    private static MemberDAO memberDAO;
    private static BookDAO bookDAO;
    private static final ConcurrentHashMap<Integer, BookIssue> bookIssueCache = new ConcurrentHashMap<>(100);
    private static final RuntimeDebugUtil logger = RuntimeDebugUtil.getInstance();
    private static final String TAG = "BookIssueDAO";

    // Giới hạn kích thước cache để tránh sử dụng quá nhiều bộ nhớ
    private static final int CACHE_SIZE_LIMIT = 200;

    // SQL Queries tối ưu
    private static final String INSERT_BOOK_ISSUE =
            "INSERT INTO \"BookIssue\"(\"member_ID\", \"barcode\", \"creation_date\", \"due_date\") VALUES (?, ?, ?::date, ?::date)";

    private static final String UPDATE_BOOK_ISSUE =
            "UPDATE \"BookIssue\" SET \"member_ID\" = ?, \"barcode\" = ?, \"creation_date\" = ?::date, \"due_date\" = ?::date, " +
                    "\"return_date\" = ?::date, \"BookIssueStatus\" = ?::book_issue_status WHERE \"issue_ID\" = ?";

    private static final String DELETE_BOOK_ISSUE =
            "DELETE FROM \"BookIssue\" WHERE \"issue_ID\" = ?";

    private static final String SELECT_BOOK_ISSUE_BY_ID =
            "SELECT * FROM \"BookIssue\" WHERE \"issue_ID\" = ?";

    private static final String SELECT_ALL_BOOK_ISSUES =
            "SELECT \"issue_ID\" FROM \"BookIssue\" ORDER BY \"creation_date\" DESC LIMIT 500";

    private static final String SELECT_BOOK_ISSUES_BY_MEMBER =
            "SELECT \"issue_ID\" FROM \"BookIssue\" WHERE \"member_ID\" = ? ORDER BY \"creation_date\" DESC";

    private static final String SELECT_BOOK_ISSUES_BY_BARCODE =
            "SELECT \"issue_ID\" FROM \"BookIssue\" WHERE \"barcode\" = ? ORDER BY \"creation_date\" DESC";

    private static final String SELECT_ACTIVE_BOOK_ISSUES =
            "SELECT \"issue_ID\" FROM \"BookIssue\" WHERE \"BookIssueStatus\" = 'BORROWED'::book_issue_status ORDER BY \"due_date\" ASC";

    private BookIssueDAO() {
        databaseConnection = DatabaseConnection.getInstance();
        bookItemDAO = BookItemDAO.getInstance();
        memberDAO = MemberDAO.getInstance();
        bookDAO = BookDAO.getInstance();
        logger.debug(TAG, "BookIssueDAO initialized with concurrent cache");
    }

    /**
     * Lấy instance của BookIssueDAO (Singleton pattern)
     *
     * @return instance duy nhất của BookIssueDAO
     */
    public static synchronized BookIssueDAO getInstance() {
        if (bookIssueDAO == null) {
            bookIssueDAO = new BookIssueDAO();
        }
        return bookIssueDAO;
    }

    /**
     * Thêm BookIssue mới vào cơ sở dữ liệu
     *
     * @param entity BookIssue cần thêm
     * @throws SQLException nếu có lỗi xảy ra khi thêm
     */
    @Override
    public void insert(@NotNull BookIssue entity) throws SQLException {
        logger.debug(TAG, "Adding new book issue for barcode: " + entity.getBookItem().getBookBarcode() +
                ", member: " + entity.getMember().getPerson().getId());

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = databaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(INSERT_BOOK_ISSUE,
                    PreparedStatement.RETURN_GENERATED_KEYS);

            preparedStatement.setInt(1, entity.getMember().getPerson().getId());
            preparedStatement.setInt(2, entity.getBookItem().getBookBarcode());
            preparedStatement.setString(3, entity.getIssueDate());
            preparedStatement.setString(4, entity.getDueDate());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                logger.error(TAG, "Creating book issue failed, no rows affected");
                throw new SQLException("Creating book issue failed, no rows affected");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int issueId = generatedKeys.getInt(1);
                    entity.setIssueID(issueId);

                    // Cập nhật cache
                    addToCache(entity);

                    // Cập nhật các cache liên quan
                    memberDAO.fetchCache(entity.getMember().getPerson().getId());
                    bookItemDAO.invalidateBookItemCache(entity.getBookItem().getBookBarcode());
                    bookDAO.invalidateBookCache(entity.getBookItem().getIsbn());

                    logger.info(TAG, "Book issue created successfully with ID: " + issueId);
                } else {
                    logger.error(TAG, "Creating book issue failed, no ID obtained");
                    throw new SQLException("Creating book issue failed, no ID obtained");
                }
            }
        } catch (SQLException e) {
            logger.error(TAG, "Error adding book issue: " + e.getMessage(), e);
            throw e;
        } finally {
            closeResources(preparedStatement, null);
        }
    }

    /**
     * Cập nhật thông tin BookIssue trong cơ sở dữ liệu
     *
     * @param entity BookIssue cần cập nhật
     * @return true nếu cập nhật thành công, false nếu không
     * @throws SQLException nếu có lỗi xảy ra khi cập nhật
     */
    @Override
    public boolean updateEntity(@NotNull BookIssue entity) throws SQLException {
        logger.debug(TAG, "Updating book issue: " + entity.getIssueID());

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = databaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(UPDATE_BOOK_ISSUE);

            preparedStatement.setInt(1, entity.getMember().getPerson().getId());
            preparedStatement.setInt(2, entity.getBookItem().getBookBarcode());
            preparedStatement.setString(3, entity.getIssueDate());
            preparedStatement.setString(4, entity.getDueDate());
            preparedStatement.setString(5, entity.getActualReturnDate());
            preparedStatement.setString(6, entity.getStatus().name());
            preparedStatement.setInt(7, entity.getIssueID());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                // Cập nhật cache
                addToCache(entity);

                // Cập nhật các cache liên quan
                memberDAO.fetchCache(entity.getMember().getPerson().getId());
                bookItemDAO.invalidateBookItemCache(entity.getBookItem().getBookBarcode());
                bookDAO.invalidateBookCache(entity.getBookItem().getIsbn());

                logger.info(TAG, "Book issue updated successfully: " + entity.getIssueID());
                return true;
            } else {
                logger.warning(TAG, "Book issue not found for update: " + entity.getIssueID());
                return false;
            }
        } catch (SQLException e) {
            logger.error(TAG, "Error updating book issue: " + e.getMessage(), e);
            throw e;
        } finally {
            closeResources(preparedStatement, null);
        }
    }

    /**
     * Xóa BookIssue khỏi cơ sở dữ liệu
     *
     * @param entity BookIssue cần xóa
     * @return true nếu xóa thành công, false nếu không
     * @throws SQLException nếu có lỗi xảy ra khi xóa
     */
    @Override
    public boolean deleteEntity(@NotNull BookIssue entity) throws SQLException {
        logger.debug(TAG, "Deleting book issue: " + entity.getIssueID());

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = databaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(DELETE_BOOK_ISSUE);

            preparedStatement.setInt(1, entity.getIssueID());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                // Xóa khỏi cache
                bookIssueCache.remove(entity.getIssueID());

                // Cập nhật các cache liên quan
                memberDAO.fetchCache(entity.getMember().getPerson().getId());
                bookItemDAO.invalidateBookItemCache(entity.getBookItem().getBookBarcode());
                bookDAO.invalidateBookCache(entity.getBookItem().getIsbn());

                logger.info(TAG, "Book issue deleted successfully: " + entity.getIssueID());
                return true;
            } else {
                logger.warning(TAG, "Book issue not found for deletion: " + entity.getIssueID());
                return false;
            }
        } catch (SQLException e) {
            logger.error(TAG, "Error deleting book issue: " + e.getMessage(), e);
            throw e;
        } finally {
            closeResources(preparedStatement, null);
        }
    }

    /**
     * Tìm BookIssue theo ID
     *
     * @param keywords ID của BookIssue cần tìm
     * @return BookIssue nếu tìm thấy, null nếu không
     * @throws SQLException nếu có lỗi xảy ra khi tìm kiếm
     */
    @Override
    public BookIssue findById(@NotNull Number keywords) throws SQLException {
        int issueId = keywords.intValue();

        // Kiểm tra cache trước
        BookIssue cachedIssue = bookIssueCache.get(issueId);
        if (cachedIssue != null) {
            logger.debug(TAG, "Cache hit for book issue: " + issueId);
            return cachedIssue;
        }

        logger.debug(TAG, "Cache miss for book issue: " + issueId + ", fetching from database");

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = databaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(SELECT_BOOK_ISSUE_BY_ID);

            preparedStatement.setInt(1, issueId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                BookIssue bookIssue = createBookIssueFromResultSet(resultSet);

                // Cập nhật cache
                addToCache(bookIssue);

                logger.info(TAG, "Book issue found: " + issueId);
                return bookIssue;
            } else {
                logger.warning(TAG, "Book issue not found: " + issueId);
                return null;
            }
        } catch (SQLException e) {
            logger.error(TAG, "Error finding book issue: " + e.getMessage(), e);
            throw e;
        } finally {
            closeResources(preparedStatement, resultSet);
        }
    }

    /**
     * Tìm kiếm BookIssue theo các tiêu chí
     *
     * @param criteria Map các tiêu chí tìm kiếm
     * @return Danh sách BookIssue tìm thấy
     * @throws SQLException nếu có lỗi xảy ra khi tìm kiếm
     */
    @Override
    public List<BookIssue> findByCriteria(@NotNull Map<String, Object> criteria) throws SQLException {
        logger.debug(TAG, "Searching book issues by criteria: " + criteria);

        if (criteria.isEmpty()) {
            logger.warning(TAG, "Empty criteria, returning all book issues");
            return findAll();
        }

        // Tối ưu cho một số trường hợp tìm kiếm phổ biến
        if (criteria.size() == 1) {
            // Trường hợp tìm theo member_ID
            if (criteria.containsKey("member_ID")) {
                try {
                    int memberId = Integer.parseInt(criteria.get("member_ID").toString());
                    return getBookIssuesByMember(memberId);
                } catch (NumberFormatException e) {
                    logger.warning(TAG, "Invalid member_ID format: " + criteria.get("member_ID"));
                    // Tiếp tục với truy vấn thông thường
                }
            }
            // Trường hợp tìm theo barcode
            if (criteria.containsKey("barcode")) {
                try {
                    int barcode = Integer.parseInt(criteria.get("barcode").toString());
                    return getBookIssuesByBarcode(barcode);
                } catch (NumberFormatException e) {
                    logger.warning(TAG, "Invalid barcode format: " + criteria.get("barcode"));
                    // Tiếp tục với truy vấn thông thường
                }
            }
            // Trường hợp tìm theo trạng thái BORROWED
            if (criteria.containsKey("BookIssueStatus") && "BORROWED".equals(criteria.get("BookIssueStatus").toString())) {
                return getActiveBorrowedIssues();
            }
        }

        // Xây dựng truy vấn động cho các trường hợp phức tạp
        StringBuilder sqlBuilder = new StringBuilder(
                "SELECT DISTINCT \"BookIssue\".\"issue_ID\", \"BookIssue\".\"creation_date\" FROM \"BookIssue\" ");

        // Chỉ thêm JOIN khi cần thiết
        boolean needMembersJoin = false;
        boolean needBookItemJoin = false;
        boolean needBooksJoin = false;

        for (String key : criteria.keySet()) {
            if (key.equals("fullname") || key.startsWith("Members.")) {
                needMembersJoin = true;
            } else if (key.startsWith("BookItem.") || (key.equals("barcode") && !key.startsWith("BookIssue."))) {
                needBookItemJoin = true;
            } else if (key.startsWith("Books.") || key.equals("ISBN") || key.equals("title")) {
                needBookItemJoin = true;
                needBooksJoin = true;
            }
        }

        if (needMembersJoin) {
            sqlBuilder.append("JOIN \"Members\" ON \"BookIssue\".\"member_ID\" = \"Members\".\"member_ID\" ");
        }

        if (needBookItemJoin) {
            sqlBuilder.append("JOIN \"BookItem\" ON \"BookIssue\".\"barcode\" = \"BookItem\".\"barcode\" ");
        }

        if (needBooksJoin) {
            sqlBuilder.append("JOIN \"Books\" ON \"BookItem\".\"ISBN\" = \"Books\".\"ISBN\" ");
        }

        sqlBuilder.append("WHERE ");

        List<Object> paramValues = new ArrayList<>();
        List<String> dateFields = List.of("creation_date", "due_date", "return_date");
        List<String> statusFields = List.of("BookIssueStatus");

        int paramCount = 0;

        for (Map.Entry<String, Object> entry : criteria.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value == null) {
                logger.warning(TAG, "Skipping null value for key: " + key);
                continue;
            }

            if (paramCount > 0) {
                sqlBuilder.append(" AND ");
            }

            if ("fullname".equals(key)) {
                sqlBuilder.append("CONCAT(\"Members\".\"last_name\", ' ', \"Members\".\"first_name\") ILIKE ?");
                paramValues.add("%" + value.toString() + "%");
            } else if (dateFields.contains(key)) {
                sqlBuilder.append("DATE(\"BookIssue\".\"").append(key).append("\") = ?::date");
                paramValues.add(value.toString());
            } else if (statusFields.contains(key)) {
                sqlBuilder.append("\"BookIssue\".\"").append(key).append("\" = ?::book_issue_status");
                paramValues.add(value.toString());
            } else if ("barcode".equals(key) || "member_ID".equals(key)) {
                try {
                    sqlBuilder.append("\"BookIssue\".\"").append(key).append("\" = ?");
                    paramValues.add(Integer.parseInt(value.toString()));
                } catch (NumberFormatException e) {
                    logger.warning(TAG, "Invalid number format for " + key + ": " + value);
                    sqlBuilder.append("\"BookIssue\".\"").append(key).append("\"::text LIKE ?");
                    paramValues.add("%" + value.toString() + "%");
                }
            } else {
                // Xử lý các trường hợp còn lại với ILIKE để tìm kiếm không phân biệt hoa thường
                String tableColumn = key;
                if (!key.contains(".")) {
                    tableColumn = "\"BookIssue\".\"" + key + "\"";
                } else {
                    String[] parts = key.split("\\.");
                    if (parts.length == 2) {
                        tableColumn = "\"" + parts[0] + "\".\"" + parts[1] + "\"";
                    }
                }

                sqlBuilder.append(tableColumn).append(" ILIKE ?");
                paramValues.add("%" + value.toString() + "%");
            }

            paramCount++;
        }

        if (paramCount == 0) {
            // Nếu không có điều kiện hợp lệ, trả về tất cả
            return findAll();
        }

        sqlBuilder.append(" ORDER BY \"BookIssue\".\"creation_date\" DESC LIMIT 500");

        String sqlQuery = sqlBuilder.toString();
        logger.debug(TAG, "Generated SQL query: " + sqlQuery);

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = databaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sqlQuery);

            // Thiết lập các tham số
            for (int i = 0; i < paramValues.size(); i++) {
                Object value = paramValues.get(i);
                if (value instanceof Integer) {
                    preparedStatement.setInt(i + 1, (Integer) value);
                } else {
                    preparedStatement.setString(i + 1, value.toString());
                }
            }

            resultSet = preparedStatement.executeQuery();

            List<BookIssue> bookIssues = new ArrayList<>();
            while (resultSet.next()) {
                int issueId = resultSet.getInt("issue_ID");
                BookIssue bookIssue = findById(issueId);
                if (bookIssue != null) {
                    bookIssues.add(bookIssue);
                }
            }

            logger.info(TAG, "Found " + bookIssues.size() + " book issues matching criteria");
            return bookIssues;
        } catch (SQLException e) {
            logger.error(TAG, "Error searching book issues: " + e.getMessage(), e);
            throw e;
        } finally {
            closeResources(preparedStatement, resultSet);
        }
    }

    /**
     * Lấy tất cả BookIssue từ cơ sở dữ liệu
     *
     * @return Danh sách tất cả BookIssue
     * @throws SQLException nếu có lỗi xảy ra khi truy vấn
     */
    @Override
    public List<BookIssue> findAll() throws SQLException {
        logger.debug(TAG, "Selecting all book issues (limited to 500)");

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = databaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(SELECT_ALL_BOOK_ISSUES);
            resultSet = preparedStatement.executeQuery();

            List<BookIssue> bookIssues = new ArrayList<>();
            while (resultSet.next()) {
                int issueId = resultSet.getInt("issue_ID");
                BookIssue bookIssue = findById(issueId);
                if (bookIssue != null) {
                    bookIssues.add(bookIssue);
                }
            }

            logger.info(TAG, "Selected " + bookIssues.size() + " book issues");
            return bookIssues;
        } catch (SQLException e) {
            logger.error(TAG, "Error selecting all book issues: " + e.getMessage(), e);
            throw e;
        } finally {
            closeResources(preparedStatement, resultSet);
        }
    }

    /**
     * Vô hiệu hóa cache cho BookIssue theo ID
     *
     * @param issueID ID của BookIssue cần vô hiệu hóa cache
     */
    public void invalidateBookIssueCache(int issueID) {
        bookIssueCache.remove(issueID);
        logger.debug(TAG, "Book issue cache invalidated for ID: " + issueID);
    }

    /**
     * Xóa toàn bộ cache
     */
    public void clearCache() {
        bookIssueCache.clear();
        logger.debug(TAG, "Cleared entire book issue cache");
    }

    /**
     * Lấy danh sách BookIssue theo Member ID
     *
     * @param memberId ID của Member
     * @return Danh sách BookIssue của Member
     * @throws SQLException nếu có lỗi xảy ra khi truy vấn
     */
    public List<BookIssue> getBookIssuesByMember(int memberId) throws SQLException {
        logger.debug(TAG, "Getting book issues for member: " + memberId);

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = databaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(SELECT_BOOK_ISSUES_BY_MEMBER);

            preparedStatement.setInt(1, memberId);
            resultSet = preparedStatement.executeQuery();

            List<BookIssue> bookIssues = new ArrayList<>();
            while (resultSet.next()) {
                int issueId = resultSet.getInt("issue_ID");
                BookIssue bookIssue = findById(issueId);
                if (bookIssue != null) {
                    bookIssues.add(bookIssue);
                }
            }

            logger.info(TAG, "Found " + bookIssues.size() + " book issues for member: " + memberId);
            return bookIssues;
        } catch (SQLException e) {
            logger.error(TAG, "Error getting book issues for member: " + e.getMessage(), e);
            throw e;
        } finally {
            closeResources(preparedStatement, resultSet);
        }
    }

    /**
     * Lấy danh sách BookIssue theo Barcode
     *
     * @param barcode Barcode của BookItem
     * @return Danh sách BookIssue của BookItem
     * @throws SQLException nếu có lỗi xảy ra khi truy vấn
     */
    public List<BookIssue> getBookIssuesByBarcode(int barcode) throws SQLException {
        logger.debug(TAG, "Getting book issues for barcode: " + barcode);

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = databaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(SELECT_BOOK_ISSUES_BY_BARCODE);

            preparedStatement.setInt(1, barcode);
            resultSet = preparedStatement.executeQuery();

            List<BookIssue> bookIssues = new ArrayList<>();
            while (resultSet.next()) {
                int issueId = resultSet.getInt("issue_ID");
                BookIssue bookIssue = findById(issueId);
                if (bookIssue != null) {
                    bookIssues.add(bookIssue);
                }
            }

            logger.info(TAG, "Found " + bookIssues.size() + " book issues for barcode: " + barcode);
            return bookIssues;
        } catch (SQLException e) {
            logger.error(TAG, "Error getting book issues for barcode: " + e.getMessage(), e);
            throw e;
        } finally {
            closeResources(preparedStatement, resultSet);
        }
    }

    /**
     * Lấy danh sách BookIssue đang mượn (status = BORROWED)
     *
     * @return Danh sách BookIssue đang mượn
     * @throws SQLException nếu có lỗi xảy ra khi truy vấn
     */
    public List<BookIssue> getActiveBorrowedIssues() throws SQLException {
        logger.debug(TAG, "Getting active borrowed issues");

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = databaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(SELECT_ACTIVE_BOOK_ISSUES);
            resultSet = preparedStatement.executeQuery();

            List<BookIssue> bookIssues = new ArrayList<>();
            while (resultSet.next()) {
                int issueId = resultSet.getInt("issue_ID");
                BookIssue bookIssue = findById(issueId);
                if (bookIssue != null) {
                    bookIssues.add(bookIssue);
                }
            }

            logger.info(TAG, "Found " + bookIssues.size() + " active borrowed issues");
            return bookIssues;
        } catch (SQLException e) {
            logger.error(TAG, "Error getting active borrowed issues: " + e.getMessage(), e);
            throw e;
        } finally {
            closeResources(preparedStatement, resultSet);
        }
    }

    /**
     * Tạo đối tượng BookIssue từ ResultSet
     *
     * @param resultSet ResultSet chứa dữ liệu BookIssue
     * @return Đối tượng BookIssue
     * @throws SQLException nếu có lỗi xảy ra khi đọc ResultSet
     */
    private BookIssue createBookIssueFromResultSet(ResultSet resultSet) throws SQLException {
        int issueId = resultSet.getInt("issue_ID");
        int memberId = resultSet.getInt("member_ID");
        int barcode = resultSet.getInt("barcode");
        String creationDate = resultSet.getString("creation_date");
        String dueDate = resultSet.getString("due_date");
        String returnDate = resultSet.getString("return_date");
        String statusStr = resultSet.getString("BookIssueStatus");

        BookIssueStatus status = BookIssueStatus.valueOf(statusStr);

        return new BookIssue(
                issueId,
                memberDAO.findById(memberId),
                bookItemDAO.findById(barcode),
                creationDate,
                dueDate,
                returnDate,
                status
        );
    }

    /**
     * Thêm BookIssue vào cache với xử lý kích thước cache
     *
     * @param bookIssue BookIssue cần thêm vào cache
     */
    private void addToCache(BookIssue bookIssue) {
        // Kiểm tra và điều chỉnh kích thước cache nếu cần
        if (bookIssueCache.size() >= CACHE_SIZE_LIMIT) {
            // Nếu cache đầy, xóa 20% cache cũ nhất
            int toRemove = CACHE_SIZE_LIMIT / 5;
            logger.debug(TAG, "Cache size limit reached, removing " + toRemove + " oldest entries");

            // Trong thực tế, cần có cơ chế để xóa các mục ít sử dụng nhất
            // Ở đây tạm thời xóa ngẫu nhiên một số lượng mục
            List<Integer> keys = new ArrayList<>(bookIssueCache.keySet());
            for (int i = 0; i < Math.min(toRemove, keys.size()); i++) {
                bookIssueCache.remove(keys.get(i));
            }
        }

        // Thêm vào cache
        bookIssueCache.put(bookIssue.getIssueID(), bookIssue);
    }

    /**
     * Đóng tài nguyên PreparedStatement và ResultSet
     *
     * @param preparedStatement PreparedStatement cần đóng
     * @param resultSet         ResultSet cần đóng
     */
    private void closeResources(PreparedStatement preparedStatement, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        } catch (SQLException e) {
            logger.error(TAG, "Error closing resources: " + e.getMessage(), e);
        }
    }

    /**
     * Tạo BookIssue mới cho truyện có ISBN cụ thể
     *
     * @param member    Thành viên mượn truyện
     * @param isbn      ISBN của truyện cần mượn
     * @param issueDate Ngày mượn
     * @param dueDate   Ngày hẹn trả
     * @return BookIssue đã tạo hoặc null nếu không có truyện có sẵn
     * @throws SQLException nếu có lỗi xảy ra khi thao tác database
     */
    public BookIssue createBookIssueByISBN(Member member, long isbn, String issueDate, String dueDate) throws SQLException {
        logger.debug(TAG, "Tạo mượn truyện mới theo ISBN: " + isbn + " cho thành viên: " + member.getPerson().getId());

        // Tìm BookItem có sẵn đầu tiên với ISBN tương ứng
        BookItem bookItem = bookItemDAO.findFirstAvailableBookItemByISBN(isbn);

        if (bookItem == null) {
            logger.warning(TAG, "Không tìm thấy bản sao nào có sẵn của truyện có ISBN: " + isbn);
            return null;
        }

        // Tạo đối tượng BookIssue
        BookIssue bookIssue = new BookIssue(0, member, bookItem, issueDate, dueDate, null, BookIssueStatus.BORROWED);

        // Lưu vào cơ sở dữ liệu
        insert(bookIssue);

        // Cập nhật trạng thái của BookItem thành LOANED
        bookItem.setBookItemStatus(BookItemStatus.LOANED);
        bookItemDAO.updateEntity(bookItem);

        logger.info(TAG, "Đã tạo mượn truyện thành công với ID: " + bookIssue.getIssueID() +
                ", barcode: " + bookItem.getBookBarcode());

        return bookIssue;
    }
}
