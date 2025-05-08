package com.library.anishelf.model.enums;

public enum Role {
    ADMIN("ADMIN"),
    NONE("NONE");

    private final String displayName;

    /**
     * Hàm khởi tạo vai trò với tên hiển thị tương ứng.
     *
     * @param displayName tên hiển thị ứng với vai trò
     */
    Role(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Lấy tên hiển thị ứng với vai trò.
     *
     * @return tên hiển thị ứng với vai trò
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Trả về tên hiển thị của vai trò.
     *
     * @return tên hiển thị của vai trò
     */
    @Override
    public String toString() {
        return displayName;
    }
}