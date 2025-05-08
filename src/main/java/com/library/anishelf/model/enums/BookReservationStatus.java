package com.library.anishelf.model.enums;

/**
 * The enum Book reservation status.
 */
public enum BookReservationStatus {
    /**
     * The Waiting.
     */
    WAITING("Đang chờ"),
    /**
     * Completed book reservation status.
     */
    COMPLETED("Đã xong"),
    /**
     * Canceled book reservation status.
     */
    CANCELED("Đã hủy");

    private final String displayName;

    BookReservationStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets display name.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return name(); // Sử dụng tên hiển thị thay vì tên enum
    }

}
