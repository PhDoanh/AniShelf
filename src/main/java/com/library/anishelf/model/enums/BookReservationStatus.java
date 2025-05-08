package com.library.anishelf.model.enums;

/**
 * Trạng thái đặt trước sách trong hệ thống
 */
public enum BookReservationStatus {
    WAITING("Đang chờ"),
    COMPLETED("Đã xong"),
    CANCELED("Đã hủy");

    private final String displayName;

    /**
     * @param displayName Tên hiển thị
     */
    BookReservationStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return Tên hiển thị
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @return Tên hiển thị
     */
    @Override
    public String toString() {
        return displayName;
    }
}
