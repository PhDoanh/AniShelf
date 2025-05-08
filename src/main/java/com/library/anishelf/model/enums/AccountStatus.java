package com.library.anishelf.model.enums;

/**
 * The enum Account status.
 */
public enum AccountStatus {
    /**
     * Active account status.
     */
    ACTIVE("ACTIVE"),
    /**
     * Blacklisted account status.
     */
    BLACKLISTED("BLACKLISTED"),
    /**
     * Closed account status.
     */
    CLOSED("CLOSED"),
    /**
     * None account status.
     */
    NONE("NONE");
    private final String statusDisplayName;

    AccountStatus(String statusDisplayName) {
        this.statusDisplayName = statusDisplayName;
    }

    /**
     * Gets display name.
     *
     * @return the display name
     */
    public String getStatusDisplayName() {
        return statusDisplayName;
    }

    @Override
    public String toString() {
        return name(); // Sử dụng tên hiển thị thay vì tên enum
    }
}
