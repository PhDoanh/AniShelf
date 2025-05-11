package com.library.anishelf.dao;

import com.github.benmanes.caffeine.cache.Cache;
import com.library.anishelf.model.Author;
import com.library.anishelf.model.Book;
import com.library.anishelf.model.Category;
import com.library.anishelf.model.enums.BookStatus;
import com.library.anishelf.util.CacheManagerUtil;
import com.library.anishelf.util.RuntimeDebugUtil;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The type Book dao.
 */
public class BookDAO implements GenericDAO<Book> {
    private static final String TAG = "BookDAO";
    private static DatabaseConnection databaseConnection;
    private static BookDAO bookDAO;
    private static Cache<Long, Book> bookCache;
    private static final RuntimeDebugUtil logger = RuntimeDebugUtil.getInstance();

    private BookDAO() {
        databaseConnection = DatabaseConnection.getInstance();
        bookCache = CacheManagerUtil.buildCache(100);
        logger.debug(TAG, "BookDAO được khởi tạo với cache size: 100");
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static synchronized BookDAO getInstance() {
        if (bookDAO == null) {
            bookDAO = new BookDAO();
        }
        return bookDAO;
    }

    private static final String SELECT_AUTHOR_BY_NAME = "SELECT * FROM \"Authors\" WHERE \"author_name\" = ?";

    private static final String INSERT_NEW_AUTHOR = "INSERT INTO \"Authors\" (\"author_name\") VALUES (?)";

    private static final String SELECT_CATEGORY_BY_NAME = "SELECT * FROM \"Category\" WHERE \"category_name\" = ?";

    private static final String INSERT_CATEGORY = "INSERT INTO \"Category\" (\"category_name\") VALUES (?)";

    private static final String INSERT_BOOK_AUTHOR
            = "INSERT INTO \"Books_Authors\"(\"ISBN\", \"author_ID\") VALUES (?, ?)";

    private static final String INSERT_BOOK_CATEGORY
            = "INSERT INTO \"Books_Category\"(\"ISBN\", \"category_ID\") VALUES (?, ?)";

    private static final String INSERT_BOOK_ITEM = "INSERT INTO \"BookItem\"(\"ISBN\") VALUES (?)";

    private static final String INSERT_BOOK
            = "INSERT INTO \"Books\" (\"ISBN\", \"image_path\", \"title\", \"description\", \"placeAt\", \"preview\") VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SELECT_BOOK_BY_ISBN = "SELECT * FROM \"Books\" WHERE \"ISBN\" = ?";

    private static final String UPDATE_BOOK_DETAILS
            = "UPDATE \"Books\" SET \"title\" = ?, \"image_path\" = ?, \"description\" = ?, \"placeAt\" = ?, \"BookStatus\" = ?::book_status WHERE \"ISBN\" = ?";

    private static final String DELETE_BOOK = "DELETE FROM \"Books\" WHERE \"ISBN\" = ?";

    private static final String DELETE_BOOK_AUTHORS = "DELETE FROM \"Books_Authors\" WHERE \"ISBN\" = ?";

    private static final String DELETE_BOOK_CATEGORIES = "DELETE FROM \"Books_Category\" WHERE \"ISBN\" = ?";

    private static final String DELETE_BOOK_ITEMS = "DELETE FROM \"BookItem\" WHERE \"ISBN\" = ?";

    private static final String FIND_BOOK_BY_ISBN = "SELECT * FROM \"Books\" WHERE \"ISBN\" = ?";
    private static final String FIND_AUTHORS_BY_BOOK
            = "SELECT * FROM \"Authors\" a JOIN \"Books_Authors\" b_a ON a.\"author_ID\" = b_a.\"author_ID\" WHERE b_a.\"ISBN\" = ?";
    private static final String FIND_CATEGORIES_BY_BOOK
            = "SELECT * FROM \"Category\" c JOIN \"Books_Category\" b_c ON c.\"category_ID\" = b_c.\"category_ID\" WHERE \"ISBN\" = ?";

    private static final String SELECT_ALL_BOOKS = "SELECT * FROM \"Books\"";
    private static final String SELECT_ALL_CATEGORIES = "SELECT * FROM \"Category\" ORDER BY \"category_name\" ASC";
    private static final String SELECT_ALL_AUTHORS = "SELECT * FROM \"Authors\" ORDER BY \"author_name\" ASC";

    private void insertBook(@NotNull Book book) throws SQLException {
        logger.debug(TAG, "Bắt đầu insert book: " + book.getIsbn());
        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(INSERT_BOOK)) {
            preparedStatement.setLong(1, book.getIsbn());
            preparedStatement.setString(2, book.getImagePath());
            preparedStatement.setString(3, book.getTitle());
            preparedStatement.setString(4, book.getSummary());
            preparedStatement.setString(5, book.getLocation());
            preparedStatement.setString(6, book.getPreview());
            int result = preparedStatement.executeUpdate();
            logger.debug(TAG, "Insert book thành công: " + result + " dòng bị ảnh hưởng");
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi insert book: " + book.getIsbn(), e);
            throw e;
        }
    }

    private int insertAuthor(@NotNull Author author) throws SQLException {
        logger.debug(TAG, "Bắt đầu insert author: " + author.getName());
        try (PreparedStatement preparedStatement
                     = databaseConnection.getConnection().prepareStatement(INSERT_NEW_AUTHOR, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, author.getName());
            preparedStatement.executeUpdate();
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    int authorId = resultSet.getInt(1);
                    logger.debug(TAG, "Insert author thành công, ID: " + authorId);
                    return authorId;
                } else {
                    logger.error(TAG, "Tạo tác giả lỗi, không có ID được trả về");
                    throw new SQLException("Tạo tác giả lỗi");
                }
            }
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi insert author: " + author.getName(), e);
            throw e;
        }
    }

    private int getOrCreateAuthorId(@NotNull Author author) throws SQLException {
        logger.debug(TAG, "Bắt đầu tìm hoặc tạo author: " + author.getName());
        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(SELECT_AUTHOR_BY_NAME)) {
            preparedStatement.setString(1, author.getName());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int authorId = resultSet.getInt("author_ID");
                    logger.debug(TAG, "Tìm thấy author hiện có, ID: " + authorId);
                    return authorId;
                } else {
                    logger.info(TAG, "Không tìm thấy author, tạo mới: " + author.getName());
                    return insertAuthor(author);
                }
            }
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi tìm hoặc tạo author: " + author.getName(), e);
            throw e;
        }
    }

    private void associateBookWithAuthor(long ISBN, int authorID) throws SQLException {
        logger.debug(TAG, "Liên kết truyện " + ISBN + " với author ID: " + authorID);
        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(INSERT_BOOK_AUTHOR)) {
            preparedStatement.setLong(1, ISBN);
            preparedStatement.setInt(2, authorID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi liên kết truyện với author: ISBN=" + ISBN + ", authorID=" + authorID, e);
            throw e;
        }
    }

    private int insertCategory(@NotNull Category category) throws SQLException {
        logger.debug(TAG, "Bắt đầu insert category: " + category.getCatagoryName());
        try (PreparedStatement preparedStatement
                     = databaseConnection.getConnection().prepareStatement(INSERT_CATEGORY, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, category.getCatagoryName());
            preparedStatement.executeUpdate();
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    int categoryId = resultSet.getInt(1);
                    logger.debug(TAG, "Insert category thành công, ID: " + categoryId);
                    return categoryId;
                } else {
                    logger.error(TAG, "Thêm category lỗi, không có ID được trả về");
                    throw new SQLException("Thêm category lỗi");
                }
            }
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi insert category: " + category.getCatagoryName(), e);
            throw e;
        }
    }

    private int getOrCreateCategoryId(@NotNull Category category) throws SQLException {
        logger.debug(TAG, "Bắt đầu tìm hoặc tạo category: " + category.getCatagoryName());
        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(SELECT_CATEGORY_BY_NAME)) {
            preparedStatement.setString(1, category.getCatagoryName());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int categoryId = resultSet.getInt("category_ID");
                    logger.debug(TAG, "Tìm thấy category hiện có, ID: " + categoryId);
                    return categoryId;
                } else {
                    logger.info(TAG, "Không tìm thấy category, tạo mới: " + category.getCatagoryName());
                    return insertCategory(category);
                }
            }
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi tìm hoặc tạo category: " + category.getCatagoryName(), e);
            throw e;
        }
    }

    private void associateBookWithCategory(long ISBN, int categoryID) throws SQLException {
        logger.debug(TAG, "Liên kết truyện " + ISBN + " với category ID: " + categoryID);
        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(INSERT_BOOK_CATEGORY)) {
            preparedStatement.setLong(1, ISBN);
            preparedStatement.setInt(2, categoryID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi liên kết truyện với category: ISBN=" + ISBN + ", categoryID=" + categoryID, e);
            throw e;
        }
    }

    private void addBookItems(long ISBN, String placeAt, int quantity) throws SQLException {
        logger.debug(TAG, "Thêm " + quantity + " bản sao cho truyện: " + ISBN);
        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(INSERT_BOOK_ITEM)) {
            for (int i = 0; i < quantity; i++) {
                preparedStatement.setLong(1, ISBN);
                preparedStatement.addBatch();
            }
            int[] results = preparedStatement.executeBatch();
            logger.debug(TAG, "Đã thêm " + results.length + " bản sao cho truyện " + ISBN);
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi thêm bản sao truyện: ISBN=" + ISBN, e);
            throw e;
        }
    }

    @Override
    public void insert(@NotNull Book entity) throws SQLException {
        logger.info(TAG, "Bắt đầu thêm truyện mới: " + entity.getTitle() + " (ISBN: " + entity.getIsbn() + ")");

        // Kiểm tra truyện đã tồn tại
        if (findById(entity.getIsbn()) != null) {
            logger.warning(TAG, "truyện đã tồn tại: ISBN=" + entity.getIsbn());
            throw new SQLException("Book is exist");
        }

        databaseConnection.getConnection().setAutoCommit(false);

        try {
            // Thêm thông tin truyện cơ bản
            insertBook(entity);
            logger.debug(TAG, "Đã thêm thông tin truyện cơ bản: " + entity.getIsbn());

            // Thêm và liên kết với tác giả
            for (Author author : entity.getAuthors()) {
                int authorID = getOrCreateAuthorId(author);
                associateBookWithAuthor(entity.getIsbn(), authorID);
            }
            logger.debug(TAG, "Đã thêm và liên kết với " + entity.getAuthors().size() + " tác giả");

            // Thêm và liên kết với danh mục
            for (Category category : entity.getCategories()) {
                int categoryID = getOrCreateCategoryId(category);
                associateBookWithCategory(entity.getIsbn(), categoryID);
            }
            logger.debug(TAG, "Đã thêm và liên kết với " + entity.getCategories().size() + " danh mục");

            // Thêm các bản sao của truyện
            addBookItems(entity.getIsbn(), entity.getLocation(), entity.getQuantity());
            logger.debug(TAG, "Đã thêm " + entity.getQuantity() + " bản sao của truyện");

            databaseConnection.getConnection().commit();
            bookCache.put(entity.getIsbn(), entity);
            logger.info(TAG, "Thêm truyện thành công và lưu vào cache: " + entity.getIsbn());
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi thêm truyện, tiến hành rollback: " + entity.getIsbn(), e);
            databaseConnection.getConnection().rollback();
            throw e;
        } finally {
            databaseConnection.getConnection().setAutoCommit(true);
        }
    }

    @Override
    public boolean updateEntity(@NotNull Book entity) throws SQLException {
        logger.info(TAG, "Bắt đầu cập nhật truyện: " + entity.getTitle() + " (ISBN: " + entity.getIsbn() + ")");

        // Kiểm tra truyện có tồn tại
        if (findById(entity.getIsbn()) == null) {
            logger.warning(TAG, "truyện không tồn tại: ISBN=" + entity.getIsbn());
            return false;
        }

        try {
            databaseConnection.getConnection().setAutoCommit(false);

            long isbn = entity.getIsbn();

            // Xóa liên kết tác giả cũ
            try (PreparedStatement stmt = databaseConnection.getConnection().prepareStatement(DELETE_BOOK_AUTHORS)) {
                stmt.setLong(1, isbn);
                int affected = stmt.executeUpdate();
                logger.debug(TAG, "Đã xóa " + affected + " liên kết tác giả cũ");
            }

            // Xóa liên kết danh mục cũ
            try (PreparedStatement stmt = databaseConnection.getConnection().prepareStatement(DELETE_BOOK_CATEGORIES)) {
                stmt.setLong(1, isbn);
                int affected = stmt.executeUpdate();
                logger.debug(TAG, "Đã xóa " + affected + " liên kết danh mục cũ");
            }

            // Cập nhật thông tin truyện
            try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(UPDATE_BOOK_DETAILS)) {
                preparedStatement.setString(1, entity.getTitle());
                preparedStatement.setString(2, entity.getImagePath());
                preparedStatement.setString(3, entity.getSummary());
                preparedStatement.setString(4, entity.getLocation());
                preparedStatement.setString(5, entity.getstatus().toString());
                preparedStatement.setLong(6, entity.getIsbn());
                int affected = preparedStatement.executeUpdate();
                logger.debug(TAG, "Đã cập nhật thông tin truyện, " + affected + " dòng bị ảnh hưởng");

                // Thêm liên kết tác giả mới
                for (Author author : entity.getAuthors()) {
                    int authorID = getOrCreateAuthorId(author);
                    associateBookWithAuthor(entity.getIsbn(), authorID);
                }
                logger.debug(TAG, "Đã thêm " + entity.getAuthors().size() + " liên kết tác giả mới");

                // Thêm liên kết danh mục mới
                for (Category category : entity.getCategories()) {
                    int categoryID = getOrCreateCategoryId(category);
                    associateBookWithCategory(entity.getIsbn(), categoryID);
                }
                logger.debug(TAG, "Đã thêm " + entity.getCategories().size() + " liên kết danh mục mới");

                databaseConnection.getConnection().commit();
                // Cập nhật cache
                bookCache.put(entity.getIsbn(), entity);
                logger.info(TAG, "Cập nhật truyện thành công và cập nhật cache: " + entity.getIsbn());
                return affected > 0;
            }

        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi cập nhật truyện, tiến hành rollback: " + entity.getIsbn(), e);
            databaseConnection.getConnection().rollback();
            throw e;
        } finally {
            databaseConnection.getConnection().setAutoCommit(true);
        }
    }

    @Override
    public boolean deleteEntity(@NotNull Book entity) throws SQLException {
        logger.info(TAG, "Bắt đầu xóa truyện: " + entity.getTitle() + " (ISBN: " + entity.getIsbn() + ")");

        // Kiểm tra truyện có tồn tại
        if (findById(entity.getIsbn()) == null) {
            logger.warning(TAG, "truyện không tồn tại: ISBN=" + entity.getIsbn());
            throw new SQLException("Book does not exist");
        }

        try {
            databaseConnection.getConnection().setAutoCommit(false);

            long isbn = entity.getIsbn();

            // Xóa liên kết tác giả
            try (PreparedStatement stmt = databaseConnection.getConnection().prepareStatement(DELETE_BOOK_AUTHORS)) {
                stmt.setLong(1, isbn);
                int affected = stmt.executeUpdate();
                logger.debug(TAG, "Đã xóa " + affected + " liên kết tác giả");
            }

            // Xóa liên kết danh mục
            try (PreparedStatement stmt = databaseConnection.getConnection().prepareStatement(DELETE_BOOK_CATEGORIES)) {
                stmt.setLong(1, isbn);
                int affected = stmt.executeUpdate();
                logger.debug(TAG, "Đã xóa " + affected + " liên kết danh mục");
            }

            // Xóa các bản sao truyện
            try (PreparedStatement stmt = databaseConnection.getConnection().prepareStatement(DELETE_BOOK_ITEMS)) {
                stmt.setLong(1, isbn);
                int affected = stmt.executeUpdate();
                logger.debug(TAG, "Đã xóa " + affected + " bản sao truyện");
            }

            // Xóa thông tin truyện chính
            try (PreparedStatement stmt = databaseConnection.getConnection().prepareStatement(DELETE_BOOK)) {
                stmt.setLong(1, isbn);
                int rowsDeleted = stmt.executeUpdate();
                logger.debug(TAG, "Đã xóa thông tin truyện chính, " + rowsDeleted + " dòng bị ảnh hưởng");

                databaseConnection.getConnection().commit();
                // Xóa khỏi cache
                bookCache.invalidate(entity.getIsbn());
                logger.info(TAG, "Xóa truyện thành công và xóa khỏi cache: " + entity.getIsbn());
                return rowsDeleted > 0;
            }
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi xóa truyện, tiến hành rollback: " + entity.getIsbn(), e);
            databaseConnection.getConnection().rollback();
            throw e;
        } finally {
            databaseConnection.getConnection().setAutoCommit(true);
        }
    }

    @Override
    public Book findById(Number keywords) throws SQLException {
        long isbn = (Long) keywords;
        logger.debug(TAG, "Tìm truyện với ISBN: " + isbn);

        // Kiểm tra cache trước
        Book cachedBook = bookCache.getIfPresent(isbn);
        if (cachedBook != null) {
            logger.debug(TAG, "Tìm thấy truyện trong cache: " + isbn);
            return cachedBook;
        }

        logger.debug(TAG, "truyện không có trong cache, tìm trong database: " + isbn);
        List<Author> authorList = new ArrayList<>();
        List<Category> categoryList = new ArrayList<>();

        // Lấy danh truyện tác giả
        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(FIND_AUTHORS_BY_BOOK)) {
            preparedStatement.setLong(1, isbn);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                authorList.add(new Author(resultSet.getInt("author_ID"), resultSet.getString("author_name")));
            }
            logger.debug(TAG, "Đã tìm thấy " + authorList.size() + " tác giả cho ISBN: " + isbn);
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi tìm tác giả của truyện: " + isbn, e);
            throw e;
        }

        // Lấy danh truyện danh mục
        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(FIND_CATEGORIES_BY_BOOK)) {
            preparedStatement.setLong(1, isbn);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                categoryList.add(new Category(resultSet.getInt("category_ID"), resultSet.getString("category_name")));
            }
            logger.debug(TAG, "Đã tìm thấy " + categoryList.size() + " danh mục cho ISBN: " + isbn);
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi tìm danh mục của truyện: " + isbn, e);
            throw e;
        }

        // Lấy thông tin truyện chính
        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(FIND_BOOK_BY_ISBN)) {
            preparedStatement.setLong(1, isbn);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Book book = new Book(
                            resultSet.getLong("ISBN"),
                            resultSet.getString("title"),
                            resultSet.getString("image_path"),
                            resultSet.getString("description"),
                            resultSet.getString("placeAt"),
                            authorList,
                            categoryList
                    );
                    book.setQuantity(resultSet.getInt("quantity"));
                    book.setLoanedBooksCount(resultSet.getInt("number_loaned_book"));
                    book.setLostBooksCount(resultSet.getInt("number_lost_book"));
                    book.setReservedBooksCount(resultSet.getInt("number_reserved_book"));
                    book.setRate(resultSet.getInt("rate"));
                    book.setBookStatus(BookStatus.valueOf(resultSet.getString("BookStatus")));
                    book.setPreview(resultSet.getString("preview"));

                    // Lưu vào cache
                    bookCache.put(isbn, book);
                    logger.info(TAG, "Đã tìm thấy truyện và lưu vào cache: " + isbn);
                    return book;
                } else {
                    logger.info(TAG, "Không tìm thấy truyện với ISBN: " + isbn);
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi tìm thông tin truyện: " + isbn, e);
            throw e;
        }
    }

    @Override
    public List<Book> findByCriteria(@NotNull Map<String, Object> criteria) throws SQLException {
        if (criteria.isEmpty()) {
            logger.warning(TAG, "Tìm kiếm với tiêu chí rỗng, trả về danh truyện trống");
            return new ArrayList<>();
        }

        logger.info(TAG, "Tìm kiếm truyện với tiêu chí: " + generateKeywords(criteria));

        StringBuilder findBookByCriteria = new StringBuilder("SELECT DISTINCT (\"Books\".\"ISBN\")\n" +
                "FROM \"Books\"\n" +
                "JOIN \"Books_Authors\" ON \"Books\".\"ISBN\" = \"Books_Authors\".\"ISBN\"\n" +
                "JOIN \"Authors\" ON \"Books_Authors\".\"author_ID\" = \"Authors\".\"author_ID\"\n" +
                "JOIN \"Books_Category\" ON \"Books\".\"ISBN\" = \"Books_Category\".\"ISBN\"\n" +
                "JOIN \"Category\" ON \"Books_Category\".\"category_ID\" = \"Category\".\"category_ID\"\n" +
                " WHERE ");

        List<String> conditions = new ArrayList<>();
        for (String key : criteria.keySet()) {
            if (key.equals("ISBN")) {
                conditions.add("CAST(\"Books\".\"ISBN\" AS TEXT) LIKE ?");
            } else if (!key.startsWith("\"")) {
                conditions.add("\"" + key + "\"" + " LIKE ?");
            } else {
                conditions.add(key + " LIKE ?");
            }
        }
        findBookByCriteria.append(String.join(" AND ", conditions));

        logger.debug(TAG, "SQL query: " + findBookByCriteria.toString());

        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(findBookByCriteria.toString())) {
            int index = 1;

            for (Object value : criteria.values()) {
                preparedStatement.setString(index++, "%" + value + "%");
                logger.debug(TAG, "Parameter " + (index - 1) + ": " + "%" + value + "%");
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Long> isbnList = new ArrayList<>();
                while (resultSet.next()) {
                    isbnList.add(resultSet.getLong("ISBN"));
                }

                logger.info(TAG, "Tìm thấy " + isbnList.size() + " kết quả phù hợp");

                // Tối ưu: Nhóm kết quả để truy vấn hiệu quả
                List<Book> results = new ArrayList<>();
                for (Long isbn : isbnList) {
                    Book book = findById(isbn);
                    if (book != null) {
                        results.add(book);
                    }
                }

                return results;
            }
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi tìm kiếm truyện theo tiêu chí", e);
            throw e;
        }
    }

    @Override
    public List<Book> findAll() throws SQLException {
        logger.info(TAG, "Lấy tất cả truyện");
        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(SELECT_ALL_BOOKS)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Long> isbnList = new ArrayList<>();
                while (resultSet.next()) {
                    isbnList.add(resultSet.getLong("ISBN"));
                }

                logger.debug(TAG, "Tìm thấy " + isbnList.size() + " ISBN");

                // Tối ưu: Nhóm kết quả để tận dụng cache
                List<Book> bookList = new ArrayList<>();
                for (Long isbn : isbnList) {
                    Book book = findById(isbn);
                    if (book != null) {
                        bookList.add(book);
                    }
                }

                logger.info(TAG, "Trả về danh truyện " + bookList.size() + " truyện");
                return bookList;
            }
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi lấy tất cả truyện", e);
            throw e;
        }
    }

    @NotNull
    private String generateKeywords(@NotNull Map<String, Object> criteria) {
        return criteria.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .map(entry -> entry.getKey() + ": " + entry.getValue().toString())
                .collect(Collectors.joining(", "));
    }

    /**
     * Select all category list.
     *
     * @return the list
     * @throws SQLException the sql exception
     */
    public List<Category> selectAllCategory() throws SQLException {
        logger.info(TAG, "Lấy tất cả danh mục");
        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(SELECT_ALL_CATEGORIES)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Category> categoryList = new ArrayList<>();
                while (resultSet.next()) {
                    categoryList.add(new Category(resultSet.getInt("category_ID"), resultSet.getString("category_name")));
                }
                logger.info(TAG, "Đã lấy " + categoryList.size() + " danh mục");
                return categoryList;
            }
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi lấy tất cả danh mục", e);
            throw e;
        }
    }

    /**
     * Select all author list.
     *
     * @return the list
     * @throws SQLException the sql exception
     */
    public List<Author> selectAllAuthor() throws SQLException {
        logger.info(TAG, "Lấy tất cả tác giả");
        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(SELECT_ALL_AUTHORS)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Author> authorList = new ArrayList<>();
                while (resultSet.next()) {
                    authorList.add(new Author(resultSet.getInt("author_ID"), resultSet.getString("author_name")));
                }
                logger.info(TAG, "Đã lấy " + authorList.size() + " tác giả");
                return authorList;
            }
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi lấy tất cả tác giả", e);
            throw e;
        }
    }

    /**
     * Invalidate book cache.
     *
     * @param isbn the isbn
     */
    public void invalidateBookCache(Long isbn) {
        if (isbn != null) {
            bookCache.invalidate(isbn);
            logger.info(TAG, "Đã xóa truyện khỏi cache: " + isbn);
        } else {
            logger.warning(TAG, "Không thể xóa cache với ISBN null");
        }
    }

    /**
     * Xóa tất cả cache, sử dụng khi cần làm mới dữ liệu
     */
    public void clearCache() {
        long cacheSize = bookCache.estimatedSize();
        bookCache.invalidateAll();
        logger.info(TAG, "Đã xóa toàn bộ cache truyện (" + cacheSize + " items)");
    }

    /**
     * Thực hiện batch insert cho nhiều truyện cùng lúc
     *
     * @param books Danh truyện truyện cần thêm
     * @return Số truyện đã thêm thành công
     * @throws SQLException Nếu có lỗi khi thêm truyện
     */
    public int batchInsert(List<Book> books) throws SQLException {
        if (books == null || books.isEmpty()) {
            logger.warning(TAG, "Batch insert với danh truyện rỗng");
            return 0;
        }

        logger.info(TAG, "Bắt đầu batch insert cho " + books.size() + " truyện");
        int successCount = 0;

        for (Book book : books) {
            try {
                insert(book);
                successCount++;
            } catch (SQLException e) {
                logger.error(TAG, "Lỗi khi thêm truyện trong batch: " + book.getIsbn(), e);
                // Tiếp tục với truyện tiếp theo
            }
        }

        logger.info(TAG, "Batch insert hoàn tất: " + successCount + "/" + books.size() + " truyện thành công");
        return successCount;
    }
}
