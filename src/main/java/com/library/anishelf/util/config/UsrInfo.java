package com.library.anishelf.util.config;

/**
 * The type Usr info.
 */
public class UsrInfo {
    private String id;
    private String color;

    /**
     * Instantiates a new Usr info.
     *
     * @param id    the id
     * @param color the color
     */
    public UsrInfo(String id, String color) {
        this.id = id;
        this.color = color;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Gets color.
     *
     * @return the color
     */
    public String getColor() {
        return color;
    }

    /**
     * Sets color.
     *
     * @param color the color
     */
    public void setColor(String color) {
        this.color = color;
    }

    // Phương thức toString để in thông tin của user
    @Override
    public String toString() {
        return "ID: " + id + ", Color: " + color;
    }

    /**
     * Matches id boolean.
     *
     * @param searchId the search id
     * @return the boolean
     */
// Phương thức để kiểm tra ID người dùng
    public boolean matchesId(String searchId) {
        return this.id.equals(searchId);
    }

    /**
     * To file string string.
     *
     * @return the string
     */
    public String toFileString() {
        return id + " " + color;
    }
}
