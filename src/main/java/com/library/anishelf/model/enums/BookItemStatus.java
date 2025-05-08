package com.library.anishelf.model.enums;

/**
 * The enum Book item status.
 */
public enum BookItemStatus {
    /**
     * Available book item status.
     */
    AVAILABLE("Có sẵn"),
    /**
     * The Reserved.
     */
    RESERVED("Đặt trước"),
    /**
     * The Loaned.
     */
    LOANED("Đang mượn"),
    /**
     * The Lost.
     */
    LOST("Làm mất");
    private final String displayName;

    BookItemStatus(String displayName) {
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
