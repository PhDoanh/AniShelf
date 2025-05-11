package com.library.anishelf.dao;

import com.library.anishelf.model.BookItem;
import com.library.anishelf.model.enums.BookItemStatus;
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
 * Lớp quản lý các BookItem trong cơ sở dữ liệu
 * Triển khai giao diện GenericDAO cho thao tác CRUD với BookItem
 */
public class BookItemDAO implements GenericDAO<BookItem> {
    private static DatabaseConnection databaseConnection;
    private static BookDAO bookDAO;
    private static BookItemDAO bookItemDAO;
    private static final ConcurrentHashMap<Integer, BookItem> bookItemCache = new ConcurrentHashMap<>(100);
    private static final RuntimeDebugUtil logger = RuntimeDebugUtil.getInstance();
    private static final String TAG = "BookItemDAO";
    
    // Giới hạn kích thước cache để tránh sử dụng quá nhiều bộ nhớ
    private static final int CACHE_SIZE_LIMIT = 100;
    
    // SQL Queries tối ưu
    private static final String INSERT_BOOK_ITEM = 
            "INSERT INTO \"BookItem\"(\"ISBN\", \"BookItemStatus\", \"note\") VALUES (?, ?::book_item_status, ?)";
    
    private static final String DELETE_BOOK_ITEM = 
            "DELETE FROM \"BookItem\" WHERE \"barcode\" = ?";

    private static final String SELECT_BOOK_ITEM_BY_BARCODE =
            "SELECT * FROM \"BookItem\" WHERE \"barcode\" = ?";

    private static final String UPDATE_BOOK_ITEM =
            "UPDATE \"BookItem\" SET \"BookItemStatus\" = ?::book_item_status, \"note\" = ?, \"ISBN\" = ? WHERE \"barcode\" = ?";

    private static final String SELECT_ALL_BOOK_ITEMS = 
            "SELECT \"barcode\" FROM \"BookItem\" LIMIT 1000";
            
    private static final String COUNT_BOOK_ITEMS = 
            "SELECT COUNT(*) FROM \"BookItem\"";
            
    private static final String SELECT_BOOK_ITEMS_BY_ISBN = 
            "SELECT \"barcode\" FROM \"BookItem\" WHERE \"ISBN\" = ?";
            
    private static final String SELECT_BOOK_ITEMS_BY_STATUS = 
            "SELECT \"barcode\" FROM \"BookItem\" WHERE \"BookItemStatus\" = ?::book_item_status";

    private BookItemDAO() {
        databaseConnection = DatabaseConnection.getInstance();
        bookDAO = BookDAO.getInstance();
        logger.debug(TAG, "BookItemDAO initialized with concurrent cache capacity: " + CACHE_SIZE_LIMIT);
    }

    /**
     * Lấy instance của BookItemDAO (Singleton pattern)
     * @return instance duy nhất của BookItemDAO
     */
    public static synchronized BookItemDAO getInstance() {
        if (bookItemDAO == null) {
            bookItemDAO = new BookItemDAO();
        }
        return bookItemDAO;
    }

    /**
     * Thêm BookItem mới vào cơ sở dữ liệu
     * @param entity BookItem cần thêm
     * @throws SQLException nếu có lỗi xảy ra khi thêm
     */
    @Override
    public void insert(BookItem entity) throws SQLException {
        logger.debug(TAG, "Adding new book item with ISBN: " + entity.getIsbn());
        
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet generatedKeys = null;
        
        try {
            connection = databaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(INSERT_BOOK_ITEM, 
                                                         PreparedStatement.RETURN_GENERATED_KEYS);
            
            preparedStatement.setLong(1, entity.getIsbn());
            preparedStatement.setString(2, entity.getBookItemStatus().name());
            preparedStatement.setString(3, entity.getRemarks());

            int affectedRows = preparedStatement.executeUpdate();
            
            if (affectedRows == 0) {
                logger.error(TAG, "Creating book item failed, no rows affected");
                throw new SQLException("Creating book item failed, no rows affected");
            }

            generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int barcode = generatedKeys.getInt(1);
                entity.setBookBarcode(barcode);
                
                // Cập nhật cache
                addToCache(entity);
                
                logger.info(TAG, "Book item created successfully with barcode: " + barcode);
            } else {
                logger.error(TAG, "Creating book item failed, no ID obtained");
                throw new SQLException("Creating book item failed, no ID obtained");
            }
        } catch (SQLException e) {
            logger.error(TAG, "Error adding book item: " + e.getMessage(), e);
            throw e;
        } finally {
            closeResources(preparedStatement, generatedKeys);
        }
    }

    /**
     * Cập nhật thông tin BookItem trong cơ sở dữ liệu
     * @param entity BookItem cần cập nhật
     * @return true nếu cập nhật thành công, false nếu không
     * @throws SQLException nếu có lỗi xảy ra khi cập nhật
     */
    @Override
    public boolean updateEntity(@NotNull BookItem entity) throws SQLException {
        logger.debug(TAG, "Updating book item with barcode: " + entity.getBookBarcode());
        
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        
        try {
            connection = databaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(UPDATE_BOOK_ITEM);
            
            preparedStatement.setString(1, entity.getBookItemStatus().name());
            preparedStatement.setString(2, entity.getRemarks());
            preparedStatement.setLong(3, entity.getIsbn());
            preparedStatement.setInt(4, entity.getBookBarcode());

            int affectedRows = preparedStatement.executeUpdate();
            
            if (affectedRows > 0) {
                // Cập nhật cache
                addToCache(entity);
                
                logger.info(TAG, "Book item updated successfully: " + entity.getBookBarcode());
                return true;
            } else {
                logger.warning(TAG, "Book item not found for update: " + entity.getBookBarcode());
                return false;
            }
        } catch (SQLException e) {
            logger.error(TAG, "Error updating book item: " + e.getMessage(), e);
            throw e;
        } finally {
            closeResources(preparedStatement, null);
        }
    }

    /**
     * Xóa BookItem khỏi cơ sở dữ liệu
     * @param entity BookItem cần xóa
     * @return true nếu xóa thành công, false nếu không
     * @throws SQLException nếu có lỗi xảy ra khi xóa
     */
    @Override
    public boolean deleteEntity(@NotNull BookItem entity) throws SQLException {
        logger.debug(TAG, "Deleting book item with barcode: " + entity.getBookBarcode());
        
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        
        try {
            connection = databaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(DELETE_BOOK_ITEM);
            
            preparedStatement.setInt(1, entity.getBookBarcode());

            int affectedRows = preparedStatement.executeUpdate();
            
            if (affectedRows > 0) {
                // Xóa khỏi cache
                bookItemCache.remove(entity.getBookBarcode());
                
                logger.info(TAG, "Book item deleted successfully: " + entity.getBookBarcode());
                return true;
            } else {
                logger.warning(TAG, "Book item not found for deletion: " + entity.getBookBarcode());
                return false;
            }
        } catch (SQLException e) {
            logger.error(TAG, "Error deleting book item: " + e.getMessage(), e);
            throw e;
        } finally {
            closeResources(preparedStatement, null);
        }
    }

    /**
     * Tìm BookItem theo barcode
     * @param keywords Barcode của BookItem cần tìm
     * @return BookItem nếu tìm thấy, null nếu không
     * @throws SQLException nếu có lỗi xảy ra khi tìm kiếm
     */
    @Override
    public BookItem findById(Number keywords) throws SQLException {
        int barcode = keywords.intValue();
        
        // Kiểm tra cache trước
        BookItem cachedItem = bookItemCache.get(barcode);
        if (cachedItem != null) {
            logger.debug(TAG, "Cache hit for book item: " + barcode);
            return cachedItem;
        }
        
        logger.debug(TAG, "Cache miss for book item: " + barcode + ", fetching from database");
        
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        try {
            connection = databaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(SELECT_BOOK_ITEM_BY_BARCODE);
            
            preparedStatement.setInt(1, barcode);
            resultSet = preparedStatement.executeQuery();
            
            if (resultSet.next()) {
                BookItem bookItem = createBookItemFromResultSet(resultSet);
                
                // Cập nhật cache
                addToCache(bookItem);
                
                logger.info(TAG, "Book item found: " + barcode);
                return bookItem;
            } else {
                logger.warning(TAG, "Book item not found: " + barcode);
                return null;
            }
        } catch (SQLException e) {
            logger.error(TAG, "Error finding book item: " + e.getMessage(), e);
            throw e;
        } finally {
            closeResources(preparedStatement, resultSet);
        }
    }

    /**
     * Tìm kiếm BookItem theo các tiêu chí
     * @param criteria Map các tiêu chí tìm kiếm
     * @return Danh sách BookItem tìm thấy
     * @throws SQLException nếu có lỗi xảy ra khi tìm kiếm
     */
    @Override
    public List<BookItem> findByCriteria(@NotNull Map<String, Object> criteria) throws SQLException {
        logger.debug(TAG, "Searching book items by criteria: " + generateKeywords(criteria));
        
        if (criteria.isEmpty()) {
            logger.warning(TAG, "Empty criteria, returning all book items");
            return findAll();
        }
        
        // Tối ưu cho một số trường hợp tìm kiếm phổ biến
        if (criteria.size() == 1) {
            // Trường hợp tìm theo ISBN
            if (criteria.containsKey("ISBN")) {
                try {
                    long isbn = Long.parseLong(criteria.get("ISBN").toString());
                    return getBookItemsByISBN(isbn);
                } catch (NumberFormatException e) {
                    logger.warning(TAG, "Invalid ISBN format: " + criteria.get("ISBN"));
                    // Tiếp tục với truy vấn thông thường
                }
            }
            // Trường hợp tìm theo trạng thái
            if (criteria.containsKey("BookItemStatus")) {
                try {
                    BookItemStatus status = BookItemStatus.valueOf(criteria.get("BookItemStatus").toString());
                    return getBookItemsByStatus(status);
                } catch (IllegalArgumentException e) {
                    logger.warning(TAG, "Invalid BookItemStatus: " + criteria.get("BookItemStatus"));
                    // Tiếp tục với truy vấn thông thường
                }
            }
        }
        
        // Xây dựng truy vấn động cho các trường hợp phức tạp
        StringBuilder sqlBuilder = new StringBuilder(
            "SELECT DISTINCT bi.\"barcode\" FROM \"BookItem\" bi ");
        
        // Chỉ thêm JOIN khi cần thiết
        boolean needBooksJoin = false;
        
        for (String key : criteria.keySet()) {
            if (key.startsWith("Books.") || (key.equals("title") || key.equals("description"))) {
                needBooksJoin = true;
                break;
            }
        }
        
        if (needBooksJoin) {
            sqlBuilder.append("JOIN \"Books\" b ON bi.\"ISBN\" = b.\"ISBN\" ");
        }
        
        sqlBuilder.append("WHERE ");

        List<Object> paramValues = new ArrayList<>();
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
            
            if (key.equals("ISBN")) {
                try {
                    sqlBuilder.append("bi.\"ISBN\" = ?");
                    paramValues.add(Long.parseLong(value.toString()));
                } catch (NumberFormatException e) {
                    sqlBuilder.append("CAST(bi.\"ISBN\" AS TEXT) LIKE ?");
                    paramValues.add("%" + value.toString() + "%");
                }
            } else if (key.equals("BookItemStatus")) {
                sqlBuilder.append("bi.\"BookItemStatus\" = ?::book_item_status");
                paramValues.add(value.toString());
            } else if (key.equals("barcode")) {
                try {
                    sqlBuilder.append("bi.\"barcode\" = ?");
                    paramValues.add(Integer.parseInt(value.toString()));
                } catch (NumberFormatException e) {
                    sqlBuilder.append("CAST(bi.\"barcode\" AS TEXT) LIKE ?");
                    paramValues.add("%" + value.toString() + "%");
                }
            } else {
                // Xử lý các trường hợp còn lại
                String tableColumn;
                if (key.startsWith("Books.")) {
                    tableColumn = "b.\"" + key.substring(6) + "\"";
                } else if (key.equals("title") || key.equals("description")) {
                    tableColumn = "b.\"" + key + "\"";
                } else {
                    tableColumn = "bi.\"" + key + "\"";
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
        
        sqlBuilder.append(" LIMIT 1000");
        
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
                } else if (value instanceof Long) {
                    preparedStatement.setLong(i + 1, (Long) value);
                } else {
                    preparedStatement.setString(i + 1, value.toString());
                }
            }
            
            resultSet = preparedStatement.executeQuery();
            
            List<BookItem> bookItems = new ArrayList<>();
            while (resultSet.next()) {
                int barcode = resultSet.getInt("barcode");
                BookItem bookItem = findById(barcode);
                if (bookItem != null) {
                    bookItems.add(bookItem);
                }
            }
            
            logger.info(TAG, "Found " + bookItems.size() + " book items matching criteria");
            return bookItems;
        } catch (SQLException e) {
            logger.error(TAG, "Error searching book items: " + e.getMessage(), e);
            throw e;
        } finally {
            closeResources(preparedStatement, resultSet);
        }
    }

    /**
     * Lấy tất cả BookItem từ cơ sở dữ liệu
     * @return Danh sách tất cả BookItem
     * @throws SQLException nếu có lỗi xảy ra khi truy vấn
     */
    @Override
    public List<BookItem> findAll() throws SQLException {
        logger.debug(TAG, "Selecting all book items (limited to 1000)");
        
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        try {
            connection = databaseConnection.getConnection();
            
            // Kiểm tra số lượng bản ghi trước khi thực hiện truy vấn đầy đủ
            int count = getBookItemCount();
            if (count > 1000) {
                logger.warning(TAG, "Large number of book items (" + count + "), limiting to 1000");
            }
            
            preparedStatement = connection.prepareStatement(SELECT_ALL_BOOK_ITEMS);
            resultSet = preparedStatement.executeQuery();
            
            List<BookItem> bookItems = new ArrayList<>();
            while (resultSet.next()) {
                int barcode = resultSet.getInt("barcode");
                BookItem bookItem = findById(barcode);
                if (bookItem != null) {
                    bookItems.add(bookItem);
                }
            }
            
            logger.info(TAG, "Selected " + bookItems.size() + " book items");
            return bookItems;
        } catch (SQLException e) {
            logger.error(TAG, "Error selecting all book items: " + e.getMessage(), e);
            throw e;
        } finally {
            closeResources(preparedStatement, resultSet);
        }
    }
    
    /**
     * Đếm số lượng BookItem trong cơ sở dữ liệu
     * @return Số lượng BookItem
     * @throws SQLException nếu có lỗi xảy ra khi truy vấn
     */
    private int getBookItemCount() throws SQLException {
        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(COUNT_BOOK_ITEMS);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        }
    }

    /**
     * Lấy danh sách BookItem theo ISBN
     * @param isbn ISBN của truyện
     * @return Danh sách BookItem có ISBN tương ứng
     * @throws SQLException nếu có lỗi xảy ra khi truy vấn
     */
    public List<BookItem> getBookItemsByISBN(long isbn) throws SQLException {
        logger.debug(TAG, "Getting book items for ISBN: " + isbn);
        
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        try {
            connection = databaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(SELECT_BOOK_ITEMS_BY_ISBN);
            
            preparedStatement.setLong(1, isbn);
            resultSet = preparedStatement.executeQuery();
            
            List<BookItem> bookItems = new ArrayList<>();
            while (resultSet.next()) {
                int barcode = resultSet.getInt("barcode");
                BookItem bookItem = findById(barcode);
                if (bookItem != null) {
                    bookItems.add(bookItem);
                }
            }
            
            logger.info(TAG, "Found " + bookItems.size() + " book items for ISBN: " + isbn);
            return bookItems;
        } catch (SQLException e) {
            logger.error(TAG, "Error getting book items for ISBN: " + e.getMessage(), e);
            throw e;
        } finally {
            closeResources(preparedStatement, resultSet);
        }
    }
    
    /**
     * Lấy danh sách BookItem theo trạng thái
     * @param status Trạng thái cần tìm
     * @return Danh sách BookItem có trạng thái tương ứng
     * @throws SQLException nếu có lỗi xảy ra khi truy vấn
     */
    public List<BookItem> getBookItemsByStatus(BookItemStatus status) throws SQLException {
        logger.debug(TAG, "Getting book items with status: " + status);
        
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        try {
            connection = databaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(SELECT_BOOK_ITEMS_BY_STATUS);
            
            preparedStatement.setString(1, status.name());
            resultSet = preparedStatement.executeQuery();
            
            List<BookItem> bookItems = new ArrayList<>();
            while (resultSet.next()) {
                int barcode = resultSet.getInt("barcode");
                BookItem bookItem = findById(barcode);
                if (bookItem != null) {
                    bookItems.add(bookItem);
                }
            }
            
            logger.info(TAG, "Found " + bookItems.size() + " book items with status: " + status);
            return bookItems;
        } catch (SQLException e) {
            logger.error(TAG, "Error getting book items by status: " + e.getMessage(), e);
            throw e;
        } finally {
            closeResources(preparedStatement, resultSet);
        }
    }
    
    /**
     * Tìm kiếm BookItem đầu tiên có sẵn theo ISBN
     * @param isbn ISBN của truyện cần tìm
     * @return BookItem đầu tiên có sẵn hoặc null nếu không tìm thấy
     * @throws SQLException nếu có lỗi xảy ra khi truy vấn
     */
    public BookItem findFirstAvailableBookItemByISBN(long isbn) throws SQLException {
        logger.debug(TAG, "Tìm kiếm BookItem có sẵn đầu tiên theo ISBN: " + isbn);
        
        List<BookItem> bookItems = getBookItemsByISBN(isbn);
        
        for (BookItem bookItem : bookItems) {
            if (bookItem.getBookItemStatus() == BookItemStatus.AVAILABLE) {
                logger.info(TAG, "Tìm thấy BookItem có sẵn với barcode: " + bookItem.getBookBarcode());
                return bookItem;
            }
        }
        
        logger.warning(TAG, "Không tìm thấy BookItem có sẵn nào với ISBN: " + isbn);
        return null;
    }
    
    /**
     * Vô hiệu hóa cache cho BookItem theo barcode
     * @param barcode Barcode của BookItem cần vô hiệu hóa cache
     */
    public void invalidateBookItemCache(int barcode) {
        bookItemCache.remove(barcode);
        logger.debug(TAG, "Book item cache invalidated for barcode: " + barcode);
    }
    
    /**
     * Xóa toàn bộ cache
     */
    public void clearCache() {
        bookItemCache.clear();
        logger.debug(TAG, "Cleared entire book item cache");
    }
    
    /**
     * Tạo đối tượng BookItem từ ResultSet
     * @param resultSet ResultSet chứa dữ liệu BookItem
     * @return Đối tượng BookItem
     * @throws SQLException nếu có lỗi xảy ra khi đọc ResultSet
     */
    private BookItem createBookItemFromResultSet(ResultSet resultSet) throws SQLException {
        int barcode = resultSet.getInt("barcode");
        long isbn = resultSet.getLong("ISBN");
        String statusStr = resultSet.getString("BookItemStatus");
        String remarks = resultSet.getString("note");
        
        BookItemStatus status;
        try {
            status = BookItemStatus.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            logger.warning(TAG, "Invalid BookItemStatus: " + statusStr + ", using default AVAILABLE");
            status = BookItemStatus.AVAILABLE;
        }
        
        try {
            return new BookItem(barcode, status, remarks, bookDAO.findById(isbn));
        } catch (Exception e) {
            logger.error(TAG, "Error creating BookItem from ResultSet: " + e.getMessage(), e);
            throw new SQLException("Could not create BookItem object: " + e.getMessage());
        }
    }
    
    /**
     * Thêm BookItem vào cache với xử lý kích thước cache
     * @param bookItem BookItem cần thêm vào cache
     */
    private void addToCache(BookItem bookItem) {
        if (bookItem == null || bookItem.getBookBarcode() <= 0) {
            logger.warning(TAG, "Attempted to add invalid book item to cache");
            return;
        }
        
        // Kiểm tra và điều chỉnh kích thước cache nếu cần
        if (bookItemCache.size() >= CACHE_SIZE_LIMIT) {
            // Nếu cache đầy, xóa một số mục ngẫu nhiên
            int toRemove = CACHE_SIZE_LIMIT / 10; // Xóa 10% cache
            logger.debug(TAG, "Cache size limit reached, removing " + toRemove + " entries");
            
            List<Integer> keys = new ArrayList<>(bookItemCache.keySet());
            for (int i = 0; i < Math.min(toRemove, keys.size()); i++) {
                bookItemCache.remove(keys.get(i));
            }
        }
        
        // Thêm vào cache
        bookItemCache.put(bookItem.getBookBarcode(), bookItem);
    }
    
    /**
     * Tạo chuỗi từ khóa tìm kiếm từ Map tiêu chí
     * @param criteria Map các tiêu chí tìm kiếm
     * @return Chuỗi từ khóa
     */
    @NotNull
    private String generateKeywords(@NotNull Map<String, Object> criteria) {
        StringBuilder keywords = new StringBuilder();

        for (Map.Entry<String, Object> entry : criteria.entrySet()) {
            if (entry.getValue() != null) {
                keywords.append(entry.getKey()).append(": ").append(entry.getValue().toString()).append(", ");
            }
        }

        if (keywords.length() > 2) {
            keywords.setLength(keywords.length() - 2); // Xóa dấu phẩy và khoảng trắng cuối cùng
        }

        return keywords.toString();
    }
    
    /**
     * Đóng tài nguyên PreparedStatement và ResultSet
     * @param preparedStatement PreparedStatement cần đóng
     * @param resultSet ResultSet cần đóng
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
}
