package com.library.anishelf.model.enums;

/**
 * The enum Book status.
 */
public enum BookStatus {
    /**
     * Available book status.
     */
    AVAILABLE("Có sẵn"),
    /**
     * The Unavailable.
     */
    UNAVAILABLE("Hết hàng");

    private final String displayName;

    BookStatus(String displayName) {
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
