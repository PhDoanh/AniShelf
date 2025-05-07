package com.library.anishelf.model.enums;

/**
 * Lựa chọn giới tính
 */
public enum Gender {
    MALE ("Nam"),
    FEMALE("Nữ"),
    OTHER("Khác");

    private final String displayName;

    /**
     * Hàm khởi tạo enum giới tính với tên hiển thị chỉ định.
     *
     * @param displayName xâu hiển thị giới tính
     */
    Gender(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Lấy tên hiển thị tương ứng.
     *
     * @return tên hiển thị giới tính
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Trả về chuỗi tên hiển thị của giá trị enum này.
     *
     * @return tên hiển thị của giới tính
     */
    @Override
    public String toString() {
        return displayName;
    }
}
