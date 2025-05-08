package com.library.anishelf.model.enums;

/**
 * Trạng thái của từng cuốn sách trong hệ thống
 */
public enum BookItemStatus {
    AVAILABLE("Có sẵn"),
    RESERVED("Đặt trước"),
    LOANED("Đang mượn"),
    LOST("Làm mất");

    private final String displayName;

    /**
     * @param displayName Tên hiển thị
     */
    BookItemStatus(String displayName) {
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