package com.library.anishelf.dao;

import com.library.anishelf.model.BookMark;
import com.library.anishelf.model.Member;
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
 * Lớp quản lý các BookMark trong cơ sở dữ liệu
 * Triển khai giao diện GenericDAO cho thao tác CRUD với BookMark
 */
public class BookMarkDAO implements GenericDAO<BookMark> {
    private static BookMarkDAO bookMarkDAO;
    private static DatabaseConnection databaseConnection;
    private static MemberDAO memberDAO;
    private static BookDAO bookDAO;
    private static BookItemDAO bookItemDAO;
    
    // Khai báo RuntimeDebugUtil và TAG
    private static final RuntimeDebugUtil logger = RuntimeDebugUtil.getInstance();
    private static final String TAG = "BookMarkDAO";
    
    // Bộ nhớ đệm để lưu trữ các bookmark theo member_ID để truy xuất nhanh
    private final ConcurrentHashMap<Integer, List<BookMark>> bookmarkCache = new ConcurrentHashMap<>();

    // Các câu lệnh SQL được định nghĩa như hằng số
    private static final String INSERT_BOOKMARK = 
            "INSERT INTO \"BookMark\" (\"member_ID\", \"ISBN\") VALUES (?, ?)";

    private static final String DELETE_BOOKMARK = 
            "DELETE FROM \"BookMark\" WHERE \"member_ID\" = ? AND \"ISBN\" = ?";

    private static final String SELECT_BOOKMARKS_BY_MEMBER = 
            "SELECT * FROM \"BookMark\" WHERE \"member_ID\" = ?";
            
    private static final String SELECT_BOOKMARK_BY_MEMBER_AND_ISBN = 
            "SELECT * FROM \"BookMark\" WHERE \"member_ID\" = ? AND \"ISBN\" = ?";
            
    private static final String SELECT_ALL_BOOKMARKS = 
            "SELECT * FROM \"BookMark\" LIMIT 1000";

    /**
     * Constructor riêng cho Singleton pattern
     */
    private BookMarkDAO() {
        databaseConnection = DatabaseConnection.getInstance();
        memberDAO = MemberDAO.getInstance();
        bookDAO = BookDAO.getInstance();
        bookItemDAO = BookItemDAO.getInstance();
        logger.debug(TAG, "BookMarkDAO đã được khởi tạo");
    }

    /**
     * Lấy instance duy nhất của BookMarkDAO (Singleton pattern)
     * @return BookMarkDAO instance
     */
    public static synchronized BookMarkDAO getInstance() {
        if (bookMarkDAO == null) {
            bookMarkDAO = new BookMarkDAO();
        }
        return bookMarkDAO;
    }

    /**
     * Thêm một bookmark mới vào cơ sở dữ liệu
     * @param entity BookMark cần thêm
     * @throws SQLException nếu có lỗi SQL
     */
    @Override
    public void insert(@NotNull BookMark entity) throws SQLException {
        logger.debug(TAG, "Thêm bookmark cho member " + entity.getMember().getPerson().getId() + 
                ", ISBN: " + entity.getBook().getIsbn());
        
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        
        try {
            // Kiểm tra xem bookmark đã tồn tại chưa
            if (isBookmarkExists(entity.getMember().getPerson().getId(), entity.getBook().getIsbn())) {
                logger.info(TAG, "Bookmark đã tồn tại, bỏ qua thao tác thêm");
                return;
            }
            
            connection = databaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(INSERT_BOOKMARK);
            
            preparedStatement.setInt(1, entity.getMember().getPerson().getId());
            preparedStatement.setLong(2, entity.getBook().getIsbn());

            int rowsAffected = preparedStatement.executeUpdate();
            
            if (rowsAffected > 0) {
                // Làm mới cache nếu thành công
                invalidateMemberCache(entity.getMember().getPerson().getId());
                logger.info(TAG, "Đã thêm bookmark thành công");
            } else {
                logger.warning(TAG, "Không thể thêm bookmark");
            }
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi thêm bookmark: " + e.getMessage(), e);
            throw e;
        } finally {
            closeResources(preparedStatement, null);
        }
    }

    /**
     * Kiểm tra xem bookmark đã tồn tại chưa
     * @param memberId ID của thành viên
     * @param isbn ISBN của truyện
     * @return true nếu bookmark đã tồn tại, false nếu chưa
     * @throws SQLException nếu có lỗi SQL
     */
    private boolean isBookmarkExists(int memberId, long isbn) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        try {
            preparedStatement = databaseConnection.getConnection().prepareStatement(SELECT_BOOKMARK_BY_MEMBER_AND_ISBN);
            preparedStatement.setInt(1, memberId);
            preparedStatement.setLong(2, isbn);
            
            resultSet = preparedStatement.executeQuery();
            return resultSet.next(); // Trả về true nếu có kết quả
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi kiểm tra bookmark tồn tại: " + e.getMessage(), e);
            throw e;
        } finally {
            closeResources(preparedStatement, resultSet);
        }
    }

    /**
     * Cập nhật bookmark - không được hỗ trợ cho BookMark
     * @param entity BookMark cần cập nhật
     * @return false vì không hỗ trợ cập nhật
     * @throws SQLException nếu có lỗi SQL
     */
    @Override
    public boolean updateEntity(BookMark entity) throws SQLException {
        logger.warning(TAG, "Phương thức update không được hỗ trợ cho BookMark");
        return false;
    }

    /**
     * Xóa bookmark khỏi cơ sở dữ liệu
     * @param entity BookMark cần xóa
     * @return true nếu xóa thành công, false nếu không
     * @throws SQLException nếu có lỗi SQL
     */
    @Override
    public boolean deleteEntity(@NotNull BookMark entity) throws SQLException {
        logger.debug(TAG, "Xóa bookmark cho member " + entity.getMember().getPerson().getId() + 
                ", ISBN: " + entity.getBook().getIsbn());
        
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        
        try {
            connection = databaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(DELETE_BOOKMARK);
            
            preparedStatement.setInt(1, entity.getMember().getPerson().getId());
            preparedStatement.setLong(2, entity.getBook().getIsbn());

            int rowsAffected = preparedStatement.executeUpdate();
            
            if (rowsAffected > 0) {
                // Làm mới cache nếu thành công
                invalidateMemberCache(entity.getMember().getPerson().getId());
                logger.info(TAG, "Đã xóa bookmark thành công");
                return true;
            } else {
                logger.warning(TAG, "Không tìm thấy bookmark để xóa");
                return false;
            }
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi xóa bookmark: " + e.getMessage(), e);
            throw e;
        } finally {
            closeResources(preparedStatement, null);
        }
    }

    /**
     * Tìm bookmark theo ID - không được hỗ trợ cho BookMark
     * @param keywords ID để tìm kiếm
     * @return null vì không hỗ trợ tìm kiếm theo ID
     * @throws SQLException nếu có lỗi SQL
     */
    @Override
    public BookMark findById(Number keywords) throws SQLException {
        logger.warning(TAG, "Phương thức find không được hỗ trợ cho BookMark");
        return null;
    }

    /**
     * Tìm kiếm bookmark theo tiêu chí
     * @param criteria Tiêu chí tìm kiếm
     * @return Danh sách bookmark phù hợp với tiêu chí
     * @throws SQLException nếu có lỗi SQL
     */
    @Override
    public List<BookMark> findByCriteria(Map<String, Object> criteria) throws SQLException {
        if (criteria == null || criteria.isEmpty()) {
            logger.warning(TAG, "Tiêu chí tìm kiếm trống");
            return new ArrayList<>();
        }
        
        logger.debug(TAG, "Tìm kiếm bookmark theo tiêu chí: " + criteria);
        
        // Nếu tiêu chí chỉ chứa member_ID, sử dụng phương thức getAllBookMarksForMember
        if (criteria.size() == 1 && criteria.containsKey("member_ID")) {
            try {
                int memberId = Integer.parseInt(criteria.get("member_ID").toString());
                Member member = memberDAO.findById(memberId);
                if (member != null) {
                    return getAllBookMarksForMember(member);
                }
            } catch (NumberFormatException e) {
                logger.warning(TAG, "member_ID không hợp lệ: " + criteria.get("member_ID"));
            }
        }
        
        logger.info(TAG, "Không hỗ trợ tìm kiếm với tiêu chí phức tạp");
        return new ArrayList<>();
    }

    /**
     * Lấy tất cả bookmark trong hệ thống (có giới hạn)
     * @return Danh sách tất cả bookmark
     * @throws SQLException nếu có lỗi SQL
     */
    @Override
    public List<BookMark> findAll() throws SQLException {
        logger.debug(TAG, "Lấy tất cả bookmark (tối đa 1000)");
        
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<BookMark> bookmarks = new ArrayList<>();
        
        try {
            preparedStatement = databaseConnection.getConnection().prepareStatement(SELECT_ALL_BOOKMARKS);
            resultSet = preparedStatement.executeQuery();
            
            while (resultSet.next()) {
                int memberId = resultSet.getInt("member_ID");
                long isbn = resultSet.getLong("ISBN");
                
                try {
                    Member member = memberDAO.findById(memberId);
                    if (member != null) {
                        BookMark bookmark = new BookMark(member, bookDAO.findById(isbn));
                        bookmarks.add(bookmark);
                    }
                } catch (SQLException e) {
                    logger.warning(TAG, "Không thể tạo bookmark cho member_ID: " + memberId + ", ISBN: " + isbn);
                }
            }
            
            logger.info(TAG, "Đã lấy " + bookmarks.size() + " bookmark");
            return bookmarks;
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi lấy tất cả bookmark: " + e.getMessage(), e);
            throw e;
        } finally {
            closeResources(preparedStatement, resultSet);
        }
    }

    /**
     * Lấy tất cả bookmark của một thành viên
     * @param member Thành viên cần lấy bookmark
     * @return Danh sách bookmark của thành viên
     * @throws SQLException nếu có lỗi SQL
     */
    public List<BookMark> getAllBookMarksForMember(@NotNull Member member) throws SQLException {
        int memberId = member.getPerson().getId();
        logger.debug(TAG, "Lấy tất cả bookmark cho member: " + memberId);
        
        // Kiểm tra cache trước
        List<BookMark> cachedBookmarks = bookmarkCache.get(memberId);
        if (cachedBookmarks != null) {
            logger.debug(TAG, "Trả về " + cachedBookmarks.size() + " bookmark từ cache cho member: " + memberId);
            return new ArrayList<>(cachedBookmarks); // Trả về bản sao để tránh thay đổi cache
        }
        
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        try {
            preparedStatement = databaseConnection.getConnection().prepareStatement(SELECT_BOOKMARKS_BY_MEMBER);
            preparedStatement.setInt(1, memberId);
            
            resultSet = preparedStatement.executeQuery();
            
            List<BookMark> bookmarks = new ArrayList<>();
            while (resultSet.next()) {
                long isbn = resultSet.getLong("ISBN");
                try {
                    BookMark bookmark = new BookMark(member, bookDAO.findById(isbn));
                    bookmarks.add(bookmark);
                } catch (Exception e) {
                    logger.warning(TAG, "Không thể tạo bookmark cho ISBN: " + isbn + ", lỗi: " + e.getMessage());
                }
            }
            
            // Lưu vào cache
            bookmarkCache.put(memberId, new ArrayList<>(bookmarks));
            
            logger.info(TAG, "Đã lấy " + bookmarks.size() + " bookmark cho member: " + memberId);
            return bookmarks;
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi lấy bookmark cho member: " + e.getMessage(), e);
            throw e;
        } finally {
            closeResources(preparedStatement, resultSet);
        }
    }
    
    /**
     * Làm mới cache cho một thành viên
     * @param memberId ID của thành viên
     */
    public void invalidateMemberCache(int memberId) {
        bookmarkCache.remove(memberId);
        logger.debug(TAG, "Đã xóa cache bookmark cho member: " + memberId);
    }
    
    /**
     * Xóa tất cả cache
     */
    public void clearCache() {
        bookmarkCache.clear();
        logger.debug(TAG, "Đã xóa toàn bộ cache bookmark");
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
            logger.error(TAG, "Lỗi khi đóng tài nguyên: " + e.getMessage(), e);
        }
    }
}
