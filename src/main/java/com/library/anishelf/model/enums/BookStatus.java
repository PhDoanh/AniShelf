package com.library.anishelf.model.enums;

/**
 * Enum đại diện cho trạng thái của sách trong thư viện.
 * Trạng thái được sử dụng để xác định xem sách có sẵn để sử dụng hay không.
 */
public enum BookStatus {
    AVAILABLE("Có sẵn"),
    UNAVAILABLE("Hết hàng");

    private final String displayName;

    /**
     * Khởi tạo một trạng thái sách mới.
     * @param displayName Tên hiển thị của trạng thái
     */
    BookStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Lấy tên hiển thị của trạng thái sách.
     * @return Tên hiển thị
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Trả về tên hiển thị của trạng thái sách.
     * Phương thức này được sử dụng để hiển thị trạng thái trong giao diện người dùng.
     * @return Tên hiển thị
     */
    @Override
    public String toString() {
        return displayName;
    }
}
