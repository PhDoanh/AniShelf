package com.library.anishelf.model.enums;

/**
 * The enum Gender.
 */
public enum Gender {
    /**
     * Male gender.
     */
    MALE("Nam"),
    /**
     * Female gender.
     */
    FEMALE("Nữ"),
    /**
     * Other gender.
     */
    OTHER("Khác");

    private final String displayName;

    Gender(String displayName) {
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
