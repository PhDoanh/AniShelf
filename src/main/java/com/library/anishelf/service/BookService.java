package com.library.anishelf.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.library.anishelf.controller.NavigationBarController;
import com.library.anishelf.dao.BookDAO;
import com.library.anishelf.dao.BookIssueDAO;
import com.library.anishelf.dao.BookMarkDAO;
import com.library.anishelf.dao.BookReservationDAO;
import com.library.anishelf.model.Book;
import com.library.anishelf.model.BookIssue;
import com.library.anishelf.model.BookMark;
import com.library.anishelf.model.BookReservation;
import com.library.anishelf.model.enums.BookIssueStatus;
import com.library.anishelf.model.enums.BookReservationStatus;
import com.library.anishelf.util.CacheManagerUtil;
import com.library.anishelf.util.RuntimeDebugUtil;
import javafx.concurrent.Task;
import javafx.scene.image.Image;

import java.sql.SQLException;
import java.util.*;

/**
 * The type Book service.
 */
public class BookService {
    private static BookService instance = null;
    private List<Book> allAvailableBooks = new ArrayList<>();
    private List<Book> mostPopularBooks;
    private List<Book> highestRatedBooks;
    private List<BookReservation> pendingReservedBooks;
    private List<BookMark> bookmarks;
    private List<BookIssue> returnedBooks;
    private List<BookIssue> currentlyBorrowedBooks;

    private static final Cache<String, Image> BOOK_IMAGE_CACHE = CacheManagerUtil.buildCache(70);
    private static final RuntimeDebugUtil logger = RuntimeDebugUtil.getInstance();
    private static final String TAG = "BookService";
    private static final String DEFAULT_IMAGE_PATH = "/image/default/book.png";

    private BookService() {
        try {
            logger.debug(TAG, "Khởi tạo BookService và tải danh sách truyện");
            allAvailableBooks = BookDAO.getInstance().findAll();
            logger.info(TAG, "Đã tải " + allAvailableBooks.size() + " truyện từ cơ sở dữ liệu");
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi truy vấn dữ liệu truyện từ cơ sở dữ liệu", e);
            throw new RuntimeException("Lỗi khi truy vấn dữ liệu truyện từ cơ sở dữ liệu", e);
        }
    }

    /**
     * Gets most popular books.
     *
     * @return the most popular books
     */
    public List<Book> getMostPopularBooks() {
        if (mostPopularBooks == null) {
            mostPopularBooks = (ArrayList<Book>) ((ArrayList<Book>) allAvailableBooks).clone();
            sortBooks(mostPopularBooks, (book1, book2) -> Integer.compare(
                    (book2.getLoanedBooksCount() + book2.getReservedBooksCount()),
                    (book1.getLoanedBooksCount() + book1.getReservedBooksCount())
            ));
        }
        return mostPopularBooks;
    }

    /**
     * Gets highest rated books.
     *
     * @return the highest rated books
     */
    public List<Book> getHighestRatedBooks() {
        if (highestRatedBooks == null) {
            highestRatedBooks = (ArrayList<Book>) ((ArrayList<Book>) allAvailableBooks).clone();
            sortBooks(highestRatedBooks, (book1, book2) -> Integer.compare(book2.getRate(), book1.getRate()));
        }
        return highestRatedBooks;
    }

    /**
     * Gets pending reserved books.
     *
     * @return the pending reserved books
     * @throws SQLException the sql exception
     */
    public List<BookReservation> getPendingReservedBooks() throws SQLException {
        if (pendingReservedBooks == null) {
            Map<String, Object> criteria = new HashMap<>();
            criteria.put("member_ID", NavigationBarController.getMember().getPerson().getId());
            criteria.put("BookReservationStatus", BookReservationStatus.WAITING);

            pendingReservedBooks = BookReservationDAO.getInstance().findByCriteria(criteria);
        }
        return pendingReservedBooks;
    }

    /**
     * Gets bookmarks.
     *
     * @return the bookmarks
     * @throws SQLException the sql exception
     */
    public List<BookMark> getBookmarks() throws SQLException {
        if (bookmarks == null) {
            bookmarks = BookMarkDAO.getInstance().getAllBookMarksForMember(NavigationBarController.getMember());
        }
        return bookmarks;
    }

    /**
     * Gets returned books.
     *
     * @return the returned books
     * @throws SQLException the sql exception
     */
    public List<BookIssue> getReturnedBooks() throws SQLException {
        if (returnedBooks == null) {
            Map<String, Object> criteria = new HashMap<>();
            criteria.put("member_ID", NavigationBarController.getMember().getPerson().getId());
            criteria.put("BookIssueStatus", BookIssueStatus.RETURNED);
            returnedBooks = BookIssueDAO.getInstance().findByCriteria(criteria);
        }
        return returnedBooks;
    }

    /**
     * Gets currently borrowed books.
     *
     * @return the currently borrowed books
     * @throws SQLException the sql exception
     */
    public List<BookIssue> getCurrentlyBorrowedBooks() throws SQLException {
        if (currentlyBorrowedBooks == null) {
            Map<String, Object> criteria = new HashMap<>();
            criteria.put("member_ID", NavigationBarController.getMember().getPerson().getId());
            criteria.put("BookIssueStatus", BookIssueStatus.BORROWED);
            currentlyBorrowedBooks = BookIssueDAO.getInstance().findByCriteria(criteria);
        }
        return currentlyBorrowedBooks;
    }

    /**
     * Gets all available books.
     *
     * @return the all available books
     */
    public List<Book> getAllAvailableBooks() {
        return allAvailableBooks;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static BookService getInstance() {
        if (instance == null) {
            logger.debug(TAG, "Tạo mới instance BookService");
            instance = new BookService();
        }
        return instance;
    }

    private void sortBooks(List<Book> books, Comparator<Book> comparator) {
        logger.debug(TAG, "Sắp xếp danh sách " + books.size() + " truyện");
        Collections.sort(books, comparator);
    }

    /**
     * Find book in all books book.
     *
     * @param book the book
     * @return the book
     */
    public Book findBookInAllBooks(Book book) {
        logger.debug(TAG, "Tìm truyện trong danh sách với ISBN: " + book.getIsbn());
        Book findBook;
        try {
            findBook = BookDAO.getInstance().findById(book.getIsbn());
            if (findBook != null) {
                logger.debug(TAG, "Tìm thấy truyện: " + findBook.getTitle());
            } else {
                logger.debug(TAG, "Không tìm thấy truyện với ISBN: " + book.getIsbn());
            }
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi tìm truyện với ISBN: " + book.getIsbn(), e);
            throw new RuntimeException("Lỗi khi tìm truyện trong cơ sở dữ liệu", e);
        }
        return findBook;
    }

    /**
     * Gets reserved books count.
     *
     * @return the reserved books count
     */
    public int getReservedBooksCount() {
        try {
            logger.debug(TAG, "Đếm số truyện đã đặt trước");
            getPendingReservedBooks();
            return pendingReservedBooks.size();
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi đếm số truyện đã đặt trước", e);
            throw new RuntimeException("Lỗi khi lấy số lượng truyện đã đặt trước", e);
        }
    }

    /**
     * Gets borrowing books count.
     *
     * @return the borrowing books count
     */
    public int getBorrowingBooksCount() {
        try {
            logger.debug(TAG, "Đếm số truyện đang mượn");
            getCurrentlyBorrowedBooks();
            return currentlyBorrowedBooks.size();
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi đếm số truyện đang mượn", e);
            throw new RuntimeException("Lỗi khi lấy số lượng truyện đang mượn", e);
        }
    }

    /**
     * Add reserved book.
     *
     * @param bookReservation the book reservation
     */
    public void addReservedBook(BookReservation bookReservation) {
        logger.debug(TAG, "Thêm truyện đặt trước: " + bookReservation.getBookItem().getTitle());
        try {
            if (pendingReservedBooks == null) {
                getPendingReservedBooks();
            }
            pendingReservedBooks.add(bookReservation);
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi thêm truyện đặt trước", e);
            throw new RuntimeException("Lỗi khi thêm truyện đặt trước", e);
        }
    }

    /**
     * Remove reserved book.
     *
     * @param bookReservation the book reservation
     */
    public void removeReservedBook(BookReservation bookReservation) {
        logger.debug(TAG, "Xóa truyện đặt trước: " + bookReservation.getBookItem().getTitle());
        int index = findReservedBookIndex(bookReservation.getBookItem().getIsbn());

        if (index != -1) {
            pendingReservedBooks.remove(index);
            logger.debug(TAG, "Đã xóa truyện đặt trước thành công");
        } else {
            logger.warning(TAG, "Không tìm thấy truyện đặt trước để xóa với ISBN: " +
                    bookReservation.getBookItem().getIsbn());
        }
    }

    private int findReservedBookIndex(long isbn) {
        logger.debug(TAG, "Tìm vị trí truyện đặt trước với ISBN: " + isbn);
        try {
            if (pendingReservedBooks == null) {
                pendingReservedBooks = BookService.getInstance().getPendingReservedBooks();
            }

            for (int i = 0; i < pendingReservedBooks.size(); i++) {
                if (pendingReservedBooks.get(i).getBookItem().getIsbn() == isbn) {
                    logger.debug(TAG, "Tìm thấy truyện đặt trước tại vị trí: " + i);
                    return i;
                }
            }
            logger.debug(TAG, "Không tìm thấy truyện đặt trước với ISBN: " + isbn);
            return -1;
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi tìm vị trí truyện đặt trước với ISBN: " + isbn, e);
            throw new RuntimeException("Lỗi khi tìm truyện đặt trước", e);
        }
    }

    /**
     * Add marked book.
     *
     * @param bookMark the book mark
     */
    public void addMarkedBook(BookMark bookMark) {
        logger.debug(TAG, "Thêm bookmark cho truyện: " + bookMark.getBook().getTitle());
        try {
            if (bookmarks == null) {
                getBookmarks();
            }
            // Kiểm tra xem đã có bookmark cho truyện này chưa trước khi thêm
            if (findMarkedBookIndex(bookMark.getBook().getIsbn()) == -1) {
                bookmarks.add(bookMark);
                logger.debug(TAG, "Đã thêm bookmark thành công");
            } else {
                logger.debug(TAG, "truyện này đã được bookmark trước đó");
            }
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi thêm bookmark cho truyện", e);
            throw new RuntimeException("Lỗi khi thêm truyện vào bookmarks", e);
        }
    }

    /**
     * Remove marked book.
     *
     * @param bookMark the book mark
     */
    public void removeMarkedBook(BookMark bookMark) {
        logger.debug(TAG, "Xóa bookmark cho truyện: " + bookMark.getBook().getTitle());
        try {
            if (bookmarks == null) {
                getBookmarks();
            }
            int index = findMarkedBookIndex(bookMark.getBook().getIsbn());
            if (index != -1) {
                bookmarks.remove(index);
                logger.debug(TAG, "Đã xóa bookmark thành công");
            } else {
                logger.warning(TAG, "Không tìm thấy bookmark để xóa cho truyện: " + bookMark.getBook().getTitle());
            }
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi xóa bookmark", e);
            throw new RuntimeException("Lỗi khi xóa truyện khỏi bookmarks", e);
        }
    }

    private int findMarkedBookIndex(long ISBN) {
        logger.debug(TAG, "Tìm vị trí bookmark với ISBN: " + ISBN);
        try {
            if (bookmarks == null) {
                getBookmarks();
            }
            for (int i = 0; i < bookmarks.size(); i++) {
                if (bookmarks.get(i).getBook().getIsbn() == ISBN) {
                    logger.debug(TAG, "Tìm thấy bookmark tại vị trí: " + i);
                    return i;
                }
            }
            logger.debug(TAG, "Không tìm thấy bookmark với ISBN: " + ISBN);
            return -1;
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi tìm bookmark với ISBN: " + ISBN, e);
            throw new RuntimeException("Lỗi khi tìm kiếm bookmark", e);
        }
    }

    /**
     * Create load image task task.
     *
     * @param book the book
     * @return the task
     */
    public Task<Image> createLoadImageTask(Book book) {
        return new Task<>() {
            @Override
            protected Image call() throws Exception {
                String imagePath = book.getImagePath();
                logger.debug(TAG, "Tạo task tải hình ảnh cho truyện: " + book.getTitle() + ", đường dẫn: " + imagePath);

                // Kiểm tra cache trước khi tải
                Image cachedImage = BOOK_IMAGE_CACHE.getIfPresent(imagePath);
                if (cachedImage != null) {
                    logger.debug(TAG, "Lấy hình ảnh từ cache cho đường dẫn: " + imagePath);
                    return cachedImage;
                }

                try {
                    logger.debug(TAG, "Tải hình ảnh từ đường dẫn: " + imagePath);
                    Image image = new Image(imagePath, true);

                    if (image.isError()) {
                        throw new Exception("Hình ảnh lỗi: " + image.getException().getMessage());
                    }

                    BOOK_IMAGE_CACHE.put(imagePath, image); // Lưu vào cache
                    logger.debug(TAG, "Đã tải và lưu hình ảnh vào cache thành công");
                    return image;
                } catch (Exception e) {
                    logger.error(TAG, "Lỗi khi tải hình ảnh từ đường dẫn: " + imagePath, e);

                    // Sử dụng đường dẫn resource thay vì đường dẫn tuyệt đối
                    logger.debug(TAG, "Sử dụng hình ảnh mặc định");
                    Image defaultImage = new Image(getClass().getResourceAsStream(DEFAULT_IMAGE_PATH));
                    BOOK_IMAGE_CACHE.put(imagePath, defaultImage); // Lưu ảnh mặc định vào cache với cùng key
                    return defaultImage;
                }
            }
        };
    }

    /**
     * Clear cache.
     */
    public void clearCache() {
        logger.debug(TAG, "Bắt đầu xóa toàn bộ cache BookService");
        instance = null;

        // Xóa cache hình ảnh
        logger.debug(TAG, "Xóa cache hình ảnh, kích thước cache trước khi xóa: " + BOOK_IMAGE_CACHE.estimatedSize());
        BOOK_IMAGE_CACHE.invalidateAll();

        // Xóa các danh truyện cache
        logger.debug(TAG, "Xóa danh sách truyện, số lượng trước khi xóa: " + allAvailableBooks.size());
        allAvailableBooks.clear();

        if (mostPopularBooks != null) {
            logger.debug(TAG, "Xóa danh sách truyện phổ biến, số lượng: " + mostPopularBooks.size());
            mostPopularBooks.clear();
        }

        if (highestRatedBooks != null) {
            logger.debug(TAG, "Xóa danh sách truyện đánh giá cao, số lượng: " + highestRatedBooks.size());
            highestRatedBooks.clear();
        }

        if (pendingReservedBooks != null) {
            logger.debug(TAG, "Xóa danh sách truyện đặt trước, số lượng: " + pendingReservedBooks.size());
            pendingReservedBooks.clear();
        }

        if (bookmarks != null) {
            logger.debug(TAG, "Xóa danh sách bookmark, số lượng: " + bookmarks.size());
            bookmarks.clear();
        }

        if (returnedBooks != null) {
            logger.debug(TAG, "Xóa danh sách truyện đã trả, số lượng: " + returnedBooks.size());
            returnedBooks.clear();
        }

        if (currentlyBorrowedBooks != null) {
            logger.debug(TAG, "Xóa danh sách truyện đang mượn, số lượng: " + currentlyBorrowedBooks.size());
            currentlyBorrowedBooks.clear();
        }

        logger.info(TAG, "Đã xóa toàn bộ cache BookService thành công");
    }

    /**
     * Is book marked boolean.
     *
     * @param ISBN the isbn
     * @return the boolean
     */
    public boolean isBookMarked(Long ISBN) {
        logger.debug(TAG, "Kiểm tra xem truyện có ISBN: " + ISBN + " đã được bookmark chưa");
        try {
            if (bookmarks == null) {
                getBookmarks();
            }

            // Tối ưu bằng cách sử dụng findMarkedBookIndex thay vì duyệt lại danh sách
            boolean result = findMarkedBookIndex(ISBN) != -1;
            logger.debug(TAG, "Kết quả kiểm tra bookmark: " + (result ? "Đã bookmark" : "Chưa bookmark"));
            return result;
        } catch (SQLException e) {
            logger.error(TAG, "Lỗi khi kiểm tra bookmark cho truyện với ISBN: " + ISBN, e);
            throw new RuntimeException("Lỗi khi kiểm tra bookmark", e);
        }
    }

}
