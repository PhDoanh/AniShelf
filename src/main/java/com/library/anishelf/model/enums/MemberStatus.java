package com.library.anishelf.model.enums;

/**
 * The enum Member status.
 */
public enum MemberStatus {
    /**
     * Active member status.
     */
    ACTIVE("ACTIVE"),
    /**
     * Blocked member status.
     */
    BLOCKED("BLOCKED"),
    /**
     * None member status.
     */
    NONE("NONE");
    private final String displayName;

    MemberStatus(String displayName) {
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
