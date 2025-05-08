package com.library.anishelf.model.enums;

/**
 * Trạng thái báo cáo trong hệ thống
 */
public enum ReportStatus {
    PENDING("Chờ xử lý"),
    RESOLVED("Đã xử lý");

    private final String displayName;

    /**
     * @param displayName Tên hiển thị
     */
    ReportStatus(String displayName) {
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
