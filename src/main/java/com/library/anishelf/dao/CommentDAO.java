package com.library.anishelf.dao;

import com.library.anishelf.model.Comment;
import com.library.anishelf.util.RuntimeDebugUtil;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The type Comment dao.
 */
public class CommentDAO implements GenericDAO<Comment> {
    private static CommentDAO commentDAO;

    private static DatabaseConnection databaseConnection;
    private static MemberDAO memberDAO;
    private static BookDAO bookDAO;
    private static final RuntimeDebugUtil logger = RuntimeDebugUtil.getInstance();

    // Các câu lệnh SQL được định nghĩa dưới dạng hằng số
    private static final String INSERT_COMMENT = "INSERT INTO \"Comments\" (\"member_ID\", \"ISBN\", \"title\", \"content\", \"rate\") VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_COMMENT_BY_ID = "SELECT * FROM \"Comments\" WHERE \"comment_ID\" = ?";
    private static final String DELETE_COMMENT = "DELETE FROM \"Comments\" WHERE \"comment_ID\" = ?";
    private static final String UPDATE_COMMENT = "UPDATE \"Comments\" SET \"title\" = ?, \"content\" = ?, \"rate\" = ? WHERE \"comment_ID\" = ?";
    private static final String SELECT_ALL_COMMENTS = "SELECT * FROM \"Comments\" ORDER BY \"comment_ID\" DESC";

    private CommentDAO() {
        databaseConnection = DatabaseConnection.getInstance();
        memberDAO = MemberDAO.getInstance();
        bookDAO = BookDAO.getInstance();
        logger.debug("CommentDAO", "CommentDAO instance initialized");
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static synchronized CommentDAO getInstance() {
        if (commentDAO == null) {
            commentDAO = new CommentDAO();
        }
        return commentDAO;
    }

    @Override
    public void insert(@NotNull Comment comment) throws SQLException {
        logger.debug("CommentDAO", "Adding new comment with title: " + comment.getTitle());
        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(INSERT_COMMENT)) {
            preparedStatement.setInt(1, comment.getMember().getPerson().getId());
            preparedStatement.setLong(2, comment.getIsbn());
            preparedStatement.setString(3, comment.getTitle());
            preparedStatement.setString(4, comment.getContent());
            preparedStatement.setString(5, String.valueOf(comment.getRate()));

            int rowsAffected = preparedStatement.executeUpdate();
            logger.info("CommentDAO", "Comment added successfully. Rows affected: " + rowsAffected);
            bookDAO.invalidateBookCache(comment.getIsbn());
        } catch (SQLException e) {
            logger.error("CommentDAO", "Failed to add comment: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean updateEntity(Comment comment) throws SQLException {
        if (comment == null || comment.getCommentId() <= 0) {
            logger.warning("CommentDAO", "Cannot update comment: Invalid comment object or ID");
            return false;
        }

        logger.debug("CommentDAO", "Updating comment with ID: " + comment.getCommentId());
        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(UPDATE_COMMENT)) {
            preparedStatement.setString(1, comment.getTitle());
            preparedStatement.setString(2, comment.getContent());
            preparedStatement.setString(3, String.valueOf(comment.getRate()));
            preparedStatement.setInt(4, comment.getCommentId());

            int rowsAffected = preparedStatement.executeUpdate();
            boolean success = rowsAffected > 0;
            logger.info("CommentDAO", "Comment update " + (success ? "successful" : "failed") +
                    ". Comment ID: " + comment.getCommentId());

            if (success) {
                bookDAO.invalidateBookCache(comment.getIsbn());
            }

            return success;
        } catch (SQLException e) {
            logger.error("CommentDAO", "Failed to update comment ID " + comment.getCommentId() + ": " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean deleteEntity(@NotNull Comment comment) throws SQLException {
        logger.debug("CommentDAO", "Deleting comment with ID: " + comment.getCommentId());
        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(DELETE_COMMENT)) {
            preparedStatement.setInt(1, comment.getCommentId());

            int rowsAffected = preparedStatement.executeUpdate();
            boolean success = rowsAffected > 0;
            logger.info("CommentDAO", "Comment deletion " + (success ? "successful" : "failed") +
                    ". Comment ID: " + comment.getCommentId());

            if (success) {
                bookDAO.invalidateBookCache(comment.getIsbn());
            }

            return success;
        } catch (SQLException e) {
            logger.error("CommentDAO", "Failed to delete comment ID " + comment.getCommentId() + ": " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Comment findById(@NotNull Number keywords) throws SQLException {
        int commentId = keywords.intValue();
        logger.debug("CommentDAO", "Finding comment with ID: " + commentId);

        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(SELECT_COMMENT_BY_ID)) {
            preparedStatement.setInt(1, commentId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Comment comment = createCommentFromResultSet(resultSet);
                    logger.debug("CommentDAO", "Found comment with ID: " + commentId);
                    return comment;
                } else {
                    logger.info("CommentDAO", "No comment found with ID: " + commentId);
                }
            }
        } catch (SQLException e) {
            logger.error("CommentDAO", "Error finding comment with ID " + commentId + ": " + e.getMessage(), e);
            throw e;
        }
        return null;
    }

    @Override
    public List<Comment> findByCriteria(@NotNull Map<String, Object> criteria) throws SQLException {
        if (criteria.isEmpty()) {
            logger.warning("CommentDAO", "Empty criteria provided for searchByCriteria");
            return Collections.emptyList();
        }

        logger.debug("CommentDAO", "Searching comments with criteria: " + criteria);
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM \"Comments\" WHERE ");

        // Xây dựng câu lệnh SQL động dựa trên tiêu chí
        for (String key : criteria.keySet()) {
            if (!key.startsWith("\"")) {
                queryBuilder.append("\"").append(key).append("\"").append(" = ? AND ");
            } else {
                queryBuilder.append(key).append(" = ? AND ");
            }
        }

        // Xóa "AND " cuối cùng
        queryBuilder.setLength(queryBuilder.length() - 5);

        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(queryBuilder.toString())) {
            int index = 1;
            for (Object value : criteria.values()) {
                if (value instanceof Number) {
                    preparedStatement.setLong(index++, ((Number) value).longValue());
                } else {
                    preparedStatement.setString(index++, value.toString());
                }
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Comment> commentList = new ArrayList<>();
                while (resultSet.next()) {
                    commentList.add(createCommentFromResultSet(resultSet));
                }
                logger.info("CommentDAO", "Found " + commentList.size() + " comments matching criteria");
                return commentList;
            }
        } catch (SQLException e) {
            logger.error("CommentDAO", "Error searching comments by criteria: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<Comment> findAll() throws SQLException {
        logger.debug("CommentDAO", "Selecting all comments");
        try (PreparedStatement preparedStatement = databaseConnection.getConnection().prepareStatement(SELECT_ALL_COMMENTS);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            List<Comment> commentList = new ArrayList<>();
            while (resultSet.next()) {
                commentList.add(createCommentFromResultSet(resultSet));
            }
            logger.info("CommentDAO", "Retrieved " + commentList.size() + " comments in total");
            return commentList;
        } catch (SQLException e) {
            logger.error("CommentDAO", "Error selecting all comments: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Phương thức tiện ích để tạo đối tượng Comment từ ResultSet
     *
     * @param resultSet ResultSet chứa dữ liệu bình luận
     * @return Đối tượng Comment được tạo từ dữ liệu
     * @throws SQLException nếu có lỗi khi đọc dữ liệu
     */
    private Comment createCommentFromResultSet(ResultSet resultSet) throws SQLException {
        try {
            int commentId = resultSet.getInt("comment_ID");
            String title = resultSet.getString("title");
            String content = resultSet.getString("content");
            int rate = Integer.parseInt(resultSet.getString("rate"));
            int memberId = resultSet.getInt("member_ID");
            long isbn = resultSet.getLong("ISBN");

            // Lấy đối tượng Member từ MemberDAO
            return new Comment(commentId, title, content, rate, memberDAO.findById(memberId), isbn);
        } catch (NumberFormatException e) {
            logger.error("CommentDAO", "Error parsing numeric values from result set: " + e.getMessage(), e);
            throw new SQLException("Error parsing comment data: " + e.getMessage(), e);
        }
    }
}
