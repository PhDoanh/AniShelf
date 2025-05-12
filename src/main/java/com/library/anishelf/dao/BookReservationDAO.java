package com.library.anishelf.dao;

import com.github.benmanes.caffeine.cache.Cache;
import com.library.anishelf.model.BookItem;
import com.library.anishelf.model.enums.BookItemStatus;
import com.library.anishelf.model.Member;
import com.library.anishelf.model.BookReservation;
import com.library.anishelf.model.enums.BookReservationStatus;
import com.library.anishelf.util.CacheManagerUtil;
import com.library.anishelf.util.RuntimeDebugUtil;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The type Book reservation dao.
 */
public class BookReservationDAO implements GenericDAO<BookReservation> {
    private static BookReservationDAO bookReservationDAO;
    private static final RuntimeDebugUtil logger = RuntimeDebugUtil.getInstance();
    private static final String TAG = "BookReservationDAO";

    private static DatabaseConnection databaseConnection;
    private static MemberDAO memberDAO;
    private static BookItemDAO bookItemDAO;
    private static BookDAO bookDAO;
    private static Cache<Integer, BookReservation> bookReservationCache;

    private BookReservationDAO() {
        databaseConnection = DatabaseConnection.getInstance();
        memberDAO = MemberDAO.getInstance();
        bookItemDAO = BookItemDAO.getInstance();
        bookDAO = BookDAO.getInstance();
        bookReservationCache = CacheManagerUtil.buildCache(100);
        logger.debug(TAG, "Khởi tạo BookReservationDAO với cache có kích thước 100");
    }


    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static synchronized BookReservationDAO getInstance() {
        if (bookReservationDAO == null) {
            bookReservationDAO = new BookReservationDAO();
        }
        return bookReservationDAO;
    }


    private static final String INSERT_BOOK_RESERVATION
            = "INSERT INTO \"BookReservation\"(\"member_ID\", \"barcode\", \"creation_date\", \"due_date\") VALUES (?, ?, ?, ?)";


    private static final String UPDATE_BOOK_RESERVATION
            = "UPDATE \"BookReservation\" SET \"member_ID\" = ?, \"barcode\" = ?, \"creation_date\" = ?, \"due_date\" = ?, \"BookReservationStatus\" = ?::reservation_status WHERE \"reservation_ID\" = ?";


    private static final String DELETE_BOOK_RESERVATION
            = "DELETE FROM \"BookReservation\" WHERE \"reservation_ID\" = ?";


    private static final String SELECT_BOOK_RESERVATION_BY_ID = "SELECT * FROM \"BookReservation\" WHERE \"reservation_ID\" = ?";


    private static final String SELECT_ALL_BOOK_RESERVATIONS = "SELECT * FROM \"BookReservation\"";


    @Override
    public void insert(@NotNull BookReservation entity) throws SQLException {
        logger.debug(TAG, "Thêm đặt chỗ mới cho truyện: ISBN=" + entity.getBookItem().getIsbn() +
                ", barcode=" + entity.getBookItem().getBookBarcode());
        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(INSERT_BOOK_RESERVATION, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, entity.getMember().getPerson().getId());
            preparedStatement.setInt(2, entity.getBookItem().getBookBarcode());
            preparedStatement.setDate(3, java.sql.Date.valueOf(entity.getReservationDate()));
            preparedStatement.setDate(4, java.sql.Date.valueOf(entity.getExpectedReturnDate()));

            int result = preparedStatement.executeUpdate();
            logger.info(TAG, "Thêm đặt chỗ thành công, " + result + " dòng bị ảnh hưởng");

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int reservationId = generatedKeys.getInt(1);
                    entity.setId(reservationId);
                    logger.debug(TAG, "Đặt chỗ mới được tạo với ID: " + reservationId);
                }
            }

            memberDAO.fetchCache(entity.getMember().getPerson().getId());
            bookItemDAO.invalidateBookItemCache(entity.getBookItem().getBookBarcode());
            bookDAO.invalidateBookCache(entity.getBookItem().getIsbn());
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi SQL khi thêm đặt chỗ: " + e.getMessage(), e);
            throw e;
        }
    }


    @Override
    public boolean updateEntity(@NotNull BookReservation entity) throws SQLException {
        logger.debug(TAG, "Cập nhật đặt chỗ ID: " + entity.getId());
        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(UPDATE_BOOK_RESERVATION)) {
            preparedStatement.setInt(1, entity.getMember().getPerson().getId());
            preparedStatement.setInt(2, entity.getBookItem().getBookBarcode());
            preparedStatement.setDate(3, java.sql.Date.valueOf(entity.getReservationDate()));
            preparedStatement.setDate(4, java.sql.Date.valueOf(entity.getExpectedReturnDate()));
            preparedStatement.setString(5, entity.getReservationStatus().name());
            preparedStatement.setInt(6, entity.getId());

            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                logger.info(TAG, "Cập nhật đặt chỗ thành công, ID: " + entity.getId());
                bookReservationCache.put(entity.getId(), entity);
                memberDAO.fetchCache(entity.getMember().getPerson().getId());
                bookItemDAO.invalidateBookItemCache(entity.getBookItem().getBookBarcode());
                bookDAO.invalidateBookCache(entity.getBookItem().getIsbn());
                return true;
            } else {
                logger.warning(TAG, "Không thể cập nhật đặt chỗ ID: " + entity.getId());
                return false;
            }
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi SQL khi cập nhật đặt chỗ: " + e.getMessage(), e);
            throw e;
        }
    }


    @Override
    public boolean deleteEntity(@NotNull BookReservation entity) throws SQLException {
        logger.debug(TAG, "Xóa đặt chỗ ID: " + entity.getId());
        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(DELETE_BOOK_RESERVATION)) {
            preparedStatement.setInt(1, entity.getId());

            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                logger.info(TAG, "Xóa đặt chỗ thành công, ID: " + entity.getId());
                bookReservationCache.invalidate(entity.getId());
                memberDAO.fetchCache(entity.getMember().getPerson().getId());
                bookItemDAO.invalidateBookItemCache(entity.getBookItem().getBookBarcode());
                bookDAO.invalidateBookCache(entity.getBookItem().getIsbn());
                return true;
            } else {
                logger.warning(TAG, "Không thể xóa đặt chỗ ID: " + entity.getId());
                return false;
            }
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi SQL khi xóa đặt chỗ: " + e.getMessage(), e);
            throw e;
        }
    }


    @Override
    public BookReservation findById(@NotNull Number keywords) throws SQLException {
        int reservationId = keywords.intValue();
        logger.debug(TAG, "Tìm kiếm đặt chỗ với ID: " + reservationId);

        // Kiểm tra cache trước
        BookReservation cachedReservation = bookReservationCache.getIfPresent(reservationId);
        if (cachedReservation != null) {
            logger.info(TAG, "Tìm thấy đặt chỗ ID " + reservationId + " trong cache");
            return cachedReservation;
        }

        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(SELECT_BOOK_RESERVATION_BY_ID)) {
            preparedStatement.setInt(1, reservationId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    BookReservation bookReservation = new BookReservation(
                            resultSet.getInt("reservation_ID"),
                            memberDAO.findById(resultSet.getInt("member_ID")),
                            bookItemDAO.findById(resultSet.getInt("barcode")),
                            resultSet.getString("creation_date"),
                            resultSet.getString("due_date"),
                            BookReservationStatus.valueOf(resultSet.getString("BookReservationStatus"))
                    );

                    // Lưu vào cache
                    bookReservationCache.put(bookReservation.getId(), bookReservation);
                    logger.info(TAG, "Tìm thấy và cache đặt chỗ ID: " + reservationId);
                    return bookReservation;
                } else {
                    logger.info(TAG, "Không tìm thấy đặt chỗ ID: " + reservationId);
                }
            }
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi SQL khi tìm kiếm đặt chỗ ID " + reservationId + ": " + e.getMessage(), e);
            throw e;
        }
        return null;
    }


    @Override
    public List<BookReservation> findByCriteria(@NotNull Map<String, Object> criteria) throws SQLException {
        if (criteria.isEmpty()) {
            logger.warning(TAG, "Tìm kiếm với tiêu chí rỗng, trả về danh sách trống");
            return new ArrayList<>();
        }

        logger.debug(TAG, "Tìm kiếm đặt chỗ theo tiêu chí: " + criteria);
        StringBuilder findBookReservationByCriteria = new StringBuilder("SELECT DISTINCT (\"reservation_ID\") FROM \"BookReservation\" " +
                "JOIN \"Members\" ON \"BookReservation\".\"member_ID\" = \"Members\".\"member_ID\" " +
                "JOIN \"BookItem\" ON \"BookReservation\".\"barcode\" = \"BookItem\".\"barcode\" " +
                "JOIN \"Books\" ON \"Books\".\"ISBN\" = \"BookItem\".\"ISBN\" " +
                "WHERE ");

        boolean[] flag = new boolean[15];
        int index = 1;

        for (String key : criteria.keySet()) {
            switch (key) {
                case "barcode" ->
                        findBookReservationByCriteria.append("CAST(\"BookReservation\".\"barcode\" AS TEXT) LIKE ? AND ");
                case "member_ID" ->
                        findBookReservationByCriteria.append("CAST(\"BookReservation\".\"member_ID\" AS TEXT) LIKE ? AND ");
                case "creation_date" -> {
                    flag[index] = true;
                    findBookReservationByCriteria.append("DATE(\"creation_date\") = ? AND ");
                }
                case "due_date" -> {
                    flag[index] = true;
                    findBookReservationByCriteria.append("DATE(\"due_date\") = ? AND ");
                }
                case "BookReservationStatus" -> {
                    flag[index] = true;

                    findBookReservationByCriteria.append("\"BookReservationStatus\" = ?::reservation_status AND ");
                }
                case "fullname" ->
                        findBookReservationByCriteria.append("CONCAT(\"Members\".\"last_name\", ' ', \"Members\".\"first_name\") LIKE ? AND ");
                default -> {
                    if (!key.startsWith("\"")) {
                        findBookReservationByCriteria.append("\"").append(key).append("\"").append(" LIKE ? AND ");
                    } else {
                        findBookReservationByCriteria.append(key).append(" LIKE ? AND ");
                    }
                }
            }
            index++;
        }

        findBookReservationByCriteria.setLength(findBookReservationByCriteria.length() - 5);

        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(findBookReservationByCriteria.toString())) {
            index = 1;
            for (Map.Entry<String, Object> entry : criteria.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (!flag[index]) {
                    preparedStatement.setString(index++, "%" + value.toString() + "%");
                } else {
                    preparedStatement.setString(index++, value.toString());
                }
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<BookReservation> bookReservations = new ArrayList<>();
                while (resultSet.next()) {
                    BookReservation reservation = findById(resultSet.getInt("reservation_ID"));
                    if (reservation != null) {
                        bookReservations.add(reservation);
                    }
                }
                logger.info(TAG, "Tìm thấy " + bookReservations.size() + " đặt chỗ theo tiêu chí");
                return bookReservations;
            }
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi SQL khi tìm kiếm theo tiêu chí: " + e.getMessage(), e);
            throw e;
        }
    }


    @Override
    public List<BookReservation> findAll() throws SQLException {
        logger.debug(TAG, "Lấy danh sách tất cả đặt chỗ");
        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(SELECT_ALL_BOOK_RESERVATIONS)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<BookReservation> bookReservations = new ArrayList<>();
                while (resultSet.next()) {
                    BookReservation reservation = findById(resultSet.getInt("reservation_ID"));
                    if (reservation != null) {
                        bookReservations.add(reservation);
                    }
                }
                logger.info(TAG, "Đã lấy " + bookReservations.size() + " đặt chỗ từ database");
                return bookReservations;
            }
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi SQL khi lấy tất cả đặt chỗ: " + e.getMessage(), e);
            throw e;
        }
    }


    /**
     * Invalidate book reservation cache.
     *
     * @param reservationID the reservation id
     */
    public void invalidateBookReservationCache(int reservationID) {
        logger.debug(TAG, "Xóa đặt chỗ ID " + reservationID + " khỏi cache");
        bookReservationCache.invalidate(reservationID);
    }

    /**
     * Tạo đặt trước sách theo ISBN thay vì quét mã vạch
     *
     * @param member          Thành viên đặt sách
     * @param isbn            ISBN của sách cần đặt
     * @param reservationDate Ngày đặt sách
     * @param dueDate         Ngày hẹn trả
     * @return BookReservation đã tạo hoặc null nếu không tìm thấy sách
     * @throws SQLException nếu có lỗi xảy ra khi thao tác database
     */
    public BookReservation createReservationByISBN(Member member, long isbn, String reservationDate, String dueDate) throws SQLException {
        logger.debug(TAG, "Tạo đặt sách mới theo ISBN: " + isbn + " cho thành viên ID: " + member.getPerson().getId());

        // Tìm BookItem có sẵn đầu tiên với ISBN tương ứng
        BookItem bookItem = bookItemDAO.findFirstAvailableBookItemByISBN(isbn);

        if (bookItem == null) {
            logger.warning(TAG, "Không tìm thấy bản sao nào có sẵn của truyện có ISBN: " + isbn);
            return null;
        }

        // Tạo đối tượng BookReservation
        BookReservation reservation = new BookReservation(0, member, bookItem, reservationDate, dueDate, BookReservationStatus.WAITING);

        // Lưu vào cơ sở dữ liệu
        insert(reservation);

        // Cập nhật trạng thái của BookItem thành RESERVED
        bookItem.setBookItemStatus(BookItemStatus.RESERVED);
        bookItemDAO.updateEntity(bookItem);

        logger.info(TAG, "Đã tạo đặt truyện thành công với ID: " + reservation.getId() +
                ", barcode: " + bookItem.getBookBarcode());

        return reservation;
    }
}
