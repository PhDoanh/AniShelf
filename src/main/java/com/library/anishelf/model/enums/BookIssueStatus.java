package com.library.anishelf.model.enums;

/**
 * The enum Book issue status.
 */
public enum BookIssueStatus {
    /**
     * The Borrowed.
     */
    BORROWED("Đang mượn"),
    /**
     * Returned book issue status.
     */
    RETURNED("Đã trả"),
    /**
     * The Lost.
     */
    LOST("Làm mất");

    private final String displayName;

    BookIssueStatus(String displayName) {
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
