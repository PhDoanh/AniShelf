package com.library.anishelf.model.enums;

/**
 * The enum Role.
 */
public enum Role {
    /**
     * Admin role.
     */
    ADMIN("ADMIN"),
    /**
     * None role.
     */
    NONE("NONE");
    private final String displayName;

    Role(String displayName) {
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
